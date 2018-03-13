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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供操作leveldb的操作方法，如增，删，改，查
 *
 * @author wanliangbing
 * @date 2018/3/12
 * @company Dingxuan
 */
public class LevelDBFactory {

    private static JavaChainLog log = JavaChainLogFactory.getLog(LevelDBFactory.class);

    private static LevelDBFactory instance = new LevelDBFactory();

    private static String path = "/home/wanliangbing/leveldb";

    private DBFactory dbFactory = Iq80DBFactory.factory;
    private Boolean cleanup = Boolean.FALSE;
    private Charset charset = Charset.forName("utf-8");


    /**
     * 私有构造方法
     */
    private LevelDBFactory() {
        if(cleanup.booleanValue()) {
            try {
                dbFactory.destroy(new File(path), null);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static LevelDBFactory getInstance() {
        return instance;
    }

    /**
     * 获取默认的 level db
     * @return
     * @throws IOException
     */
    public static DB getDB() throws IOException {
        return getInstance().dbFactory.open(new File(path),new Options().createIfMissing(Boolean.TRUE));
    }

    /**
     * 获取指定路径的 level db
     * @param level db 安装路径
     * @return
     * @throws IOException
     */
    public static DB getDB(String leveldbPath) throws IOException {
        return getInstance().dbFactory.open(new File(leveldbPath),new Options().createIfMissing(Boolean.TRUE));
    }

    /**
     * 关闭 level db 连接
     * @param db
     */
    public static void closeDB(DB db) {
        try {
            db.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 关闭 write batch 对象
     * @param writeBatch
     */
    public static void closeWriteBatch(WriteBatch writeBatch) {
        try {
            writeBatch.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * level db 中写入新的数据
     * @param db - level db 数据库
     * @param key - 要写入的key
     * @param value - 要写入的value
     */
    public static void add(DB db, byte[] key, byte[] value) {
        WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
        db.put(key, value, writeOptions);
        closeDB(db);
    }

    /**
     * 向默认的level db中写入数据
     * @param key - 要写入的key
     * @param value - 要写入的value
     * @throws IOException
     */
    public static void add(byte[] key,byte[] value) throws IOException {
        DB db = getDB();
        add(db, key, value);
    }

    /**
     * 向指定的level db中写入数据
     * @param path - level db的安装路径
     * @param key - 要写入的key
     * @param value - 要写入的value
     * @throws IOException
     */
    public static void add(String path, byte[] key, byte[] value) throws IOException {
        DB db = getDB(path);
        add(db, key, value);
    }

    /**
     * 向level db批量写入数据
     * @param db - 要写入的level db数据库
     * @param map - 要批量写入的数据
     */
    public static void add(DB db, Map<byte[],byte[]> map) {
        WriteBatch writeBatch = db.createWriteBatch();
        WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
        Set<Map.Entry<byte[], byte[]>> entries = map.entrySet();
        for (Map.Entry<byte[],byte[]> entry: entries) {
            byte[] key = entry.getKey();
            byte[] value = entry.getValue();
            writeBatch.put(key, value);
            db.write(writeBatch, writeOptions);
        }
        closeWriteBatch(writeBatch);
        closeDB(db);
    }

    /**
     * 向默认的level db数据库中批量写入数据
     * @param map - 批量写入的数据
     * @throws IOException
     */
    public static void add(Map<byte[], byte[]> map) throws IOException {
        DB db = getDB();
        add(db, map);
    }

    /**
     * 向指定的level db数据库中批量写入数据
     * @param path - level db的安装路径
     * @param map - 要批量写入的数据
     * @throws IOException
     */
    public static void add(String path, Map<byte[], byte[]> map) throws IOException {
        DB db = getDB(path);
        add(db, map);
    }

    /**
     * 删除level db中的key
     * @param db - 要删除的level db数据库
     * @param key - 要删除的key
     */
    public static void delete(DB db, byte[] key) {
        WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
        db.delete(key, writeOptions);
        closeDB(db);
    }

    /**
     * 删除默认level db中的key
     * @param key - 要删除的key
     * @throws IOException
     */
    public static void delete(byte[] key) throws IOException {
        DB db = getDB();
        delete(db, key);
    }

    /**
     * 删除指定level db数据库中的key
     * @param path - level db的安装路径
     * @param key - 要删除的key
     * @throws IOException
     */
    public static void delete(String path, byte[] key) throws IOException {
        DB db = getDB(path);
        delete(db, key);
    }

    /**
     * 批量删除level db数据库中的keys
     * @param db - 要删除keys的level db数据库
     * @param list - 要删除的keys
     */
    public static void delete(DB db, List<byte[]> list) {
        WriteBatch writeBatch = db.createWriteBatch();
        WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
        for (byte[] key: list) {
            writeBatch.delete(key);
        }
        db.write(writeBatch, writeOptions);
        closeWriteBatch(writeBatch);
        closeDB(db);
    }

    /**
     * 批量删除默认level db数据库的keys
     * @param list - 要删除的keys
     * @throws IOException
     */
    public static void delete(List<byte[]> list) throws IOException {
        DB db = getDB();
        delete(db, list);
    }

    /**
     * 批量删除指定level db数据库的keys
     * @param path - level db数据库的安装路径
     * @param list - 要删除的keys
     * @throws IOException
     */
    public static void delete(String path, List<byte[]> list) throws IOException {
        DB db = getDB(path);
        delete(db, list);
    }

    /**
     * 查询level db数据库的key
     * @param db - 要查询的level db
     * @param key - 要查询的key
     * @return
     */
    public static byte[] get(DB db, byte[] key) {
        Snapshot snapshot = db.getSnapshot();
        ReadOptions readOptions = new ReadOptions();
        readOptions.fillCache(Boolean.FALSE);
        readOptions.snapshot(snapshot);
        byte[] value = db.get(key, readOptions);
        closeDB(db);
        return value;
    }

    /**
     * 查询默认level db数据库的key
     * @param key - 要查询的key
     * @return
     * @throws IOException
     */
    public static byte[] get(byte[] key) throws IOException {
        DB db = getDB();
        return get(db, key);
    }

    /**
     * 查询指定level db的key
     * @param path - level db的安装路径
     * @param key - 要查询的key
     * @return
     * @throws IOException
     */
    public static byte[] get(String path, byte[] key) throws IOException {
        DB db = getDB(path);
        return get(db, key);
    }

}
