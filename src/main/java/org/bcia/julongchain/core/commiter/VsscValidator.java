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
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.cauthdsl.CAuthDslBuilder;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.julongchain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.julongchain.core.common.sysscprovider.SystemSmartContractProvider;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.CollHashedRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.julongchain.core.smartcontract.SmartContractExecutor;
import org.bcia.julongchain.node.common.helper.SpecHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class VsscValidator implements IVsscValidator {
    private static JavaChainLog log = JavaChainLogFactory.getLog(VsscValidator.class);

    private ICommitterSupport committerSupport;
    private ISystemSmartContractProvider sysSmartContractProvider;

    public VsscValidator(ICommitterSupport committerSupport) {
        this.committerSupport = committerSupport;

        //TODO
        this.sysSmartContractProvider = new SystemSmartContractProvider();
    }

    @Override
    public TransactionPackage.TxValidationCode vsscValidateTx(Common.GroupHeader groupHeader,
                                                              ProposalPackage.SmartContractHeaderExtension extension,
                                                              byte[] envelopeBytes,
                                                              ProposalResponsePackage.ProposalResponsePayload
                                                                      proposalResponsePayload) {
        ProposalPackage.SmartContractAction action = null;
        try {
            ValidateUtils.isNotNull(proposalResponsePayload.getExtension(), "Extension can not be null");
            action = ProposalPackage.SmartContractAction.parseFrom(proposalResponsePayload.getExtension());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return TransactionPackage.TxValidationCode.BAD_RESPONSE_PAYLOAD;
        }

        boolean writesToLSSC = false;
        boolean writesToNonInvokableSSC = false;

        TxRwSet txRwSet = new TxRwSet();
        try {
            txRwSet.fromProtoBytes(action.getResults());
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            return TransactionPackage.TxValidationCode.BAD_RESPONSE_PAYLOAD;
        }

        List<String> namespaceList = new ArrayList<>();
        for (NsRwSet nsRwSet : txRwSet.getNsRwSets()) {
            if (txWritesToNamespace(nsRwSet)) {
                namespaceList.add(nsRwSet.getNameSpace());

                if (!writesToLSSC && CommConstant.LSSC.equals(nsRwSet.getNameSpace())) {
                    writesToLSSC = true;
                }

                if (!writesToNonInvokableSSC && sysSmartContractProvider.isSysSCAndNotInvokableSC2SC(nsRwSet
                        .getNameSpace())) {
                    writesToNonInvokableSSC = true;
                }

                if (!writesToNonInvokableSSC && sysSmartContractProvider.isSysSCAndNotInvkeableExternal(nsRwSet
                        .getNameSpace())) {
                    writesToNonInvokableSSC = true;
                }
            }
        }

        if (extension.getSmartContractId() == null || extension.getSmartContractId().getName() == null) {
            log.warn("Empty extension smart contract id");
            return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
        }

        if (action.getSmartContractId() == null) {
            log.warn("Empty action smart contract id");
            return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
        }

        if (!extension.getSmartContractId().getName().equals(action.getSmartContractId().getName())) {
            log.warn("Different smart contract id");
            return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
        }

        if (action.getSmartContractId().getVersion() == null) {
            log.warn("Empty action smart contract version");
            return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
        }

        String scName = extension.getSmartContractId().getName();
        String scVersion = action.getSmartContractId().getVersion();

        if (!sysSmartContractProvider.isSysSmartContract(scName)) {
            if (writesToLSSC) {
                log.warn("writesToLSSC and app smart contract");
                return TransactionPackage.TxValidationCode.ILLEGAL_WRITESET;
            }

            if (writesToNonInvokableSSC) {
                log.warn("writesToNonInvokableSSC and app smart contract");
                return TransactionPackage.TxValidationCode.ILLEGAL_WRITESET;
            }

            for (String namespace : namespaceList) {
                Object[] infos = null;
                try {
                    infos = getInfoForValidate(groupHeader.getGroupId(), namespace);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
                }

                SmartContractInstance scInstance = (SmartContractInstance) infos[0];
                SmartContractInstance vsscInstance = (SmartContractInstance) infos[1];
                byte[] policy = (byte[]) infos[2];

                if (namespace.equals(scName) && !scInstance.getSmartContractVersion().equals(scVersion)) {
                    log.warn("smart contract didn't match version in lssc");
                    return TransactionPackage.TxValidationCode.EXPIRED_SMARTCONTRACT;
                }

                try {
                    vsscValidateTxForSC(envelopeBytes, groupHeader.getTxId(), groupHeader.getGroupId(), vsscInstance
                            .getSmartContractName(), vsscInstance.getSmartContractVersion(), policy);
                } catch (SmartContractException e) {
                    log.warn("vssc validate fail");
                    return TransactionPackage.TxValidationCode.ENDORSEMENT_POLICY_FAILURE;
                }
            }
        } else {
            if (sysSmartContractProvider.isSysSCAndNotInvkeableExternal(scName)) {
                log.warn("isSysSCAndNotInvkeableExternal " + scName);
                return TransactionPackage.TxValidationCode.ILLEGAL_WRITESET;
            }

            Object[] infos = null;
            try {
                infos = getInfoForValidate(groupHeader.getGroupId(), scName);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return TransactionPackage.TxValidationCode.INVALID_OTHER_REASON;
            }

            SmartContractInstance vsscInstance = (SmartContractInstance) infos[1];
            byte[] policy = (byte[]) infos[2];

            try {
                vsscValidateTxForSC(envelopeBytes, groupHeader.getTxId(), groupHeader.getGroupId(), vsscInstance
                        .getSmartContractName(), vsscInstance.getSmartContractVersion(), policy);
            } catch (SmartContractException e) {
                log.warn("vssc validate fail");
                return TransactionPackage.TxValidationCode.ENDORSEMENT_POLICY_FAILURE;
            }
        }

        return TransactionPackage.TxValidationCode.VALID;
    }

    private Object[] getInfoForValidate(String groupId, String scId) throws ValidateException,
            InvalidProtocolBufferException, LedgerException {
        SmartContractInstance scInstance = new SmartContractInstance();
        SmartContractInstance vsscInstance = new SmartContractInstance();
        byte[] policy = null;

        if (!sysSmartContractProvider.isSysSmartContract(scId)) {
            SmartContractDataPackage.SmartContractData scData = getSCData(scId);
            scInstance.setSmartContractName(scData.getName());
            scInstance.setSmartContractVersion(scData.getVersion());

            vsscInstance.setSmartContractName(scData.getVssc());

            policy = scData.getPolicy().toByteArray();
            //TODO: policy
        } else {
            scInstance.setSmartContractName(scId);
            //TODO:wen iian zhong du
            scInstance.setSmartContractVersion(CommConstant.METADATA_VERSION);

            vsscInstance.setSmartContractName(CommConstant.VSSC);
            policy = CAuthDslBuilder.signedByAnyAdmin(committerSupport.getMspIds(groupId)).toByteArray();
        }

        vsscInstance.setSmartContractVersion(CommConstant.METADATA_VERSION);

        return new Object[]{scInstance, vsscInstance, policy};
    }

    private SmartContractDataPackage.SmartContractData getSCData(String scId) throws ValidateException,
            LedgerException, InvalidProtocolBufferException {
        INodeLedger nodeLedger = committerSupport.getLedger();
        ValidateUtils.isNotNull(nodeLedger, "nodeLedger can not be null");

        IQueryExecutor queryExecutor = null;
        byte[] bytes = null;
        try {
            queryExecutor = nodeLedger.newQueryExecutor();
            bytes = queryExecutor.getState(CommConstant.LSSC, scId);
        } finally {
            if (queryExecutor != null) {
                queryExecutor.done();
            }
        }

        ValidateUtils.isNotNull(bytes, "lssc's state can not be null");

        SmartContractDataPackage.SmartContractData scData = SmartContractDataPackage.SmartContractData.parseFrom(bytes);
        ValidateUtils.isNotNull(scData.getVssc(), "lssc's vssc can not be null");
        ValidateUtils.isNotNull(scData.getPolicy(), "lssc's policy can not be null");

        return scData;
    }

    private boolean txWritesToNamespace(NsRwSet nsRwSet) {
        if (nsRwSet.getKvRwSet() != null && nsRwSet.getKvRwSet().getWritesCount() > 0) {
            return true;
        }

        if (!committerSupport.getCapabilities().isPrivateGroupData()) {
            return false;
        }

        for (CollHashedRwSet collHashedRwSet : nsRwSet.getCollHashedRwSets()) {
            if (collHashedRwSet.getHashedRwSet() != null && collHashedRwSet.getHashedRwSet().getHashedWritesCount() >
                    0) {
                return true;
            }
        }

        return false;
    }

    private ProposalResponsePackage.Response vsscValidateTxForSC(byte[] envelopeBytes, String txId, String groupId,
                                                                 String vsscName, String vsscVersion, byte[] policy)
            throws SmartContractException {
        String vsscTxId = UUID.randomUUID().toString();

        // args[0] - function name (not used now)
        // args[1] - serialized Envelope
        // args[2] - serialized policy
        byte[][] args = new byte[][]{new byte[0], envelopeBytes, policy};

        SmartContractPackage.SmartContractInvocationSpec invocationSpec = SpecHelper.buildInvocationSpec(vsscName, args);
        SmartContractContext scContext = new SmartContractContext(groupId, vsscName, vsscVersion, vsscTxId, true,
                null, null);

        Object[] objs = new SmartContractExecutor().execute(scContext, invocationSpec);
        return (ProposalResponsePackage.Response) objs[0];
    }
}