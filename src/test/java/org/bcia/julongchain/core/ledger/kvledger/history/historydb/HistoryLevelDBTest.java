package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
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
	String ledgerID = "test id";

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
		db.newHistoryQueryExecutor(blockStore);
	}

	@Test
	public void commit() throws Exception{
//		db.commit(FsBlockStoreTest.constructBlock(null));
	}

	@Test
	public void getLastSavepaoint() throws Exception {
		LedgerHeight lastSavepoint = db.getLastSavepoint();
		Assert.assertSame(lastSavepoint.getBlockNum(), (long) 0);
		//获取的txNum从1开始
		Assert.assertSame(lastSavepoint.getTxNum(), (long) 1);
	}
}