package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.bcia.julongchain.common.ledger.util.Utils.constructDefaultBlock;
import static org.bcia.julongchain.common.ledger.util.Utils.constructDefaultLedger;
import static org.bcia.julongchain.common.ledger.util.Utils.rmrf;
import static org.junit.Assert.*;

/**
 * 历史查询器测试
 * 账本创建参见org.bcia.julongchain.common.ledger.util.Utils
 * 起始世界状态	key0:value0
 * 				key1:value1
 * 				key2:value2
 * 				key3:value3
 * 				中文测试:中文测试
 *
 * @author sunzongyu
 * @date 2018/06/13
 * @company Dingxuan
 */
public class HistoryLevelDBTest {
	HistoryLevelDBProvider provider;
	IHistoryDB db;
	FsBlockStoreProvider blkStoreProvider;
	IBlockStore blockStore;
	String ledgerID = "myGroup";
	static INodeLedger l;
	static Common.Block block;
	static IHistoryQueryExecutor hqe;

	@BeforeClass
	public static void setUp() throws Exception {
		//重置目录
		rmrf(LedgerConfig.getRootPath());
		//初始化账本
		l = constructDefaultLedger();
		hqe = l.newHistoryQueryExecutor();
	}


	@Before
	public void before() throws Exception {
		provider = new HistoryLevelDBProvider();
		db = provider.getDBHandle(ledgerID);
		String[] attrsToIndex = {
				BlockStorage.INDEXABLE_ATTR_BLOCK_HASH,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM,
				BlockStorage.INDEXABLE_ATTR_TX_ID,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM,
				BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID,
				BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE
		};
		IndexConfig indexConfig = new IndexConfig(attrsToIndex);
		//文件系统初始化参数
		blkStoreProvider =
				new FsBlockStoreProvider(new Config(LedgerConfig.getBlockStorePath(), LedgerConfig.getMaxBlockfileSize()), indexConfig);
		blockStore = blkStoreProvider.openBlockStore(ledgerID);
	}

	@Test
	public void newHistoryQueryExecutor() throws Exception {
		IHistoryQueryExecutor hqe = db.newHistoryQueryExecutor(blockStore);
		assertNotNull(hqe);
	}

	@Test
	public void commit() throws Exception{
		long height = l.getBlockchainInfo().getHeight();
		block = constructDefaultBlock(l, l.getBlockByNumber(height - 1), ledgerID, "mycc");
		db.commit(block);
		IHistoryQueryExecutor hqe = db.newHistoryQueryExecutor(blockStore);
		IResultsIterator itr = hqe.getHistoryForKey("mycc", "key0");

		int i = 0;
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			KvRwset.Version version = (KvRwset.Version) next.getObj();
			assertSame(i + 1L, version.getBlockNum());
			assertSame(0L, version.getTxNum());
			i++;
		}
	}

	@Test
	public void getLastSavepaoint() throws Exception {
		LedgerHeight lastSavepoint = db.getLastSavepoint();
		assertNotNull(lastSavepoint);
		assertSame(lastSavepoint.getBlockNum(), 2L);
		assertSame(lastSavepoint.getTxNum(), 5L);
	}

	@Test
	public void shouldRecover() throws Exception {
		LedgerHeight lastSavepoint = db.getLastSavepoint();
		long l = db.shouldRecover();
		assertTrue(l > 0);
		LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getHistoryLevelDBPath());
		provider.setLedgerID("myGroup");
		provider.delete(new byte[]{0x00}, true);
		l = db.shouldRecover();
		assertTrue(l < 0);
		provider.put(new byte[]{0x00}, lastSavepoint.toBytes(), true);
	}
}