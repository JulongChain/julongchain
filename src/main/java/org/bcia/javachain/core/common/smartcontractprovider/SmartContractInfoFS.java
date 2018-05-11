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

import org.bcia.javachain.protos.node.Smartcontract;

/**
 * SmartContractInfoFS provides the implementation for SC on the FS and the access to it
 * It implements ISmartContractCacheSupport
 *
 * @author sunianle
 * @date 5/11/18
 * @company Dingxuan
 */
public class SmartContractInfoFS implements ISmartContractCacheSupport {

    /**
     * GetSmartContractFromFS  this is a wrapper for hiding package implementation.
     * @param name
     * @param version
     * @return
     */
    @Override
    public ISmartContractPackage getSmartContract(String name, String version) {
        return null;
    }

    /**
     * putSmartContractIntoFS is a wrapper for putting raw SmartContractDeploymentSpec
     * using CDSPackage. This is only used in UTs
     * @param deploymentSpec
     * @return
     */
    public ISmartContractPackage putSmartContract(Smartcontract.SmartContractDeploymentSpec deploymentSpec){
        return null;
    }


}
