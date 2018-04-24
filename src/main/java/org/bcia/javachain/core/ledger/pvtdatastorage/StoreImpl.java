/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.pvtdatastorage;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.PvtNsCollFilter;
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.ledger.rwset.Rwset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class StoreImpl implements Store {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(StoreImpl.class);

    private DBProvider db;
    private String ledgerID;
    private boolean isEmpty;
    private long lastCommittedBlock;
    private boolean batchPending;

    public void initState() throws LedgerException {
        lastCommittedBlock = getLastCommittedBlockNum();
        isEmpty = lastCommittedBlock == 0;
        batchPending = hasPendingCommit();
    }


    @Override
    public void initLastCommitedBlock(long blockNum) throws LedgerException {
        if(!(isEmpty && !batchPending)){
            throw new LedgerException("The private data store is not empty. InitLastCommittedBlock() function call is not allowed");
        }
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        batch.put(KvEncoding.LAST_COMMITTED_BLK_KEY, KvEncoding.encodeBlockNum(blockNum));
        db.writeBatch(batch, true);
        isEmpty = false;
        lastCommittedBlock = blockNum;
        logger.debug("InitLastCommittedBlock set to block " + blockNum);
    }

    @Override
    public List<TxPvtData> getPvtDataByBlockNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        logger.debug("Getting private data for block " + blockNum);
        if (isEmpty){
            throw new LedgerException("Thr store is empty");
        }
        if(blockNum > lastCommittedBlock){
            throw new LedgerException("Last committed block " + lastCommittedBlock + " block reuqested " + blockNum);
        }
        byte[] startKey = KvEncoding.getStartKeyForRangeScanByBlockNum(blockNum);
        byte[] endKey = KvEncoding.getEndKeyForRangeScanByBlockNum(blockNum);
        logger.debug(String.format("Querying private data for write sets using startKey %s, endKey %s", startKey, endKey));
        Iterator<Map.Entry<byte[], byte[]>> itr = db.getIterator(startKey, endKey);
        List<TxPvtData> pvtData = new ArrayList<>();
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
            byte[] key = entry.getKey();
            byte[] value = entry.getValue();
            long bNum = KvEncoding.decodePKToBlockNum(key);
            long tNum = KvEncoding.decodePKToTranNum(key);
            Rwset.TxPvtReadWriteSet pvtRWSet = null;
            try {
                pvtRWSet = Rwset.TxPvtReadWriteSet.parseFrom(value);
            } catch (InvalidProtocolBufferException e) {
                throw new LedgerException(e);
            }
            logger.debug(String.format("Retrieved private data write set for block %d, tran %d", bNum, tNum));
            Rwset.TxPvtReadWriteSet fileteredWSet = trimPvtWSet(pvtRWSet, filter);
            TxPvtData data = new TxPvtData();
            data.setSeqInBlock(tNum);
            data.setWriteSet(fileteredWSet);
            pvtData.add(data);
        }
        return pvtData;
    }

    @Override
    public void prepare(long blockNum, List<TxPvtData> pvtData) throws LedgerException{
        if(batchPending){
            throw new LedgerException("A pending batch exists as as result of last invoke to 'Prepare' call." +
                    " Invoke 'Commit' or 'Rollback' on the pending batch before invoking 'Prepare' function");
        }
        long expectedBlockNum = nextBlockNum();
        if(expectedBlockNum != blockNum){
            throw new LedgerException(String.format("Expected block number=%d, recived block number=%d", expectedBlockNum, blockNum));
        }
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        for(TxPvtData txPvtData : pvtData){
            byte[] key = KvEncoding.encodePK(blockNum, txPvtData.getSeqInBlock());
            byte[] value = txPvtData.getWriteSet().toByteArray();
            batch.put(key, value);
        }
        batch.put(KvEncoding.PENDING_COMMIT_KEY, KvEncoding.EMPTY_VALUE);
        db.writeBatch(batch, true);
        batchPending = true;
        logger.debug(String.format("Saved %d private data write sets for block [%d]", pvtData.size(), blockNum));
    }

    @Override
    public void commit() throws LedgerException{
        if(!batchPending){
            throw new LedgerException("No pending batch to commit");
        }
        long committingBlockNum = nextBlockNum();
        logger.debug("Committing private data for block " + committingBlockNum);
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        batch.delete(KvEncoding.PENDING_COMMIT_KEY);
        batch.put(KvEncoding.LAST_COMMITTED_BLK_KEY, KvEncoding.encodeBlockNum(committingBlockNum));
        db.writeBatch(batch, true);
        batchPending = false;
        isEmpty = false;
        lastCommittedBlock = committingBlockNum;
        logger.debug("Committed private data for block " + committingBlockNum);
    }

    @Override
    public void rollback() throws LedgerException {
        if(!batchPending){
            throw new LedgerException("No pending batch to commit");
        }
        long rollingbackBlockNum = nextBlockNum();
        logger.debug("Rolling back private data for block " + rollingbackBlockNum);
        List<byte[]> pendingBatchKeys = retrievePendingBatchKeys();
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        for(byte[] key : pendingBatchKeys){
            batch.delete(key);
        }
        batch.delete(KvEncoding.PENDING_COMMIT_KEY);
        db.writeBatch(batch, true);
        batchPending = false;
        logger.debug("Rolled back private data for block " + rollingbackBlockNum);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long lastCommitedBlockHeight() {
        if(isEmpty){
            return 0;
        }
        return lastCommittedBlock + 1;
    }

    @Override
    public boolean hasPendingBatch() {
        return batchPending;
    }

    @Override
    public void shutdown() {
        //do nothing
    }

    private Rwset.TxPvtReadWriteSet trimPvtWSet(Rwset.TxPvtReadWriteSet pvtWSet, PvtNsCollFilter filter){
        if(filter == null){
            return pvtWSet;
        }
        List<Rwset.NsPvtReadWriteSet> filteredNsRwSet = new ArrayList<>();
        for(Rwset.NsPvtReadWriteSet ns : pvtWSet.getNsPvtRwsetList()){
            List<Rwset.CollectionPvtReadWriteSet> filterdCollRWSet = new ArrayList<>();
            for(Rwset.CollectionPvtReadWriteSet coll : ns.getCollectionPvtRwsetList()){
                if(filter.has(ns.getNamespace(), coll.getCollectionName())){
                    filterdCollRWSet.add(coll);
                }
            }
            if(filterdCollRWSet.size() != 0){
                filteredNsRwSet.add(setCollectionPvtRwset(Rwset.NsPvtReadWriteSet.newBuilder(), filterdCollRWSet)
                        .setNamespace(ns.getNamespace())
                        .build());
            }
        }
        Rwset.TxPvtReadWriteSet filteredTxPvtRwSet = null;
        if(filteredNsRwSet.size() != 0){
            filteredTxPvtRwSet = setNsPvtRwset(Rwset.TxPvtReadWriteSet.newBuilder(), filteredNsRwSet)
                    .setDataModel(pvtWSet.getDataModel())
                    .build();
        }
        return filteredTxPvtRwSet;
    }

    private Rwset.NsPvtReadWriteSet.Builder setCollectionPvtRwset(Rwset.NsPvtReadWriteSet.Builder builder, List<Rwset.CollectionPvtReadWriteSet> list){
        for (int i = 0; i < list.size(); i++) {
            builder.setCollectionPvtRwset(i, list.get(i));
        }
        return builder;
    }

    private Rwset.TxPvtReadWriteSet.Builder setNsPvtRwset(Rwset.TxPvtReadWriteSet.Builder builder, List<Rwset.NsPvtReadWriteSet> list){
        for (int i = 0; i < list.size(); i++) {
            builder.setNsPvtRwset(i, list.get(i));
        }
        return builder;
    }

    private boolean hasPendingCommit() throws LedgerException{
        byte[] v = db.get(KvEncoding.PENDING_COMMIT_KEY);
        return v != null;
    }

    /**
     * 0 true
     * !0 false
     */
    private long getLastCommittedBlockNum() throws LedgerException {
        byte[] v = db.get(KvEncoding.LAST_COMMITTED_BLK_KEY);
        if(v == null){
            return 0;
        }
        return KvEncoding.decodeBlockNum(v);
    }

    private long nextBlockNum(){
        if (isEmpty){
            return 0;
        }
        return lastCommittedBlock + 1;
    }

    private List<byte[]> retrievePendingBatchKeys() throws LedgerException{
        List<byte[]> pendingBatchKeys = new ArrayList<>();
        Iterator<Map.Entry<byte[], byte[]>> itr = db.getIterator(KvEncoding.encodePK(nextBlockNum(), 0), null);
        while(itr.hasNext()){
            pendingBatchKeys.add(itr.next().getKey());
        }
        return pendingBatchKeys;
    }


    public DBProvider getDb() {
        return db;
    }

    public void setDb(DBProvider db) {
        this.db = db;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public long getLastCommittedBlock() {
        return lastCommittedBlock;
    }

    public void setLastCommittedBlock(long lastCommittedBlock) {
        this.lastCommittedBlock = lastCommittedBlock;
    }

    public boolean isBatchPending() {
        return batchPending;
    }

    public void setBatchPending(boolean batchPending) {
        this.batchPending = batchPending;
    }

    public String getLedgerID() {
        return ledgerID;
    }

    public void setLedgerID(String ledgerID) {
        this.ledgerID = ledgerID;
    }
}
