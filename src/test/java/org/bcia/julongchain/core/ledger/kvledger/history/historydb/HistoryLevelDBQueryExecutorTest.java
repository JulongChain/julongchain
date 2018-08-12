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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/31
 * @company Dingxuan
 */
public class HistoryLevelDBQueryExecutorTest {
	String ledgerID = "myGroup";
	String ns = "mycc";
	IHistoryQueryExecutor queryExecutor;

	@Before
	public void before() throws Exception{
		LedgerManager.initialize(null);
		queryExecutor = LedgerManager.openLedger(ledgerID).newHistoryQueryExecutor();
	}

	@Test
	public void testGetHistoryForKey() throws Exception{
		IResultsIterator itr = queryExecutor.getHistoryForKey(ns, "a");
		QueryResult qr = itr.next();
		KvRwset.Version result = (KvRwset.Version) qr.getObj();
		Assert.assertSame((long) 0, result.getTxNum());
		Assert.assertSame((long) 1, result.getBlockNum());
		qr = itr.next();
		result = (KvRwset.Version) qr.getObj();
		Assert.assertSame((long) 0, result.getTxNum());
		Assert.assertSame((long) 2, result.getBlockNum());
		itr = queryExecutor.getHistoryForKey(ns, "c");
		qr = itr.next();
		result = (KvRwset.Version) qr.getObj();
		Assert.assertSame((long) 0, result.getTxNum());
		Assert.assertSame((long) 1, result.getBlockNum());
		qr = itr.next();
		result = (KvRwset.Version) qr.getObj();
		Assert.assertSame((long) -1, result.getTxNum());
		Assert.assertSame((long) -1, result.getBlockNum());
	}

	@After
	public void after() throws Exception{

	}
}
