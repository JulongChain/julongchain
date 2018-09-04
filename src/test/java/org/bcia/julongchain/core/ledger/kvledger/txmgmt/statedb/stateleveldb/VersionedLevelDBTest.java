package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/30
 * @company Dingxuan
 */
public class VersionedLevelDBTest {
	static INodeLedger ledger = null;
	static final String ledgerID = "myGroup";
	final String ns = "mycc";
	static IVersionedDB vdb;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void beforeClass() throws Exception  {
		Utils.resetEnv();
		Utils.constructDefaultLedger();
		ledger = LedgerManager.openLedger(ledgerID);
		IVersionedDBProvider provider = new VersionedLevelDBProvider();
		vdb = provider.getDBHandle("myGroup");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getState() throws Exception {
		VersionedValue val = null;
		for (int i = 1; i < 4; i++) {
			val = vdb.getState(ns, "key" + i);
			String s = new String(val.getValue(), StandardCharsets.UTF_8);
			assertEquals("value" + i, s);
		}
		//不存在的key
		val = vdb.getState(ns, "key00");
		assertNull(val);
		//不存在的ｎｓ
		val = vdb.getState(ns + 0, "key0");
		assertNull(val);
	}

	@Test
	public void getHeight() throws Exception {
		LedgerHeight height;
		for (int i = 1; i < 4; i++) {
			height = vdb.getHeight(ns, "key" + i);
			assertSame(1L, height.getBlockNum());
			assertSame((long) i, height.getTxNum());
		}
		//不存在的key
		height = vdb.getHeight(ns, "key--");
		assertNull(height);
		//不存在的ｎｓ
		height = vdb.getHeight(ns + 0, "key0");
		assertNull(height);
	}

	@Test
	public void getStateMultipleKeys() throws Exception {
		List<VersionedValue> list = new ArrayList<>();
		List<String> keys = new ArrayList<String>(){{
			add("key0");
			add("key1");
			add("key2");
			add("key3");
			add("key4");	//不存在的key
		}};
		list = vdb.getStateMultipleKeys(ns, keys);
		assertSame(5, list.size());
		int i = 1;
		for (; i < 4; i++) {
			VersionedValue versionedValue = list.get(i);
			assertSame(1L, versionedValue.getHeight().getBlockNum());
			assertSame((long) i, versionedValue.getHeight().getTxNum());
			assertEquals("value" + i, new String(versionedValue.getValue(), StandardCharsets.UTF_8));
		}
		//key4对应value为null
		assertNull(list.get(i));
	}

	@Test
	public void applyUpdates() throws Exception {
		UpdateBatch updateBatch = new UpdateBatch();
		NsUpdates nsUpdates = new NsUpdates();
		LedgerHeight height = new LedgerHeight(1, 5);
		VersionedValue value = new VersionedValue(height, "value".getBytes());
		nsUpdates.getMap().put("key", value);
		nsUpdates.getMap().put(null, null);
		nsUpdates.getMap().put(null, value);
		//删除key
		nsUpdates.getMap().put("key0", null);
		updateBatch.getUpdates().put(ns, nsUpdates);
		vdb.applyUpdates(updateBatch, height);
		VersionedValue key4 = vdb.getState(ns, "key");
		assertEquals(value, key4);
	}

	@Test
	public void getLatestSavePoint() throws Exception {
		LedgerHeight latestSavePoint = vdb.getLatestSavePoint();
		assertSame(1L, latestSavePoint.getBlockNum());
	}
}