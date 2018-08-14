package org.bcia.julongchain.core.ledger.kvledger.history;

import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.bcia.julongchain.common.ledger.util.Utils.*;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/14
 * @company Dingxuan
 */
public class IHistoryQueryExecutorTest {
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

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getHistoryForKey() throws Exception{
		for (int i = 0; i < 4; i++) {
			IResultsIterator itr = hqe.getHistoryForKey("mycc", "key" + i);
			KvRwset.Version version = (KvRwset.Version) itr.next().getObj();
			assertSame((long) 1, version.getBlockNum());
			assertSame((long) i, version.getTxNum());
		}
	}

	@Test
	public void getLastHistoryForKey() throws Exception{
		for (int i = 0; i < 3; i++) {
			IResultsIterator itr = hqe.getLastHistoryForKey("mycc", "key" + i);
			KvRwset.Version version = (KvRwset.Version) itr.next().getObj();
			assertSame((long) 1, version.getBlockNum());
			assertSame((long) i + 1, version.getTxNum());
		}
	}
}