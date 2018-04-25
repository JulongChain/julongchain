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

import org.bcia.javachain.common.cauthdsl.CAuthDslBuilder;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.javachain.core.common.smartcontractprovider.SignedSDSPackage;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.javachain.core.node.NodeTool;
import org.bcia.javachain.protos.common.Policies;
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
        try {
            scPackage.putSmartcontractToFS();
        } catch (JavaChainException e) {
            String msg=String.format("Error installing smartcontract code %s:%s(%s)",
                    scPackage.getSmartContractData().getName(),
                    scPackage.getSmartContractData().getVersion(),
                    e.getMessage());
            throw new SysSmartContractException(msg);
        }
    }

    /**
     * getSmartcontractFromLocalStorage retrieves the smartcontract package
     * for the requested smartcontract, specified by name and version
     * @param smartcontractName
     * @param version
     * @return
     */
    public ISmartContractPackage getSmartContractFromLocalStorage(String smartcontractName,String version) throws JavaChainException{
        return SmartContractProvider.getSmartContractFromFS(smartcontractName,version);
    }

    /**
     * getSmartcontractsFromLocalStorage returns an array of all smartcontract
     * data that have previously been persisted to local storage
     * @return
     */
    public Query.SmartContractQueryResponse getSmartContractsFromLocalStorage()throws JavaChainException{
        return SmartContractProvider.getInstalledSmartcontracts();
    }

    /**
     * getInstantiationPolicy returns the instantiation policy for the
     * supplied smartcontract (or the channel's default if none was specified)
     * @param group
     * @param scPackage
     * @return
     */
    byte[] getInstantiationPolicy(String group,ISmartContractPackage scPackage)throws SysSmartContractException{
        byte[] instantiationPolicy;
        if(scPackage instanceof SignedSDSPackage){
            SignedSDSPackage sscPackage=(SignedSDSPackage)scPackage;
            instantiationPolicy=sscPackage.getInstantiationPolicy();
            if(instantiationPolicy==null){
                String msg="Instantiation policy cannot be null for a SignedSCDeploymentSpec";
                throw new SysSmartContractException(msg);
            }
        }
        else{
            // the default instantiation policy allows any of the group MSP admins
            // to be able to instantiate
            String[] mspIds = NodeTool.getMspIDs(group);
            Policies.SignaturePolicyEnvelope p = CAuthDslBuilder.signedByAnyAdmin(mspIds);
            instantiationPolicy=p.toByteArray();
        }
        return instantiationPolicy;
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
        //create a policy object from the policy bytes

    }
}
