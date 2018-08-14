package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.bcia.julongchain.common.ledger.util.Utils.*;
import static org.bcia.julongchain.core.ledger.util.Util.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/14
 * @company Dingxuan
 */
public class INodeLedgerTest {
	static INodeLedger l;
	static Common.Block block;

	@BeforeClass
	public static void setUp() throws Exception {
		//重置目录
		rmrf(LedgerConfig.getRootPath());
		//初始化账本
		l = constructDefaultLedger();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getTransactionByID() throws Exception{
		for (int i = 0; i < 4; i++) {
			TransactionPackage.ProcessedTransaction tx = l.getTransactionByID("txID" + i);
			assertNotNull(tx);
		}
	}

	@Test
	public void getBlockByHash() throws Exception {
		for (int i = 0; i < 2; i++) {
			block = l.getBlockByNumber(i);
			byte[] hashBytes = block.getHeader().getDataHash().toByteArray();
			assertEquals(block, l.getBlockByHash(hashBytes));
		}
	}

	@Test
	public void getBlockByTxID() throws Exception {
		for (int i = 0; i < 4; i++) {
			Common.Block block = l.getBlockByTxID("txID" + i);
			assertNotNull(block);
			Common.Block block1 = l.getBlockByNumber(1);
			assertEquals(block, block1);
		}
	}

	@Test
	public void getTxValidationCodeByTxID() throws Exception {
		for (int i = 0; i < 4; i++) {
			TransactionPackage.TxValidationCode code = l.getTxValidationCodeByTxID("txID" + i);
			assertEquals(TransactionPackage.TxValidationCode.VALID, code);
		}
	}

	@Test
	public void newTxSimulator() throws Exception {
		ITxSimulator simulator = l.newTxSimulator("txID");
		assertNotNull(simulator);
	}

	@Test
	public void newQueryExecutor() throws Exception {
		IQueryExecutor qe = l.newQueryExecutor();
		assertNotNull(qe);
	}

	@Test
	public void newHistoryQueryExecutor() throws Exception {
		IHistoryQueryExecutor he = l.newHistoryQueryExecutor();
		assertNotNull(he);
	}

	@Test
	public void getLedgerID() {
		String ledgerID = l.getLedgerID();
		assertEquals(ledgerID, "myGroup");
	}

	@Test
	public void commitWithPvtData() throws Exception{
		Common.Block block = constructDefaultBlock(l, l.getBlockByNumber(1), "myGroup", "mycc");
		l.commitWithPvtData(new BlockAndPvtData(block, null, null));
		assertSame(((long) 3), l.getBlockchainInfo().getHeight());
	}
}