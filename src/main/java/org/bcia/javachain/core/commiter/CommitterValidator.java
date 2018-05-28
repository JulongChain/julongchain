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
package org.bcia.javachain.core.commiter;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.CommitterException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.protos.ConfigEnvelopeVO;
import org.bcia.javachain.common.protos.PayloadVO;
import org.bcia.javachain.common.protos.TransactionVO;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.javachain.core.common.validation.MsgValidation;
import org.bcia.javachain.core.ledger.util.TxValidationFlags;
import org.bcia.javachain.node.common.helper.SpecHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
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
    public void validate(Common.Block block) throws ValidateException {
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getData(), "block.data can not be null");

        TxValidationFlags txValidationFlags = new TxValidationFlags(block.getData().getDataCount());

        BlockValidationResult result = new BlockValidationResult();
        if (block.getData().getDataList() != null && block.getData().getDataCount() > 0) {
            for (int i = 0; i < block.getData().getDataCount(); i++) {
                BlockValidationRequest request = new BlockValidationRequest(block, block.getData().getData(i)
                        .toByteArray(), i, this);
                validateTx(request, result);
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
        Smartcontract.SmartContractInvocationSpec invocationSpec = transactionVO.getTransactionActionVOList().get(0)
                .getSmartContractActionPayloadVO().getSmartContractProposalPayloadVO().getInput();

        if (CommConstant.LSSC.equals(invokeInstance.getSmartContractName())) {
            if (CommConstant.UPGRADE.equals(invocationSpec.getSmartContractSpec().getInput().getArgs(0).toStringUtf8
                    ())) {
                Smartcontract.SmartContractDeploymentSpec deploymentSpec = Smartcontract.SmartContractDeploymentSpec
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
