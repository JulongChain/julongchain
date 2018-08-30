package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.protos.common.Common;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/29
 * @company Dingxuan
 */
public class IHistoryDBProviderTest {
	static INodeLedger l;
	static Common.Block block;
	static IHistoryQueryExecutor hqe;
	static IHistoryDBProvider provider;

	@BeforeClass
	public static void setUp() throws Exception {
		provider = new HistoryLevelDBProvider();
	}


	@Test
	public void getDBHandle() throws Exception{
		IHistoryDB db = provider.getDBHandle("myGroup");
		assertNotNull(db);
		assertEquals(db, provider.getDBHandle("myGroup"));
	}
}