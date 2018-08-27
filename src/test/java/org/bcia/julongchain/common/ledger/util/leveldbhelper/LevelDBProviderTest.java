package org.bcia.julongchain.common.ledger.util.leveldbhelper;

import org.bcia.julongchain.common.ledger.util.IDBHandler;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;
import static org.bcia.julongchain.common.ledger.util.Utils.*;

/**
 *
 * 使用
 * http://192.168.1.165:3000/
 * 查看leveldb
 *
 * @author sunzongyu
 * @date 2018/08/20
 * @company Dingxuan
 */
public class LevelDBProviderTest {
	static LevelDBProvider provider;
	static String dir = "/tmp/julongchain/levledb";
	static String groupID = "myGroup";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		rmrf(dir);
		provider = new LevelDBProvider(dir);
		provider.put("a".getBytes(), "a".getBytes(), true);
		provider.setLedgerID(groupID);
		provider.put("b".getBytes(), "b".getBytes(), true);
		provider.setLedgerID(null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getDBHandle() throws Exception {
		IDBProvider db = provider.getDBHandle("myGroup");
		assertNotNull(db);
		assertEquals(db, provider.getDBHandle("myGroup"));
	}

	@Test
	public void getDBPath() {
		String dbPath = provider.getDBPath();
		assertEquals(dir, dbPath);
	}

	@Test
	public void getDb() {
		IDBHandler db = provider.getDb();
		assertEquals(dir, db.getDbName());
	}

	@Test
	public void get() throws Exception{
		byte[] a;
		byte[] b;
		a = provider.get("a".getBytes());
		b = provider.get("b".getBytes());
		assertNotNull(a);
		assertNull(b);

		provider.setLedgerID(groupID);
		a = provider.get("a".getBytes());
		b = provider.get("b".getBytes());
		assertNull(a);
		assertNotNull(b);
	}

	@Test
	public void put() throws Exception {
		byte[] c;
		byte[] d;
		c = provider.get("c".getBytes());
		assertNull(c);
		provider.put("c".getBytes(), "c".getBytes(), true);
		c = provider.get("c".getBytes());
		assertNotNull(c);

		provider.setLedgerID(groupID);
		d = provider.get("d".getBytes());
		assertNull(d);
		provider.put("d".getBytes(), "d".getBytes(), true);
		d = provider.get("d".getBytes());
		assertNotNull(d);
	}

	@Test
	public void delete() throws Exception {
		byte[] a;
		byte[] b;
		a = provider.get("a".getBytes());
		assertNotNull(a);
		provider.delete("a".getBytes(), true);
		a = provider.get("a".getBytes());
		assertNull(a);

		provider.setLedgerID(groupID);
		b = provider.get("b".getBytes());
		assertNotNull(b);
		provider.delete("b".getBytes(), true);
		b = provider.get("b".getBytes());
		assertNull(b);
	}

	@Test
	public void writeBatch() throws Exception {
		byte[] a;
		byte[] b;
		byte[] c;
		byte[] d;
		UpdateBatch updateBatch;

		a = provider.get("a".getBytes());
		c = provider.get("c".getBytes());
		assertNotNull(a);
		assertNull(c);
		updateBatch = new UpdateBatch();
		updateBatch.delete("a".getBytes());
		updateBatch.put("c".getBytes(), "c".getBytes());
		provider.writeBatch(updateBatch, true);
		a = provider.get("a".getBytes());
		c = provider.get("c".getBytes());
		assertNull(a);
		assertNotNull(c);

		provider.setLedgerID(groupID);
		b = provider.get("b".getBytes());
		d = provider.get("d".getBytes());
		assertNotNull(b);
		assertNull(d);
		updateBatch = new UpdateBatch();
		updateBatch.delete("b".getBytes());
		updateBatch.put("d".getBytes(), "d".getBytes());
		provider.writeBatch(updateBatch, true);
		b = provider.get("b".getBytes());
		d = provider.get("d".getBytes());
		assertNull(b);
		assertNotNull(d);
	}

	@Test
	public void getIterator() throws Exception {
		Iterator<Map.Entry<byte[], byte[]>> iterator;
		provider = new LevelDBProvider(LedgerConfig.getStateLevelDBPath());
		iterator = provider.getIterator(null);
		while (iterator.hasNext()) {
			Map.Entry<byte[], byte[]> next = iterator.next();
			String key = new String(next.getKey(), StandardCharsets.UTF_8);
			System.out.println(Arrays.toString(next.getKey()));
		}
	}

	@Test
	public void constructLevelKey() {

		String a = "{'args':['save','{\"vouchers\":[{\"accessory_num\":65195296,\"audit_date\":\"20140707\",\"audit_user\":\"m481ow3vc0\",\"cover_voucher\":72644016,\"id_auto\":0,\"is_covered\":1,\"keep_date\":\"20140707\",\"keep_user\":\"1jhaoqfxos\",\"make_date\":\"20140707\",\"make_user\":\"55mf4l2tzl\",\"make_user_name\":\"g6rymcx5j9\",\"manage_user\":\"zhq7fo3x2i\",\"og_code\":\"nmscmqplax\",\"set_code\":\"hzjzp66oea\",\"set_code_name\":\"栖霞酒店\",\"sum_money\":8804.09,\"voucherDetails\":[{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2013},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2014},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2015}],\"voucher_id\":13270995,\"voucher_no\":7942865,\"voucher_status\":0,\"voucher_type\":44228848,\"year\":2014},{\"accessory_num\":4590682,\"audit_date\":\"20171020\",\"audit_user\":\"p7a2bbk9o4\",\"cover_voucher\":29097086,\"id_auto\":0,\"is_covered\":1,\"keep_date\":\"20171020\",\"keep_user\":\"d5iikhr5zl\",\"make_date\":\"20171020\",\"make_user\":\"ctv9drmsm6\",\"make_user_name\":\"neu4bpeh3c\",\"manage_user\":\"x1mglfpp93\",\"og_code\":\"k4jloak0b9\",\"set_code\":\"fh7qylzat7\",\"set_code_name\":\"晨风幼儿园\",\"sum_money\":3983.16,\"voucherDetails\":[{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2013},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2014},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2015}],\"voucher_id\":65574955,\"voucher_no\":87233454,\"voucher_status\":0,\"voucher_type\":26151002,\"year\":2017},{\"accessory_num\":78846248,\"audit_date\":\"20110417\",\"audit_user\":\"899m0642yo\",\"cover_voucher\":78879329,\"id_auto\":1,\"is_covered\":1,\"keep_date\":\"20110417\",\"keep_user\":\"5uq5v938fc\",\"make_date\":\"20110417\",\"make_user\":\"og1a1aigpt\",\"make_user_name\":\"96ibx4afkk\",\"manage_user\":\"6aaaqnxhr2\",\"og_code\":\"4g5zs5wwt3\",\"set_code\":\"sm12fynjd2\",\"set_code_name\":\"云亭旅馆\",\"sum_money\":578.28,\"voucherDetails\":[{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2013},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2014},{\"assistant_id\":0,\"detail_id\":0,\"voucher_id\":0,\"year\":2015}],\"voucher_id\":28004796,\"voucher_no\":97171060,\"voucher_status\":1,\"voucher_type\":82811917,\"year\":2011}]}']}";
		System.out.println(a);
	}

	@Test
	public void retrieveAppKey() {
	}

	@Test
	public void getLedgerID() {
	}

	@Test
	public void setLedgerID() {
	}
}