/**
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
package org.bcia.julongchain.common.ledger.util.leveldbhelper;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LevelDBException;
import org.bcia.julongchain.common.ledger.util.IDBHandler;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.*;

/**
 * 提供操作leveldb的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBProvider implements IDBProvider {

	private static JavaChainLog logger = JavaChainLogFactory.getLog(LevelDBProvider.class);

	private IDBHandler db = null;
	private static byte[] DB_NAME_KEY_SEP = new byte[]{0x00};
	private static byte[] DB_LEDGERID_KEY_SEP = new byte[]{0x03};
	private String dbPath = null;
	private String ledgerID = null;
	static Map<String, IDBProvider> dbs = new HashMap<>();

	public LevelDBProvider(String dbPath) throws LevelDBException {
		this.dbPath = dbPath;
		db = new LevelDBHandler();
		db.createDB(dbPath);
	}

	public IDBProvider getDBHandle(String ledgerID) throws LevelDBException{
		IDBProvider db = dbs.get(ledgerID + dbPath);
		if(db == null) {
			db = new LevelDBProvider(dbPath);
			db.setLedgerID(ledgerID);
			dbs.put(ledgerID + dbPath, db);
		}
		return db;
	}

	@Override
	public String getDBPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	@Override
	public IDBHandler getDb() {
		return db;
	}

	public void setDb(IDBHandler db) {
		this.db = db;
	}

	@Override
	public void close() throws LevelDBException {
		db.close();
	}

	@Override
	public byte[] get(byte[] key) throws LevelDBException {
		return db.get(constructLevelKey(ledgerID, key));
	}

	@Override
	public void put(byte[] key, byte[] value, boolean sync) throws LevelDBException {
		db.put(constructLevelKey(ledgerID, key), value, sync);
	}

	@Override
	public void delete(byte[] key, boolean sync) throws LevelDBException {
		db.put(constructLevelKey(ledgerID, key), null, sync);
	}

	@Override
	public void writeBatch(UpdateBatch batch, boolean sync) throws LevelDBException {
		UpdateBatch b = new UpdateBatch();
		b.addAll(batch, ledgerID);
		db.writeBatch(b, sync);
	}

	@Override
	public Iterator<Map.Entry<byte[], byte[]>> getIterator(byte[] startKey) throws LevelDBException {
		return db.getIterator(constructLevelKey(ledgerID, startKey));
	}

	public static byte[] constructLevelKey(String ledgerID, byte[] key) {
		if (ledgerID == null) {
			return key;
		}
		byte[] arr = ArrayUtils.addAll(ledgerID.getBytes(), DB_LEDGERID_KEY_SEP);
		arr = ArrayUtils.addAll(arr, key);
		return arr;
	}

	public byte[] retrieveAppKey(byte[] levelKey) {
		if (levelKey == null) {
			return null;
		}
		String str = new String(levelKey);
		String follow = null;
		int start = 0;
		while(str.indexOf(new String(DB_NAME_KEY_SEP), start) != -1){
			start++;
		}
		follow = str.substring(start, str.length());
		return follow.getBytes();
	}

	@Override
	public String getLedgerID() {
		return ledgerID;
	}

	@Override
	public void setLedgerID(String ledgerID) {
		this.ledgerID = ledgerID;
	}
}