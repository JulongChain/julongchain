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
package org.bcia.julongchain.core.smartcontract;

import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;

import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/18
 * @company Dingxuan
 */
public class TransactionContext {

    private String chainID;
    private ProposalPackage.SignedProposal signedProp;
    private ProposalPackage.Proposal proposal;
    private SmartContractShim.SmartContractMessage responseNotifier;
    private Map<String, IResultsIterator> queryIteratorMap;
    private Map<String, PendingQueryResult> pendingQueryResults;
    private ITxSimulator txSimulator;
    private IHistoryQueryExecutor historyQueryExecutor;

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public ProposalPackage.SignedProposal getSignedProp() {
        return signedProp;
    }

    public void setSignedProp(ProposalPackage.SignedProposal signedProp) {
        this.signedProp = signedProp;
    }

    public ProposalPackage.Proposal getProposal() {
        return proposal;
    }

    public void setProposal(ProposalPackage.Proposal proposal) {
        this.proposal = proposal;
    }

    public SmartContractShim.SmartContractMessage getResponseNotifier() {
        return responseNotifier;
    }

    public void setResponseNotifier(SmartContractShim.SmartContractMessage responseNotifier) {
        this.responseNotifier = responseNotifier;
    }

    public Map<String, IResultsIterator> getQueryIteratorMap() {
        return queryIteratorMap;
    }

    public void setQueryIteratorMap(Map<String, IResultsIterator> queryIteratorMap) {
        this.queryIteratorMap = queryIteratorMap;
    }

    public Map<String, PendingQueryResult> getPendingQueryResults() {
        return pendingQueryResults;
    }

    public void setPendingQueryResults(Map<String, PendingQueryResult> pendingQueryResults) {
        this.pendingQueryResults = pendingQueryResults;
    }

    public ITxSimulator getTxSimulator() {
        return txSimulator;
    }

    public void setTxSimulator(ITxSimulator txSimulator) {
        this.txSimulator = txSimulator;
    }

    public IHistoryQueryExecutor getHistoryQueryExecutor() {
        return historyQueryExecutor;
    }

    public void setHistoryQueryExecutor(IHistoryQueryExecutor historyQueryExecutor) {
        this.historyQueryExecutor = historyQueryExecutor;
    }
}
