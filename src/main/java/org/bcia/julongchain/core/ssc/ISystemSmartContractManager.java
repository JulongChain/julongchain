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
package org.bcia.julongchain.core.ssc;

import org.bcia.julongchain.common.exception.SysSmartContractException;

/**
 * 系统智能合约管理器接口
 * 各接口被调用的时机：
 *　1.Node启动时，调用registerSysSmartContracts()
 *  a)（SystemSmartContractManager内部）,调用loadSysSmartContracts()加载外部系统合约，与本地系统合约形成系统合约集合；
 *  b)（SystemSmartContractManager内部）,对每个系统合约，调用registerSysSmartContract(String smartContractID)
 *  2.调用deploySysSmartContracts("");
 *  a)(SystemSmartContractManager内部)deploySysSmartContracts调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 *  3.分别为每个组，调用deploySysSmartContracts(groupID);
 *  a)(SystemSmartContractManager内部)deploySysSmartContracts(groupID)调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 * @author sunianle, sunzongyu
 * @date 3/5/18
 * @company Dingxuan
 */
public interface ISystemSmartContractManager {
	/**
	 * 注册外部系统智能合约插件
	 */

    void registerSysSmartContracts();

	/**
	 * 为某个群组部署系统智能合约
	 * @param groupID
	 */
    void deploySysSmartContracts(String groupID);

	/**
	 * 取消部署系统智能合约
	 * @param groupID
	 */
    void deDeploySysSmartContracts(String groupID);

	/**
	 * 判断是否为系统智能合约
	 * @param smartContractID
	 * @return
	 */
    boolean isSysSmartContract(String smartContractID);

	/**
	 * 判断某个系统智能合约是否在白名单中
	 * @param contract
	 * @return
	 */
    boolean isWhitelisted(ISystemSmartContract contract);

	/**
	 * 获取系统智能合约
	 * @param smartContractID
	 * @return
	 */
    ISystemSmartContract getSystemSmartContract(String smartContractID);

	/**
	 * 是否为系统智能合约以及是否可以外部调用
	 */
	boolean isSysSmartContractAndNotInvokableExternal(String smartContractID);

	/**
	 * 是否为系统智能合约以及是否可以由其他智能合约调用
	 * @param smartContractID
	 * @return
	 */
    boolean isSysSmartContractAndNotInvokableSC2SC(String smartContractID);
}
