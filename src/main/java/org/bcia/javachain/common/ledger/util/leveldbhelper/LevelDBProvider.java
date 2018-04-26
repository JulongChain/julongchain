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
import org.bcia.javachain.common.ledger.util.DBHandle;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

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
public class LevelDBProvider implements DBProvider {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBProvider.class);

    private DBHandle db = null;
    private byte[] dbNameKeySep = new byte[1];
    private byte lastKeyIndicator = 0x01;
    private String dbPath = null;

    private LevelDBProvider() throws LedgerException {
        dbNameKeySep[0] = 0x00;
        db = new LevelDBHandle();
    }

    public static UpdateBatch newUpdateBatch() {
        UpdateBatch batch = new UpdateBatch();
        batch.setKvs(new HashMap<>());
        return batch;
    }

    @Override
    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public DBHandle getDb() {
        return db;
    }

    public void setDb(DBHandle db) {
        this.db = db;
    }

    public static synchronized LevelDBProvider newProvider(String dbPath) throws LedgerException {
        LevelDBProvider provider =  new LevelDBProvider();
        provider.getDb().createDB(dbPath);
        provider.setDbPath(dbPath);
        return provider;
    }

    @Override
    public void close() throws LedgerException {
        db.close();
    }

    @Override
    public byte[] get(byte[] key) throws LedgerException {
        return db.get(key);
    }

    @Override
    public void put(byte[] key, byte[] value, boolean sync) throws LedgerException {
        db.put(key, value, sync);
    }

    @Override
    public void delete(byte[] key, boolean sync) throws LedgerException {
        db.delete(key, sync);
    }

    @Override
    public void writeBatch(UpdateBatch batch, boolean sync) throws LedgerException {
        db.writeBatch(batch, sync);
    }

    @Override
    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey) throws LedgerException {
        return db.getIterator(startKey);
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
