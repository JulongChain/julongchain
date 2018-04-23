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

package org.bcia.javachain.core.ledger.leveldb;

import com.jcraft.jsch.IO;
import javafx.fxml.LoadException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Ledger;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import sun.security.util.LegacyAlgorithmConstraints;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供操作leveldb的操作方法，如增，删，改，查
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBFactory {

    private static JavaChainLog log = JavaChainLogFactory.getLog(LevelDBFactory.class);

    private static LevelDBFactory instance;
    private static DB db;

    private static String path = "/home/bcia/leveldb";

    private DBFactory dbFactory = Iq80DBFactory.factory;
    private Boolean cleanup = Boolean.FALSE;

    /**
     * 私有构造方法
     */
    private LevelDBFactory() throws LedgerException {
        if(cleanup) {
            try {
                dbFactory.destroy(new File(path), null);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new LedgerException(e);
            }
        }
    }

    public static LevelDBFactory getInstance() throws LedgerException {
        if(instance == null){
            instance = new LevelDBFactory();
        }
        return instance;
    }

    /**
     * 获取默认的 level db
     */
    public static DB getDB() throws LedgerException {
        try{
            return getInstance().dbFactory.open(new File(path),new Options().createIfMissing(Boolean.TRUE));
        } catch (Exception e) {
            throw new LedgerException(e);
        }
    }

    /**
     * 获取指定路径的 level db
     */
    public static DB getDB(String leveldbPath) throws LedgerException {
        try {
            return getInstance().dbFactory.open(new File(leveldbPath),new Options().createIfMissing(Boolean.TRUE));
        } catch (Exception e) {
            throw new LedgerException(e);
        }
    }

    /**
     * 关闭 level db 连接
     */
    public static void closeDB(DB db) throws LedgerException {
        try {
            db.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }
    }

    public static void closeSnapshot(Snapshot snapshot) throws LedgerException {
        try{
            snapshot.close();
        } catch (IOException e){
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }
    }

    /**
     * 关闭 write batch 对象
     */
    public static void closeWriteBatch(WriteBatch writeBatch) throws LedgerException {
        try {
            writeBatch.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException();
        }
    }

    /**
     * level db 中写入新的数据
     * @param db - level db 数据库
     * @param key - 要写入的key
     * @param value - 要写入的value
     */
    public static void add(DB db, byte[] key, byte[] value, boolean sync) throws LedgerException {
        WriteOptions writeOptions = new WriteOptions().sync(sync);
        db.put(key, value, writeOptions);
    }

    /**
     * 向默认的level db中写入数据
     * @param key - 要写入的key
     * @param value - 要写入的value
     */
    public static void add(byte[] key,byte[] value, boolean sync) throws LedgerException {
        try {
            DB db = getDB();
            add(db, key, value, sync);
        } catch (Exception e) {
            throw new LedgerException(e);
        }
    }

    /**
     * 向指定的level db中写入数据
     * @param path - level db的安装路径
     * @param key - 要写入的key
     * @param value - 要写入的value
     */
    public static void add(String path, byte[] key, byte[] value, boolean sync) throws LedgerException {
        DB db = getDB(path);
        add(db, key, value, sync);
    }

    /**
     * 向level db批量写入数据
     * @param db - 要写入的level db数据库
     * @param map - 要批量写入的数据
     */
    public static void add(DB db, Map<byte[],byte[]> map, boolean sync) throws LedgerException {
        WriteBatch writeBatch = db.createWriteBatch();
        WriteOptions writeOptions = new WriteOptions().sync(sync);
        map.forEach((k, v) -> {
            writeBatch.put(k, v);
            db.write(writeBatch, writeOptions);
        });
        closeWriteBatch(writeBatch);
    }

    /**
     * 向默认的level db数据库中批量写入数据
     * @param map - 批量写入的数据
     */
    public static void add(Map<byte[], byte[]> map, boolean sync) throws LedgerException {
        DB db = getDB();
        add(db, map, sync);
    }

    /**
     * 向指定的level db数据库中批量写入数据
     * @param path - level db的安装路径
     * @param map - 要批量写入的数据
     */
    public static void add(String path, Map<byte[], byte[]> map, boolean sync) throws LedgerException {
        DB db = getDB(path);
        add(db, map, sync);
    }

    /**
     * 删除level db中的key
     * @param db - 要删除的level db数据库
     * @param key - 要删除的key
     */
    public static void delete(DB db, byte[] key, boolean sync) throws LedgerException {
        WriteOptions writeOptions = new WriteOptions().sync(sync);
        db.delete(key, writeOptions);
    }

    /**
     * 删除默认level db中的key
     * @param key - 要删除的key
     * @throws IOException
     */
    public static void delete(byte[] key, boolean sync) throws LedgerException {
        DB db = getDB();
        delete(db, key, sync);
    }

    /**
     * 删除指定level db数据库中的key
     * @param path - level db的安装路径
     * @param key - 要删除的key
     */
    public static void delete(String path, byte[] key, boolean sync) throws LedgerException {
        DB db = getDB(path);
        delete(db, key, sync);
    }

    /**
     * 批量删除level db数据库中的keys
     * @param db - 要删除keys的level db数据库
     * @param list - 要删除的keys
     */
    public static void delete(DB db, List<byte[]> list, boolean sync) throws LedgerException {
        WriteBatch writeBatch = db.createWriteBatch();
        WriteOptions writeOptions = new WriteOptions().sync(sync);
        list.forEach((k) ->
            writeBatch.delete(k)
        );
        db.write(writeBatch, writeOptions);
        closeWriteBatch(writeBatch);
    }

    /**
     * 批量删除默认level db数据库的keys
     * @param list - 要删除的keys
     */
    public static void delete(List<byte[]> list, boolean sync) throws LedgerException {
        DB db = getDB();
        delete(db, list, sync);
    }

    /**
     * 批量删除指定level db数据库的keys
     * @param path - level db数据库的安装路径
     * @param list - 要删除的keys
     */
    public static void delete(String path, List<byte[]> list, boolean sync) throws LedgerException {
        DB db = getDB(path);
        delete(db, list, sync);
    }

    /**
     * 查询level db数据库的key
     * @param db - 要查询的level db
     * @param key - 要查询的key
     */
    public static byte[] get(DB db, byte[] key, boolean fileCache) throws LedgerException {
        Snapshot snapshot = db.getSnapshot();
        ReadOptions readOptions = new ReadOptions();
        readOptions.fillCache(fileCache);
        readOptions.snapshot(snapshot);
        byte[] value = db.get(key, readOptions);
        closeSnapshot(snapshot);
        return value;
    }

    /**
     * 查询默认level db数据库的key
     * @param key - 要查询的key
     */
    public static byte[] get(byte[] key, boolean fileCache) throws LedgerException {
        DB db = getDB();
        return get(db, key, fileCache);
    }

    /**
     * 查询指定level db的key
     * @param path - level db的安装路径
     * @param key - 要查询的key
     */
    public static byte[] get(String path, byte[] key, boolean fileCache) throws LedgerException {
        DB db = getDB(path);
        return get(db, key, fileCache);
    }

    /**
     * 获取指定level db的迭代器
     */
//    public static Iterator<Map.Entry<byte[], byte[]>> getIterator() throws LedgerException {
    public static DBIterator getIterator(DB db) throws LedgerException {
        Snapshot snapshot = db.getSnapshot();
        ReadOptions readOptions = new ReadOptions()
                .fillCache(false)
                .snapshot(snapshot);
        DBIterator iterator = db.iterator(readOptions);
        closeSnapshot(snapshot);
        return iterator;
    }
}
