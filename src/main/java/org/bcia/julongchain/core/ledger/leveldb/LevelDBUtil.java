/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.ledger.leveldb;

import org.bcia.julongchain.common.exception.LevelDBException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.HistoryDBHelper;
import org.iq80.leveldb.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 提供操作leveldb的操作方法，如增，删，改，查
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBUtil {

	private static JavaChainLog log = JavaChainLogFactory.getLog(LevelDBUtil.class);

	/** 保存当前打开的levelDB连接 */
	private static Map<String, LevelDB> dbMap =
			Collections.synchronizedMap(new HashMap<String, LevelDB>());

	/** 获取指定路径的 level db */
	public static LevelDB getDB(String levelDbPath) throws LevelDBException {
		try {
			LevelDB db = dbMap.get(levelDbPath);
			if (db != null) {
				return db;
			}
			db = new LevelDB(new Options().createIfMissing(Boolean.TRUE), new File(levelDbPath));
			dbMap.put(levelDbPath, db);
			return db;
		} catch (Exception e) {
			throw new LevelDBException(e);
		}
	}

	/**
	 * 从map中移除db
	 *
	 * @param db 移除的db
	 */
	public static void removeDb(LevelDB db) {
		Set<Map.Entry<String, LevelDB>> entries = dbMap.entrySet();
		for (Map.Entry<String, LevelDB> entry : entries) {
			if (db == entry.getValue()) {
				dbMap.remove(entry.getKey());
			}
		}
	}

	/** 关闭 level db 连接 */
	public static void closeDB(DB db) throws LevelDBException {
		try {
			db.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new LevelDBException(e);
		}
	}

	public static void closeSnapshot(Snapshot snapshot) throws LevelDBException {
		try {
			snapshot.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new LevelDBException(e);
		}
	}

	/** 关闭 write batch 对象 */
	public static void closeWriteBatch(WriteBatch writeBatch) throws LevelDBException {
		try {
			writeBatch.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new LevelDBException(e);
		}
	}

	/**
	 * level db 中写入新的数据
	 *
	 * @param db - level db 数据库
	 * @param key - 要写入的key
	 * @param value - 要写入的value
	 */
	public static void add(DB db, byte[] key, byte[] value, boolean sync) throws LevelDBException {
		WriteOptions writeOptions = new WriteOptions().sync(sync);
		db.put(key, value, writeOptions);
	}

	/**
	 * 向level db批量写入数据
	 *
	 * @param db - 要写入的level db数据库
	 * @param map - 要批量写入的数据
	 */
	public static void add(DB db, Map<byte[], byte[]> map, boolean sync) throws LevelDBException {
		WriteBatch writeBatch = db.createWriteBatch();
		WriteOptions writeOptions = new WriteOptions().sync(sync);
		map.forEach(
				(k, v) -> {
					if (v != null) {
						writeBatch.put(k, v);
					} else {
						writeBatch.delete(k);
					}
					db.write(writeBatch, writeOptions);
				});
		closeWriteBatch(writeBatch);
	}

	/**
	 * 删除level db中的key
	 * 删除应将level db中key对应的value设置为null，否则无法删除key
	 *
	 * @param db - 要删除的level db数据库
	 * @param key - 要删除的key
	 */
	public static void delete(DB db, byte[] key, boolean sync) throws LevelDBException {
		WriteOptions writeOptions = new WriteOptions().sync(sync);
		db.delete(key, writeOptions);
	}

	/**
	 * 批量删除level db数据库中的keys
	 *
	 * @param db - 要删除keys的level db数据库
	 * @param list - 要删除的keys
	 */
	public static void delete(DB db, List<byte[]> list, boolean sync) throws LevelDBException {
		WriteBatch writeBatch = db.createWriteBatch();
		WriteOptions writeOptions = new WriteOptions().sync(sync);
		list.forEach((k) -> writeBatch.delete(k));
		db.write(writeBatch, writeOptions);
		closeWriteBatch(writeBatch);
	}

	/**
	 * 查询level db数据库的key
	 *
	 * @param db - 要查询的level db
	 * @param key - 要查询的key
	 */
	public static byte[] get(DB db, byte[] key, boolean fileCache) throws LevelDBException {
		Snapshot snapshot = db.getSnapshot();
		ReadOptions readOptions = new ReadOptions();
		readOptions.fillCache(fileCache);
		readOptions.snapshot(snapshot);
		byte[] value = db.get(key, readOptions);
		closeSnapshot(snapshot);
		return value;
	}

	/** 获取指定level db的迭代器 */
	public static DBIterator getIterator(DB db) throws LevelDBException {
		Snapshot snapshot = db.getSnapshot();
		ReadOptions readOptions = new ReadOptions().fillCache(false).snapshot(snapshot);
		DBIterator iterator = db.iterator(readOptions);
		closeSnapshot(snapshot);
		return iterator;
	}

	/**
	 * 获取以startKey开始的所有key中,其中最大的key
	 *
	 * @param db
	 * @param startKey
	 * @return
	 * @throws LevelDBException
	 */
	public static byte[] getLastKey(DB db, byte[] startKey) throws LevelDBException {
		Snapshot snapshot = db.getSnapshot();
		ReadOptions readOptions = new ReadOptions().fillCache(false).snapshot(snapshot);
		DBIterator iterator = db.iterator(readOptions);
		iterator.seek(startKey);
		byte[] lastKey = new byte[] {};
		while (iterator.hasNext()) {
			Map.Entry<byte[], byte[]> next = iterator.next();
			if (HistoryDBHelper.checkStart(next.getKey(), startKey)) {
				lastKey = next.getKey();
			} else {
				break;
			}
		}
		closeSnapshot(snapshot);
		return lastKey;
	}

	public static void main(String[] args) throws Exception {
		LevelDB db = LevelDBUtil.getDB("d:" + File.separator + "leveldb");
		LevelDBUtil.add(db, "getKey1".getBytes(), "getValue1".getBytes(), true);
		LevelDBUtil.add(db, "getKey1".getBytes(), "getValue1".getBytes(), true);
		LevelDBUtil.add(db, "getKey1".getBytes(), "getValue1".getBytes(), true);
	}
}
