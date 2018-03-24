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

import java.util.Map;

/**
 * TxSimulator simulates a transaction on a consistent snapshot of the 'as recent state as possible'
 * Set* methods are for supporting KV-based data model. ExecuteUpdate method is for supporting a rich datamodel and query support
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface ITxSimulator extends IQueryExecutor {

    /**
     * SetState sets the given value for the given namespace and key. For a chaincode, the namespace corresponds to the chaincodeId
     *
     * @param namespace
     * @param key
     * @param value
     * @throws LedgerException
     */
    void setState(String namespace, String key, byte[] value) throws LedgerException;

    /**
     * DeleteState deletes the given namespace and key
     *
     * @param namespace
     * @param key
     * @throws LedgerException
     */
    void deleteState(String namespace, String key) throws LedgerException;

    /**
     * SetMultipleKeys sets the values for multiple keys in a single call
     *
     * @param namespace
     * @param kvs
     * @throws LedgerException
     */
    void setStateMultipleKeys(String namespace, Map<String, byte[]> kvs) throws LedgerException;

    /**
     * ExecuteUpdate for supporting rich data model (see comments on QueryExecutor above)
     *
     * @param query
     * @throws LedgerException
     */
    void executeUPdate(String query) throws LedgerException;

    /**
     * GetTxSimulationResults encapsulates the results of the transaction simulation.
     * This should contain enough detail for
     * - The update in the state that would be caused if the transaction is to be committed
     * - The environment in which the transaction is executed so as to be able to decide the validity of the environment
     * (at a later time on a different peer) during committing the transactions
     * Different ledger implementation (or configurations of a single implementation) may want to represent the above two pieces
     * of information in different way in order to support different data-models or optimize the information representations.
     *
     * @param bytes
     * @throws LedgerException
     */
//    void getTxSimulationResults(byte[] bytes) throws LedgerException;

    /**
     * 获得模拟交易结果
     *
     * @return
     * @throws LedgerException
     */
    TxSimulationResults getTxSimulationResults() throws LedgerException;

}
