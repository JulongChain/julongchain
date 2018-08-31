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

/**
 * 组合key的hash
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class HashedCompositeKey {
    private String namespace;
    private String collectionName;
    private String keyHash;

    public HashedCompositeKey() {
    }

    public HashedCompositeKey(String namespace, String collectionName, String keyHash) {
        this.namespace = namespace;
        this.collectionName = collectionName;
        this.keyHash = keyHash;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    @Override
    public boolean equals(Object obj) {
        HashedCompositeKey hcKey;
        try {
            hcKey = (HashedCompositeKey) obj;
        } catch (Exception e) {
            return false;
        }
        if (namespace==null) {
            if (hcKey.getNamespace() != null) {
                return false;
            }
        } else {
            if (!namespace.equals(hcKey.getNamespace())) {
                return false;
            }
        }
        if (collectionName==null) {
            if (hcKey.getCollectionName() != null) {
                return false;
            }
        } else {
            if (!collectionName.equals(hcKey.getCollectionName())) {
                return false;
            }
        }
        if (keyHash==null) {
            return hcKey.getKeyHash() == null;
        } else {
            return keyHash.equals(hcKey.getKeyHash());
        }
    }

    @Override
    public int hashCode() {
        return (namespace + collectionName + keyHash).hashCode();
    }
}
