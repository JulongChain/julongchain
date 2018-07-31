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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.ledger.util.Util;

import java.util.List;

/**
 * namespace迭代器
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class NsIterator implements IResultsIterator {

    private String ns;
    private NsUpdates nsUpdates;
    private List<String> sortedKeys;
    private Integer nextIndex;
    private Integer lastIndex;

    public NsIterator(String ns, String startKey, String endKey, UpdateBatch batch){
        NsUpdates nsUpdates = new NsUpdates();
        nsUpdates.setMap(batch.getUpdates(ns));
        List<String> sortKeys = Util.getSortedKeys(nsUpdates.getMap());
        int nextIndex;
        int lastIndex;
        if(startKey == null || ("").equals(startKey)){
            nextIndex = 0;
        } else {
            nextIndex = sortKeys.indexOf(startKey);
        }
        if(endKey == null || ("").equals(endKey)){
            lastIndex = sortKeys.size();
        } else {
            lastIndex = sortKeys.indexOf(endKey);
        }
        this.ns = ns;
        this.nsUpdates = nsUpdates;
        this.sortedKeys = sortKeys;
        this.nextIndex = nextIndex;
        this.lastIndex = lastIndex;
    }

    /** Next gives next key and versioned value. It returns a nil when exhausted
     *
     * @return
     */
    @Override
    public QueryResult next() throws LedgerException {
        if(nextIndex >= lastIndex){
            return null;
        }
        String key = sortedKeys.get(nextIndex);
        VersionedValue vv = nsUpdates.getMap().get(key);
        nextIndex++;
        return new QueryResult(new VersionedKV(new CompositeKey(ns, key), vv));
    }

    /** Close implements the method from QueryResult interface
     *
     */
    @Override
    public void close() {
        //nothing to do
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

    public List<String> getSortedKeys() {
        return sortedKeys;
    }

    public void setSortedKeys(List<String> sortedKeys) {
        this.sortedKeys = sortedKeys;
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
