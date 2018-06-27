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
package org.bcia.julongchain.core.ledger.pvtdatastorage;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.BytesHexStrTranslate;
import org.bcia.julongchain.core.ledger.PvtNsCollFilter;
import org.bcia.julongchain.core.ledger.TxPvtData;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;

import java.util.*;

/**
 * 实现pvt数据与blockchain同步写入
 * 主要方法为prepare、commit和rollback
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class PvtDataStoreImpl implements IPvtDataStore {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(PvtDataStoreImpl.class);

    private IDBProvider db;
    private String ledgerID;
    private boolean isEmpty;
    private long lastCommittedBlock;
    private boolean batchPending;

    public PvtDataStoreImpl(IDBProvider db, String ledgerID) {
        this.db = db;
        this.ledgerID = ledgerID;
    }

    public PvtDataStoreImpl initState() throws LedgerException {
        lastCommittedBlock = getLastCommittedBlockNum();
        isEmpty = lastCommittedBlock == 0;
        batchPending = hasPendingCommit();
        return this;
    }

    /**
     * 根据给出的blockNum初始化
     */
    @Override
    public void initLastCommittedBlock(long blockNum) throws LedgerException {
        if(!(isEmpty && !batchPending)){
            throw new LedgerException("The private data store is not empty. InitLastCommittedBlock() function call is not allowed");
        }
        UpdateBatch batch = new UpdateBatch();
        batch.put(KvEncoding.getLastCommittedBlkKey(ledgerID), KvEncoding.encodeBlockNum(blockNum));
        db.writeBatch(batch, true);
        isEmpty = false;
        lastCommittedBlock = blockNum;
        logger.debug("InitLastCommittedBlock set to block " + blockNum);
    }

    /**
     * 根据blockid获取pvtdata
     */
    @Override
    public List<TxPvtData> getPvtDataByBlockNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        logger.debug("Getting private data for block " + blockNum);
        if (isEmpty){
            throw new LedgerException("Thr store is empty");
        }
        //对于提供的blockid不正确情况抛出异常
        if(blockNum > lastCommittedBlock){
            throw new LedgerException("Last committed block " + lastCommittedBlock + " block reuqested " + blockNum);
        }
        byte[] startKey = KvEncoding.getStartKeyForRangeScanByBlockNum(blockNum);
        logger.debug(String.format("Querying private data for write sets using startKey %s", BytesHexStrTranslate.bytesToHexFun1(startKey)));
        Iterator<Map.Entry<byte[], byte[]>> itr = db.getIterator(startKey);
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
            //过滤无效的rwset
            Rwset.TxPvtReadWriteSet fileteredWSet = trimPvtWSet(pvtRWSet, filter);
            TxPvtData data = new TxPvtData(tNum, fileteredWSet);
            pvtData.add(data);
        }
        return pvtData;
    }

    /**
     * 写入pvtdata数据
     */
    @Override
    public void prepare(long blockNum, List<TxPvtData> pvtData) throws LedgerException{
        if(batchPending){
            throw new LedgerException("A pending batch exists as as result of last invoke to 'Prepare' call." +
                    " Invoke 'Commit' or 'Rollback' on the pending batch before invoking 'Prepare' function");
        }
        //区块写入异常
        long expectedBlockNum = nextBlockNum();
        if(expectedBlockNum != blockNum){
            throw new LedgerException(String.format("Expected block number=%d, received block number=%d", expectedBlockNum, blockNum));
        }
        UpdateBatch batch = new UpdateBatch();
        for(TxPvtData txPvtData : pvtData){
            byte[] key = KvEncoding.encodePK(blockNum, txPvtData.getSeqInBlock());
            byte[] value = txPvtData.getWriteSet().toByteArray();
            batch.put(key, value);
        }
        //设置pending_commit_key(为commit或rollback准备)
        batch.put(KvEncoding.getPendingCommitKey(ledgerID), KvEncoding.EMPTY_VALUE);
        //执行写入
        db.writeBatch(batch, true);
        batchPending = true;
        logger.info(String.format("Saved %d private data write sets for block [%d]", pvtData.size(), blockNum));
    }

    /**
     * 完成写入
     * 删除pending_commit_key
     * 插入last_committed_blk_key
     */
    @Override
    public void commit() throws LedgerException{
        if(!batchPending){
            throw new LedgerException("No pending batch to commit");
        }
        long committingBlockNum = nextBlockNum();
        logger.debug("Committing private data for block " + committingBlockNum);
        UpdateBatch batch = new UpdateBatch();
        batch.delete(KvEncoding.getPendingCommitKey(ledgerID));
        batch.put(KvEncoding.getLastCommittedBlkKey(ledgerID), KvEncoding.encodeBlockNum(committingBlockNum));
        db.writeBatch(batch, true);
        batchPending = false;
        isEmpty = false;
        lastCommittedBlock = committingBlockNum;
        logger.debug("Committed private data for block " + committingBlockNum);
    }

    /**
     * 回滚
     * 删除pending_commit_key及数据
     */
    @Override
    public void rollback() throws LedgerException {
        if(!batchPending){
            throw new LedgerException("No pending batch to commit");
        }
        long rollingbackBlockNum = nextBlockNum();
        logger.debug("Rolling back private data for block " + rollingbackBlockNum);
        List<byte[]> pendingBatchKeys = retrievePendingBatchKeys();
        UpdateBatch batch = new UpdateBatch();
        for(byte[] key : pendingBatchKeys){
            batch.delete(key);
        }
        batch.delete(KvEncoding.getPendingCommitKey(ledgerID));
        db.writeBatch(batch, true);
        batchPending = false;
        logger.debug("Rolled back private data for block " + rollingbackBlockNum);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
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

    /**
     * 过滤屌filter中不包含的namespace
     */
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

    /**
     * grpc中list数据插入只能一个一个cha如:)
     */
    private Rwset.NsPvtReadWriteSet.Builder setCollectionPvtRwset(Rwset.NsPvtReadWriteSet.Builder builder, List<Rwset.CollectionPvtReadWriteSet> list){
        for (int i = 0; i < list.size(); i++) {
            builder.addCollectionPvtRwset(i, list.get(i));
        }
        return builder;
    }

    /**
     * grpc中list数据插入只能一个一个cha如:)
     */
    private Rwset.TxPvtReadWriteSet.Builder setNsPvtRwset(Rwset.TxPvtReadWriteSet.Builder builder, List<Rwset.NsPvtReadWriteSet> list){
        for (int i = 0; i < list.size(); i++) {
            builder.addNsPvtRwset(i, list.get(i));
        }
        return builder;
    }

    /**
     * 是否未完成的提交
     */
    private boolean hasPendingCommit() throws LedgerException{
        byte[] v = db.get(KvEncoding.getPendingCommitKey(ledgerID));
        return Arrays.equals(v, KvEncoding.EMPTY_VALUE);
    }

    /**
     * 0 true
     * !0 false
     */
    private long getLastCommittedBlockNum() throws LedgerException {
        byte[] v = db.get(KvEncoding.getLastCommittedBlkKey(ledgerID));
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

	/**
	 * 查找预备数据，既查找区块号为nextBlockNum的pvtData
	 */
	private List<byte[]> retrievePendingBatchKeys() throws LedgerException{
        List<byte[]> pendingBatchKeys = new ArrayList<>();
        Iterator<Map.Entry<byte[], byte[]>> itr = db.getIterator(KvEncoding.encodePK(nextBlockNum(), 0));
        while(itr.hasNext()){
            pendingBatchKeys.add(itr.next().getKey());
        }
        return pendingBatchKeys;
    }


    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
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
