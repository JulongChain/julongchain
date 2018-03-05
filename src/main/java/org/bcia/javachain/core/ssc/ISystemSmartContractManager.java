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
package org.bcia.javachain.core.ssc;

/**
 * 系统智能合约管理器接口
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
public interface ISystemSmartContractManager {
    //为某个群组部署系统智能合约
    void deploySysSmartContracts(String groupID);
    //取消部署系统智能合约
    void deDeploySysSmartContracts(String groupID);
    //判断是否为系统智能合约
    boolean isSysSmartContract(String smartContractID);
    //加载外部系统智能合约插件
    void loadSysSmartContracts();
    //注册外部系统智能合约插件
    void registerSysSmartContracts();
    //编译系统智能合约
    void registerSysSmartContract();
}
