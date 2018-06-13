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

import java.util.ArrayList;
import java.util.List;

/**
 * 操作NsPvtRwSet辅助类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class NsPvtRwSet {
    private String nameSpace = null;
    private List<CollPvtRwSet> collPvtRwSets = new ArrayList<>();

    public NsPvtRwSet(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public List<CollPvtRwSet> getCollPvtRwSets() {
        return collPvtRwSets;
    }

    public void setCollPvtRwSets(List<CollPvtRwSet> collPvtRwSets) {
        this.collPvtRwSets = collPvtRwSets;
    }

    /**
     * 将NsPvtRwSet转换为proto中NsPvtReadWriteSet
     */
    public Rwset.NsPvtReadWriteSet toProtoMsg(){
        Rwset.NsPvtReadWriteSet.Builder builder = Rwset.NsPvtReadWriteSet.newBuilder()
                .setNamespace(nameSpace);
        Rwset.CollectionPvtReadWriteSet collPvtRwSetProtoMsg = null;
        for(CollPvtRwSet collPvtRwSet : collPvtRwSets){
            collPvtRwSetProtoMsg = collPvtRwSet.toProtoMsg();
            builder.addCollectionPvtRwset(collPvtRwSetProtoMsg);
        }
        return builder.build();
    }
}
