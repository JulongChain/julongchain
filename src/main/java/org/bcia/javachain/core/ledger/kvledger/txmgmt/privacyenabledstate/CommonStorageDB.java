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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.*;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;

import java.util.List;
import java.util.Map;

/**
 * 处理pvt、pubdata
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class CommonStorageDB implements DB {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(CommonStorageDB.class);
    private static final String NS_JOINER  = "$$";
    private static final String PVT_DATA_PREFIX  = "p";
    private static final String HASH_DATA_PREFIX = "h";

    private IVersionedDB vdb;

    public static CommonStorageDB newCommonStorageDB(IVersionedDB vdb, String id){
        CommonStorageDB db = new CommonStorageDB();
        db.setVdb(vdb);
        return db;
    }

    @Override
    public boolean isBulkOptimizable() {
        return vdb instanceof BulkOptimizable;
    }

    @Override
    public void loadCommittedVersionsOfPubAndHashedKeys(List<CompositeKey> pubKeys,
                                                        List<HashedCompositeKey> hashKeys) {
        BulkOptimizable bulkOptimizable = (BulkOptimizable) vdb;

        for(HashedCompositeKey key : hashKeys){
            String ns = deriveHashedDataNs(key.getNamespace(), key.getCollectionName());
            String keyHashStr = null;
            if(!bytesKeySuppoted()){
                //TODO SM3 Hash
                keyHashStr = new String(Util.getHashBytes(key.getKeyHash().getBytes()));
            } else {
                keyHashStr = key.getKeyHash();
            }
            CompositeKey compositeKey = new CompositeKey();
            compositeKey.setNamespace(ns);
            compositeKey.setKey(keyHashStr);
            pubKeys.add(compositeKey);
        }

        bulkOptimizable.loadCommittedVersions(pubKeys);
    }

    @Override
    public Height getCacheKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException{
        try {
            BulkOptimizable bulkOptimizable = (BulkOptimizable) vdb;
            String keyHashStr = new String(keyHash);
            if(!bytesKeySuppoted()){
                //TODO SM3 Hash
                keyHashStr = new String(Util.getHashBytes(keyHash));
            }
            return bulkOptimizable.getCachedVersion(deriveHashedDataNs(ns, coll), keyHashStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCachedVersions() {
        try {
            BulkOptimizable bulkOptimizable = (BulkOptimizable) vdb;
            bulkOptimizable.clearCachedVersions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ISmartContractLifecycleEventListener getSmartcontractEventListener() {
        try {
            return (ISmartContractLifecycleEventListener) vdb;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public VersionedValue getPrivateData(String ns, String coll, String key) throws LedgerException {
        return getState(derivePvtDataNs(ns, coll), key);
    }

    @Override
    public VersionedValue getValueHash(String ns, String coll, byte[] keyHash) throws LedgerException {
        String keyHashStr = new String(keyHash);
        if(!bytesKeySuppoted()){
            //TODO SM3 Hash
            keyHashStr = new String(Util.getHashBytes(keyHash));
        }
        return getState(deriveHashedDataNs(ns, coll), keyHashStr);
    }

    @Override
    public Height getKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException {
        String keyHashStr = new String(keyHash);
        if(!bytesKeySuppoted()){
            //TODO SM3 Hash
            keyHashStr = new String(Util.getHashBytes(keyHash));
        }
        return getVersion(deriveHashedDataNs(ns, coll), keyHashStr);
    }

    @Override
    public List<VersionedValue> getPrivateDataMultipleKeys(String ns, String coll, List<String> keys) throws LedgerException {
        return getStateMultipleKeys(deriveHashedDataNs(ns, coll), keys);
    }

    @Override
    public ResultsIterator getPrivateDataRangeScanIterator(String ns, String coll, String startKey, String endKey) throws LedgerException{
        return getStateRangeScanIterator(deriveHashedDataNs(ns, coll), startKey, endKey);
    }

    @Override
    public ResultsIterator executeQueryOnPrivateData(String ns, String coll, String query) throws LedgerException{
        return executeQuery(deriveHashedDataNs(ns, coll), query);
    }

    @Override
    public void applyPrivacyAwareUpdates(UpdateBatch updates, Height height) throws LedgerException {
        addPvtUpdates(updates.getPubUpdateBatch(), updates.getPvtUpdateBatch());
        addHashedUpdates(updates.getPubUpdateBatch(), updates.getHashUpdates(), !bytesKeySuppoted());
        vdb.applyUpdates(updates.getPubUpdateBatch().getBatch(), height);
    }

    private String derivePvtDataNs(String ns, String coll){
        return ns + NS_JOINER + PVT_DATA_PREFIX + coll;
    }

    private String deriveHashedDataNs(String ns, String coll){
        return ns + NS_JOINER + HASH_DATA_PREFIX + coll;
    }

    private void addPvtUpdates(PubUpdateBatch pubUpdateBatch, PvtUpdateBatch pvtUpdateBatch){
        for(Map.Entry<String, NsBatch> entry : pvtUpdateBatch.getMap().getMap().entrySet()){
            NsBatch nsBatch = entry.getValue();
            String ns = entry.getKey();
            for(String coll : nsBatch.getCollectionNames()){
                for(Map.Entry<String, VersionedValue> entry1 : nsBatch.getBatch().getUpdates(coll).entrySet()){
                    pubUpdateBatch.getBatch().update(deriveHashedDataNs(ns, coll), entry1.getKey(), entry1.getValue());
                }
            }
        }
    }

    private void addHashedUpdates(PubUpdateBatch pubUpdateBatch, HashedUpdateBatch hashedUpdateBatch, boolean SM3Key){
        for(Map.Entry<String, NsBatch> entry : hashedUpdateBatch.getMap().getMap().entrySet()){
            String ns = entry.getKey();
            NsBatch nsBatch = entry.getValue();
            for(String coll : nsBatch.getCollectionNames()){
                for(Map.Entry<String, VersionedValue> entry1 : nsBatch.getBatch().getUpdates(coll).entrySet()){
                    String key = entry1.getKey();
                    VersionedValue vv = entry1.getValue();
                    if(SM3Key){
                        //TODO SM3 Hash
                        key = new String(Util.getHashBytes(key.getBytes()));
                    }
                    pubUpdateBatch.getBatch().update(deriveHashedDataNs(ns, coll), key, vv);
                }
            }
        }
    }

    @Override
    public VersionedValue getState(String namespace, String key) throws LedgerException {
        return vdb.getState(namespace, key);
    }

    @Override
    public Height getVersion(String namespace, String key) throws LedgerException {
        return vdb.getVersion(namespace, key);
    }

    @Override
    public List<VersionedValue> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return vdb.getStateMultipleKeys(namespace, keys);
    }

    @Override
    public ResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        return vdb.getStateRangeScanIterator(namespace, startKey, endKey);
    }

    @Override
    public ResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return vdb.executeQuery(namespace, query);
    }

    @Override
    public void applyUpdates(org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch batch, Height height) throws LedgerException {
        throw new LedgerException("this fun should not be invoke on this type. Please invoke fun applyPrivacyAwareUpdates()");
    }

    @Override
    public Height getLatestSavePoint() throws LedgerException {
        return vdb.getLatestSavePoint();
    }

    @Override
    public void validateKeyValue(String key, byte[] value) throws LedgerException {

    }

    @Override
    public void open() throws LedgerException {

    }

    @Override
    public void close() throws LedgerException {

    }

    @Override
    public boolean bytesKeySuppoted() {
        return true;
    }

    public IVersionedDB getVdb() {
        return vdb;
    }

    public void setVdb(IVersionedDB vdb) {
        this.vdb = vdb;
    }
}
