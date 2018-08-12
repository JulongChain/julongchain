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
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

/**
 * 操作CollHashedRwSet辅助类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class CollHashedRwSet {
    private String collectionName = null;
    private KvRwset.HashedRWSet hashedRwSet = null;
    private ByteString pvtRwSetHash = null;

    public CollHashedRwSet(String collectionName, ByteString pvtRwSetHash, KvRwset.HashedRWSet hashedRwSet) {
        this.collectionName = collectionName;
        this.hashedRwSet = hashedRwSet;
        this.pvtRwSetHash = pvtRwSetHash;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public KvRwset.HashedRWSet getHashedRwSet() {
        return hashedRwSet;
    }

    public void setHashedRwSet(KvRwset.HashedRWSet hashedRwSet) {
        this.hashedRwSet = hashedRwSet;
    }

    public ByteString getPvtRwSetHash() {
        return pvtRwSetHash;
    }

    public void setPvtRwSetHash(ByteString pvtRwSetHash) {
        this.pvtRwSetHash = pvtRwSetHash;
    }

    /**
     * 将CollHashedRwSet转换为proto中CollectionHashedReadWriteSet
     */
    public Rwset.CollectionHashedReadWriteSet toProtoMsg(){
        Rwset.CollectionHashedReadWriteSet.Builder builder = Rwset.CollectionHashedReadWriteSet.newBuilder()
                .setCollectionName(collectionName)
                .setPvtRwsetHash(pvtRwSetHash)
                .setHashedRwset(hashedRwSet.toByteString());
        return builder.build();
    }
}
