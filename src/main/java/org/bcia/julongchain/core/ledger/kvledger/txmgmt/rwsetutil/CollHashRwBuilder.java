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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CollectionHashRWSet
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class CollHashRwBuilder {
    private String collName;
    private Map<String, KvRwset.KVReadHash> readMap = new HashMap<>();
    private Map<String, KvRwset.KVWriteHash> writeMap = new HashMap<>();
    private byte[] pvtDataHash;

    public CollHashRwBuilder(String collName, byte[] pvtDataHash){
        this.collName = collName;
        this.pvtDataHash = pvtDataHash;
    }

    public CollHashedRwSet build(){
        List<KvRwset.KVReadHash> readSet = Util.getValuesBySortedKeys(readMap);
        List<KvRwset.KVWriteHash> writeSet = Util.getValuesBySortedKeys(writeMap);

        return new CollHashedRwSet(collName,
                ByteString.copyFrom(pvtDataHash),
                RwSetUtil.newHashedRWSet(readSet, writeSet));
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public Map<String, KvRwset.KVReadHash> getReadMap() {
        return readMap;
    }

    public void setReadMap(Map<String, KvRwset.KVReadHash> readMap) {
        this.readMap = readMap;
    }

    public Map<String, KvRwset.KVWriteHash> getWriteMap() {
        return writeMap;
    }

    public void setWriteMap(Map<String, KvRwset.KVWriteHash> writeMap) {
        this.writeMap = writeMap;
    }

    public byte[] getPvtDataHash() {
        return pvtDataHash;
    }

    public void setPvtDataHash(byte[] pvtDataHash) {
        this.pvtDataHash = pvtDataHash;
    }
}
