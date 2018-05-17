/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.common.channelconfig;

import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.groupconfig.config.GroupConfig;
import org.bcia.javachain.common.policies.IManager;
import org.bcia.javachain.common.resourceconfig.IPolicyMapper;
import org.bcia.javachain.common.resourceconfig.ISmartContractRegistry;
import org.bcia.javachain.msp.IMspManager;

/**
 * @author zhangmingyang
 * @Date: 2018/5/10
 * @company Dingxuan
 */
public class Bundle implements org.bcia.javachain.common.resourceconfig.IResources {

    private final static String ROOTGROUP = "GROUP";
    private IManager policyManager;

    private IMspManager mspManager;

    private GroupConfig groupConfig;

    private IValidator configtxManager;


    public Bundle(IManager policyManager, IMspManager mspManager, GroupConfig groupConfig, IValidator configtxManager) {
        this.policyManager = policyManager;
        this.mspManager = mspManager;
        this.groupConfig = groupConfig;
        this.configtxManager = configtxManager;
    }


    @Override
    public IValidator configtxValidator() {
        return null;
    }

    @Override
    public IManager managerPolicyManager() {
        return null;
    }

    @Override
    public IPolicyMapper APIPolicyMapper() {
        return null;
    }

    @Override
    public ISmartContractRegistry chaincodeRegistry() {
        return null;
    }

    @Override
    public IResources resourcesChannelConfig() {
        return null;
    }
}
