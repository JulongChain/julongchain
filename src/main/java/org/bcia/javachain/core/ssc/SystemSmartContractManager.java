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
 * 系统智能合约管理器,整合管理功能函数接口
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
public class SystemSmartContractManager implements ISystemSmartContractManager{
    @Override
    public void deploySysSmartContracts(String groupID) {

    }

    @Override
    public void deDeploySysSmartContracts(String groupID) {

    }

    @Override
    public boolean isSysSmartContract(String smartContractID) {
        return false;
    }

    @Override
    public void loadSysSmartContracts() {

    }

    @Override
    public void registerSysSmartContracts() {

    }

    @Override
    public void registerSysSmartContract() {

    }
}
