package org.bcia.julongchain.common.ledger.blockledger.file;

import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/08/13
 * @company Dingxuan
 */
public class FileLedgerFactoryTest {
	static String dir = "/tmp/julongchain/fileLedger";
	FileLedgerFactory fileLedgerFactory;

	@BeforeClass
	public static void setUp() throws Exception {
		Utils.rmrf(dir);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getOrCreate() throws Exception {
		fileLedgerFactory = new FileLedgerFactory(dir);
		ReadWriteBase myGroup = fileLedgerFactory.getOrCreate("myGroup");
		assertNotNull(myGroup);
	}

	@Test
	public void groupIDs() throws Exception {
		fileLedgerFactory = new FileLedgerFactory(dir);
		List<String> groupIDs = fileLedgerFactory.groupIDs();
		assertNotNull(groupIDs);
		fileLedgerFactory.getOrCreate("myGroup");
		assertSame(1, fileLedgerFactory.groupIDs().size());
		assertEquals("myGroup", fileLedgerFactory.groupIDs().get(0));
	}
}