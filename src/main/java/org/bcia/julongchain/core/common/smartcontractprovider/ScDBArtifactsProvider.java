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
package org.bcia.julongchain.core.common.smartcontractprovider;

import org.bcia.julongchain.common.exception.SmartContractException;

/**
 * 提取智能合约压缩包
 *
 * @author sunianle
 * @date 5/10/18
 * @company Dingxuan
 */
public class ScDBArtifactsProvider {
    /**
     * extractStatedbArtifactsForSmartcontract extracts the statedb artifacts from the code package tar and create a statedb artifact tar.
     * The state db artifacts are expected to contain state db specific artifacts such as index specification in the case of couchdb.
     * This function is intented to be used during chaincode instantiate/upgrade so that statedb artifacts can be created.
     * @param smartContractName
     * @param smartContractVersion
     * @return
     * @throws SmartContractException
     */
    public static byte[] extractStatedbArtifactsForSmartcontract(String smartContractName,String smartContractVersion)throws SmartContractException{
         return null;
    }

    /**
     * extractStatedbArtifactsFromSCPackage extracts the statedb artifacts from the code package tar and create a statedb artifact tar.
     *The state db artifacts are expected to contain state db specific artifacts such as index specification in the case of couchdb.
     *This function is called during chaincode instantiate/upgrade (from above), and from install, so that statedb artifacts can be created.
     * @param scPackage
     * @return
     * @throws SmartContractException
     */
    public static byte[] extractStatedbArtifactsFromSCPackage(ISmartContractPackage scPackage)throws SmartContractException{
        return null;
    }
}
