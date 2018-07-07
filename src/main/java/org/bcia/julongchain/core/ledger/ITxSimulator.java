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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;

import java.util.List;
import java.util.Map;

/**
 * 模拟交易执行但不改变世界状态
 * Set*提供基本的操作
 * executeUPdate提供丰富的操作
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface ITxSimulator extends IQueryExecutor {

    /**
     * 根据key和namespace(对于SmartContract为scID), 修改value
     */
    void setState(String namespace, String key, byte[] value) throws LedgerException;

    /**
     * 根据namespace和key删除
     */
    void deleteState(String namespace, String key) throws LedgerException;

    /**
     * 批量更新
     */
    void setStateMultipleKeys(String namespace, Map<String, byte[]> kvs) throws LedgerException;

    /**
     * 提供丰富修改功能
     * leveldb不支持
     */
    void executeUpdate(String query) throws LedgerException;

    /**
     * 获得模拟交易结果
     */
    TxSimulationResults getTxSimulationResults() throws LedgerException;

    /**
     * 设置private data
     */
    void setPrivateData(String namespace, String collection, String key, byte[] value) throws LedgerException;

    /**
     * 批量设置private data
     */
    void setPirvateDataMultipleKeys(String namespace, String collection, Map<String, byte[]> kvs) throws LedgerException;

    /**
     * 删除private data
     */
    void deletePrivateData(String namespace, String collection, String key) throws LedgerException;

	@Override
	byte[] getState(String namespace, String key) throws LedgerException;

	@Override
	List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException;

	@Override
	IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException;

	@Override
	IResultsIterator executeQuery(String namespace, String query) throws LedgerException;

	@Override
	byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException;

	@Override
	List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException;

	@Override
	IResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException;

	@Override
	void done();
}
