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
package org.bcia.javachain.core.ssc.lssc;

import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractProvider;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Query;
import org.springframework.stereotype.Component;

/**
 * LSSC的支持类
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
@Component
public class LsscSupport {
    // PutChaincodeToLocalStorage stores the supplied chaincode
    // package to local storage (i.e. the file system)
    public void putSmartContractToLocalStorage(ISmartContractPackage scPackage){

    }
    // GetChaincodeFromLocalStorage retrieves the chaincode package
    // for the requested chaincode, specified by name and version
    public ISmartContractPackage getSmartContractFromLocalStorage(String smartcontractName,String version){
        return null;
    }
    // GetChaincodesFromLocalStorage returns an array of all chaincode
    // data that have previously been persisted to local storage
    public Query.SmartContractQueryResponse getSmartContractsFromLocalStorage(){
        return null;
    }
    // GetInstantiationPolicy returns the instantiation policy for the
    // supplied chaincode (or the channel's default if none was specified)
    byte[] getInstantiationPolicy(String group,ISmartContractPackage scPackage){
        return null;
    }
    // CheckInstantiationPolicy checks whether the supplied signed proposal
    // complies with the supplied instantiation policy
    void checkInstantiationPolicy(ProposalPackage.SignedProposal signedProposal,
                                  String groupName,
                                  byte[] instantiationPolicy){

    }
}
