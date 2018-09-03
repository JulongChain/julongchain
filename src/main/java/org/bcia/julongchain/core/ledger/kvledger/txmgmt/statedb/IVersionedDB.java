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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.List;

/**
 * VersionDB接口
 *
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public interface IVersionedDB{

	/**
	 * 获取当前世界状态
	 * @param namespace	命名空间
	 * @param key	key
	 */
    VersionedValue getState(String namespace, String key) throws LedgerException;

	/**
	 * 获取key所在区块号及交易号
	 */
    LedgerHeight getHeight(String namespace, String key) throws LedgerException;

    /**
	 * 批量查询世界状态
     */
    List<VersionedValue> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException;

    /**
	 * 获取世界状态查询迭代器
     */
    IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException;

    /**
	 * 执行查询语句
	 * TODO: 暂不支持
     */
    IResultsIterator executeQuery(String namespace, String query) throws LedgerException;

    /**
	 * 执行提交
     */
    void applyUpdates(UpdateBatch batch, LedgerHeight height) throws LedgerException;

    /**
	 * 获取最后保存点
     */
    LedgerHeight getLatestSavePoint() throws LedgerException;

    /**
	 * 检验K-V
     */
    void validateKeyValue(String key, byte[] value) throws LedgerException;

    /**
	 * 打开DB
     */
    void open() throws LedgerException;

	/**
	 * 关闭
	 */
	void close() throws LedgerException;

    /**
	 * 是否支持字节形式key
	 * LevelDB	true
	 * CouchDB	false
     */
    boolean bytesKeySuppoted();
}
