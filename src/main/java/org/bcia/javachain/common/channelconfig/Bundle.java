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
import org.bcia.javachain.consenter.consensus.IOrderer;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.protos.common.Configuration;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/5/10
 * @company Dingxuan
 */
public class Bundle implements IResources {

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
        return configtxManager;
    }

    @Override
    public IManager policyManager() {
        return policyManager;
    }

    @Override
    public Channel channelConfig() {
        return null;
    }

    @Override
    public IOrderer ordererConfig() {
        return null;
    }

    @Override
    public Configuration.Consortium consortiumsConfig() {
        return null;
    }


    @Override
    public GenesisConfig.Application applicationConfig() {
        return null;
    }

    @Override
    public IMspManager mspManager() {
        return groupConfig.getMspManager();
    }

    @Override
    public void validateNew(IResources resources) {

    }
}
