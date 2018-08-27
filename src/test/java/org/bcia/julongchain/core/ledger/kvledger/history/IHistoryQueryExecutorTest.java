package org.bcia.julongchain.core.ledger.kvledger.history;

import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
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
 * 历史查询器测试
 * 账本创建参见org.bcia.julongchain.common.ledger.util.Utils
 * 起始世界状态	key0:value0
 * 				key1:value1
 * 				key2:value2
 * 				key3:value3
 * 				中文测试:中文测试
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
		int i = 0;
		while (true) {
			IResultsIterator itr = hqe.getHistoryForKey("mycc", "key" + i);
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			KvRwset.Version version = (KvRwset.Version) next.getObj();
			assertSame((long) 1, version.getBlockNum());
			assertSame((long) i, version.getTxNum());
			i++;
		}

		i = 0;
		IResultsIterator itr = hqe.getHistoryForKey("mycc", "key0");
		while (true) {
			QueryResult next = itr.next();
			if (next == null) {
				break;
			}
			i++;
		}
		assertSame(1, i);
	}
}