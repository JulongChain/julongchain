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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.BooleanUtils;
import org.bcia.javachain.common.cauthdsl.CAuthDslBuilder;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.aclmgmt.resources.Resources;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractData;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.javachain.core.node.NodeTool;
import org.bcia.javachain.core.policy.IPolicyChecker;
import org.bcia.javachain.core.policy.PolicyFactory;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.msp.mgmt.Principal;
import org.bcia.javachain.protos.common.Policies;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于智能合约生命周期管理的系统智能合约　Lifecycle System Smart Contract,LSSC
 *　The life cycle system smartcontract manages smartcontracts deployed
 *  on this node. It manages smartcontracts via Invoke proposals.
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
    public final static String UPGRADE="upgrade";
    //GET_SC_INFO get smartcontract
    public final static String GET_SC_INFO="getid";
    //GETCCINFO get smartcontract
    public final static String GET_DEP_SPEC="getdepspec";
    //GET_SC_DATA get SmartcontractData
    public final static String GET_SC_DATA="getscdata";
    //GET_SMART_CONTRACTS gets the instantiated smartcontracts on a group
    public final static String GET_SMART_CONTRACTS="getsmartcontracts";
    //GETINSTALLEDMARTCONTRACTS gets the installed smartcontracts on a node
    public final static String GET_INSTALLED_SMARTCONTRACTS="getinstalledsmartcontracts";

    public final static String allowedCharsSmartContractName="[A-Za-z0-9_-]+";
    public final static String allowedCharsVersion="[A-Za-z0-9_.+-]+";

    @Autowired
    private LsscSupport support;
    // sscprovider is the interface with which we call
    // methods of the system smartcontract package without
    // import cycles
    private ISystemSmartContractProvider sscProvider;
    // policyChecker is the interface used to perform
    // access control
    private IPolicyChecker policyChecker;

    /**
     * Init only initializes the system chaincode provider
     * @param stub
     * @return
     */
    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        this.sscProvider=SystemSmartContractFactory.getSystemSmartContractProvider();
        this.policyChecker = PolicyFactory.getPolicyChecker();
        log.info("Successfully initialized LSSC");
        return newSuccessResponse();
    }

    /**
     * Invoke implements lifecycle functions "deploy", "start", "stop", "upgrade".
     * Deploy's arguments -  {[]byte("deploy"), []byte(<chainname>), <unmarshalled pb.ChaincodeDeploymentSpec>}
     * Invoke also implements some query-like functions
     * Get chaincode arguments -  {[]byte("getid"), []byte(<chainname>), []byte(<chaincodename>)}
     * @param stub
     * @return
     */
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub) {
        log.debug("Enter LSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<1){
            return newErrorResponse(String.format("Incorrect number of arguments, expected a minimum of 2,provided %d",size));
        }
        String function= ByteString.copyFrom(args.get(0)).toStringUtf8();
        //Handle ACL:
        //1. get the signed proposal
        ProposalPackage.SignedProposal sp=null;
        try {
            sp=stub.getSignedProposal();
        }catch (Exception e){
            return newErrorResponse(String.format("Failed retrieving signed proposal on executing %s with error %s",function,e.getMessage()));
        }

        switch(function){
            case INSTALL:
                log.debug("Lifecycle Install");
                if(size<2){
                    return newErrorResponse(String.format("Incorrect number of arguments, expected a minimum of 2,provided %d",size));
                }
                // 2. check local MSP Admins policy
                try{
                    if(!BooleanUtils.isTrue(policyChecker.checkPolicyNoGroup(Principal.Admins,sp))){
                        return newErrorResponse(String.format("Authorization for INSTALL has been denied"));
                    }
                }catch (JavaChainException e){
                    log.error(e.getMessage(), e);
                    return newErrorResponse(String.format("Authorization for INSTALL has been denied (error-%s)",e.getMessage()));
                }


                byte[] depSpecBytes=args.get(1);
                try {
                    executeInstall(stub, depSpecBytes);
                }catch (SysSmartContractException e){
                    return newErrorResponse(String.format("Execute install failed, %s",e.getMessage()));
                }
                return newSuccessResponse("OK");
            case DEPLOY:
                //log.debug("Lifecycle Deploy");
                //deploy与upgrade的代码逻辑重用
            case UPGRADE:
                log.debug("Lifecycle Deploy/Upgrade:{}",function);
                if(size<3){
                    return newErrorResponse(String.format("Incorrect number of arguments,expected a minimum of 3,provided %d",size));
                }
                String groupName= ByteString.copyFrom(args.get(1)).toStringUtf8();
                try{
                    isValidGroupName(groupName);
                }catch(SysSmartContractException e){
                    return newErrorResponse(String.format("Invalid group name %s:%s",groupName,e.getMessage()));
                }
                IApplicationConfig ac=sscProvider.getApplicationConfig(groupName);
                if(ac==null){
                    return newErrorResponse(String.format("Programming error, non-existent appplication config for group '%s'",groupName));
                }
                //the maximum number of arguments depends on the capability of the group
                if((ac.getCapabilities().privateGroupData()==false && size>6) ||
                        (ac.getCapabilities().privateGroupData()==true && size>7)){
                    return newErrorResponse(String.format("Incorrect number of arguments, %d",size));
                }
                byte[] depSpecBytes2=args.get(2);
                Smartcontract.SmartContractDeploymentSpec spec=null;
                try {
                    spec=ProtoUtils.getSmartContractDeploymentSpec(depSpecBytes2);
                }catch (InvalidProtocolBufferException e){
                    return newErrorResponse(String.format("Get SmartContractDeploymentSpec error:%s",e.getMessage()));
                }
                // optional arguments here (they can each be nil and may or may not be present)
                // args[3] is a marshalled SignaturePolicyEnvelope representing the endorsement policy
                // args[4] is the name of essc
                // args[5] is the name of vssc
                // args[6] is a marshalled CollectionConfigPackage struct
                byte[] ep=null;
                if(size>3 && args.get(3).length!=0){
                    ep=args.get(3);
                }else{
                    Policies.SignaturePolicyEnvelope signaturePolicyEnvelope=CAuthDslBuilder.signedByAnyMember(NodeTool.getMspIDs(groupName));
                    try {
                        ep = ProtoUtils.marshalOrPanic(signaturePolicyEnvelope);
                    }catch (Exception e){
                        return newErrorResponse(String.format("MarshalOrPanic failed:%s",e.getMessage()));
                    }
                }

                byte[] essc=null;
                if(size>4 && args.get(4).length!=0){
                    essc=args.get(4);
                }else{
                    essc=ByteString.copyFromUtf8("ESSC").toByteArray();
                }

                byte[] vssc=null;
                if(size>5 && args.get(5).length!=0){
                    vssc=args.get(5);
                }else{
                    vssc=ByteString.copyFromUtf8("VSSC").toByteArray();
                }

                byte[] collectionsConfig=null;
                // we proceed with a non-nil collection configuration only if
                // we support the PrivateChannelData capability
                if(ac.getCapabilities().privateGroupData()==true  && size>6){
                    collectionsConfig=args.get(6);
                }

                SmartContractData scd=null;
                try {
                    scd=executeDeployOrUpgrade(stub, groupName, spec, ep, essc, vssc, collectionsConfig, function);
                } catch (SysSmartContractException e) {
                    return newErrorResponse(String.format("ExecuteDeployOrUpgrade failed,%s",e.getMessage()));
                }

                byte[] cdbytes=scd.marshal();
                if(cdbytes==null){
                    return newErrorResponse(String.format("Marshal SmartContractData failed"));
                }
                return newSuccessResponse(cdbytes);
            case GET_SC_INFO:
                //GET_SC_INFO与GET_SC_DATA的代码逻辑重用
            case GET_DEP_SPEC:
                //GET_DEP_SPEC与GET_SC_DATA的代码逻辑重用
            case GET_SC_DATA:
                log.debug("Lifecycle GetScInfo/GetDepSpec/GetScData:{}",function);
                if(size!=3){
                    return newErrorResponse(String.format("Incorrect number of arguments,expected 3,provided %d",size));
                }
                String groupName2=ByteString.copyFrom(args.get(1)).toStringUtf8();
                String smartContractName2=ByteString.copyFrom(args.get(2)).toStringUtf8();
                // 2. check local Group Readers policy
                String resource="";
                switch (function){
                    case GET_SC_INFO:
                        resource= Resources.LSSC_GETSCINFO;
                        break;
                    case GET_DEP_SPEC:
                        resource=Resources.LSSC_GETDEPSPEC;
                        break;
                    case GET_SC_DATA:
                        resource=Resources.LSSC_GETSCDATA;
                        break;
                }
                try{
                    AclManagement.getACLProvider().checkACL(resource,groupName2,sp);
                }catch (Exception e){
                    return newErrorResponse(String.format("Authorization request for resource %s failed %s: %s",resource,groupName2,e.getMessage()));
                }

                byte[] scbytes=null;
                try {
                    scbytes=getSmartContractInstance(stub, smartContractName2);
                } catch (SysSmartContractException e) {
                    log.error("Error getting smartcontract {} on group {}:{}",smartContractName2,groupName2,e.getMessage());
                    return newErrorResponse(String.format("Error getting smartcontract %s on group %s:%s",groupName2,resource,e.getMessage()));
                }
                switch (function){
                    case GET_SC_INFO:
                        SmartContractData scd2=null;
                        try {
                            scd2=getSmartContractData(smartContractName2, scbytes);
                        } catch (SysSmartContractException e) {
                            return newErrorResponse(String.format("%s",e.getMessage()));
                        }
                        return newSuccessResponse(scd2.getSmartContractName());
                    case GET_SC_DATA:
                        return newSuccessResponse(scbytes);
                    default:
                        byte[] depSpecByte=null;
                        try {
                            depSpecByte=getSmartContractCode(smartContractName2, scbytes);
                        } catch (SysSmartContractException e) {
                            return newErrorResponse(String.format("%s",e.getMessage()));
                        }
                        return newSuccessResponse(depSpecByte);
                }
            case GET_SMART_CONTRACTS:
                if(size!=1){
                    return newErrorResponse(String.format("Incorrect number of arguments,expected 1,provided %d",size));
                }
                //2. check local MSP Admins policy
                try {
                    if(!BooleanUtils.isTrue(policyChecker.checkPolicyNoGroup(Principal.Admins,sp))) {
                        return newErrorResponse(String.format("Authorization for GETSMARTCONTRACTS has been denied"));
                    }
                } catch (JavaChainException e) {
                    log.error(e.getMessage(), e);
                    return newErrorResponse(String.format("Authorization for GETSMARTCONTRACTS has been denied with error:%s",e.getMessage()));
                }
                return getSmartContracts(stub);
            case GET_INSTALLED_SMARTCONTRACTS:
                if(size!=1){
                    return newErrorResponse(String.format("Incorrect number of arguments,expected 1,provided %d",size));
                }
                //2. check local MSP Admins policy
                try {
                    if(!BooleanUtils.isTrue(policyChecker.checkPolicyNoGroup(Principal.Admins,sp))){
                        return newErrorResponse(String.format("Authorization for GETINSTALLEDSMARTCONTRACTS has been denied."));
                    }
                } catch (JavaChainException e) {
                    log.error(e.getMessage(), e);
                    return newErrorResponse(String.format("Authorization for GETINSTALLEDSMARTCONTRACTS has been denied with error:%s",e.getMessage()));
                }
                return getInstalledSmartContracts();
            default:
                return newErrorResponse(String.format("Invalid Function %s",function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与生命周期管理相关的系统智能合约";
    }


    /**
     * create the smartcontract on the given group
     * @param stub
     * @param data
     */
    private void putSmartContractData(ISmartContractStub stub,
                                      SmartContractData data) throws SysSmartContractException{

    }


    /**
     * putSmartcontractCollectionData adds collection data for the smartcontract
     * @param stub
     * @param data
     * @param collectionConfigBytes
     */
    private void putSmartContractCollectionData(ISmartContractStub stub,
                                                SmartContractData data,
                                                byte[] collectionConfigBytes)
            throws  SysSmartContractException{

    }

    /**
     * checks for existence of smartcontract on the given group
     * @param stub
     * @param contractName
     * @return
     */
    private byte[] getSmartContractInstance(ISmartContractStub stub,
                                            String contractName)
            throws SysSmartContractException
    {
        return null;
    }

    /**
     * gets the cd out of the bytes
     * @param contractName
     * @param scdBytes
     * @return
     * @throws SysSmartContractException
     */
    private SmartContractData getSmartContractData(String contractName,byte[] scdBytes)
            throws SysSmartContractException
    {
        return null;
    }


    /**
     * checks for existence of smartcontract on the given chain
     * @param name
     * @param scdBytes
     * @return
     */
    private byte[] getSmartContractCode(String name, byte[] scdBytes)throws SysSmartContractException{
        return null;
    }


    /**
     * getSmartcontracts returns all smartcontracts instantiated on this LSSC's group
     * @param stub
     * @return
     */
    private SmartContractResponse getSmartContracts(ISmartContractStub stub){
        return null;
    }

    /**
     * getInstalledChaincodes returns all smartcontracts installed on the node
     * @return
     */
    private SmartContractResponse getInstalledSmartContracts(){
        return null;
    }


    /**
     * check validity of chain name
     * @param group
     * @return
     */
    private void isValidGroupName(String group) throws SysSmartContractException{

    }


    /**
     * isValidSmartcontractName checks the validity of smartcontract name. Smartcontract names
     * should never be blank and should only consist of alphanumerics, '_', and '-'
     * @param contractName
     * @return
     */
    private void isValidSmartContractName(String contractName) throws SysSmartContractException{

    }


    /**
     * isValidSmartcontractVersion checks the validity of smartcontract version. Versions
     * should never be blank and should only consist of alphanumerics, '_',  '-',
     * '+', and '.'
     * @param contractName
     * @param version
     * @return
     */
    private boolean isValidSmartContractVersion(String contractName,String version){
        return true;
    }

    private boolean isValidSmartContractNameOrVersion(String scNameOrVersion,
                                                      String regExp){
        return true;
    }

    /**
     * executeInstall implements the "install" Invoke transaction
     * @param stub
     * @param scBytes
     */
    private void executeInstall(ISmartContractStub stub,byte[] scBytes) throws SysSmartContractException{
        log.debug("Execute install.");
    }


    /**
     * executeDeployOrUpgrade routes the code path either to executeDeploy or executeUpgrade
     * depending on its function argument
     * @param stub
     * @param groupName
     * @param scds
     * @param policy
     * @param escc
     * @param vscc
     * @param collectionConfigBytes
     * @param function
     * @return
     * @throws SysSmartContractException
     */
    private SmartContractData executeDeployOrUpgrade(ISmartContractStub stub,
                                                     String groupName,
                                                     Smartcontract.SmartContractDeploymentSpec scds,
                                                     byte [] policy,
                                                     byte [] escc,
                                                     byte [] vscc,
                                                     byte [] collectionConfigBytes,
                                                     String function)
            throws  SysSmartContractException{
        return null;
    }


    /**
     * executeDeploy implements the "instantiate" Invoke transaction
     * @param stub
     * @param groupName
     * @param scds
     * @param policy
     * @param escc
     * @param vscc
     * @param scdata
     * @param scPackage
     * @param collectionConfigBytes
     * @return
     */
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
    )throws SysSmartContractException{
        return null;
    }



    /**
     * executeUpgrade implements the "upgrade" Invoke transaction.
     * @param stub
     * @param groupName
     * @param scds
     * @param policy
     * @param essc
     * @param vssc
     * @param scdata
     * @param scPackage
     * @return
     */
    private SmartContractData executeUpgrade(
            ISmartContractStub stub,
            String groupName,
            Smartcontract.SmartContractDeploymentSpec scds,
            byte[] policy,
            byte[] essc,
            byte[] vssc,
            SmartContractData scdata,
            ISmartContractPackage scPackage
    )throws  SysSmartContractException{
        return null;
    }


}

