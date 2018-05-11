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
package org.bcia.javachain.core.common.smartcontractprovider;

import org.bcia.javachain.common.exception.SmartContractException;

/**
 * 提供关于智能合约是否部署的信息
 *
 * @author sunianle
 * @date 5/10/18
 * @company Dingxuan
 */
public class SmartContractInfoProvider {
    /**
     * isSmartContractDeployed returns true if the smartcontract with given name and version is deployed
     * @param groupID
     * @param smartContractName
     * @param smartContractVersion
     * @param smartContractHash
     * @return
     * @throws SmartContractException
     */
    public static boolean isSmartContractDeployed(String groupID,String smartContractName,
                                                  String smartContractVersion,
                                                  byte[] smartContractHash)throws SmartContractException{
        return true;
    }
}

