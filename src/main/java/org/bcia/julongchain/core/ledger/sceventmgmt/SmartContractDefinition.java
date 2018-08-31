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
package org.bcia.julongchain.core.ledger.sceventmgmt;

/**
 * 智能合约基本属性
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class SmartContractDefinition {
    private String name = null;
    private byte[] hash = null;
    private String version = null;

    public SmartContractDefinition(String smartContractName, String smartContractVersion, byte[] id) {
        this.name=smartContractName;
        this.version=smartContractVersion;
        this.hash=id;
    }

    @Override
    public String toString() {
        return String.format("Name=%s, Version=%s, Hash=%s", name, version, getHashString());
    }

    private String getHashString(){
        String hashString = "";
        for (int i = 0; i < hash.length; i++) {
            hashString += hash[i];
        }
        return hashString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
