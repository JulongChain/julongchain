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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.*;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxMgr;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxMgr;
import org.bcia.javachain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.javachain.core.ledger.sceventmgmt.ScEventMgmt;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class KvLedger implements INodeLedger {

    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(KvLedger.class);

    private String ledgerID;
    private BlockStore blockStore;
    private TxMgr txtmgmt;
    private IHistoryDB historyDB;

    /** NewKVLedger constructs new `KVLedger`
     *
     * @param ledgerID
     * @param blockStore
     * @param historyDB
     * @return
     */
    public static KvLedger newKVLedger(String ledgerID,
                                       BlockStore blockStore,
                                       DB versionedDB,
                                       IHistoryDB historyDB,
                                       Map<String, StateListener> stateListeners) throws LedgerException {
        logger.debug("Creating KVLedger ledgerID = " + ledgerID);

        //TODO get txMgr
        TxMgr  txmgmt = LockBasedTxMgr.newLockBasedTxMgr(ledgerID, versionedDB, stateListeners);

        KvLedger kvLedger = new KvLedger();
        kvLedger.setLedgerID(ledgerID);
        kvLedger.setBlockStore(blockStore);
        kvLedger.setTxtmgmt(txmgmt);
        kvLedger.setHistoryDB(historyDB);

        //TODO get scEventListener

        ISmartContractLifecycleEventListener scEventListener = null;
        logger.debug("Register state db for smartcontract lifecycle event " + (scEventListener != null));

        if(scEventListener != null){
            ScEventMgmt.getMgr().register(ledgerID, scEventListener);
        }

        //TODO recover state DB & history DB
        kvLedger.recoverDBs();

        return kvLedger;
    }

    /** Recover the state database and history database (if exist)
      * by recommitting last valid blocks
     */
    public void recoverDBs() throws LedgerException {
        logger.debug("Evtering revocerDBs()");
        Ledger.BlockchainInfo info = blockStore.getBlockchainInfo();
        if(info.getHeight() == 0){
            logger.debug("Block storage is empty");
            return;
        }
        long lastAvailableBlockNum = info.getHeight() - 1;
        List<Recoverable> recoverables = new ArrayList<>();
        List<Recoverer> recoverers = new ArrayList<>();

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

    @Override
    public ITxSimulator newTxSimulator(String txId) throws LedgerException {
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

    @Override
    public BlockAndPvtData getPvtDataAndBlockByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        return null;
    }

    @Override
    public List<TxPvtData> getPvtDataByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        return null;
    }

    @Override
    public void purgePrivateData(long maxBlockNumToRetain) throws LedgerException {

    }

    @Override
    public long privateDataMinBlockNum() throws LedgerException {
        return 0;
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

    @Override
    public void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException {

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

    public IHistoryDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(IHistoryDB historyDB) {
        this.historyDB = historyDB;
    }
}
