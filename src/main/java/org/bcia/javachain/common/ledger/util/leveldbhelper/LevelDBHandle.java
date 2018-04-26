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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.DBHandle;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.leveldb.LevelDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 提供操作leveldb的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBHandle implements DBHandle {
    private String dbName = null;
    private DB db = null;
    private boolean opened = false;
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBHandle.class);

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
    public DB createDB(String dbPath) throws LedgerException{
//        this.db = LevelDBFactory.getDB(dbPath);
        this.opened = true;
        this.dbName = dbPath;
        return db;
    }

    /**
     * 关闭db
     */
    @Override
    public void close() throws LedgerException{
        try {
            db.close();
        } catch (IOException e) {
            throw new LedgerException(e);
        }
        opened = false;
    }

    /**
     * 根据key获取value
     */
    @Override
    public byte[] get(byte[] key) throws LedgerException {
        if(!opened){
            throw new LedgerException("No db created");
        } else {
            return LevelDBFactory.get(dbName, key ,false);
        }
    }

    /**
     * 插入当前kv
     */
    @Override
    public void put(byte[] key, byte[] value, boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            LevelDBFactory.add(dbName, key, value, sync);
        }
    }

    /**
     * 删除给定key
     */
    @Override
    public void delete(byte[] key, boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            LevelDBFactory.delete(dbName, key, sync);
        }
    }

    /**
     * 批量执行操作
     */
    @Override
    public void writeBatch(UpdateBatch batch, boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            LevelDBFactory.add(dbName, batch.getKvs(), sync);
        }
    }

    /**
     * 根据给出的开始、结束Key遍历
     */
    @Override
    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey) throws LedgerException {
        DBIterator dbItr = LevelDBFactory.getIterator(dbName);
        if(startKey != null){
            dbItr.seek(startKey);
        }
        return dbItr;
    }
}
