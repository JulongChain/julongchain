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
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractCode;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractData;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.javachain.core.policy.IPolicyChecker;
import org.bcia.javachain.core.policy.PolicyFactory;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于智能合约生命周期管理的系统智能合约　Lifecycle System Smart Contract,LSSC
 *　The life cycle system smartcontract manages smartcontracts deployed
 *  on this peer. It manages smartcontracts via Invoke proposals.
 *    "Args":["deploy",<SmartcontractDeploymentSpec>]
 *    "Args":["upgrade",<SmartcontractDeploymentSpec>]
 *    "Args":["stop",<SmartcontractInvocationSpec>]
 *    "Args":["start",<SmartcontractInvocationSpec>]
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
    //UPGRADE upgrade smartcontract
    public final static String UPGRADE = "upgrade";
    //GET_SC_INFO get smartcontract
    public final static String GET_SC_INFO = "getid";
    //GETCCINFO get smartcontract
    public final static String GET_DEP_SPEC = "getdepspec";
    //GET_SC_DATA get SmartcontractData
    public final static String GET_SC_DATA = "getscdata";
    //GET_SMART_CONTRACTS gets the instantiated smartcontracts on a group
    public final static String GET_SMART_CONTRACTS = "getsmartcontracts";
    //GETINSTALLEDMARTCONTRACTS gets the installed smartcontracts on a node
    public final static String GET_INSTALLED_SMARTCONTRACTS = "getinstalledsmartcontracts";

    public final static String allowedCharsSmartContractName = "[A-Za-z0-9_-]+";
    public final static String allowedCharsVersion       = "[A-Za-z0-9_.+-]+";

    @Autowired
    private LsscSupport support;

    private ISystemSmartContractProvider sscProvider;

    private IPolicyChecker checker;
    @Override
    public Response init(ISmartContractStub stub) {
        this.sscProvider=SystemSmartContractFactory.getSystemSmartContractProvider();
        this.checker= PolicyFactory.getPolicyChecker();
        log.info("Successfully initialized LSSC");
        return newSuccessResponse();
    }

    @Override
    public Response invoke(ISmartContractStub stub) {
        log.debug("Enter LSCC invoke function");
        List<String> args = stub.getStringArgs();
        int size=args.size();
        if(size<1){
            return newErrorResponse(String.format("Incorrect number of arguments, %d",size));
        }
        String function=args.get(0);
        //Handle ACL:
        //1. get the signed proposal
        ProposalPackage.SignedProposal sp = stub.getSignedProposal();

        switch(function){
            case INSTALL:
                ;
            case DEPLOY:
                ;
            case UPGRADE:
                ;
            case GET_SC_INFO:
                ;
            case GET_DEP_SPEC:
                ;
            case GET_SC_DATA:
                ;
            case GET_SMART_CONTRACTS:
                ;
            case GET_INSTALLED_SMARTCONTRACTS:
                ;
            default:
                ;
        }

        log.debug("LSSC exits successfully");
        return newSuccessResponse();
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与生命周期管理相关的系统智能合约";
    }

    //create the smartcontract on the given chain
    private void putSmartContractData(ISmartContractStub stub,
                                      SmartContractData data){

    }

    // putSmartcontractCollectionData adds collection data for the smartcontract
    private void putSmartContractCollectionData(ISmartContractStub stub,
                                                SmartContractData data,
                                                byte[] collectionConfigBytes){

    }

    //checks for existence of smartcontract on the given channel
    private byte[] getSmartContractInstance(ISmartContractStub stub,
                                          String contractName){
        return null;
    }

    //gets the cd out of the bytes
    private SmartContractData getSmartContractData(String contractName,byte[] scdBytes){
        return null;
    }

    //checks for existence of smartcontract on the given chain
    private SmartContractCode getSmartContractCode(String name, byte[] scdBytes){
        return null;
    }

    // getSmartcontracts returns all smartcontracts instantiated on this LSSC's group
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

    // isValidSmartcontractName checks the validity of smartcontract name. Smartcontract names
    // should never be blank and should only consist of alphanumerics, '_', and '-'
    private boolean isValidSmartContractName(String contractName){
        return true;
    }

    // isValidSmartcontractVersion checks the validity of smartcontract version. Versions
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

