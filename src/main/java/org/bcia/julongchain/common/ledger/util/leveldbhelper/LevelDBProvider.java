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
package org.bcia.julongchain.common.ledger.util.leveldbhelper;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LevelDBException;
import org.bcia.julongchain.common.ledger.util.IDBHandle;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * 提供操作leveldb的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBProvider implements IDBProvider {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBProvider.class);

    private IDBHandle db = null;
    private byte[] dbNameKeySep = new byte[1];
    private byte lastKeyIndicator = 0x01;
    private String dbPath = null;

    public LevelDBProvider(String dbPath) throws LevelDBException {
        this.dbPath = dbPath;
        dbNameKeySep[0] = 0x00;
        db = new LevelDBHandle();
        db.createDB(dbPath);
    }

    @Override
    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public IDBHandle getDb() {
        return db;
    }

    public void setDb(IDBHandle db) {
        this.db = db;
    }

    @Override
    public void close() throws LevelDBException {
        db.close();
    }

    @Override
    public byte[] get(byte[] key) throws LevelDBException {
        return db.get(key);
    }

    @Override
    public void put(byte[] key, byte[] value, boolean sync) throws LevelDBException {
        db.put(key, value, sync);
    }

    @Override
    public void delete(byte[] key, boolean sync) throws LevelDBException {
        db.delete(key, sync);
    }

    @Override
    public void writeBatch(UpdateBatch batch, boolean sync) throws LevelDBException {
        db.writeBatch(batch, sync);
    }

    @Override
    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey) throws LevelDBException {
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
