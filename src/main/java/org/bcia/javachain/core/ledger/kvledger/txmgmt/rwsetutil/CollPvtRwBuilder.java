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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil;

import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class CollPvtRwBuilder {
    private String collectionName;
    private Map<String, KvRwset.KVWrite> writeMap = new HashMap<>();

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<String, KvRwset.KVWrite> getWriteMap() {
        return writeMap;
    }

    public void setWriteMap(Map<String, KvRwset.KVWrite> writeMap) {
        this.writeMap = writeMap;
    }
}
