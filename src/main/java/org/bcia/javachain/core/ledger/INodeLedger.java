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
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * INodeLedger differs from the OrdererLedger in that NodeLedger locally maintain a bitmask
 * that tells apart valid transactions from invalid ones
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface INodeLedger extends INodeLedgerProvider{

    /**
     * getTransactionByID retrieves a transaction by id
     *
     * @param txID
     * @return
     * @throws LedgerException
     */
    TransactionPackage.ProcessedTransaction getTransactionByID(String txID) throws LedgerException;

    /**
     * getBlockByHash returns a block given it's hash
     *
     * @param blockHash
     * @return
     * @throws LedgerException
     */
    Common.Block getBlockByHash(byte[] blockHash) throws LedgerException;

    /**
     * getBlockByTxID returns a block which contains a transaction
     *
     * @param txID
     * @return
     * @throws LedgerException
     */
    Common.Block getBlockByTxID(String txID) throws LedgerException;

    /**
     * getTxValidationCodeByTxID returns reason code of transaction validation
     *
     * @param txID
     * @return
     * @throws LedgerException
     */
    TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException;

    /**
     * newTxSimulator gives handle to a transaction simulator.
     * A client can obtain more than one 'TxSimulator's for parallel execution.
     * Any snapshoting/synchronization should be performed at the implementation level if required
     *
     * @return ITxSimulator
     * @throws LedgerException
     * @param txId
     */
    ITxSimulator newTxSimulator(String txId) throws LedgerException;

    /**
     * newQueryExecutor gives handle to a query executor.
     * A client can obtain more than one 'QueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     *
     * @return
     * @throws LedgerException
     */
    IQueryExecutor newQueryExecutor() throws LedgerException;

    /**
     * newHistoryQueryExecutor gives handle to a history query executor.
     * A client can obtain more than one 'HistoryQueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     *
     * @return
     * @throws LedgerException
     */
    IHistoryQueryExecutor newHistoryQueryExecutor() throws LedgerException;

    /**
     * prune prunes the blocks/transactions that satisfy the given policy
     *
     * @param policy
     * @throws LedgerException
     */
    void prune(PrunePolicy policy) throws LedgerException;

}
