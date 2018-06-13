package org.bcia.julongchain.core.ledger.leveldb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.util.Util;
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
    public void getDB() throws LedgerException {
        String path = "";
        DB db = LevelDBUtil.getDB("");
        Assert.assertNotNull(db);
        LevelDBUtil.closeDB(db);
    }

    @Test
    public void closeDB() throws IOException {
//        IDB db = LevelDBFactory.getDB();
//        LevelDBFactory.closeDB(db);
    }

    @Test
    public void add() throws LedgerException {
//        String key = "key-001";
//        String value = "value-001["+ UUID.randomUUID().toString() + "]" ;
//        log.info("add key");
//        LevelDBFactory.add(key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")), true);
//        log.info("query key");
//        byte[] bytes = LevelDBFactory.get(key.getBytes(Charset.forName("utf-8")), false);
//        String query = new String(bytes);
//        log.info("original value:" + value);
//        log.info("query value:" + query);
//        Assert.assertEquals(value, query);

        String path = "/home/bcia/test";
        LevelDBProvider provider = new LevelDBProvider(path);

        LevelDB db = LevelDBUtil.getDB(path);
        for (int i = 0; i < 100; i++) {
            byte[] key = String.valueOf(i).getBytes();
            byte[] value1 = Util.longToBytes(1000 + i, 8);
            byte[] value2 = Util.longToBytes(10000 + i, 8);
            byte[] value = ArrayUtils.addAll(value1, value2);
            LevelDBUtil.add(db, key, value, true);
            provider.put(key, value, true);
//            closeDB();
        }
        byte[] key = {0x00};
        LevelDBUtil.add(db, key, "askjaklj".getBytes(), true);
//        for (int i = 0; i < 100; i++) {
//            String path = "/home/bcia/leveldb";
//            IDB db = Iq80DBFactory.factory.open(new File(path), new Options().createIfMissing(true));
//        }

    }

    @Test
    public void delete() throws LedgerException {
        String path = "";
        String key = UUID.randomUUID().toString();
        log.info("key:" + key);
        String value = UUID.randomUUID().toString();
        log.info("value:" + value);
        LevelDB db = LevelDBUtil.getDB(path);
        LevelDBUtil.add(db, key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")), true);
        log.info("add to level db");
        Assert.assertEquals(value, new String(LevelDBUtil.get(db, key.getBytes(Charset.forName("utf-8")), false)));
        LevelDBUtil.delete(db, key.getBytes(Charset.forName("utf-8")), true);
        Assert.assertNull(LevelDBUtil.get(db, key.getBytes(Charset.forName("utf-8")), false));
    }

    @Test
    public void get() throws LedgerException {
//        log.info(new String(LevelDBFactory.get("key-001".getBytes(Charset.forName("utf-8")))));
//        String key = UUID.randomUUID().toString();
//        log.info("key:" + key);
//        String value = UUID.randomUUID().toString();
//        log.info("value:" + value);
//        LevelDBFactory.add(key.getBytes(Charset.forName("utf-8")), value.getBytes(Charset.forName("utf-8")), true);
//        log.info("add to level db");
//        byte[] queryBytes = LevelDBFactory.get(key.getBytes(Charset.forName("utf-8")), false);
//        String queryString = new String(queryBytes);
//        log.info("query key from level db");
//        log.info("query string:" + queryString);
//        Assert.assertEquals(value, queryString);
        String path = "";
        byte[] key = "underConstructionLedgerKey".getBytes();
        LevelDB db = LevelDBUtil.getDB(path);
        byte[] value = LevelDBUtil.get(db, key, true);
        System.out.println(new String(value));
        byte[] value1 = LevelDBUtil.get(db, key, true);
//        System.out.println(new String(value1));

    }

    @Test
    public void getIterator() throws LedgerException {
//        DBIterator itr = LevelDBFactory.getIterator(LevelDBFactory.getDB("/home/bcia/test"));
//        itr.seek("18".getBytes());
//        for (int i = 0; i < 10; i++) {
//            System.out.println(new String(itr.next().getKey()));
//        }
    }

    @Test
    public void test() throws Throwable {
        String path = "";
        DB db = LevelDBUtil.getDB(path);
        byte[] key = {0x00};
        LevelDBUtil.get(db, key, false);
        LevelDBUtil.get(db, key, false);
    }

    public static void soutByte(byte[] bytes) {
        for (byte aByte : bytes) {
            System.out.println(aByte + " ");
        }
        System.out.println();
    }
}