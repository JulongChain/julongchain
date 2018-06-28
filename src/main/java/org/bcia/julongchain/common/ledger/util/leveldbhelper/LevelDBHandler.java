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

import org.bcia.julongchain.common.exception.LevelDBException;
import org.bcia.julongchain.common.ledger.util.IDBHandler;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.leveldb.LevelDB;
import org.bcia.julongchain.core.ledger.leveldb.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * 提供操作leveldb的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBHandler implements IDBHandler {

    private String dbName = null;
    private DB db = null;
    private boolean opened = false;
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBHandler.class);

    public LevelDBHandler() {
    }

    public LevelDBHandler(String dbName, DB db, boolean opened) {
        this.dbName = dbName;
        this.db = db;
        this.opened = opened;
    }

    @Override
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 数据库是否开启
     */
    @Override
    public boolean isOpened() {
        return opened;
    }

    /**
     * 创建db
     */
    @Override
    public DB createDB(String dbPath) throws LevelDBException{
        this.opened = true;
        this.dbName = dbPath;
        return db;
    }

    /**
     * 关闭db
     */
    @Override
    public void close() throws LevelDBException{
        try {
            db.close();
        } catch (IOException e) {
            throw new LevelDBException(e);
        }
        opened = false;
    }

    /**
     * 根据key获取value
     */
    @Override
    public byte[] get(byte[] key) throws LevelDBException {
        if (!opened) {
            throw new LevelDBException("No db created");
        }

        LevelDB db = LevelDBUtil.getDB(dbName);
        return LevelDBUtil.get(db, key, false);
    }

    /**
     * 插入当前kv
     */
    @Override
    public void put(byte[] key, byte[] value, boolean sync) throws LevelDBException {
        if (!opened) {
            logger.error("No db created");
            throw new LevelDBException("No db created");
        }
        LevelDB db = LevelDBUtil.getDB(dbName);
        LevelDBUtil.add(db, key, value, sync);
    }

    /**
     * 删除给定key
     */
    @Override
    public void delete(byte[] key, boolean sync) throws LevelDBException {
        if (!opened) {
            logger.error("No db created");
            throw new LevelDBException("No db created");
        }
        LevelDB db = LevelDBUtil.getDB(dbName);
        LevelDBUtil.delete(db, key, sync);
    }

    /**
     * 批量执行操作
     */
    @Override
    public void writeBatch(UpdateBatch batch, boolean sync) throws LevelDBException {
        if (!opened) {
            logger.error("No db created");
            throw new LevelDBException("No db created");
        }
        LevelDB db = LevelDBUtil.getDB(dbName);
        LevelDBUtil.add(db, batch.getKvs(), sync);
    }

    /**
     * 根据给出的开始、结束Key遍历
     */
    @Override
    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey) throws LevelDBException {
        LevelDB db = LevelDBUtil.getDB(dbName);
        DBIterator dbItr = LevelDBUtil.getIterator(db);
        if(startKey != null){
            dbItr.seek(startKey);
        }
        return dbItr;
    }
}
