package org.bcia.javachain.core.ledger.leveldb;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LevelDBFactory {

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
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return
     */
    public static LevelDBFactory getInstance() {
        return instance;
    }

    public static DB getDB() throws IOException {
        return getInstance().dbFactory.open(new File(path),new Options().createIfMissing(Boolean.TRUE));
    }

    public static DB getDB(String leveldbPath) throws IOException {
        return getInstance().dbFactory.open(new File(leveldbPath),new Options().createIfMissing(Boolean.TRUE));
    }

    public static void closeDB(DB db) {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeWriteBatch(WriteBatch writeBatch) {
        try {
            writeBatch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(DB db, byte[] key, byte[] value) {
        try{
            WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
            db.put(key, value, writeOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
    }

    public static void add(byte[] key,byte[] value) throws IOException {
        DB db = getDB();
        add(db, key, value);
    }

    public static void add(String path, byte[] key, byte[] value) throws IOException {
        DB db = getDB(path);
        add(db, key, value);
    }

    public static void add(DB db, Map<byte[],byte[]> map) {
        WriteBatch writeBatch = db.createWriteBatch();
        try{
            WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
            Set<Map.Entry<byte[], byte[]>> entries = map.entrySet();
            for (Map.Entry<byte[],byte[]> entry: entries) {
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();
                writeBatch.put(key, value);
                db.write(writeBatch, writeOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeWriteBatch(writeBatch);
            closeDB(db);
        }
    }

    public static void add(Map<byte[], byte[]> map) throws IOException {
        DB db = getDB();
        add(db, map);
    }

    public static void add(String path, Map<byte[], byte[]> map) throws IOException {
        DB db = getDB(path);
        add(db, map);
    }

    public static void delete(DB db, byte[] key) {
        try{
            WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
            db.delete(key, writeOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
    }

    public static void delete(byte[] key) throws IOException {
        DB db = getDB();
        delete(db, key);
    }

    public static void delete(String path, byte[] key) throws IOException {
        DB db = getDB(path);
        delete(db, key);
    }

    public static void delete(DB db, List<byte[]> list) {
        WriteBatch writeBatch = db.createWriteBatch();
        try {
            WriteOptions writeOptions = new WriteOptions().sync(Boolean.TRUE);
            for (byte[] key: list) {
                writeBatch.delete(key);
            }
            db.write(writeBatch, writeOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeWriteBatch(writeBatch);
            closeDB(db);
        }
    }

    public static void delete(List<byte[]> list) throws IOException {
        DB db = getDB();
        delete(db, list);
    }

    public static void delete(String path, List<byte[]> list) throws IOException {
        DB db = getDB(path);
        delete(db, list);
    }

    public static byte[] get(DB db, byte[] key) {
        byte[] value = new byte[]{};
        try {
            Snapshot snapshot = db.getSnapshot();
            ReadOptions readOptions = new ReadOptions();
            readOptions.fillCache(Boolean.FALSE);
            readOptions.snapshot(snapshot);
            value = db.get(key, readOptions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
        return value;
    }

    public static byte[] get(byte[] key) throws IOException {
        DB db = getDB();
        return get(db, key);
    }

    public static byte[] get(String path, byte[] key) throws IOException {
        DB db = getDB(path);
        return get(db, key);
    }

}
