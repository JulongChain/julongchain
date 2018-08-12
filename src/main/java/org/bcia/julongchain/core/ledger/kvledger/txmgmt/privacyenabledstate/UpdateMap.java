/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.HashMap;
import java.util.Map;

/**
 * namespace-nsBatch map
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class UpdateMap {
    private Map<String, NsBatch> map = new HashMap<>();

    public boolean isEmpty(){
       return map.isEmpty();
    }

    public void put(String ns, String coll, String key, byte[] value, LedgerHeight version) throws LedgerException {
        getOrCreateNsBatch(ns).getBatch().put(coll, key, value, version);
    }

    public void delete(String ns, String coll, String key, LedgerHeight version){
        getOrCreateNsBatch(ns).getBatch().delete(coll, key, version);
    }

    public VersionedValue get(String ns, String coll, String key){
        NsBatch nsPvtBatch = map.get(ns);
        if(nsPvtBatch == null){
            return null;
        }
        return nsPvtBatch.getBatch().get(coll, key);
    }

    public NsBatch getOrCreateNsBatch(String ns){
        NsBatch batch = map.get(ns);
        if(batch == null){
            batch = new NsBatch();
            map.put(ns, batch);
        }
        return batch;
    }

    public Map<String, NsBatch> getMap() {
        return map;
    }

    public void setMap(Map<String, NsBatch> map) {
        this.map = map;
    }
}
