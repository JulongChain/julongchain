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

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class NsIterator {

    private String ns;
    private NsUpdates nsUpdates;
    private String[] storedKeys;
    private Integer nextIndex;
    private Integer lastIndex;

    /** Next gives next key and versioned value. It returns a nil when exhausted
     *
     * @return
     */
    public QueryResult next() {
        return null;
    }

    /** Close implements the method from QueryResult interface
     *
     */
    public void close() {

    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public NsUpdates getNsUpdates() {
        return nsUpdates;
    }

    public void setNsUpdates(NsUpdates nsUpdates) {
        this.nsUpdates = nsUpdates;
    }

    public String[] getStoredKeys() {
        return storedKeys;
    }

    public void setStoredKeys(String[] storedKeys) {
        this.storedKeys = storedKeys;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    public Integer getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(Integer lastIndex) {
        this.lastIndex = lastIndex;
    }
}
