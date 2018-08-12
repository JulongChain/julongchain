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
package org.bcia.julongchain.common.resourceconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 智能合约集配置
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class SmartContractsConfig implements ISmartContractsConfig {
    private Map<String, ISmartContractDefinition> smartContractConfigMap;

    public SmartContractsConfig(Configtx.ConfigTree tree) throws ValidateException, InvalidProtocolBufferException {
        if (tree != null && tree.getValuesCount() > 0) {
            throw new ValidateException("SmartContracts does not support value");
        }

        smartContractConfigMap = new HashMap<String, ISmartContractDefinition>();
        if (tree != null && tree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = tree.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String childName = entry.getKey();
                Configtx.ConfigTree childTree = entry.getValue();

                ISmartContractDefinition smartContractConfig = new SmartContractConfig(childName, childTree);
                smartContractConfigMap.put(childName, smartContractConfig);
            }
        }

    }

    @Override
    public ISmartContractDefinition getSmartContractByName(String scName) {
        return smartContractConfigMap.get(scName);
    }
}
