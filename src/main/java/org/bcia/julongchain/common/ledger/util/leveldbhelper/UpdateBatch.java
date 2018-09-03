/*
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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 批量操作更新包
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class UpdateBatch  {
	private static JulongChainLog log = JulongChainLogFactory.getLog(UpdateBatch.class);

	private Map<byte[],byte[]> kvs = new HashMap<>();

	/**
	 * 添加K-V
	 */
	public void put(byte[] key, byte[] value) {
//		if(value == null){
//			log.error("Can not put [null] value into update batch");
//			throw new RuntimeException("Can not put [null] value into update batch");
//		}
		kvs.put(key, value);
	}

	/**
	 * 删除K-V
	 */
	public void delete(byte[] key) {
		kvs.put(key, null);
	}

	/**
	 * 批量添加K-V
	 */
	public void addAll(UpdateBatch updateBatch, String ledgerID){
		if(ledgerID == null){
			kvs = updateBatch.getKvs();
		} else {
			byte[] b = ArrayUtils.addAll(ledgerID.getBytes(StandardCharsets.UTF_8), new byte[]{0x00});
			updateBatch.getKvs().forEach((k, v) -> kvs.put(LevelDBProvider.constructLevelKey(ledgerID, k), v));
		}

	}

	public Map<byte[], byte[]> getKvs() {
		return kvs;
	}

	public void setKvs(Map<byte[], byte[]> kvs) {
		this.kvs = kvs;
	}
}