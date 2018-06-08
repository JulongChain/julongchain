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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

/**
 * 封装查询namespace、key
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class CompositeKey {

    private String namespace;
    private String key;

    public CompositeKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        CompositeKey cKey = null;
        try {
            cKey = (CompositeKey) obj;
        } catch (Exception e) {
            return false;
        }
        if(namespace == null){
            if(cKey.getNamespace() != null){
                return false;
            }
        } else {
            if(!namespace.equals(cKey.getNamespace())){
                return false;
            }
        }
        if (key == null) {
            return cKey.getKey() == null;
        } else {
            return key.equals(cKey.getKey());
        }
    }

    @Override
    public int hashCode() {
        return (namespace + key).hashCode();
    }
}
