package org.bcia.javachain.core.ledger.leveldb;

import org.bcia.javachain.common.exception.LevelDBException;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.iq80.leveldb.DBIterator;
import org.junit.Test;

import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/27
 * @company Dingxuan
 */
public class LevelDBUtilTest {

  @Test
  public void getDB() {}

  @Test
  public void removeDb() {}

  @Test
  public void closeDB() {}

  @Test
  public void closeSnapshot() {}

  @Test
  public void closeWriteBatch() {}

  @Test
  public void add() {
    try {
      LevelDB db = LevelDBUtil.getDB("/var/javachain/stateLeveldb");
      LevelDBUtil.add(db, "aaa".getBytes(), "value".getBytes(), false);
    } catch (LevelDBException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void add1() {}

  @Test
  public void delete() {}

  @Test
  public void delete1() {}

  @Test
  public void get() {}

  @Test
  public void getIterator() {}

  @Test
  public void getLastKey() {}

  @Test
  public void getAll() {

    try {
      // LevelDB db = LevelDBUtil.getDB("/var/javachain/stateLeveldb");
      LevelDB db =
          LevelDBUtil.getDB(
              "/root/e2e_cli/peer0.org0/hyperledger/production/ledgersData/chains/index");
      // byte[] bytes = LevelDBUtil.get(db, "aaa".getBytes(), false);
       // System.out.println(new String(bytes));

      DBIterator iterator = LevelDBUtil.getIterator(db);
      while(iterator.hasNext()){
        Map.Entry<byte[], byte[]> next = iterator.next();
        System.out.println("key:[" + new String(next.getKey()) + "] value:[" + new String(next.getValue()) + "]");
      }

    } catch (LevelDBException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void aaa() {
    KvRwset.KVRWSet.newBuilder().addReads(KvRwset.KVRead.newBuilder().build());
  }



}