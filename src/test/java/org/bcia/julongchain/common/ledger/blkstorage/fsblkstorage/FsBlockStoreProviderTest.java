package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/30
 * @company Dingxuan
 */
public class FsBlockStoreProviderTest {
	static FsBlockStoreProvider provider;
	static String groupID = "myGroup";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void beforeClass() throws Exception  {
	}

	@Before
	public void setUp() throws Exception {
		Utils.resetEnv();
		String[] attrsToIndex = {
				BlockStorage.INDEXABLE_ATTR_BLOCK_HASH,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM,
				BlockStorage.INDEXABLE_ATTR_TX_ID,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM,
				BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID,
				BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE
		};
		IndexConfig indexConfig = new IndexConfig(attrsToIndex);
		provider =
				new FsBlockStoreProvider(new Config(LedgerConfig.getBlockStorePath(), LedgerConfig.getMaxBlockfileSize()), indexConfig);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createBlockStore() throws Exception {
		IBlockStore blockStore = provider.createBlockStore(groupID);
		assertNotNull(blockStore);
		File file = new File("/var/julongchain/production/node/chains/chains/myGroup/blockfile_000000");
		assertTrue(file.exists());
	}

	@Test
	public void exists() throws Exception {
		boolean exists = provider.exists(groupID);
		assertFalse(exists);
		IBlockStore blockStore = provider.createBlockStore(groupID);
		exists = provider.exists(groupID);
		assertTrue(exists);
	}

	@Test
	public void list() throws Exception {
		List<String> list = provider.list();
		assertNull(list);
		provider.createBlockStore(groupID);
		list = provider.list();
		assertSame(1, list.size());
	}
}