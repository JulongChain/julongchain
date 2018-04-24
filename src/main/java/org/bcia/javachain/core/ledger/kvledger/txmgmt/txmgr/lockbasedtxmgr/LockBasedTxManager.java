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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.StateListener;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.UpdateBatch;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedValue;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxManager;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.IValidator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LockBasedTxManager implements TxManager {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LockBasedTxManager.class);

    private String ledgerID;
    private DB db;
    private IValidator validator;
    private UpdateBatch batch;
    private Common.Block currentBlock;
    private Map<String, StateListener> stateListeners;
    private ReentrantReadWriteLock lock;

    public static LockBasedTxManager newLockBasedTxMgr(String ledgerID,
                                                       DB db,
                                                       Map<String, StateListener> stateListeners) throws LedgerException{
        db.open();  //open will do nothing
        LockBasedTxManager txMgr = new LockBasedTxManager();
        txMgr.setLedgerID(ledgerID);
        txMgr.setDb(db);
        txMgr.setStateListeners(stateListeners);
        txMgr.setValidator(null);
        txMgr.setLock(new ReentrantReadWriteLock());
        return txMgr;
    }

    @Override
    public IQueryExecutor newQueryExecutor(String txid) throws LedgerException {
        lock.readLock().lock();
        return LockBasedQueryExecutor.newQueryExecutor(this, txid);
    }

    @Override
    public ITxSimulator newTxSimulator(String txid) throws LedgerException {
        logger.debug("Constructing new tx simulator");
        LockBasedTxSimulator s = LockBasedTxSimulator.newLockBasedTxSimulator(this, txid);
        lock.readLock().lock();
        return s;
    }

    @Override
    public void validateAndPrepare(BlockAndPvtData blockAndPvtData, Boolean doMVCCValidation) throws LedgerException {
        try {
            Common.Block block = blockAndPvtData.getBlock();
            logger.debug("Validating new block with num trans = " + block.getData().getDataList().size());
            UpdateBatch b = validator.validateAndPrepareBatch(blockAndPvtData, doMVCCValidation);
            currentBlock = block;
            batch = b;
            invokeNamespaceListeners(batch);
        } catch (Exception e){
            clearCache();
            throw new LedgerException(e);
        }
    }

    @Override
    public Height getLastSavepoint() throws LedgerException {
        return db.getLatestSavePoint();
    }

    /**
     * return 0 true
     * return !0 return - 1 != lastAvailableBlock
     */
    @Override
    public long shouldRecover() throws LedgerException {
        long result = 0;
        Height savePoint = getLastSavepoint();
        if(savePoint == null){
            return 0;
        }
        result = savePoint.getBlockNum() + 1;
        return result;
    }

    @Override
    public void commitLostBlock(BlockAndPvtData blockAndPvtData) throws LedgerException {
        Common.Block block = blockAndPvtData.getBlock();
        logger.debug("Constructing updateSet for the block " + block.getHeader().getNumber());
        validateAndPrepare(blockAndPvtData, false);
        logger.debug(String.format("Committing block %d to state database", block.getHeader().getNumber()));
        commit();
    }

    @Override
    public synchronized void commit() throws LedgerException {
        try{
            logger.debug("Committing updates to state db");
            if(batch == null){
                throw new LedgerException("validateAndPrepare() method should have been called before calling commit()");
            }
            db.applyPrivacyAwareUpdates(batch,
                    Height.newHeight(currentBlock.getHeader().getNumber(), (long) (currentBlock.getData().getDataList().size() - 1)));
            logger.debug("Update committed to state db");
        } finally {
            clearCache();
            batch = null;
        }
    }

    @Override
    public void rollback() throws LedgerException {
        batch = null;
        clearCache();
    }

    @Override
    public void shutdown() throws LedgerException {
        db.close();
    }

    private void clearCache(){
        if(db.isBulkOptimizable()){
            db.clearCachedVersions();
        }
    }

    private void invokeNamespaceListeners(UpdateBatch batch){
        List<String> namespaces = batch.getPubUpdateBatch().getBatch().getUpdatedNamespaces();
        for(String ns : namespaces){
            StateListener listener = stateListeners.get(ns);
            if(listener == null){
                continue;
            }
            logger.debug("Invoking listener for state changes overs namespace " + ns);
            Map<String, VersionedValue> updateMap = batch.getPubUpdateBatch().getBatch().getUpdates(ns);
            List<KvRwset.KVWrite> kvWrites = new ArrayList<>();
            for(Map.Entry<String, VersionedValue> entry : updateMap.entrySet()){
                String key = entry.getKey();
                VersionedValue versionedValue = entry.getValue();
                KvRwset.KVWrite kvw = KvRwset.KVWrite.newBuilder()
                        .setKey(key)
                        .setIsDelete(versionedValue.getValue() == null)
                        .setValue(ByteString.copyFrom(versionedValue.getValue()))
                        .build();
                kvWrites.add(kvw);
            }
            listener.handleStateUpdates(ledgerID, kvWrites);
        }
    }

    public String getLedgerID() {
        return ledgerID;
    }

    public void setLedgerID(String ledgerID) {
        this.ledgerID = ledgerID;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }

    public IValidator getValidator() {
        return validator;
    }

    public void setValidator(IValidator validator) {
        this.validator = validator;
    }

    public UpdateBatch getBatch() {
        return batch;
    }

    public void setBatch(UpdateBatch batch) {
        this.batch = batch;
    }

    public Common.Block getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Common.Block currentBlock) {
        this.currentBlock = currentBlock;
    }

    public Map<String, StateListener> getStateListeners() {
        return stateListeners;
    }

    public void setStateListeners(Map<String, StateListener> stateListeners) {
        this.stateListeners = stateListeners;
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    public void setLock(ReentrantReadWriteLock lock) {
        this.lock = lock;
    }
}
