package org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreTest;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.CommonStorageDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.IDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/06/14
 * @company Dingxuan
 */
public class LockBasedTxManagerTest {
	String txID = "txID";
	String ledgerID = "test id";
	LockBasedTxManager mgr;
	CommonStorageDBProvider vdbProvider;
	IDB db;

	@Before
	public void before() throws Exception {
		vdbProvider = CommonStorageDBProvider.newCommonStorageDBProvider();
		mgr = new LockBasedTxManager(ledgerID, vdbProvider.getDBHandle(ledgerID), null);
	}

	@Test
	public void newTxSimulator() throws Exception {
		ITxSimulator simulator = mgr.newTxSimulator(txID);
		Assert.assertNotNull(simulator);
	}

	@Test
	public void validateAndPrepare() throws Exception {
		mgr.validateAndPrepare(new BlockAndPvtData(FsBlockStoreTest.constructBlock(null), null, null), true);
		Assert.assertNotNull(mgr.getBatch());
		Map<String, VersionedValue> updates = mgr.getBatch().getPubUpdateBatch().getBatch().getUpdates(ledgerID);
		Assert.assertSame(updates.get("key").getVersion().getTxNum(), (long) 0);
		Assert.assertSame(updates.get("key").getVersion().getBlockNum(), (long) 0);
		Assert.assertArrayEquals(updates.get("key").getValue(), "pub value".getBytes());
	}

	@Test
	public void commit() throws Exception {
		mgr.validateAndPrepare(new BlockAndPvtData(FsBlockStoreTest.constructBlock(null), null, null), true);
		mgr.commit();
		ITxSimulator simulator = mgr.newTxSimulator(txID);
		byte[] keys = simulator.getState(ledgerID, "key");
		Assert.assertEquals("pub value", new String(keys));
	}
}