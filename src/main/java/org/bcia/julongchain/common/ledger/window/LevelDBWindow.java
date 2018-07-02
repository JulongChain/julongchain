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
package org.bcia.julongchain.common.ledger.window;

import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;

import java.util.Iterator;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/06/28
 * @company Dingxuan
 */
public class LevelDBWindow {
	private final static int HISTORY_LEVELDB = 0;
	private final static int INDEX_LEVELDB = 1;
	private final static int STATE_LEVELDB = 2;
	private final static int PVT_DATA_LEVELDB = 3;

	public static void main(String[] args) throws Exception {
					//数据库类型
		new LevelDBWindow().soutLevelDB(STATE_LEVELDB,
					//kv数据库中起始key, null为全部
					null,
					//展示new String(key)
					true,
					//每行展示key byte[]长度
					-1,
					//展示new String(value)
					false,
					//每行展示value byte[]长度
					-1
		);
	}

	private void soutLevelDB(int levelDBType, byte[] startKey, boolean keyString, int keyBytes, boolean valueString, int valueBytes) throws Exception {
		LevelDBProvider provider = getLevelDBProvider(levelDBType);
		System.out.println(provider.getDBPath());

		Iterator<Map.Entry<byte[], byte[]>> itr = provider.getIterator(startKey);
		while (itr.hasNext()) {
			Map.Entry<byte[], byte[]> entry = itr.next();
			if (keyString) {
				System.out.println(new String(entry.getKey()));
			}
			if (keyBytes > 0) {
				WindowUtil.soutByte(entry.getKey(), keyBytes);
			}
			if (valueString) {
				System.out.println(new String(entry.getValue()));
			}
			if (valueBytes > 0) {
				WindowUtil.soutByte(entry.getValue(), valueBytes);
			}
			System.out.println("_____________________________________");
		}
	}

	private LevelDBProvider getLevelDBProvider(int levelDBType) throws Exception {
		switch (levelDBType) {
			case HISTORY_LEVELDB:
				return new LevelDBProvider(LedgerConfig.getHistoryLevelDBPath());
			case INDEX_LEVELDB:
				return new LevelDBProvider(LedgerConfig.getIndexPath());
			case STATE_LEVELDB:
				return new LevelDBProvider(LedgerConfig.getStateLevelDBPath());
			case PVT_DATA_LEVELDB:
				return new LevelDBProvider(LedgerConfig.getPvtDataStorePath());
			default:
				throw new Exception("Wrong db type");
		}
	}
}
