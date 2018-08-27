package org.bcia.julongchain.core.ledger.kvledger;

import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.bcia.julongchain.common.ledger.util.Utils.*;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/16
 * @company Dingxuan
 */
public class IdStoreTest {
	static INodeLedger l;
	static IdStore idStore;

	@BeforeClass
	public static void setUp() throws Exception {
		//重置目录
		rmrf(LedgerConfig.getRootPath());
		//初始化账本
		l = constructDefaultLedger();
		idStore = IdStore.openIDStore();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void openIDStore() throws Exception {
		idStore = IdStore.openIDStore();
		assertNotNull(idStore);
	}

	@Test
	public void ledgerIDExists() throws Exception {
		boolean exists = idStore.ledgerIDExists("exists");
		assertFalse(exists);
		exists = idStore.ledgerIDExists("myGroup");
		assertTrue(exists);
	}

	@Test
	public void getAllLedgerIDs() throws Exception {
		List<String> ledgerIDs = idStore.getAllLedgerIDs();
		assertSame(1, ledgerIDs.size());
		assertEquals("myGroup", ledgerIDs.get(0));
	}
}