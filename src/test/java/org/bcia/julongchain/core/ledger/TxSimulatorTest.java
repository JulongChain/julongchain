/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;


/**
 * 交易模拟器测试
 * 账本创建参见org.bcia.julongchain.common.ledger.util.Utils
 * 起始世界状态	key0:value0
 * 				key1:value1
 * 				key2:value2
 * 				key3:value3
 * 				中文测试:中文测试
 *
 * @author sunzongyu1
 * @date 2018/04/24
 * @company Dingxuan
 */
public class TxSimulatorTest {
    ITxSimulator simulator = null;
    static INodeLedger ledger = null;
    TxSimulationResults txSimulationResults = null;
    static final String ledgerID = "myGroup";
    final String ns = "mycc";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() throws Exception  {
		Utils.resetEnv();
		Utils.constructDefaultLedger();
        ledger = LedgerManager.openLedger(ledgerID);
    }

    @Before
	public void before() throws Exception {
    	LedgerManager.initialize(null);
		ledger = LedgerManager.openLedger(ledgerID);
		simulator = ledger.newTxSimulator("5");
	}

    @Test
    public void testSetState() throws Exception {
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.setState(ns, "key", "test set state".getBytes(StandardCharsets.UTF_8));
        simulator.setState(ns, "key1", "test set state".getBytes(StandardCharsets.UTF_8));
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertEquals(kvRWSet.getWrites(1).getKey(), "key1");
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set state");
        Assert.assertEquals(kvRWSet.getWrites(1).getValue().toStringUtf8(), "test set state");
    	thrown.expect(LedgerException.class);
        simulator.setState(ns + "1", "key", "test set state".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testDeleteState() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
		thrown.expect(LedgerException.class);
        simulator.setState(ns, "key", "test set state".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSetStateMulitipleKeys() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.setStateMultipleKeys(ledgerID, new HashMap<String, byte[]>(){{
            put("key1", "test set state1".getBytes(StandardCharsets.UTF_8));
            put("key2", "test set state2".getBytes(StandardCharsets.UTF_8));
            put("key3", "test set state3".getBytes(StandardCharsets.UTF_8));
            put("key4", "test set state4".getBytes(StandardCharsets.UTF_8));
            put("key5", "test set state5".getBytes(StandardCharsets.UTF_8));
            put("key6", "test set state6".getBytes(StandardCharsets.UTF_8));
        }});
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        int i = 0;
        for(KvRwset.KVWrite write : kvRWSet.getWritesList()){
            int j = ++i;
            Assert.assertEquals(write.getKey(), "key" + j);
            Assert.assertFalse(write.getIsDelete());
            Assert.assertEquals(write.getValue().toStringUtf8(), "test set state" + j);
        }
		thrown.expect(LedgerException.class);
		simulator.setState(ns, "key", "test set state".getBytes(StandardCharsets.UTF_8));
    }

    /*-------------------------------------------------------------------
		现阶段没有使用PvtData
    @Test
    public void testsetPrivateData() throws Exception{
	    thrown.expect(LedgerException.class);
	    thrown.expectMessage("This instance should not be used after calling Done()");
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes(StandardCharsets.UTF_8));
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set private data");
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSetPirvateDataMultipleKeys() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes(StandardCharsets.UTF_8));
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes(StandardCharsets.UTF_8));
        simulator.setPrivateData(ns, coll + "1", "key1", "test set private data1".getBytes(StandardCharsets.UTF_8));
        simulator.setPirvateDataMultipleKeys(ns, coll + "2", new HashMap<String, byte[]>(){{
            put("key1", "test set private data mulitiple keys1".getBytes(StandardCharsets.UTF_8));
            put("key2", "test set private data mulitiple keys2".getBytes(StandardCharsets.UTF_8));
            put("key3", "test set private data mulitiple keys3".getBytes(StandardCharsets.UTF_8));
            put("key4", "test set private data mulitiple keys4".getBytes(StandardCharsets.UTF_8));
            put("key5", "test set private data mulitiple keys5".getBytes(StandardCharsets.UTF_8));
            put("key6", "test set private data mulitiple keys6".getBytes(StandardCharsets.UTF_8));

        }});
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(2).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        int i = 0;
        for(KvRwset.KVWrite write : kvRWSet.getWritesList()){
            int j = ++i;
            Assert.assertEquals(write.getKey(), "key" + j);
            Assert.assertFalse(write.getIsDelete());
            Assert.assertEquals(write.getValue().toStringUtf8(), "test set private data mulitiple keys" + j);
        }
    }

    @Test
    public void testDeletePrivateData() throws Exception{
	    thrown.expect(LedgerException.class);
	    thrown.expectMessage("This instance should not be used after calling Done()");
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes(StandardCharsets.UTF_8));
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes(StandardCharsets.UTF_8));
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes(StandardCharsets.UTF_8));
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(1).getKey(), "key1");
        Assert.assertFalse(kvRWSet.getWrites(1).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(1).getValue().toStringUtf8(), "test set private data1");
    }
    -------------------------------------------------------------------*/

    @Test
    public void testGetState() throws Exception {
		ByteString rwset = null;
		KvRwset.KVRWSet kvRWSet = null;
    	//存在的state
		for (int i = 0; i < 4; i++) {
			byte[] value = simulator.getState(ns, "key" + i);
			Assert.assertArrayEquals(value, ("value" + i).getBytes(StandardCharsets.UTF_8));
		}
		//不存在的state
		byte[] notExists = simulator.getState(ns, "not exist");
		assertNull(notExists);
		txSimulationResults = simulator.getTxSimulationResults();
		rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
		kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
		int i = 0;
		for (; i < 4; i++) {
			KvRwset.KVRead read = kvRWSet.getReadsList().get(i);
			Assert.assertEquals(read.getKey(), "key" + i);
			assertSame(1L, read.getVersion().getBlockNum());
			assertSame((long) i, read.getVersion().getTxNum());
		}
		KvRwset.KVRead read = kvRWSet.getReadsList().get(i);
		Assert.assertEquals("not exist", read.getKey());
		assertEquals(ByteString.EMPTY, read.getVersion().toByteString());
	}

	@Test
	public void testGetStateMultipleKeys() throws Exception{
		ByteString rwset = null;
		KvRwset.KVRWSet kvRWSet = null;
		//存在的state
		List<byte[]> states = simulator.getStateMultipleKeys(ns, new ArrayList<String>() {{
			add("key0");
			add("key1");
			add("key2");
			add("key3");
		}});
		states.forEach(Assert::assertNotNull);
		//不存在的state
		states = simulator.getStateMultipleKeys(ns, new ArrayList<String>() {{
			add("not exists 0");
			add("not exists 1");
		}});
		states.forEach(Assert::assertNull);
		txSimulationResults = simulator.getTxSimulationResults();
		rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
		kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
		int i = 0;
		for (; i < 4; i++) {
			KvRwset.KVRead read = kvRWSet.getReadsList().get(i);
			Assert.assertEquals(read.getKey(), "key" + i);
			assertSame(1L, read.getVersion().getBlockNum());
			assertSame((long) i, read.getVersion().getTxNum());
		}
		KvRwset.KVRead read = kvRWSet.getReadsList().get(i++);
		Assert.assertEquals("not exists 0", read.getKey());
		assertEquals(ByteString.EMPTY, read.getVersion().toByteString());
		read  = kvRWSet.getReadsList().get(i++);
		Assert.assertEquals("not exists 1", read.getKey());
		assertEquals(ByteString.EMPTY, read.getVersion().toByteString());
	}

	@Test
	public void testGetStateRangeScanIterator() throws Exception{
		IResultsIterator itr = simulator.getStateRangeScanIterator(ns, "key", "kez");
		int i = 0;
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
			VersionedKV kv = (VersionedKV) next.getObj();
			assertNotNull(kv.getCompositeKey().getKey());
			assertTrue(kv.getCompositeKey().getKey().contains("key"));
		}
		assertSame(4, i);

		i = 0;
		itr = simulator.getStateRangeScanIterator(ns, "key0", "key3");
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
			VersionedKV kv = (VersionedKV) next.getObj();
			assertNotNull(kv.getCompositeKey().getKey());
			assertTrue(kv.getCompositeKey().getKey().contains("key"));
		}
		assertSame(3, i);

		i = 0;
		itr = simulator.getStateRangeScanIterator(ns, "kez", null);
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
			VersionedKV kv = (VersionedKV) next.getObj();
			assertNotNull(kv.getCompositeKey().getKey());
		}
		assertSame(1, i);

		i = 0;
		itr = simulator.getStateRangeScanIterator(ns, null, "key2");
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
			VersionedKV kv = (VersionedKV) next.getObj();
		}
		assertSame(2, i);


		i = 0;
		itr = simulator.getStateRangeScanIterator(ns, null, null);
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
			VersionedKV kv = (VersionedKV) next.getObj();
		}
		assertSame(5, i);
	}

	/*-------------------------------------------------------------

	@Test
	public void testGetPrivateData() throws Exception{
		for (int i = 0; i < 6; i++) {
			byte[] privateData = simulator.getPrivateData(ledgerID, "coll", "key" + i);
			Assert.assertTrue(Arrays.equals(privateData, ("pvt value" + i).getBytes(StandardCharsets.UTF_8)));
		}
	}

	@Test
	public void testGetPrivateDataMultipleKeys() throws Exception{
		List<byte[]> privateDatas = simulator.getPrivateDataMultipleKeys(ledgerID, "coll", new ArrayList<String>() {{
			add("key0");
			add("key1");0] = (byte) 0;
			add("key2");
			add("key3");
			add("key4");
			add("key5");
		}});
		for (int i = 0; i < 6; i++) {
			Assert.assertTrue(Arrays.equals(privateDatas.get(i), ("pvt value" + i).getBytes(StandardCharsets.UTF_8)));
		}
	}

	@Test
	public void testGetPrivateDataRangeScanIterator() throws Exception{
		IResultsIterator itr = simulator.getPrivateDataRangeScanIterator(ledgerID, "coll", "key", "l");
		for (int i = 0; i < 6; i++) {
			QueryResult qr = itr.next();
			KvQueryResult.KV kv = ((KvQueryResult.KV) qr.getObj());
			Assert.assertEquals(kv.getNamespace(), ledgerID);
			Assert.assertEquals(kv.getKey(), "key" + i);
			Assert.assertEquals(kv.getValue(),ByteString.copyFromUtf8("pvt value" + i));
		}
	}

	-------------------------------------------------------------*/
}


