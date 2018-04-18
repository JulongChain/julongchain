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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/16
 * @company Dingxuan
 */
public class NodeLedger implements INodeLedger {
    @Override
    public TransactionPackage.ProcessedTransaction getTransactionByID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public Common.Block getBlockByHash(byte[] blockHash) throws LedgerException {
        return null;
    }

    @Override
    public Common.Block getBlockByTxID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public ITxSimulator newTxSimulator(String txId) throws LedgerException {
//        return new TxSimulator();
        return null;
    }

    @Override
    public IQueryExecutor newQueryExecutor() throws LedgerException {
        return null;
    }

    @Override
    public IHistoryQueryExecutor newHistoryQueryExecutor() throws LedgerException {
        return null;
    }

    @Override
    public void prune(PrunePolicy policy) throws LedgerException {

    }

    public INodeLedger create(Common.Block genesisBlock) throws LedgerException {
        return null;
    }

    public INodeLedger open(String ledgerID) throws LedgerException {
        return null;
    }

    public Boolean exists(String ledgerID) throws LedgerException {
        return null;
    }

    public String[] list() throws LedgerException {
        return new String[0];
    }

    @Override
    public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
        return null;
    }

    @Override
    public Common.Block getBlockByNumber(Long blockNumber) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator getBlocksIterator(Long startBlockNumber) throws LedgerException {
        return null;
    }

    @Override
    public void close() throws LedgerException {

    }

    @Override
    public void commit(Common.Block block) throws LedgerException {

    }

    @Override
    public void CommitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException {

    }

}
