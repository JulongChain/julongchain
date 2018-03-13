package org.bcia.javachain.core.ledger.leveldb;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.iq80.leveldb.DB;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * LevelDBFactoryTest测试类
 *
 * @author
 * @date 2018/3/12
 * @company Dingxuan
 */
public class LevelDBFactoryTest {

    private static JavaChainLog log = JavaChainLogFactory.getLog(LevelDBFactoryTest.class);

    @Test
    public void getDB() throws IOException {
        DB db = LevelDBFactory.getDB();
        Assert.assertNotNull(db);
        LevelDBFactory.closeDB(db);
    }

    @Test
    public void closeDB() throws IOException {
        DB db = LevelDBFactory.getDB();
        LevelDBFactory.closeDB(db);
    }

    @Test
    public void add() throws IOException {
        String key = "key-001";
        String value = "value-001["+ UUID.randomUUID().toString() + "]" ;
        log.info("add key");
        LevelDBFactory.add(key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")));
        log.info("query key");
        byte[] bytes = LevelDBFactory.get(key.getBytes(Charset.forName("utf-8")));
        String query = new String(bytes);
        log.info("original value:" + value);
        log.info("query value:" + query);
        Assert.assertEquals(value, query);
    }

    @Test
    public void delete() throws IOException {
        String key = UUID.randomUUID().toString();
        log.info("key:" + key);
        String value = UUID.randomUUID().toString();
        log.info("value:" + value);
        LevelDBFactory.add(key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")));
        log.info("add to level db");
        Assert.assertEquals(value, new String(LevelDBFactory.get(key.getBytes(Charset.forName("utf-8")))));
        LevelDBFactory.delete(key.getBytes(Charset.forName("utf-8")));
        Assert.assertNull(LevelDBFactory.get(key.getBytes(Charset.forName("utf-8"))));
    }

    @Test
    public void get() throws IOException {
        String key = UUID.randomUUID().toString();
        log.info("key:" + key);
        String value = UUID.randomUUID().toString();
        log.info("value:" + value);
        LevelDBFactory.add(key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")));
        log.info("add to level db");
        byte[] queryBytes = LevelDBFactory.get(key.getBytes(Charset.forName("utf-8")));
        String queryString = new String(queryBytes);
        log.info("query key from level db");
        log.info("query string:" + queryString);
        Assert.assertEquals(value, queryString);
    }

}