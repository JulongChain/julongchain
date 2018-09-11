package org.bcia.julongchain.core.ledger.ledgermgmt;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static org.bcia.julongchain.common.ledger.util.Utils.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/09/11
 * @company Dingxuan
 */
public class LedgerManagerTest {
	String groupID = "myGroup";
	String ns = "mycc";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		rmrf(LedgerConfig.getRootPath());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void initialize() throws Exception {
		LedgerManager.initialize(null);
		assertTrue(new File(LedgerConfig.getRootPath()).exists());
		thrown.expect(Exception.class);
		LedgerManager.initialize(null);
	}

	@Test
	public void createLedger() throws Exception {
		LedgerManager.initialize(null);
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		Common.Block genesisBlock = factory.getGenesisBlock(groupID);
		INodeLedger ledger = LedgerManager.createLedger(genesisBlock);
		assertNotNull(ledger);
		assertTrue(new File(LedgerConfig.getRootPath()).exists());
		assertSame(1L, ledger.getBlockchainInfo().getHeight());
	}

	@Test
	public void openLedger() throws Exception {
		LedgerManager.initialize(null);
		thrown.expect(LedgerException.class);
		INodeLedger ledger = LedgerManager.openLedger(groupID);
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		Common.Block genesisBlock = factory.getGenesisBlock(groupID);
		LedgerManager.createLedger(genesisBlock);
		ledger = LedgerManager.openLedger(groupID);
		assertNotNull(ledger);
		assertSame(1L, ledger.getBlockchainInfo().getHeight());
	}

	@Test
	public void getLedgerIDs() throws Exception {
		LedgerManager.initialize(null);
		List<String> ledgerIDs = LedgerManager.getLedgerIDs();
		assertSame(0, ledgerIDs.size());
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		Common.Block genesisBlock = factory.getGenesisBlock(groupID);
		LedgerManager.createLedger(genesisBlock);
		ledgerIDs = LedgerManager.getLedgerIDs();
		assertSame(1, ledgerIDs.size());
		assertEquals(groupID, ledgerIDs.get(0));
	}
}