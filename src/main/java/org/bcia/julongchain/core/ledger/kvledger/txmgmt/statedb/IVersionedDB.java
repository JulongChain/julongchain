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

    /** GetState gets the value for given namespace and key. For a chaincode, the namespace corresponds to the chaincodeId
     *
     * @param namespace
     * @param key
     * @return
     * @throws LedgerException
     */
    VersionedValue getState(String namespace, String key) throws LedgerException;

    LedgerHeight getVersion(String namespace, String key) throws LedgerException;

    /** GetStateMultipleKeys gets the values for multiple keys in a single call
     *
     * @param namespace
     * @param keys
     * @return
     * @throws LedgerException
     */
    List<VersionedValue> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException;

    /** GetStateRangeScanIterator returns an iterator that contains all the key-values between given key ranges.
     * startKey is inclusive
     * endKey is exclusive
     * The returned IResultsIterator contains results of type *VersionedKV
     *
     * @param namespace
     * @param startKey
     * @param endKey
     * @return
     * @throws LedgerException
     */
    IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException;

    /** ExecuteQuery executes the given query and returns an iterator that contains results of type *VersionedKV.
     *
     * @param namespace
     * @param query
     * @return
     * @throws LedgerException
     */
    IResultsIterator executeQuery(String namespace, String query) throws LedgerException;

    /** ApplyUpdates applies the batch to the underlying db.
     * height is the height of the highest transaction in the Batch that
     * a state db implementation is expected to ues as a save point
     *
     * @param batch
     * @param height
     * @throws LedgerException
     */
    void applyUpdates(UpdateBatch batch, LedgerHeight height) throws LedgerException;

    /** GetLatestSavePoint returns the height of the highest transaction upto which
     * the state db is consistent
     *
     * @return
     * @throws LedgerException
     */
    LedgerHeight getLatestSavePoint() throws LedgerException;

    /** ValidateKey tests whether the key is supported by the db implementation.
     * For instance, leveldb supports any bytes for the key while the couchdb supports only valid utf-8 string
     *
     * @param key
     * @throws LedgerException
     */
    void validateKeyValue(String key, byte[] value) throws LedgerException;

    /** Open opens the db
     *
     * @throws LedgerException
     */
    void open() throws LedgerException;

    /** Close closes the db
     *
     * @throws LedgerException
     */
    void close() throws LedgerException;

    /**
     * BytesKeySuppoted implements method in VersionedDB interface
     */
    boolean bytesKeySuppoted();
}
