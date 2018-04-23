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
package org.bcia.javachain.common.ledger.util.leveldbhelper;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.iq80.leveldb.DBIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 提供操作leveldb的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDbProvider{

    private static JavaChainLog logger = JavaChainLogFactory.getLog(LevelDbProvider.class);

    private LevelDBHandle db = null;
    private byte[] dbNameKeySep = new byte[1];
    private byte lastKeyIndicator = 0x01;
    private String dbPath = null;

    private LevelDbProvider() throws LedgerException {
        dbNameKeySep[0] = 0x00;
        db = new LevelDBHandle();
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public LevelDBHandle getDb() {
        return db;
    }

    public void setDb(LevelDBHandle db) {
        this.db = db;
    }

    public static synchronized LevelDbProvider newProvider(String dbPath) throws LedgerException {
        LevelDbProvider provider =  new LevelDbProvider();
        provider.getDb().createDB(dbPath);
        provider.setDbPath(dbPath);
        return provider;
    }

//    public LevelDBHandle getDbHandle(String dbName) {
//        LevelDBHandle LevelDBHandle = dbHandles.get(dbName);
//        if(LevelDBHandle == null){
//            LevelDBHandle = new LevelDBHandle();
//            dbHandles.put(dbName, LevelDBHandle);
//        }
//        return LevelDBHandle;
//    }

    public void close() throws LedgerException {
        db.close();
    }

    public byte[] get(byte[] key) throws LedgerException {
        return db.get(key);
    }

    public void put(byte[] key, byte[] value, boolean sync) throws LedgerException {
        db.put(key, value, sync);
    }

    public void delete(byte[] key, boolean sync) throws LedgerException {
        db.delete(key, sync);
    }

    public void writeBatch(UpdateBatch batch, boolean sync) throws LedgerException {
        db.writeBatch(batch, sync);
    }

    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey, byte[] endKey) throws LedgerException {
        return db.getIterator(startKey, endKey);
    }

    public static UpdateBatch newUpdateBatch() {
        UpdateBatch batch = new UpdateBatch();
        batch.setKvs(new HashMap<>());
        return batch;
    }

    public byte[] constructLevelKey(String dbName, byte[] key) {
        byte[] arr = ArrayUtils.addAll(dbName.getBytes(), dbNameKeySep);
        arr = ArrayUtils.addAll(arr, key);
        return arr;
    }

    public byte[] retrieveAppKey(byte[] levelKey) {
        String str = new String(levelKey);
        String follow = null;
        int start = 0;
        while(str.indexOf(new String(dbNameKeySep), start) != -1){
            start++;
        }
        follow = str.substring(start, str.length());
        return follow.getBytes();
    }
}
