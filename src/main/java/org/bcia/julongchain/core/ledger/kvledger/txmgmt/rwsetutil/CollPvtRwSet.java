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

/**
 * 操作CollPvtRwSet辅助类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class CollPvtRwSet {
    private String collectionName = null;
    private KvRwset.KVRWSet kvRwSet = null;

    public CollPvtRwSet(String collectionName, KvRwset.KVRWSet kvRwSet) {
        this.collectionName = collectionName;
        this.kvRwSet = kvRwSet;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public KvRwset.KVRWSet getKvRwSet() {
        return kvRwSet;
    }

    public void setKvRwSet(KvRwset.KVRWSet kvRwSet) {
        this.kvRwSet = kvRwSet;
    }

    /**
     * 将collPvtSet转换为proto中CollectionPvtReadWriteSet
     */
    public Rwset.CollectionPvtReadWriteSet toProtoMsg(){
        Rwset.CollectionPvtReadWriteSet.Builder builder = Rwset.CollectionPvtReadWriteSet.newBuilder()
                .setCollectionName(collectionName)
                .setRwset(kvRwSet.toByteString());
        return builder.build();
    }
}
