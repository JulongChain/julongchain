/*
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;

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

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
		//存在的txID
		for (int i = 0; i < 4; i++) {
			TransactionPackage.ProcessedTransaction tx = l.getTransactionByID("txID" + i);
			assertNotNull(tx);
		}
		//不存在的txID
		TransactionPackage.ProcessedTransaction notExists = l.getTransactionByID("not exists");
		assertNull(notExists);
	}

	@Test
	public void getBlockByHash() throws Exception {
		//存在的block hash
		for (int i = 0; i < 2; i++) {
			block = l.getBlockByNumber(i);
			byte[] hashBytes = block.getHeader().getDataHash().toByteArray();
			assertEquals(block, l.getBlockByHash(hashBytes));
		}
		//不存在的block hash
		Common.Block notExists = l.getBlockByHash("not exists".getBytes(StandardCharsets.UTF_8));
		assertNull(notExists);
	}

	@Test
	public void getBlockByTxID() throws Exception {
		//存在的txID
		for (int i = 0; i < 4; i++) {
			Common.Block block = l.getBlockByTxID("txID" + i);
			assertNotNull(block);
			Common.Block block1 = l.getBlockByNumber(1);
			assertEquals(block, block1);
		}
		//不存在的txID
		Common.Block notExists = l.getBlockByTxID("not exists");
		assertNull(notExists);
	}

	@Test
	public void getTxValidationCodeByTxID() throws Exception {
		//存在的txID
		for (int i = 0; i < 4; i++) {
			TransactionPackage.TxValidationCode code = l.getTxValidationCodeByTxID("txID" + i);
			assertEquals(TransactionPackage.TxValidationCode.VALID, code);
		}
		//不存在的txID
		TransactionPackage.TxValidationCode notExists = l.getTxValidationCodeByTxID("not exists");
		assertNull(notExists);
	}

	@Test
	public void newTxSimulator() throws Exception {
		ITxSimulator simulator = l.newTxSimulator("txID");
		assertNotNull(simulator);
		ITxSimulator simulator1 = l.newTxSimulator("txID");
		ITxSimulator simulator2 = l.newTxSimulator("not same");
		assertEquals(simulator, simulator1);
		assertNotEquals(simulator, simulator2);
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
		//正确区块提交
		long height = INodeLedgerTest.l.getBlockchainInfo().getHeight();
		Common.Block block = constructDefaultBlock(INodeLedgerTest.l, INodeLedgerTest.l.getBlockByNumber(height - 1), "myGroup", "mycc");
		INodeLedgerTest.l.commitWithPvtData(new BlockAndPvtData(block, null, null));
		assertSame(++height, INodeLedgerTest.l.getBlockchainInfo().getHeight());
		//错误区块提交
		block = constructDefaultBlock(l, l.getBlockByNumber(height - 2), "myGroup", "mycc");
		thrown.expect(LedgerException.class);
		l.commitWithPvtData(new BlockAndPvtData(block, null, null));
	}
}