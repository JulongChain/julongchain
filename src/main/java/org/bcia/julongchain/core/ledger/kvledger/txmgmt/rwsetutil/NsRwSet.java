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


import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作NsRwSet辅助类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class NsRwSet {
    private String nameSpace = null;
    private KvRwset.KVRWSet kvRwSet = null;
    private List<CollHashedRwSet> collHashedRwSets = new ArrayList<>();

    public NsRwSet(String nameSpace, KvRwset.KVRWSet kvRwSet) {
        this.nameSpace = nameSpace;
        this.kvRwSet = kvRwSet;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public KvRwset.KVRWSet getKvRwSet() {
        return kvRwSet;
    }

    public void setKvRwSet(KvRwset.KVRWSet kvRwSet) {
        this.kvRwSet = kvRwSet;
    }

    public List<CollHashedRwSet> getCollHashedRwSets() {
        return collHashedRwSets;
    }

    public void setCollHashedRwSets(List<CollHashedRwSet> collHashedRwSets) {
        this.collHashedRwSets = collHashedRwSets;
    }

    /**
     * 将NsRwSet转换为proto中NsReadWriteSet
     */
    public Rwset.NsReadWriteSet toProtoMsg(){
        Rwset.NsReadWriteSet.Builder builder = Rwset.NsReadWriteSet.newBuilder()
                    .setNamespace(nameSpace)
                    .setRwset(kvRwSet.toByteString());
        for(CollHashedRwSet collHashedRwSet : collHashedRwSets) {
            builder.addCollectionHashedRwset(collHashedRwSet.toProtoMsg());
        }
        return builder.build();
    }
}
