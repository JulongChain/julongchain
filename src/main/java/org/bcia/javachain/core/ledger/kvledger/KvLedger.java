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
package org.bcia.javachain.core.ledger.kvledger;

import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.HistoryDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.ResultsIterator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxMgr;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class KvLedger {

    private String ledgerID;
    private BlockStore blockStore;
    private TxMgr txtmgmt;
    private HistoryDB historyDB;

    /** NewKVLedger constructs new `KVLedger`
     *
     * @param ledgerID
     * @param blockStore
     * @param versionedDB
     * @param historyDB
     * @return
     */
    public static KvLedger newKVLedger(String ledgerID, BlockStore blockStore, VersionedDB versionedDB, HistoryDB historyDB) {
        return null;
    }

    /** Recover the state database and history database (if exist)
      * by recommitting last valid blocks
     */
    public void recoverDBs() {

    }

    /** recommitLostBlocks retrieves blocks in specified range and commit the write set to either
     * state DB or history DB or both
     */
    public void recommitLostBlocks(Long firstBlockNum, Long lastBlockNum, Recoverable ... Recoverablrecoverables) {
        return;
    }

    /** GetTransactionByID retrieves a transaction by id
     *
     * @param txID
     * @return
     */
    public TransactionPackage.ProcessedTransaction getTransactionByID(String txID) {
        return null;
    }

    /** GetBlockchainInfo returns basic info about blockchain
     *
     * @return
     */
    public Ledger.BlockchainInfo getBlockchainInfo() {
        return null;
    }

    /** GetBlockByNumber returns block at a given height
     * blockNumber of  math.MaxUint64 will return last block
     */
    public Common.Block getBlockByNumber(Long blockNumber) {
        return null;
    }

    /** GetBlocksIterator returns an iterator that starts from `startBlockNumber`(inclusive).
     * The iterator is a blocking iterator i.e., it blocks till the next block gets available in the ledger
     * ResultsIterator contains type BlockHolder
     */
    public ResultsIterator getBlocksIterator(Long startBlockNumber) {
        return null;
    }

    /** GetBlockByHash returns a block given it's hash
     *
     * @param blockHash
     * @return
     */
    public Common.Block getBlockByHash(byte[] blockHash) {
        return null;
    }

    /** GetBlockByTxID returns a block which contains a transaction
     *
     * @param txID
     * @return
     */
    public Common.Block getBlockByTxID(String txID) {
        return null;
    }

    public TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) {
        return null;
    }

    /** Prune prunes the blocks/transactions that satisfy the given policy
     *
     * @param prunePolicy
     */
    public void prune(PrunePolicy prunePolicy) {
        return;
    }

    /** NewTxSimulator returns new `ledger.TxSimulator`
     *
     * @return
     */
    public ITxSimulator newTxSimulator() {
        return null;
    }

    /** NewQueryExecutor gives handle to a query executor.
     * A client can obtain more than one 'QueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     */
    public IQueryExecutor newQueryExecutor() {
        return null;
    }

    /** NewHistoryQueryExecutor gives handle to a history query executor.
     * A client can obtain more than one 'HistoryQueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     * Pass the ledger blockstore so that historical values can be looked up from the chain
     */
    public IHistoryQueryExecutor newHistoryQueryExecutor() {
        return null;
    }

    /** Commit commits the valid block (returned in the method RemoveInvalidTransactionsAndPrepare) and related state changes
     *
     * @param block
     */
    public void commit(Common.Block block) {
        return;
    }

    /** Close closes `KVLedger`
     *
     */
    public void close() {

    }


    public String getLedgerID() {
        return ledgerID;
    }

    public void setLedgerID(String ledgerID) {
        this.ledgerID = ledgerID;
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    public TxMgr getTxtmgmt() {
        return txtmgmt;
    }

    public void setTxtmgmt(TxMgr txtmgmt) {
        this.txtmgmt = txtmgmt;
    }

    public HistoryDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(HistoryDB historyDB) {
        this.historyDB = historyDB;
    }
}
