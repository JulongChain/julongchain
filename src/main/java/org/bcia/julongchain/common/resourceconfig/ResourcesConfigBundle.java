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
package org.bcia.julongchain.common.resourceconfig;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.configtx.ConfigtxValidator;
import org.bcia.julongchain.common.configtx.IConfigtxValidator;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.policies.IPolicyProvider;
import org.bcia.julongchain.common.policies.PolicyManager;
import org.bcia.julongchain.common.policies.PolicyRouter;
import org.bcia.julongchain.common.resourceconfig.config.IResourcesConfig;
import org.bcia.julongchain.common.resourceconfig.config.ResourcesConfig;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class ResourcesConfigBundle implements IResourcesConfigBundle {
    private String groupId;
    private Configtx.Config config;
    private IGroupConfigBundle groupConfigBundle;
    private IResourcesConfig resourcesConfig;
    private IConfigtxValidator configtxValidator;
    private IPolicyManager policyManager;

    private List<Callback> callbackList;

    public ResourcesConfigBundle(String groupId, Configtx.Config config, IGroupConfigBundle groupConfigBundle,
                                 List<Callback> callbackList) throws InvalidProtocolBufferException,
            ValidateException, PolicyException {
        this.groupId = groupId;
        this.config = config;
        this.groupConfigBundle = groupConfigBundle;
        this.callbackList = callbackList;

        this.resourcesConfig = new ResourcesConfig(config.getGroupTree());

        //TODO:需要赋值
        Map<Integer, IPolicyProvider> policyProviderMap = new HashMap<Integer, IPolicyProvider>();
//        policyProviderMap.put(Policies.PolicyConstant.PolicyType.SIGNATURE_VALUE, new PolicyProvider(configResources.getPolicyManager()));

        IPolicyManager resourcesPolicyManager = new PolicyManager(ResourcesConfigConstant.RESOURCES,
                policyProviderMap, config.getGroupTree());
        IPolicyManager groupPolicyManager = groupConfigBundle.getPolicyManager();

        this.policyManager = new PolicyRouter(groupPolicyManager, resourcesPolicyManager);

        this.configtxValidator = new ConfigtxValidator(groupId, config, ResourcesConfigConstant.RESOURCES, policyManager);

    }

    @Override
    public IGroupConfigBundle getGroupConfigBundle() {
        return groupConfigBundle;
    }

    @Override
    public IResourcesConfig getResourcesConfig() {
        return resourcesConfig;
    }

    @Override
    public IConfigtxValidator getConfigtxValidator() {
        return configtxValidator;
    }

    @Override
    public IPolicyManager getPolicyManager() {
        return policyManager;
    }

    @Override
    public Configtx.Config getCurrentConfig() {
        return config;
    }

    @Override
    public Configtx.ConfigEnvelope updateProposeConfig(Common.Envelope configtx) throws InvalidProtocolBufferException,
            ValidateException {
        return configtxValidator.proposeConfigUpdate(configtx);
    }
}
