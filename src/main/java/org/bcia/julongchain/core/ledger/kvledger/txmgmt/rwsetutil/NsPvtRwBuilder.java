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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * namespace private rwset 构建
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class NsPvtRwBuilder {
    private String namespace;
    private Map<String, CollPvtRwBuilder> collPvtRwBuilders = new HashMap<>();

    public NsPvtRwBuilder(String namespace) {
        this.namespace = namespace;
    }

    public NsPvtRwSet build(){
        List<CollPvtRwBuilder> sortedCollBuilders = Util.getValuesBySortedKeys(collPvtRwBuilders);
        List<CollPvtRwSet> collPvtRwSets = new ArrayList<>();

        if (sortedCollBuilders != null) {
            for(CollPvtRwBuilder builder : sortedCollBuilders){
                collPvtRwSets.add(builder.build());
            }
        }
        NsPvtRwSet nsPvtRwSet = new NsPvtRwSet(namespace);
        nsPvtRwSet.setCollPvtRwSets(collPvtRwSets);
        return nsPvtRwSet;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, CollPvtRwBuilder> getCollPvtRwBuilders() {
        return collPvtRwBuilders;
    }

    public void setCollPvtRwBuilders(Map<String, CollPvtRwBuilder> collPvtRwBuilders) {
        this.collPvtRwBuilders = collPvtRwBuilders;
    }
}
