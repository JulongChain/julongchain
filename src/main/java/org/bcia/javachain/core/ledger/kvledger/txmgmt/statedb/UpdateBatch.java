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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/9
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

    public void put(String ns, String key, byte[] value, Height version) throws LedgerException {
        if(value == null){
            throw new LedgerException("Null value not allow");
        }
        VersionedValue vv = new VersionedValue();
        vv.setValue(value);
        vv.setVersion(version);
        update(ns, key, vv);
    }

    public void delete(String ns, String key, Height version){
        VersionedValue vv = new VersionedValue();
        vv.setValue(null);
        vv.setVersion(version);
        update(ns, key, vv);
    }

    public List<String> getUpdatedNamespaces(){
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, NsUpdates> entry : updates.entrySet()){
            list.add(entry.getKey());
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

    public ResultsIterator getRangeScanIterator(String ns, String startKey, String endKey){
        return NsIterator.newNsIterator(ns, startKey, endKey, this);
    }

    public NsUpdates getOrCreateNsUpdates(String ns){
        NsUpdates nsUpdates = updates.get(ns);
        if(ns == null){
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
