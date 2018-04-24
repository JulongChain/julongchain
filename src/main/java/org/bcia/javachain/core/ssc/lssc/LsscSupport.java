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

import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Query;
import org.springframework.stereotype.Component;

/**
 * LSSC的支持类,包含LSSC执行任务所需的函数
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
@Component
public class LsscSupport {
    /**
     * putSmartcontractToLocalStorage stores the supplied smartcontract
     * package to local storage (i.e. the file system)
     * @param scPackage
     */
    public void putSmartContractToLocalStorage(ISmartContractPackage scPackage) throws SysSmartContractException{

    }

    /**
     * getSmartcontractFromLocalStorage retrieves the smartcontract package
     * for the requested smartcontract, specified by name and version
     * @param smartcontractName
     * @param version
     * @return
     */
    public ISmartContractPackage getSmartContractFromLocalStorage(String smartcontractName,String version) throws SysSmartContractException{
        return null;
    }

    /**
     * getSmartcontractsFromLocalStorage returns an array of all smartcontract
     * data that have previously been persisted to local storage
     * @return
     */
    public Query.SmartContractQueryResponse getSmartContractsFromLocalStorage(){
        return null;
    }

    /**
     * getInstantiationPolicy returns the instantiation policy for the
     * supplied smartcontract (or the channel's default if none was specified)
     * @param group
     * @param scPackage
     * @return
     */
    byte[] getInstantiationPolicy(String group,ISmartContractPackage scPackage){
        return null;
    }


    /**
     * checkInstantiationPolicy checks whether the supplied signed proposal
     * complies with the supplied instantiation policy
     * @param signedProposal
     * @param groupName
     * @param instantiationPolicy
     */
    void checkInstantiationPolicy(ProposalPackage.SignedProposal signedProposal,
                                  String groupName,
                                  byte[] instantiationPolicy) throws SysSmartContractException{

    }
}
