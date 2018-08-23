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

/**
 * 执行查询
 * Get*提供基础查询支持
 * ExecuteQuery提供丰富的查询支持
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public interface IQueryExecutor {

    /**
     * 通过namespace以及key查询State,对于SmartContract,key为scID
     */
    byte[] getState(String namespace, String key) throws LedgerException;

    /**
     * 在一次查询中,查询多个key
     */
    List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException;

    /**
     * 根据给出的namespace以及[startKey, endKey)返回查询迭代器
     */
    IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException;

    /**
     * 丰富的查询支持,leveldb暂时无法满足
     */
    IResultsIterator executeQuery(String namespace, String query) throws LedgerException;

    /**
     * 根据namespace, collection, key查询private data
     */
    byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException;

    /**
     * 在一次查询中,查询多个key
     */
    List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException;

    /**
     * 根据给出的namespace, collection以及[startKey, endKey)返回查询迭代器
     */
    IResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey)throws LedgerException;

    /**
     * 结束查询
     */
    void done();
}
