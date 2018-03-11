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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/9
 * @company Dingxuan
 */
public class UpdateBatch {
    private Map<String,NsUpdates> map = new HashMap<>();

    /** Get returns the VersionedValue for the given namespace and key
     *
     * @param ns
     * @param key
     * @return
     */
    public VersionedValue get(String ns, String key) {
        return null;
    }

    /** Put adds a VersionedKV
     *
     * @param ns
     * @param key
     * @param value
     * @param version
     */
    public void put(String ns, String key, byte[] value, Height version) {
        return;
    }

    /** Delete deletes a Key and associated value
     *
     * @param ns
     * @param key
     * @param version
     */
    public void delete(String ns, String key, Height version) {
        return;
    }

    /** Exists checks whether the given key exists in the batch
     *
     * @param ns
     * @param key
     * @return
     */
    public Boolean exists(String ns, String key) {
        return Boolean.FALSE;
    }

    /** GetUpdatedNamespaces returns the names of the namespaces that are updated
     *
     * @return
     */
    public String[] getUpdatedNamespaces() {
        return null;
    }

    /** GetUpdates returns all the updates for a namespace
     *
     * @param ns
     * @return
     */
    public Map<String,VersionedValue> getUpdates(String ns) {
        return null;
    }

    /** GetRangeScanIterator returns an iterator that iterates over keys of a specific namespace in sorted order
     * In other word this gives the same functionality over the contents in the `UpdateBatch` as
     * `VersionedDB.GetStateRangeScanIterator()` method gives over the contents in the statedb
     * This function can be used for querying the contents in the updateBatch before they are committed to the statedb.
     * For instance, a validator implementation can used this to verify the validity of a range query of a transaction
     * where the UpdateBatch represents the union of the modifications performed by the preceding valid transactions in the same block
     * (Assuming Group commit approach where we commit all the updates caused by a block together).
     */
    public ResultsIterator getRangeScanIterator(String ns, String startKey, String endKey) {
        return null;
    }

    public NsUpdates getOrCreateNsUpdates(String ns) {
        return null;
    }

    public Map<String, NsUpdates> getMap() {
        return map;
    }

    public void setMap(Map<String, NsUpdates> map) {
        this.map = map;
    }
}
