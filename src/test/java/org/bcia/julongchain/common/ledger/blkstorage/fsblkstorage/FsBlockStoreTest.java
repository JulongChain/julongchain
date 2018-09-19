package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.common.protos.EnvelopeVO;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.*;
import org.junit.rules.ExpectedException;
import scala.collection.concurrent.INode;

import static org.junit.Assert.*;

/**
 * FsBlockStore测试类
 *
 * @author sunzongyu
 * @date 2018/08/30
 * @company Dingxuan
 */
public class FsBlockStoreTest {
	static FsBlockStoreProvider provider;
	static String groupID = "myGroup";
	static String ns = "mycc";
	static IBlockStore store;
	static Common.Block block;
	static INodeLedger l;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void beforeClass() throws Exception  {
		Utils.resetEnv();
		l = Utils.constructDefaultLedger();
	}

	@Before
	public void setUp() throws Exception {
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
		store = provider.createBlockStore(groupID);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void addBlock() throws Exception {
		long height = store.getBlockchainInfo().getHeight();
		block = Utils.constructDefaultBlock1(l, l.getBlockByNumber(height - 1), groupID, ns);
		store.addBlock(block);
		assertSame(height + 1, store.getBlockchainInfo().getHeight());
		//区块号错误的区块无法添加
		height = store.getBlockchainInfo().getHeight();
		block = Utils.constructDefaultBlock1(l, l.getBlockByNumber(0), groupID, ns);
		thrown.expect(LedgerException.class);
		store.addBlock(block);
	}

	@Test
	public void getBlockchainInfo() throws Exception {
		Ledger.BlockchainInfo bcInfo = store.getBlockchainInfo();
		long height = bcInfo.getHeight();
		block = Utils.constructDefaultBlock1(l, l.getBlockByNumber(height - 1), groupID, ns);
		store.addBlock(block);
		bcInfo = store.getBlockchainInfo();
		assertSame(block.getHeader().getNumber(), bcInfo.getHeight() - 1);
		assertArrayEquals(block.getHeader().toByteArray(), bcInfo.getCurrentBlockHash().toByteArray());
		assertEquals(block.getHeader().getPreviousHash(), bcInfo.getPreviousBlockHash());
	}

	@Test
	public void retrieveBlocks() throws Exception {
		IResultsIterator itr = store.retrieveBlocks(1);
		assertNotNull(itr);
		Common.Block block = itr.next().getObj(Common.Block.class);
		assertSame(1L, block.getHeader().getNumber());
	}

	@Test
	public void retrieveBlockByHash() throws Exception {
		Common.Block block = l.getBlockByNumber(1L);
		ByteString dataHash = block.getHeader().toByteString();
		Common.Block block1 = store.retrieveBlockByHash(Util.getHashBytes(dataHash.toByteArray()));
		assertEquals(block, block1);

		thrown.expect(LedgerException.class);
		Common.Block block2 = store.retrieveBlockByHash(null);
	}

	@Test
	public void retrieveBlockByNumber() throws Exception {
		long height = store.getBlockchainInfo().getHeight();
		block = Utils.constructDefaultBlock(l, l.getBlockByNumber(height - 1), groupID, ns);
		store.addBlock(block);
		assertEquals(block, store.retrieveBlockByNumber(height));
		block = store.retrieveBlockByNumber(-1);
		assertNull(block);
	}

	@Test
	public void retrieveTxByID() throws Exception {
		Common.Envelope evn	= store.retrieveTxByID("txID4");
		assertNotNull(evn);
		//不存在的txid
		evn = store.retrieveTxByID("txID5");
		assertNull(evn);

		thrown.expect(LedgerException.class);
		store.retrieveTxByID(null);
	}

	@Test
	public void retrieveTxByBlockNumTranNum() throws Exception {
		Common.Envelope env = store.retrieveTxByBlockNumTranNum(1, 0);
		assertNotNull(env);
		//不存在的txid
		env = store.retrieveTxByBlockNumTranNum(0, 100);
		assertNull(env);
	}

	@Test
	public void retrieveBlockByTxID() throws Exception {
		Common.Block block = store.retrieveBlockByTxID("txID4");
		assertNotNull(block);
		assertSame(1L, block.getHeader().getNumber());
		block = store.retrieveBlockByTxID("txID5");
		assertNull(block);

		thrown.expect(LedgerException.class);
		store.retrieveBlockByTxID(null);
	}

	@Test
	public void retrieveTxValidationCodeByTxID() throws Exception {
		TransactionPackage.TxValidationCode code = store.retrieveTxValidationCodeByTxID("txID4");
		assertNotNull(code);
		assertSame(TransactionPackage.TxValidationCode.VALID, code);
		code = store.retrieveTxValidationCodeByTxID("txID5");
		assertNull(code);

		thrown.expect(LedgerException.class);
		store.retrieveTxValidationCodeByTxID(null);
	}
}