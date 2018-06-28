/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.commiter;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.CommitterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.protos.ConfigEnvelopeVO;
import org.bcia.julongchain.common.protos.PayloadVO;
import org.bcia.julongchain.common.protos.TransactionVO;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.julongchain.core.common.validation.MsgValidation;
import org.bcia.julongchain.core.ledger.util.TxValidationFlags;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.*;

/**
 * Committer节点校验器
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class CommitterValidator implements ICommitterValidator {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CommitterValidator.class);

    private ICommitterSupport committerSupport;
    private IVsscValidator vsscValidator;

    public CommitterValidator(ICommitterSupport committerSupport) {
        this.committerSupport = committerSupport;

        this.vsscValidator = new VsscValidator(committerSupport);
    }

    @Override
    public Common.Block validate(Common.Block block) throws ValidateException {
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getData(), "block.data can not be null");
        ValidateUtils.isNotNull(block.getData().getDataList(), "block.data.dataList can not be null");

        //该区块中交易的数量
        int txCount = block.getData().getDataCount();
        if (txCount <= 0) {
            //如果交易数量少于1，该区块无效
            throw new ValidateException("txCount should bigger than 0");
        }

        //交易验证器标识，对Block里面每一项交易打标记，到底是有效还是无效
        TxValidationFlags txValidationFlags = new TxValidationFlags(txCount);

        //有效的交易id集合(没有去重)
        String[] txIdArray = new String[txCount];

        Map<Integer, SmartContractInstance> txInvokedSCInstances = new HashMap<>();
        Map<Integer, SmartContractInstance> txUpgradedSCInstances = new HashMap<>();

        BlockValidationResult result = new BlockValidationResult();
        for (int i = 0; i < txCount; i++) {
            BlockValidationRequest request = new BlockValidationRequest(block, block.getData().getData(i)
                    .toByteArray(), i, this);
            validateTx(request, result);

            if (result.getTxValidationCode().equals(TransactionPackage.TxValidationCode.VALID)) {
                txIdArray[i] = result.getTxId();

                if (result.getSmartContractInstance() != null) {
                    txInvokedSCInstances.put(i, result.getSmartContractInstance());
                }

                if (result.getSmartContractUpdateInstance() != null) {
                    txUpgradedSCInstances.put(i, result.getSmartContractUpdateInstance());
                }
            }
        }

        if (committerSupport.getCapabilities().isForbidDuplicateTxId()) {
            markTxIdDuplicates(txIdArray, txValidationFlags);
        }

        invalidTxsForUpgradeSC(txInvokedSCInstances, txUpgradedSCInstances, txValidationFlags);

        Common.Block.Builder newBlockBuilder = block.toBuilder();
        BlockUtils.initBlockMetadata(newBlockBuilder);
        newBlockBuilder.getMetadataBuilder().setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER_VALUE,
                txValidationFlags.toByteString());
        return newBlockBuilder.build();
    }

    private TxValidationFlags invalidTxsForUpgradeSC(Map<Integer, SmartContractInstance> txInvokedSCInstances,
                                                     Map<Integer, SmartContractInstance> txUpgradedSCInstances,
                                                     TxValidationFlags txValidationFlags) {
        if (txInvokedSCInstances.size() <= 0) {
            return txValidationFlags;
        }

        Map<String, Integer> finalValidUpgradeTxs = new HashMap<>();
        Map<String, SmartContractInstance> upgradedSCInstances = new HashMap<>();

        Iterator<Map.Entry<Integer, SmartContractInstance>> upgradedIterator = txUpgradedSCInstances.entrySet()
                .iterator();
        while (upgradedIterator.hasNext()) {
            Map.Entry<Integer, SmartContractInstance> entry = upgradedIterator.next();
            Integer txIndex = entry.getKey();
            SmartContractInstance smartContractInstance = entry.getValue();

            String upgradedSCKey = generateSCKey(smartContractInstance.getSmartContractName(), smartContractInstance
                    .getGroupId());

            if (!finalValidUpgradeTxs.containsKey(upgradedSCKey)) {
                finalValidUpgradeTxs.put(upgradedSCKey, txIndex);
                upgradedSCInstances.put(upgradedSCKey, smartContractInstance);
            } else {
                int finalIndex = finalValidUpgradeTxs.get(upgradedSCKey);
                if (txIndex > finalIndex) {
                    txValidationFlags.setFlag(finalIndex, TransactionPackage.TxValidationCode
                            .SMARTCONTRACT_VERSION_CONFLICT);

                    finalValidUpgradeTxs.put(upgradedSCKey, txIndex);
                    upgradedSCInstances.put(upgradedSCKey, smartContractInstance);
                } else {
                    log.info("Invalid transaction with index {}: was upgraded by latter tx", txIndex);
                    txValidationFlags.setFlag(txIndex, TransactionPackage.TxValidationCode
                            .SMARTCONTRACT_VERSION_CONFLICT);
                }
            }
        }

        Iterator<Map.Entry<Integer, SmartContractInstance>> invokedIterator = txInvokedSCInstances.entrySet()
                .iterator();
        while (invokedIterator.hasNext()) {
            Map.Entry<Integer, SmartContractInstance> entry = invokedIterator.next();
            Integer txIndex = entry.getKey();
            SmartContractInstance smartContractInstance = entry.getValue();

            String sCKey = generateSCKey(smartContractInstance.getSmartContractName(), smartContractInstance
                    .getGroupId());

            if (upgradedSCInstances.containsKey(sCKey)) {
                if (txValidationFlags.isValid(txIndex)) {
                    txValidationFlags.setFlag(txIndex, TransactionPackage.TxValidationCode
                            .SMARTCONTRACT_VERSION_CONFLICT);
                }
            }
        }

        return txValidationFlags;
    }

    private String generateSCKey(String smartContractName, String groupId) {
        return smartContractName + CommConstant.PATH_SEPARATOR + groupId;
    }

    /**
     * 标记交易id是否重复
     *
     * @param txIdArray
     * @param txValidationFlags
     */
    private void markTxIdDuplicates(String[] txIdArray, TxValidationFlags txValidationFlags) {
        List<String> existedTxList = new ArrayList<>();
        for (int i = 0; i < txIdArray.length; i++) {
            String txId = txIdArray[i];

            if (existedTxList.contains(txId)) {
                txValidationFlags.setFlag(i, TransactionPackage.TxValidationCode.DUPLICATE_TXID);
            } else {
                existedTxList.add(txId);
            }
        }
    }

    private void validateTx(BlockValidationRequest request, BlockValidationResult result) {
        if (request.getData() == null) {
            result.setTxIndex(request.getTxIndex());
            return;
        }

        Common.Envelope envelope = null;
        try {
            envelope = Common.Envelope.parseFrom(request.getData());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            result.setTxIndex(request.getTxIndex());
            result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
            return;
        }

        Object[] validateTxObjs = MsgValidation.validateTransaction(envelope, committerSupport.getCapabilities());
        if (!validateTxObjs[0].equals(TransactionPackage.TxValidationCode.VALID)) {
            result.setTxIndex(request.getTxIndex());
            result.setTxValidationCode((TransactionPackage.TxValidationCode) validateTxObjs[0]);
            return;
        }

        Common.Payload payload = (Common.Payload) validateTxObjs[1];
        Common.GroupHeader groupHeader = (Common.GroupHeader) validateTxObjs[2];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension)
                validateTxObjs[3];
        ProposalResponsePackage.ProposalResponsePayload proposalResponsePayload = (ProposalResponsePackage
                .ProposalResponsePayload) validateTxObjs[4];

        if (!chainExists(groupHeader.getGroupId())) {
            result.setTxIndex(request.getTxIndex());
            result.setTxValidationCode(TransactionPackage.TxValidationCode.TARGET_CHAIN_NOT_FOUND);
            return;
        }

        SmartContractInstance invokeInstance = null;
        SmartContractInstance upgradeInstance = null;

        switch (groupHeader.getType()) {
            case Common.HeaderType.ENDORSER_TRANSACTION_VALUE:
                try {
                    if (committerSupport.getLedger().getTransactionByID(groupHeader.getTxId()) != null) {
                        result.setTxIndex(request.getTxIndex());
                        result.setTxValidationCode(TransactionPackage.TxValidationCode.DUPLICATE_TXID);
                        return;
                    }
                } catch (LedgerException e) {
                    log.error(e.getMessage(), e);
                    result.setTxIndex(request.getTxIndex());
                    result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    return;
                }

                TransactionPackage.TxValidationCode txValidationCode = vsscValidator.vsscValidateTx(groupHeader,
                        extension, request.getData(), proposalResponsePayload);
                if (!txValidationCode.equals(TransactionPackage.TxValidationCode.VALID)) {
                    result.setTxIndex(request.getTxIndex());
                    result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    return;
                }

                try {
                    SmartContractInstance[] txScInstances = getTxScInstance(payload);
                    invokeInstance = txScInstances[0];
                    upgradeInstance = txScInstances[1];
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    result.setTxIndex(request.getTxIndex());
                    result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    return;
                }

                break;
            case Common.HeaderType.CONFIG_VALUE:
                PayloadVO payloadVO = new PayloadVO();
                try {
                    payloadVO.parseFrom(payload);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    result.setTxIndex(request.getTxIndex());
                    result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    return;
                }

                ConfigEnvelopeVO configEnvelopeVO = (ConfigEnvelopeVO) payloadVO.getDataVO();
                try {
                    committerSupport.apply(configEnvelopeVO.toProto());
                } catch (CommitterException e) {
                    log.error(e.getMessage(), e);
                    result.setTxIndex(request.getTxIndex());
                    result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    return;
                }

                break;
            case Common.HeaderType.NODE_RESOURCE_UPDATE_VALUE:
                break;
            default:
                result.setTxIndex(request.getTxIndex());
                result.setTxValidationCode(TransactionPackage.TxValidationCode.UNKNOWN_TX_TYPE);
                return;
        }

        result.setTxIndex(request.getTxIndex());
        result.setTxValidationCode(TransactionPackage.TxValidationCode.VALID);
        result.setSmartContractInstance(invokeInstance);
        result.setSmartContractUpdateInstance(upgradeInstance);
        result.setTxId(groupHeader.getTxId());
    }

    private SmartContractInstance[] getTxScInstance(Common.Payload payload) throws InvalidProtocolBufferException,
            ValidateException {
        PayloadVO payloadVO = new PayloadVO();
        payloadVO.parseFrom(payload);

        SmartContractInstance invokeInstance = new SmartContractInstance();
        invokeInstance.setGroupId(payloadVO.getGroupHeaderVO().getGroupId());

        ProposalPackage.SmartContractHeaderExtension extension = payloadVO.getGroupHeaderVO().getGroupHeaderExtension();
        invokeInstance.setSmartContractName(extension.getSmartContractId().getName());
        invokeInstance.setSmartContractVersion(extension.getSmartContractId().getVersion());

        SmartContractInstance upgradeInstance = null;

        TransactionVO transactionVO = (TransactionVO) payloadVO.getDataVO();
        SmartContractPackage.SmartContractInvocationSpec invocationSpec = transactionVO.getTransactionActionVOList().get(0)
                .getSmartContractActionPayloadVO().getSmartContractProposalPayloadVO().getInput();

        if (CommConstant.LSSC.equals(invokeInstance.getSmartContractName())) {
            if (CommConstant.UPGRADE.equals(invocationSpec.getSmartContractSpec().getInput().getArgs(0).toStringUtf8
                    ())) {
                SmartContractPackage.SmartContractDeploymentSpec deploymentSpec = SmartContractPackage.SmartContractDeploymentSpec
                        .parseFrom(invocationSpec.getSmartContractSpec().getInput().getArgs(2));
                upgradeInstance = new SmartContractInstance();
                upgradeInstance.setGroupId(payloadVO.getGroupHeaderVO().getGroupId());
                upgradeInstance.setSmartContractName(deploymentSpec.getSmartContractSpec().getSmartContractId().getName());
                upgradeInstance.setSmartContractVersion(deploymentSpec.getSmartContractSpec().getSmartContractId().getVersion());
            }
        }

        return new SmartContractInstance[]{invokeInstance, upgradeInstance};
    }

    private boolean chainExists(String chain) {
        //TODO:yao shi xian
        return true;
    }
}
