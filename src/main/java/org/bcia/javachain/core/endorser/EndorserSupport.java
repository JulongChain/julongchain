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
package org.bcia.javachain.core.endorser;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.exception.SmartContractException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.common.resourceconfig.config.SmartContractConfig;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.node.util.NodeUtils;
import org.bcia.javachain.core.smartcontract.SmartContractExecutor;
import org.bcia.javachain.core.ssc.ISystemSmartContractManager;
import org.bcia.javachain.core.ssc.SystemSmartContractManager;
import org.bcia.javachain.core.ssc.lssc.LSSC;
import org.bcia.javachain.node.common.helper.SpecHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.*;

/**
 * 背书能力支持对象(隔离对其他模块的依赖)
 *
 * @author zhouhui
 * @date 2018/3/15
 * @company Dingxuan
 */
public class EndorserSupport implements IEndorserSupport {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EndorserSupport.class);

    //TODO:Spring
    private ISystemSmartContractManager sysSmartContractManager = new SystemSmartContractManager();

    //TODO:Spring
    private SmartContractExecutor smartContractExecutor = new SmartContractExecutor();

    @Override
    public boolean isSysSCAndNotInvokableExternal(String scName) {
        return sysSmartContractManager.isSysSmartContractAndNotInvokableExternal(scName);
    }

    @Override
    public ITxSimulator getTxSimulator(String ledgerName, String txId) throws NodeException {
        try {
            INodeLedger nodeLedger = LedgerManager.openLedger(ledgerName);
            return nodeLedger.newTxSimulator(txId);
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    @Override
    public IHistoryQueryExecutor getHistoryQueryExecutor(String ledgerName) throws NodeException {
        INodeLedger nodeLedger = NodeUtils.getLedger(ledgerName);
        try {
            return nodeLedger.newHistoryQueryExecutor();
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    @Override
    public TransactionPackage.ProcessedTransaction getTransactionById(String groupId, String txId) throws
            NodeException {
        INodeLedger nodeLedger = NodeUtils.getLedger(groupId);
        try {
            return nodeLedger.getTransactionByID(txId);
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    @Override
    public boolean isSysSmartContract(String scName) {
        return sysSmartContractManager.isSysSmartContract(scName);
    }

    @Override
    public Object[] execute(String groupId, String scName, String scVersion, String txId, boolean sysSC,
                            ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal proposal,
                            Smartcontract.SmartContractInvocationSpec spec) throws NodeException {
        SmartContractContext scContext = new SmartContractContext(groupId, scName, scVersion, txId, sysSC,
                signedProposal, proposal);
        //TODO:Decorator功能未实现
        try {
            return smartContractExecutor.execute(scContext, spec);
        } catch (SmartContractException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    @Override
    public Object[] execute(String groupId, String scName, String scVersion, String txId, boolean sysSC,
                            ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal proposal,
                            Smartcontract.SmartContractDeploymentSpec spec) throws NodeException {
        SmartContractContext scContext = new SmartContractContext(groupId, scName, scVersion, txId, sysSC,
                signedProposal, proposal);
        try {
            return smartContractExecutor.execute(scContext, spec);
        } catch (SmartContractException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    @Override
    public ISmartContractDefinition getSmartContractDefinition(String groupId, String scName, String txId,
                                                               ProposalPackage.SignedProposal signedProposal,
                                                               ProposalPackage.Proposal proposal, ITxSimulator
                                                                       txSimulator) throws NodeException {
        //TODO:1、txSimulator是否有用 2、version配置如何更改
        String version = CommConstant.METADATA_VERSION;
        SmartContractContext scContext = new SmartContractContext(groupId, CommConstant.LSSC, version, txId, true,
                signedProposal, proposal);

        Smartcontract.SmartContractInvocationSpec lsscSpec = SpecHelper.buildInvocationSpec(CommConstant.LSSC,
                LSSC.GET_SC_DATA.getBytes(), groupId.getBytes(), scName.getBytes());

        try {
            Object[] objs = smartContractExecutor.execute(scContext, lsscSpec);

            if (objs != null && objs.length > 0) {
                ProposalResponsePackage.Response response = (ProposalResponsePackage.Response) objs[0];
                if (response.getStatus() == Common.Status.SUCCESS_VALUE) {
                    SmartContractDataPackage.SmartContractData data = SmartContractDataPackage.SmartContractData
                            .parseFrom(response.getPayload());

                    return new SmartContractConfig(data);


//                    Query.SmartContractInfo info = Query.SmartContractInfo.parseFrom(response.getPayload());
//                    SmartContractData data = new SmartContractData(info);
//                    return data;
                } else {
                    throw new NodeException("Execute smart contract fail, get status code: " + response.getStatus());
                }
            }
        } catch (SmartContractException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }

        throw new NodeException("Unknown error");
    }

    @Override
    public void checkACL(ProposalPackage.SignedProposal signedProposal, Common.GroupHeader groupHeader, Common
            .SignatureHeader signatureHeader, ProposalPackage.SmartContractHeaderExtension extension) {
        //TODO：有些参数未使用?
        try {
            AclManagement.getACLProvider().checkACL(null, groupHeader.getGroupId(), signedProposal);
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isJavaSC(byte[] buffer) {
        return true;
    }


    @Override
    public void checkInstantiationPolicy(String name, String version, ISmartContractDefinition scDefinition) {
        //TODO：未实现
        //new SmartContractProvider()
    }
}
