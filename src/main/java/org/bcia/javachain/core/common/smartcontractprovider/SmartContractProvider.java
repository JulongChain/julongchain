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

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.Query;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/7/18
 * @company Dingxuan
 */
public class SmartContractProvider {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractProvider.class);

    /**
     * getSmartContractPackage tries each known package implementation one by one
     * till the right package is found
     *
     * @param buf
     * @return
     * @throws JavaChainException
     */
    public static ISmartContractPackage getSmartContractPackage(byte[] buf) throws JavaChainException {
        ISmartContractPackage smartContractPackage = new SDSPackage();

        try {
            smartContractPackage.initFromBuffer(buf);
        } catch (JavaChainException e) {
            log.warn("try signed CDS");
            smartContractPackage = new SignedSDSPackage();
            smartContractPackage.initFromBuffer(buf);
        }

        return smartContractPackage;
    }

    /**
     * ExtractStatedbArtifactsFromCCPackage extracts the statedb artifacts from the code package tar and create a statedb artifact tar.
     * The state db artifacts are expected to contain state db specific artifacts such as index specification in the case of couchdb.
     * This function is called during chaincode instantiate/upgrade (from above), and from install, so that statedb artifacts can be created.
     *
     * @param scPack
     * @return
     */

    public static byte[] extractStateDBArtifactsFromSCPackage(ISmartContractPackage scPack) throws JavaChainException {
        return null;
    }

    public static ISmartContractPackage getSmartContractFromFS(String name, String version) throws JavaChainException {
        return null;
    }

    public static Query.SmartContractQueryResponse getInstalledSmartcontracts() throws JavaChainException {
        return null;

    }
}
