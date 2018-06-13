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
package org.bcia.julongchain.core.common.smartcontractprovider;

import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * SmartContractInfoCache implements in-memory cache for SmartContractData
* needed by endorser to verify if the local instantiation policy
* matches the instantiation policy on a channel before honoring
* an invoke
 * @author sunianle, sunzongyu
 * @date 5/10/18
 * @company Dingxuan
 */
public class SmartContractInfoCache {
    private Map<String,SmartContractDataPackage.SmartContractData> cache;
    private ISmartContractCacheSupport cacheSuppot;

    public SmartContractInfoCache(ISmartContractCacheSupport cacheSuppot) {
        cache = new HashMap<>();
        this.cacheSuppot = cacheSuppot;
    }

    /**
     * cache中存在相应smartcontractData, 直接返回。
     * 不存在创建并缓存如cache中返回
     * @param name
     * @param version
     * @return
     * @throws SmartContractException
     */
    public synchronized SmartContractDataPackage.SmartContractData getSmartContractData(String name,String version) throws SmartContractException{
        String key = name + "/" + version;
        if(cache.containsKey(key)){
            return cache.get(key);
        } else {
            ISmartContractPackage smartContract = cacheSuppot.getSmartContract(name, version);
            if(smartContract == null){
                return null;
            }
            SmartContractDataPackage.SmartContractData smartContractData = smartContract.getSmartContractData();
            cache.put(key, smartContractData);
            return smartContractData;
        }
    }

    public Map<String, SmartContractDataPackage.SmartContractData> getCache() {
        return cache;
    }

    public void setCache(Map<String, SmartContractDataPackage.SmartContractData> cache) {
        this.cache = cache;
    }

    public ISmartContractCacheSupport getCacheSuppot() {
        return cacheSuppot;
    }

    public void setCacheSuppot(ISmartContractCacheSupport cacheSuppot) {
        this.cacheSuppot = cacheSuppot;
    }
}
