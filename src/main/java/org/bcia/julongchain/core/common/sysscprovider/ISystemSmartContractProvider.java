/*
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

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.groupconfig.config.IApplicationConfig;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.core.ledger.IQueryExecutor;

/**
 * 系统智能合约
 * 提供抽象的系统智能合约服务
 *
 * @author sunianle, sunzongyu
 * @date 3/13/18
 * @company Dingxuan
 */
public interface ISystemSmartContractProvider {
    /**
	 * 判断智能合约是否为系统智能合约
     */
    boolean isSysSmartContract(String name);

    /**
	 * 智能合约是否可以通过智能合约调用
     */
    boolean isSysSCAndNotInvokeSC2SC(String name);

    /**
	 * 不能通过提案调用的系统智能合约　
     */
    boolean isSysSCAndNotInvokableExternal(String name);

    /**
	 * 获取账本查询器
     */
    IQueryExecutor getQueryExecutorForLedger(String groupID) throws JulongChainException;

    /**
	 * 获取配置
     */
    IApplicationConfig getApplicationConfig(String groupId);

    /**
	 * 获取策略管理者
     */
    IPolicyManager policyManager(String groupID);
}
