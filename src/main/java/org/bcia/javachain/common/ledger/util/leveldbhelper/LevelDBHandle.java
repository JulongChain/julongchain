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
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.leveldb.LevelDBFactory;
import org.bcia.javachain.core.node.NodeConfig;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

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
public class LevelDBHandle {
    private String dbName = null;
    private DB db = null;
    private boolean opened = false;
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBHandle.class);

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 数据库是否开启
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * 创建db
     */
    public DB createDB(String dbPath) throws LedgerException{
        if (dbPath == null || "".equals(dbPath)) {
            //TODO: 测试使用
            db = LevelDBFactory.getDB();
        } else {
            db = LevelDBFactory.getDB(dbPath);
        }
        opened = true;
        this.dbName = dbPath;
        return db;
    }

    /**
     * 关闭db
     */
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
    public byte[] get(byte[] key) throws LedgerException {
        if(!opened){
            throw new LedgerException("No db created");
        } else {
            return LevelDBFactory.get(key ,false);
        }
    }

    /**
     * 插入当前kv
     */
    public void put(byte[] key, byte[] value, Boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            LevelDBFactory.add(key, value, sync);
        }
    }

    /**
     * 删除给定key
     */
    public void delete(byte[] key, Boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            LevelDBFactory.delete(key, sync);
        }
    }

    /**
     * 批量执行操作
     */
    public void writeBatch(UpdateBatch batch, Boolean sync) throws LedgerException {
        if(!opened){
            logger.error("No db created");
        } else {
            try {
                Map<byte[], byte[]> kvs = batch.getKvs();
//                for(Map.Entry<byte[], KeyType> entry : batch.getKts().entrySet()){
//                    if(KeyType.KEY_TYPE_VAL == entry.getValue().getKeyType()){
//                        LevelDBFactory.add(db, entry.getKey()
//                                , kvs.get(entry.getKey()) == null ? "".getBytes() : kvs.get(entry.getKey())
//                                , sync);
//                    } else if(KeyType.KEY_TYPE_DEL == entry.getValue().getKeyType()){
//                        LevelDBFactory.delete(db, entry.getKey(), sync);
//                    }
//                }
            } catch (Exception e) {
                throw new LedgerException(e);
//            } finally {
//                close();
            }
        }
    }

    /**
     * 根据给出的开始、结束Key遍历
     */
    public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey, byte[] endKey) throws LedgerException {
        DBIterator dbItr = null;
        dbItr = (DBIterator) LevelDBFactory.getIterator();
        List<Map.Entry<byte[], byte[]>> list = new ArrayList<>();
        if(startKey != null){
            dbItr.seek(startKey);
        }
        while(dbItr.hasNext()){
            Map.Entry<byte[], byte[]> entry = dbItr.next();
            list.add(entry);
            if(entry.getKey() == endKey){
                break;
            }
        }
        return list.iterator();
    }
}
