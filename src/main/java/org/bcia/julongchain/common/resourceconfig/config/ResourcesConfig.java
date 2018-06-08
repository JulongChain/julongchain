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
import org.bcia.julongchain.common.resourceconfig.ResourcesConfigConstant;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.Iterator;
import java.util.Map;

/**
 * 资源配置对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class ResourcesConfig implements IResourcesConfig {
    private IApisConfig apiConfig;
    private NodePoliciesConfig nodePoliciesConfig;
    private ISmartContractsConfig smartContractsConfig;

    public ResourcesConfig(Configtx.ConfigTree configTree) throws InvalidProtocolBufferException, ValidateException {
        if (configTree != null && configTree.getChildsCount() > 0) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> iterator = configTree.getChildsMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = iterator.next();
                String childName = entry.getKey();
                Configtx.ConfigTree childTree = entry.getValue();

                if (ResourcesConfigConstant.APIS.equals(childName)) {
                    this.apiConfig = new ApisConfig(childTree);
                } else if (ResourcesConfigConstant.NODE_POLICIES.equals(childName)) {
                    this.nodePoliciesConfig = new NodePoliciesConfig(childTree);
                } else if (ResourcesConfigConstant.SMART_CONTRACTS.equals(childName)) {
                    this.smartContractsConfig = new SmartContractsConfig(childTree);
                }
            }
        }
    }

    @Override
    public IApisConfig getApiConfig() {
        return apiConfig;
    }

    @Override
    public NodePoliciesConfig getNodePoliciesConfig() {
        return nodePoliciesConfig;
    }

    @Override
    public ISmartContractsConfig getSmartContractsConfig() {
        return smartContractsConfig;
    }
}
