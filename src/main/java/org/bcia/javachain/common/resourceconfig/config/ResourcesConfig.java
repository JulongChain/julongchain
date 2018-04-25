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
package org.bcia.javachain.common.resourceconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.policies.IPolicyManager;
import org.bcia.javachain.common.policies.IPolicyProvider;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policies.PolicyRouter;
import org.bcia.javachain.common.resourceconfig.ResourceConfigConstant;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 资源配置对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class ResourcesConfig {
    interface Callback {
        void call(IResourcesConfig resourcesConfig);
    }

    private String groupId;
    private Configtx.Config config;
    private IGroupConfigBundle configResources;
    private IValidator validator;
    private PolicyRouter policyRouter;

    private IApiConfig apiConfig;

    private NodePoliciesConfig nodePoliciesConfig;
    private SmartContractsConfig smartContractsConfig;

    private List<Callback> callbackList;

    public ResourcesConfig(String groupId, Configtx.Config config, IGroupConfigBundle configResources, List<Callback>
            callbackList) throws InvalidProtocolBufferException, ValidateException, PolicyException {
        this.groupId = groupId;
        this.config = config;
        this.configResources = configResources;
        this.callbackList = callbackList;

        if (config != null && config.getGroupTree() != null && config.getGroupTree().getChildsCount() > 0) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> iterator = config.getGroupTree().getChildsMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = iterator.next();
                String childName = entry.getKey();
                Configtx.ConfigTree childTree = entry.getValue();

                if (ResourceConfigConstant.APIS.equals(childName)) {
                    this.apiConfig = new ApiConfig(childTree);
                } else if (ResourceConfigConstant.NODE_POLICIES.equals(childName)) {
                    this.nodePoliciesConfig = new NodePoliciesConfig(childTree);
                } else if (ResourceConfigConstant.SMART_CONTRACTS.equals(childName)) {
                    this.smartContractsConfig = new SmartContractsConfig(childTree);
                }
            }
        }

        Map<Integer, IPolicyProvider> policyProviderMap = new HashMap<Integer, IPolicyProvider>();
//        policyProviderMap.put(Policies.Policy.PolicyType.SIGNATURE_VALUE, new PolicyProvider(configResources.getPolicyManager()));

        IPolicyManager resourcesPolicyManager = new PolicyManager(ResourceConfigConstant.RESOURCES,
                policyProviderMap, config.getGroupTree());
        IPolicyManager groupPolicyManager = configResources.getPolicyManager();










    }
}
