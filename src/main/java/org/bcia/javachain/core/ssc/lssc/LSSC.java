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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractProvider;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractCode;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractData;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.policy.IPolicyChecker;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用于智能合约生命周期管理的系统智能合约　Lifecycle System Smart Contract,LSSC
 *　The life cycle system smartcontract manages smartcontracts deployed
 *  on this peer. It manages smartcontracts via Invoke proposals.
 *    "Args":["deploy",<ChaincodeDeploymentSpec>]
 *    "Args":["upgrade",<ChaincodeDeploymentSpec>]
 *    "Args":["stop",<ChaincodeInvocationSpec>]
 *    "Args":["start",<ChaincodeInvocationSpec>]
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class LSSC  extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LSSC.class);
    //INSTALL install command
    public final static String INSTALL="install";
    //DEPLOY deploy command
    public final static String DEPLOY="deploy";
    //UPGRADE upgrade chaincode
    public final static String UPGRADE = "upgrade";
    //GETCCINFO get chaincode
    public final static String GETDEPSPEC = "getdepspec";
    //GETSCDATA get SmartcontractData
    public final static String GETSCDATA = "getscdata";
    //GETSMARTCONTRACTS gets the instantiated smartcontracts on a group
    public final static String GETSMARTCONTRACTS = "getsmartcontracts";
    //GETINSTALLEDMARTCONTRACTS gets the installed smartcontracts on a node
    public final static String GETINSTALLEDMARTCONTRACTS = "getinstalledsmartcontracts";

    public final static String allowedCharsSmartContractName = "[A-Za-z0-9_-]+";
    public final static String allowedCharsVersion       = "[A-Za-z0-9_.+-]+";

    @Autowired
    private LsscSupport support;

    private ISystemSmartContractProvider sscProvider;

    private IPolicyChecker checker;
    @Override
    public Response init(ISmartContractStub stub) {
        return null;
    }

    @Override
    public Response invoke(ISmartContractStub stub) {
        return null;
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与生命周期管理相关的系统智能合约";
    }

    //create the chaincode on the given chain
    private void putSmartContractData(ISmartContractStub stub,
                                      SmartContractData data){

    }

    // putChaincodeCollectionData adds collection data for the chaincode
    private void putSmartContractCollectionData(ISmartContractStub stub,
                                                SmartContractData data,
                                                byte[] collectionConfigBytes){

    }

    //checks for existence of chaincode on the given channel
    private byte[] getSmartContractInstance(ISmartContractStub stub,
                                          String contractName){
        return null;
    }

    //gets the cd out of the bytes
    private SmartContractData getSmartContractData(String contractName,byte[] scdBytes){
        return null;
    }

    //checks for existence of chaincode on the given chain
    private SmartContractCode getSmartContractCode(String name, byte[] scdBytes){
        return null;
    }

    // getChaincodes returns all chaincodes instantiated on this LSSC's group
    private ProposalResponsePackage.Response getSmartContracts(ISmartContractStub stub){
        return null;
    }

    private ProposalResponsePackage.Response getInstalledSmartContracts(){
        return null;
    }

    //check validity of chain name
    private boolean isValidGroupName(String group){
        return true;
    }

    // isValidChaincodeName checks the validity of chaincode name. Chaincode names
    // should never be blank and should only consist of alphanumerics, '_', and '-'
    private boolean isValidSmartContractName(String contractName){
        return true;
    }

    // isValidChaincodeVersion checks the validity of chaincode version. Versions
    // should never be blank and should only consist of alphanumerics, '_',  '-',
    // '+', and '.'
    private boolean isValidSmartContractVersion(String contractName,String version){
        return true;
    }

    private boolean isValidSmartContractNameOrVersion(String scNameOrVersion,
                                                      String regExp){
        return true;
    }

    // executeInstall implements the "install" Invoke transaction
    private void executeInstall(ISmartContractStub stub,byte[] scBytes){

    }

    // executeDeployOrUpgrade routes the code path either to executeDeploy or executeUpgrade
    // depending on its function argument
    private SmartContractData executeDeployOrUpgrade(ISmartContractStub stub,
                                                     String groupName,
                                                     Smartcontract.SmartContractDeploymentSpec scds,
                                                     byte [] policy,
                                                     byte [] escc,
                                                     byte [] vscc,
                                                     byte [] collectionConfigBytes,
                                                     String function){
         return null;
    }

    //executeDeploy implements the "instantiate" Invoke transaction
    private SmartContractData executeDeploy(
            ISmartContractStub stub,
            String groupName,
            Smartcontract.SmartContractDeploymentSpec scds,
            byte[] policy,
            byte[] escc,
            byte[] vscc,
            SmartContractData scdata,
            ISmartContractPackage scPackage,
            byte[] collectionConfigBytes
            ){
        return null;
    }


    //executeUpgrade implements the "upgrade" Invoke transaction.
    private SmartContractData executeUpgrade(
            ISmartContractStub stub,
            String groupName,
            Smartcontract.SmartContractDeploymentSpec scds,
            byte[] policy,
            byte[] escc,
            byte[] vscc,
            SmartContractData scdata,
            ISmartContractPackage scPackage
    ){
        return null;
    }


}

