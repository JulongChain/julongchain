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
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * versiondb 更新包
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class UpdateBatch {
    private Map<String, NsUpdates> updates = new HashMap<>();

    public VersionedValue get(String ns, String key){
        NsUpdates nsUpdates = updates.get(ns);
        if(nsUpdates == null){
            return null;
        }
        VersionedValue vv = nsUpdates.getMap().get(key);
        return vv;
    }

    public void put(String ns, String key, byte[] value, LedgerHeight version) throws LedgerException {
        if(value == null){
            throw new LedgerException("Null value not allow");
        }
        VersionedValue vv = new VersionedValue(version, value);
        update(ns, key, vv);
    }

    public void delete(String ns, String key, LedgerHeight version){
        VersionedValue vv = new VersionedValue(version, null);
        update(ns, key, vv);
    }

    public List<String> getUpdatedNamespaces(){
        List<String> list = new ArrayList<>();
        if (updates.keySet().size() != 0) {
            list.addAll(updates.keySet());
        }
        return list;
    }

    public void update(String ns, String key, VersionedValue vv){
        getOrCreateNsUpdates(ns).getMap().put(key, vv);
    }

    public Map<String, VersionedValue> getUpdates(String ns){
        NsUpdates nsUpdates = updates.get(ns);
        if(nsUpdates == null){
            return null;
        }
        return nsUpdates.getMap();
    }

    public IResultsIterator getRangeScanIterator(String ns, String startKey, String endKey){
        return new NsIterator(ns, startKey, endKey, this);
    }

    public NsUpdates getOrCreateNsUpdates(String ns){
        NsUpdates nsUpdates = updates.get(ns);
        if(nsUpdates == null){
            nsUpdates = new NsUpdates();
            updates.put(ns, nsUpdates);
        }
        return nsUpdates;
    }

    public boolean exists(String ns, String key){
        NsUpdates nsUpdates = updates.get(ns);
        if(nsUpdates == null){
            return false;
        }
        return nsUpdates.getMap().get(key) != null;
    }

    public Map<String, NsUpdates> getUpdates() {
        return updates;
    }

    public void setUpdates(Map<String, NsUpdates> updates) {
        this.updates = updates;
    }
}
