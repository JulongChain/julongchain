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
package org.bcia.javachain.core.common.sysscprovider;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.MSPConfigHandler;
import org.bcia.javachain.common.groupconfig.capability.ApplicationProvider;
import org.bcia.javachain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.javachain.common.groupconfig.config.ApplicationConfig;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.policies.IManager;
import org.bcia.javachain.common.util.SpringContext;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.node.util.NodeUtils;
import org.bcia.javachain.core.ssc.ISystemSmartContractManager;
import org.bcia.javachain.core.ssc.SystemSmartContractManager;
import org.bcia.javachain.node.common.helper.ConfigTreeHelper;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.IOException;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
public class SystemSmartContractProvider implements ISystemSmartContractProvider {
    private static final String PROFILE_CREATE_GROUP = "SampleSingleMSPGroup";
    private ISystemSmartContractManager sscManager;

    public SystemSmartContractProvider(){
        this.sscManager = SpringContext.getInstance().getBean(SystemSmartContractManager.class);
    }

    @Override
    public IApplicationConfig getApplicationConfig(String groupId) {
        //TODO: add by zhouhui 使测试通过
        //构造应用子树
        GenesisConfig.Profile profile = null;
        try {
            profile = GenesisConfigFactory.loadGenesisConfig().getCompletedProfile(PROFILE_CREATE_GROUP);
            Configtx.ConfigTree appTree = ConfigTreeHelper.buildApplicationTree(profile.getApplication());
            //得到最终的应用配置
            ApplicationConfig appConfig = new ApplicationConfig(appTree, new MSPConfigHandler(0));
            IApplicationCapabilities applicationCapabilities = appConfig.getCapabilities();
            return appConfig;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidateException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public IManager policyManager(String groupID) {
        return null;
    }

    @Override
    public boolean isSysSmartContract(String name) {
        return sscManager.isSysSmartContract(name);
    }

    @Override
    public boolean isSysSCAndNotInvokableSC2SC(String name) {
        return false;
//        return sscManager.isSysSmartContractAndNotInvokableSC2SC(name);
    }

    @Override
    public boolean isSysSCAndNotInvkeableExternal(String name) {
        return sscManager.isSysSmartContractAndNotInvokableExternal(name);
    }

    @Override
    public IQueryExecutor getQueryExecutorForLedger(String groupID) throws JavaChainException {
        INodeLedger l = NodeUtils.getLedger(groupID);
        if(l == null){
            throw new JavaChainException("Can not get ledger for group " + groupID);
        }
        return l.newQueryExecutor();
    }
}
