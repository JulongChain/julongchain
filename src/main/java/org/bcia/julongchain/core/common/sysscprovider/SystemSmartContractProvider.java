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
package org.bcia.julongchain.core.common.sysscprovider;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.julongchain.common.groupconfig.config.ApplicationConfig;
import org.bcia.julongchain.common.groupconfig.config.IApplicationConfig;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.util.SpringContext;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.node.NodeSupport;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.core.ssc.ISystemSmartContractManager;
import org.bcia.julongchain.core.ssc.SystemSmartContractManager;
import org.bcia.julongchain.node.common.helper.ConfigTreeHelper;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;

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
    	return new NodeSupport().getApplicationConfig(groupId);
    }

    @Override
    public IPolicyManager policyManager(String groupID) {
		// TODO: 7/2/18 策略管理者
        return null;
    }

    @Override
    public boolean isSysSmartContract(String name) {
        return sscManager.isSysSmartContract(name);
    }

    @Override
    public boolean isSysSCAndNotInvokableSC2SC(String name) {
        return sscManager.isSysSmartContractAndNotInvokableSC2SC(name);
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
