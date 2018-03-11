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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;

/**
 * QueryExecutor executes the queries
 * Get* methods are for supporting KV-based data model. ExecuteQuery method is for supporting a rich datamodel and query support
 *
 * ExecuteQuery method in the case of a rich data model is expected to support queries on
 * latest state, historical state and on the intersection of state and transactions
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface IQueryExecutor {

    /**
     * GetState gets the value for given namespace and key. For a chaincode, the namespace corresponds to the chaincodeId
     *
     * @param namespace
     * @param key
     * @return
     * @throws LedgerException
     */
    byte[] getState(String namespace, String key) throws LedgerException;

    /**
     * GetStateMultipleKeys gets the values for multiple keys in a single call
     *
     * @param namespace
     * @param keys
     * @return
     * @throws LedgerException
     */
    byte[][] getStateMultipleKeys(String namespace, String[] keys) throws LedgerException;

    /**
     * returns an iterator that contains all the key-values between given key ranges.
     * startKey is included in the results and endKey is excluded. An empty startKey refers to the first available key
     * and an empty endKey refers to the last available key. For scanning all the keys, both the startKey and the endKey
     * can be supplied as empty strings. However, a full scan shuold be used judiciously for performance reasons.
     * The returned ResultsIterator contains results of type *KV which is defined in protos/ledger/queryresult.
     *
     * @param namespace
     * @param startKey
     * @param endKey
     * @return
     * @throws LedgerException
     */
    ResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException;

    /**
     * executes the given query and returns an iterator that contains results of type specific to the underlying data store.
     * Only used for state databases that support query
     * For a chaincode, the namespace corresponds to the chaincodeId
     * The returned ResultsIterator contains results of type *KV which is defined in protos/ledger/queryresult.
     *
     * @param namespace
     * @param query
     * @return
     * @throws LedgerException
     */
    ResultsIterator ExecuteQuery(String namespace, String query) throws LedgerException;

    /**
     * releases resources occupied by the QueryExecutor
     *
     * @throws LedgerException
     */
    void done() throws LedgerException;
}
