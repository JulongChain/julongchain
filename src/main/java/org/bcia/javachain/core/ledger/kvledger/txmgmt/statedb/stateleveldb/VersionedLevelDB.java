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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.common.ledger.util.IDBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.StatedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * LevelDB实现的VersionDB
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class VersionedLevelDB implements IVersionedDB {

    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(VersionedLevelDB.class);
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};
    private static final byte LAST_KEY_INDICATOR = 0x01;
    private static final byte[] SAVE_POINT_KEY = {0x00};

    private IDBProvider db;
    private String dbName;

    @Override
    public VersionedValue getState(String namespace, String key) throws LedgerException {
        logger.debug(String.format("getState() ns = %s, key = %s", namespace, key));
        byte[] compositeKey = constructCompositeKey(namespace, key);
        byte[] dbVal = db.get(compositeKey);
        if (dbVal == null) {
            return null;
        }
        //根据0~7字节组装blockNum
        //根据8~16字节组装txNum
        Height h = Height.newHeightFromBytes(dbVal);
        //其余为block信息
        byte[] value = new byte[dbVal.length - 16];
        System.arraycopy(dbVal, 16, value, 0, value.length);
        //组装versionValue
        VersionedValue versionedValue = new VersionedValue();
        versionedValue.setValue(value);
        versionedValue.setVersion(h);

        return versionedValue;
    }

    @Override
    public Height getVersion(String namespace, String key) throws LedgerException {
        return null;
    }

    @Override
    public List<VersionedValue> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        List<VersionedValue> vals = new ArrayList<>();
        for (String key : keys) {
            VersionedValue val = getState(namespace, key);
            vals.add(val);
        }
        return vals;
    }

    @Override
    public IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        byte[] compositeStartKey = constructCompositeKey(namespace, startKey);
        byte[] compositeEndKey = constructCompositeKey(namespace, endKey);
        if("".equals(endKey)){
            compositeEndKey[compositeEndKey.length - 1] = LAST_KEY_INDICATOR;
        }
        Iterator dbItr = db.getIterator(compositeStartKey);
        return KvScanner.newKVScanner(namespace, dbItr);
    }

    @Override
    public IResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        throw new LedgerException("ExecuteQuery is not support for leveldb");
    }

    /**
     * 批量写操作
     */
    @Override
    public void applyUpdates(UpdateBatch batch, Height height) throws LedgerException {
        org.bcia.javachain.common.ledger.util.leveldbhelper.UpdateBatch dbBatch = LevelDBProvider.newUpdateBatch();
        List<String> nameSpaces = batch.getUpdatedNamespaces();
        for(String ns : nameSpaces){
            Map<String,VersionedValue> updates = batch.getUpdates(ns);
            int i = 0;
            for(Map.Entry<String, VersionedValue> entry : updates.entrySet()){
                byte[] compositeKey = constructCompositeKey(ns, String.valueOf(i));
                logger.debug(String.format("Gtoup [%s]: Applying key(String)=[%s] key(bytes) length=[%d]"
                        , dbName, new String(compositeKey), compositeKey.length));

                if(entry.getValue() == null){
                    dbBatch.delete(compositeKey);
                } else {
                    dbBatch.put(compositeKey, StatedDB.encodeValue(entry.getValue().getValue(), entry.getValue().getVersion()));
                }
            }
        }
        dbBatch.put(SAVE_POINT_KEY, height.toBytes());
        db.writeBatch(dbBatch, true);
    }

    @Override
    public Height getLatestSavePoint() throws LedgerException {
        byte[] versionBytes = db.get(SAVE_POINT_KEY);
        if(versionBytes == null){
            return null;
        }
       return Height.newHeightFromBytes(versionBytes);
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

    public static byte[] constructCompositeKey(String ns, String key){
        byte[] result = ArrayUtils.addAll(ns.getBytes(), COMPOSITE_KEY_SEP);
        return ArrayUtils.addAll(result, key.getBytes());
    }

    public static String splitCompositeKeyToKey(byte[] compositeKey){
       String tmp = new String(compositeKey);
       String[] result = tmp.split(new String(COMPOSITE_KEY_SEP));
       return result[0];
    }

    public static String splitCompositeKeyToNs(byte[] compositeKey){
        String tmp = new String(compositeKey);
        String[] result = tmp.split(new String(COMPOSITE_KEY_SEP));
        return result[1];
    }

    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
        this.db = db;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
