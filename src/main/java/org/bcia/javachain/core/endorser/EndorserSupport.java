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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.node.NodeTool;
import org.bcia.javachain.core.ssc.ISystemSmartContractManager;
import org.bcia.javachain.core.ssc.SystemSmartContractManager;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 背书能力支持对象
 *
 * @author
 * @date 2018/3/15
 * @company Dingxuan
 */
public class EndorserSupport implements IEndorserSupport {
    //TODO:Spring
    private ISystemSmartContractManager sysSmartContractManager = new SystemSmartContractManager();

    @Override
    public boolean isSysCCAndNotInvokableExternal(String name) {
        return sysSmartContractManager.isSysSmartContractAndNotInvokableExternal(name);
    }

    @Override
    public ITxSimulator getTxSimulator(String ledgerName, String txId) {
        INodeLedger nodeLedger = NodeTool.getLedger(ledgerName);

        try {
            return nodeLedger.newTxSimulator(txId);
        } catch (LedgerException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public IHistoryQueryExecutor getHistoryQueryExecutor(String ledgerName) {
        INodeLedger nodeLedger = NodeTool.getLedger(ledgerName);
        try {
            return nodeLedger.newHistoryQueryExecutor();
        } catch (LedgerException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public TransactionPackage.ProcessedTransaction getTransactionByID(String groupId, String txId) {
        INodeLedger nodeLedger = NodeTool.getLedger(groupId);
        try {
            return nodeLedger.getTransactionByID(txId);
        } catch (LedgerException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isSysCC(String name) {
        return sysSmartContractManager.isSysSmartContract(name);
    }

    @Override
    public ProposalResponsePackage.Response execute(String cid, String name, String version, String txid, boolean syscc, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop, Object spec) {
        if(spec instanceof Smartcontract.SmartContractInvocationSpec){

        }


        return null;
    }

    @Override
    public void checkACL(ProposalPackage.SignedProposal signedProposal, Common.GroupHeader groupHeader, Common.SignatureHeader signatureHeader, ProposalPackage.SmartContractHeaderExtension extension) {

    }

    @Override
    public boolean isJavaCC(byte[] buffer) {
        return false;
    }
}
