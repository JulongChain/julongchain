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

import io.grpc.internal.LogExceptionRunnable;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.FsBlockStore;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.*;
import org.bcia.javachain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxManager;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxManager;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.ledgerstorage.Store;
import org.bcia.javachain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.javachain.core.ledger.sceventmgmt.ScEventMgmt;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.*;

/**
 * 账本类
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class KvLedger implements INodeLedger {

    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(KvLedger.class);

    private String ledgerID;
    private BlockStore blockStore;
    private TxManager txtmgmt;
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
        TxManager txmgmt = LockBasedTxManager.newLockBasedTxMgr(ledgerID, versionedDB, stateListeners);

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
        recoverables.add(txtmgmt);
        recoverables.add(historyDB);
        for(Recoverable recoverable : recoverables){
            long firstBlockNum = recoverable.shouldRecover();
            if(firstBlockNum - 1 != lastAvailableBlockNum){
                Recoverer recoverer = new Recoverer();
                recoverer.setFirstBlockNum(firstBlockNum);
                recoverer.setRecoverable(recoverable);
                recoverers.add(recoverer);
            }
        }
        if(recoverers.size() == 0){
            return;
        } else if(recoverers.size() == 1){
            recommitLostBlocks(recoverers.get(0).getFirstBlockNum(), lastAvailableBlockNum, recoverers.get(0).getRecoverable());
        } else {
            //小号放前面 升序
            Collections.sort(recoverers, new Comparator<Recoverer>() {
                @Override
                public int compare(Recoverer o1, Recoverer o2) {
                    return o1.getFirstBlockNum() > o2.getFirstBlockNum() ? 1 : -1;
                }
            });
            //小号向大号看齐
            recommitLostBlocks(recoverers.get(0).getFirstBlockNum(), recoverers.get(1).getFirstBlockNum() - 1,
                    recoverers.get(0).getRecoverable());
            //大号向正确看齐
            recommitLostBlocks(recoverers.get(1).getFirstBlockNum(), lastAvailableBlockNum,
                    recoverers.get(0).getRecoverable(), recoverers.get(1).getRecoverable());
        }
    }

    /** recommitLostBlocks retrieves blocks in specified range and commit the write set to either
     * state DB or history DB or both
     */
    public void recommitLostBlocks(long firstBlockNum, long lastBlockNum, Recoverable ... recoverables) throws LedgerException{
        BlockAndPvtData blockAndPvtData;
        for (long blockNumber = firstBlockNum; blockNumber <= lastBlockNum; blockNumber++) {
            blockAndPvtData = getPvtDataAndBlockByNum(blockNumber, null);
            for(Recoverable recoverable : recoverables){
                recoverable.commitLostBlock(blockAndPvtData);
            }
        }
    }

    /** GetTransactionByID retrieves a transaction by id
     *
     * @param txID
     * @return
     */
    @Override
    public TransactionPackage.ProcessedTransaction getTransactionByID(String txID) throws LedgerException {
        Common.Envelope tranEvn = blockStore.retrieveTxByID(txID);
        TransactionPackage.TxValidationCode txVResult = blockStore.retrieveTxValidationCodeByTxID(txID);
        if(tranEvn == null || txVResult == null){
           return null;
        }
        return TransactionPackage.ProcessedTransaction.newBuilder()
                .setTransactionEnvelope(tranEvn)
                .setValidationCode(txVResult.getNumber())
                .build();
    }

    /** GetBlockchainInfo returns basic info about blockchain
     *
     * @return
     */
    @Override
    public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
        return blockStore.getBlockchainInfo();
    }

    /** GetBlockByNumber returns block at a given height
     * blockNumber of  math.MaxUint64 will return last block
     */
    @Override
    public Common.Block getBlockByNumber(long blockNumber) throws LedgerException {
        return blockStore.retrieveBlockByNumber(blockNumber);
    }

    /** GetBlocksIterator returns an iterator that starts from `startBlockNumber`(inclusive).
     * The iterator is a blocking iterator i.e., it blocks till the next block gets available in the ledger
     * ResultsIterator contains type BlockHolder
     */
    @Override
    public ResultsIterator getBlocksIterator(long startBlockNumber) throws LedgerException{
        return blockStore.retrieveBlocks(startBlockNumber);
    }

    /** GetBlockByHash returns a block given it's hash
     *
     * @param blockHash
     * @return
     */
    @Override
    public Common.Block getBlockByHash(byte[] blockHash) throws LedgerException {
        return blockStore.retrieveBlockByHash(blockHash);
    }

    /** GetBlockByTxID returns a block which contains a transaction
     *
     * @param txID
     * @return
     */
    @Override
    public Common.Block getBlockByTxID(String txID) throws LedgerException {
        return blockStore.retrieveBlockByTxID(txID);
    }

    @Override
    public TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException {
        return blockStore.retrieveTxValidationCodeByTxID(txID);
    }

    /** NewTxSimulator returns new `ledger.TxSimulator`
     *
     * @return
     */
    @Override
    public ITxSimulator newTxSimulator(String txId) throws LedgerException {
        return txtmgmt.newTxSimulator(txId);
    }

    /** Prune prunes the blocks/transactions that satisfy the given policy
     *
     * @param prunePolicy
     */
    @Override
    public void prune(PrunePolicy prunePolicy) throws LedgerException {
        throw new LedgerException("Not yet implement");
    }

    /** NewQueryExecutor gives handle to a query executor.
     * A client can obtain more than one 'QueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     */
    @Override
    public IQueryExecutor newQueryExecutor() throws LedgerException{
        //TODO uuid
        return txtmgmt.newQueryExecutor(UUID.randomUUID().toString());
    }

    /** NewHistoryQueryExecutor gives handle to a history query executor.
     * A client can obtain more than one 'IHistoryQueryExecutor's for parallel execution.
     * Any synchronization should be performed at the implementation level if required
     * Pass the ledger blockstore so that historical values can be looked up from the chain
     */
    @Override
    public IHistoryQueryExecutor newHistoryQueryExecutor() throws LedgerException {
        return historyDB.newHistoryQueryExecutor(blockStore);
    }

    @Override
    public BlockAndPvtData getPvtDataAndBlockByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        return ((Store) blockStore).getPvtDataAndBlockByNum(blockNum, filter);
    }

    @Override
    public List<TxPvtData> getPvtDataByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        return ((Store) blockStore).getPvtDataByNum(blockNum, filter);
    }

    @Override
    public void purgePrivateData(long maxBlockNumToRetain) throws LedgerException {
        throw new LedgerException("Not yet implement");
    }

    @Override
    public long privateDataMinBlockNum() throws LedgerException {
        throw new LedgerException("Not yet implement");
    }

    /** Commit commits the valid block (returned in the method RemoveInvalidTransactionsAndPrepare) and related state changes
     *
     * @param block
     */
    @Override
    public void commit(Common.Block block) {
        return;
    }

    /** Close closes `KVLedger`
     *
     */
    @Override
    public void close() {
        blockStore.shutdown();
        try {
            txtmgmt.shutdown();
        } catch (LedgerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException {
        Common.Block block = blockAndPvtData.getBlock();
        long blockNo = block.getHeader().getNumber();
        logger.debug(String.format("Channel %s: Validating state for block %d", ledgerID, blockNo));
        //TODO 验证器
        txtmgmt.validateAndPrepare(blockAndPvtData, true);
        logger.debug(String.format("Channel %s: Committing block %d to storage", ledgerID, blockNo));
        blockStore.commitWithPvtData(blockAndPvtData);
        logger.info(String.format("Channel %s: Committed block %d to storage", ledgerID, blockNo));

        logger.debug(String.format("Channel %s: Committing block %d transaction to state db", ledgerID, blockNo));
        txtmgmt.commit();
        //TODO history db enable
        if(LedgerConfig.isHistoryDBEnabled()){
            logger.debug(String.format("Channel %s: Committing block %d transaction to history db", ledgerID, blockNo));
            historyDB.commit(block);
        }
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

    public TxManager getTxtmgmt() {
        return txtmgmt;
    }

    public void setTxtmgmt(TxManager txtmgmt) {
        this.txtmgmt = txtmgmt;
    }

    public IHistoryDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(IHistoryDB historyDB) {
        this.historyDB = historyDB;
    }
}
