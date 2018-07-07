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

import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NamespacePublicRWset 构建
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class NsPubRwBuilder {
    private String nameSpace;
    private Map<String, KvRwset.KVRead> readMap = new HashMap<>();
    private Map<String, KvRwset.KVWrite> writeMap = new HashMap<>();
    private Map<RangeQueryKey, KvRwset.RangeQueryInfo> rangeQueriesMap = new HashMap<>();
    private List<RangeQueryKey> rangeQueryKeys = new ArrayList<>();
    private Map<String, CollHashRwBuilder> collHashRwBuilders = new HashMap<>();

    public NsPubRwBuilder(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public NsRwSet build(){
        List<KvRwset.KVRead> readSet = Util.getValuesBySortedKeys(readMap);
        List<KvRwset.KVWrite> writeSet = Util.getValuesBySortedKeys(writeMap);
        List<KvRwset.RangeQueryInfo> rangeQueriesInfo = new ArrayList<>();
        List<CollHashedRwSet> collHashedRwSet = new ArrayList<>();
        List<CollHashRwBuilder> sortedCollBuilders = Util.getValuesBySortedKeys(collHashRwBuilders);

        if (rangeQueryKeys != null) {
            for(RangeQueryKey key : rangeQueryKeys){
                rangeQueriesInfo.add(rangeQueriesMap.get(key));
            }
        }

        if (sortedCollBuilders != null) {
            for(CollHashRwBuilder collBuilder : sortedCollBuilders){
                collHashedRwSet.add(collBuilder.build());
            }
        }

        NsRwSet nsRwSet = new NsRwSet(nameSpace, RwSetUtil.newKVRWSet(readSet, writeSet, rangeQueriesInfo));
        nsRwSet.setNameSpace(nameSpace);
        nsRwSet.setCollHashedRwSets(collHashedRwSet);
        return nsRwSet;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Map<String, KvRwset.KVRead> getReadMap() {
        return readMap;
    }

    public void setReadMap(Map<String, KvRwset.KVRead> readMap) {
        this.readMap = readMap;
    }

    public Map<String, KvRwset.KVWrite> getWriteMap() {
        return writeMap;
    }

    public void setWriteMap(Map<String, KvRwset.KVWrite> writeMap) {
        this.writeMap = writeMap;
    }

    public Map<RangeQueryKey, KvRwset.RangeQueryInfo> getRangeQueriesMap() {
        return rangeQueriesMap;
    }

    public void setRangeQueriesMap(Map<RangeQueryKey, KvRwset.RangeQueryInfo> rangeQueriesMap) {
        this.rangeQueriesMap = rangeQueriesMap;
    }

    public List<RangeQueryKey> getRangeQueryKeys() {
        return rangeQueryKeys;
    }

    public void setRangeQueryKeys(List<RangeQueryKey> rangeQueryKeys) {
        this.rangeQueryKeys = rangeQueryKeys;
    }

    public Map<String, CollHashRwBuilder> getCollHashRwBuilders() {
        return collHashRwBuilders;
    }

    public void setCollHashRwBuilders(Map<String, CollHashRwBuilder> collHashRwBuilders) {
        this.collHashRwBuilders = collHashRwBuilders;
    }
}
