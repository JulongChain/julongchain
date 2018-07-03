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
package org.bcia.julongchain.core.ssc.lssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.groupconfig.config.IApplicationConfig;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.IPolicyChecker;
import org.bcia.julongchain.common.policycheck.PolicyChecker;
import org.bcia.julongchain.common.policycheck.cauthdsl.CAuthDslBuilder;
import org.bcia.julongchain.common.policycheck.policies.GroupPolicyManagerGetter;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.core.aclmgmt.AclManagement;
import org.bcia.julongchain.core.aclmgmt.resources.Resources;
import org.bcia.julongchain.core.common.privdata.CollectionStoreSupport;
import org.bcia.julongchain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.julongchain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.julongchain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.julongchain.core.ledger.sceventmgmt.ScEventManager;
import org.bcia.julongchain.core.ledger.sceventmgmt.SmartContractDefinition;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyValue;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IQueryResultsIterator;
import org.bcia.julongchain.core.ssc.SystemSmartContractBase;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.msp.mgmt.MSPPrincipalGetter;
import org.bcia.julongchain.protos.common.Collection;
import org.bcia.julongchain.protos.common.Policies;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.Query;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于智能合约生命周期管理的系统智能合约　Lifecycle System Smart Contract,LSSC
 * 支持智能合约的安装(install)、部署(deploy)、升级(upgrade)等生命周期管理，
 * 支持查询智能合约信息(getid)，获取智能合约部署规范(getdepspec)、获取智能合约数据（getscdata）、
 * 获取实例化智能合约（getsmartcontracts）、获取安装的智能合约（getinstalledsmartcontracts）等功能。
 * LSSC的invoke函数，接受的args格式说明如下：
 * "Args":["deploy",<groupID>,<SmartcontractDeploymentSpec>]
 * "Args":["upgrade",<groupID>,<SmartcontractDeploymentSpec>]
 * "Args":["install",<SmartContractDeploymentSpec>]
 * "Args":["getid",<groupID>,<smartcontractName>]
 * "Args":["getdepspec",<groupID>,<smartcontractName>]
 * "Args":["getscdata",<groupID>,<smartcontractName>]
 * "Args":["getsmartcontracts"]
 * "Args":["getinstalledsmartcontracts"]
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
    //GET_DEP_SPEC get get SmartContractDeploymentSpec
    public final static String GET_DEP_SPEC="getdepspec";
    //GET_SC_DATA get SmartcontractData
    public final static String GET_SC_DATA="getscdata";
    //GET_SMART_CONTRACTS gets the instantiated smartcontracts on a group
    public final static String GET_SMART_CONTRACTS="getsmartcontracts";
    //GETINSTALLEDMARTCONTRACTS gets the installed smartcontracts on a node
    public final static String GET_INSTALLED_SMARTCONTRACTS="getinstalledsmartcontracts";

    public final static String allowedCharsSmartContractName="[A-Za-z0-9_-]+";
    public final static String allowedCharsVersion="[A-Za-z0-9_.+-]+";

//    @Autowired
    private LsscSupport support = new LsscSupport();
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
        IMsp localMSP = GlobalMspManagement.getLocalMsp();
        policyChecker=new PolicyChecker(new GroupPolicyManagerGetter(),localMSP,new MSPPrincipalGetter());
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
                   policyChecker.checkPolicyNoGroup(MSPPrincipalGetter.Admins,sp);
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
				// TODO: 7/2/18 暂时没有能力capabilities概念
                if((size>6 && !ac.getCapabilities().isPrivateGroupData()) ||
                        (size>7 && ac.getCapabilities().isPrivateGroupData())){
                    return newErrorResponse(String.format("Incorrect number of arguments, %d",size));
                }
                byte[] depSpecBytes2=args.get(2);
                SmartContractPackage.SmartContractDeploymentSpec spec=null;
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
                    Policies.SignaturePolicyEnvelope signaturePolicyEnvelope= CAuthDslBuilder.signedByAnyMember(NodeUtils.getMspIDs(groupName));
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
                    essc=ByteString.copyFromUtf8("essc").toByteArray();
                }

                byte[] vssc=null;
                if(size>5 && args.get(5).length!=0){
                    vssc=args.get(5);
                }else{
                    vssc=ByteString.copyFromUtf8("vssc").toByteArray();
                }

                byte[] collectionsConfig=null;
                // we proceed with a non-nil collection configuration only if
                // we support the PrivateChannelData capability
                // TODO: 5/21/18  ac.getCapabilities() == null
                if(size > 6 && ac.getCapabilities().isPrivateGroupData()==true){
                    collectionsConfig=args.get(6);
                }

                SmartContractDataPackage.SmartContractData scd=null;
                try {
                    scd=executeDeployOrUpgrade(stub, groupName, spec, ep, essc, vssc, collectionsConfig, function);
                } catch (SysSmartContractException e) {
                    return newErrorResponse(String.format("ExecuteDeployOrUpgrade failed,%s",e.getMessage()));
                }

                byte[] cdbytes= new byte[0];
                cdbytes = scd.toByteArray();

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

                byte[] scbytes=getSmartContractInstance(stub, smartContractName2);
               if(scbytes==null){
                    log.error("Error getting smartcontract {} on group {}",smartContractName2,groupName2);
                    return newErrorResponse(String.format("Error getting smartcontract %s on group %s",groupName2,resource));
                }
                switch (function){
                    case GET_SC_INFO:
                        SmartContractDataPackage.SmartContractData scd2=null;
                        try {
                            scd2=getSmartContractData(smartContractName2, scbytes);
                        } catch (SysSmartContractException e) {
                            return newErrorResponse(String.format("%s",e.getMessage()));
                        }
                        return newSuccessResponse(scd2.getName());
                    case GET_SC_DATA:
                        return newSuccessResponse(scbytes);
                    default:
                        SmartContractCode smartContractCode=null;
                        try {
                            smartContractCode=getSmartContractCode(smartContractName2, scbytes);
                        } catch (SysSmartContractException e) {
                            return newErrorResponse(String.format("%s",e.getMessage()));
                        }
                        return newSuccessResponse(smartContractCode.getDepSpecBytes());
                }
            case GET_SMART_CONTRACTS:
                if(size!=1){
                    return newErrorResponse(String.format("Incorrect number of arguments,expected 1,provided %d",size));
                }
                //2. check local MSP Admins policy
                try {
                    policyChecker.checkPolicyNoGroup(MSPPrincipalGetter.Admins,sp);
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
                    policyChecker.checkPolicyNoGroup(MSPPrincipalGetter.Admins,sp);
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
                                      SmartContractDataPackage.SmartContractData data) throws SysSmartContractException{
        if(!sscProvider.isSysSmartContract(data.getEssc())){
            String msg=String.format("%s is not a valid endorsement system smartcontract",data.getEssc());
            throw new SysSmartContractException(msg);
        }
        if(!sscProvider.isSysSmartContract(data.getVssc())){
            String msg=String.format("%s is not a valid endorsement system smartcontract",data.getVssc());
            throw new SysSmartContractException(msg);
        }

        byte[] scdBytes = data.toByteArray();
        if(scdBytes==null){
            String msg=String.format("Marshall smartcontractdata %s get null",data.getName());
            throw new SysSmartContractException(msg);
        }

        stub.putState(data.getName(),scdBytes);
    }


    /**
     * putSmartcontractCollectionData adds collection data for the smartcontract
     * @param stub
     * @param data
     * @param collectionConfigBytes
     */
    private void putSmartContractCollectionData(ISmartContractStub stub,
                                                SmartContractDataPackage.SmartContractData data,
                                                byte[] collectionConfigBytes)
            throws  SysSmartContractException{
        if(data==null){
            String msg=String.format("Null SmartcontractData");
            throw new SysSmartContractException(msg);
        }

        // TODO: 5/21/18  collectionConfigBytes == null
        if(collectionConfigBytes == null || collectionConfigBytes.length==0){
            log.debug("No collection configuration specified");
            return;
        }
        // TODO: 5/21/18 没有测试的部分
        Collection.CollectionConfigPackage collectionConfigPackage=null;
        try {
            collectionConfigPackage=Collection.CollectionConfigPackage.parseFrom(collectionConfigBytes);
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Invalid collection configuration supplied for smartcontract %s ,version %s:%s",
                    data.getName(),data.getVersion(),e.getMessage());
            throw new SysSmartContractException(msg);
        }

        CollectionStoreSupport collectionStoreSupport=new CollectionStoreSupport();
        String key = collectionStoreSupport.buildCollectionKVSKey(data.getName());
        log.info("begin=========>" + key);
        byte[] existingCollection = stub.getState(key);
        log.info("=========>" + existingCollection);
        if(existingCollection!=null){
            String msg=String.format("collection data should not exist for chaincode %s:%s",data.getName(),data.getVersion());
            throw new SysSmartContractException(msg);
        }

        stub.putState(key,collectionConfigBytes);
    }

    /**
     * checks for existence of smartcontract on the given group
     * @param stub
     * @param contractName
     * @return
     */
    private byte[] getSmartContractInstance(ISmartContractStub stub,
                                            String contractName)
    {
        byte[] scdBytes = stub.getState(contractName);
        return scdBytes;
    }

    /**
     * gets the cd out of the bytes
     * @param contractName
     * @param scdBytes
     * @return
     * @throws SysSmartContractException
     */
    private SmartContractDataPackage.SmartContractData getSmartContractData(String contractName, byte[] scdBytes)
            throws SysSmartContractException
    {
        SmartContractDataPackage.SmartContractData scd=null;
        try {
            scd=SmartContractDataPackage.SmartContractData.parseFrom(scdBytes);
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("SmartContractdata for%s unmarshal failed ",contractName);
            throw new SysSmartContractException(msg);
        }
        if(!(scd.getName().equals(contractName))){
            String msg=String.format("SmartContract mismatch:%s!=%s",contractName,scd.getName());
            throw new SysSmartContractException(msg);
        }
        return scd;
    }


    /**
     * checks for existence of smartcontract on the given chain
     * @param name
     * @param scdBytes
     * @return
     */
    private SmartContractCode getSmartContractCode(String name, byte[] scdBytes)throws SysSmartContractException{
        SmartContractDataPackage.SmartContractData scd = getSmartContractData(name, scdBytes);
        ISmartContractPackage scPack =null;
        try {
            scPack =support.getSmartContractFromLocalStorage(name, scd.getVersion());
        } catch (JavaChainException e) {
            String msg=String.format("Get smartcontract %s from localstorage failed:%s",name ,e.getMessage());
            throw new SysSmartContractException(msg);
        }
        try {
            scPack.validateSC(scd);
        } catch (JavaChainException e) {
            String msg=String.format("InvalidSCOnFSError:%s",e.getMessage());
            throw new SysSmartContractException(msg);
        }
        //these are guaranteed to be non-nil because we got a valid scpack
        SmartContractPackage.SmartContractDeploymentSpec depSpec = scPack.getDepSpec();
        byte[] depSpecBytes = scPack.getDepSpecBytes();
        SmartContractCode smartContractCode=new SmartContractCode(scd,depSpec,depSpecBytes);
        return smartContractCode;
    }


    /**
     * getSmartcontracts returns all smartcontracts instantiated on this LSSC's group
     * @param stub
     * @return
     */
    private SmartContractResponse getSmartContracts(ISmartContractStub stub){
        IQueryResultsIterator<IKeyValue> itr = stub.getStateByRange("", "");
        Iterator<IKeyValue> iterator = itr.iterator();
        ArrayList<Query.SmartContractInfo> scInfoList=new  ArrayList<Query.SmartContractInfo>();
        while(iterator.hasNext()){
            IKeyValue response = iterator.next();
            SmartContractDataPackage.SmartContractData data=null;
            try {
                data=SmartContractDataPackage.SmartContractData.parseFrom(response.getValue());
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("InvalidProtocolBufferException:%s",e.getMessage());
                return newErrorResponse(msg);
            }
            String path="";
            String input="";
            ISmartContractPackage scPack =null;
            //if smartcontract is not installed on the system we won't have
            //data beyond name and version
            try {
                scPack = support.getSmartContractFromLocalStorage(data.getName(), data.getVersion());
                path=scPack.getDepSpec().getSmartContractSpec().getSmartContractId().getPath();
                input=scPack.getDepSpec().getSmartContractSpec().getInput().toString();
            } catch (JavaChainException e) {
                log.error(e.getMessage());
            }

            Query.SmartContractInfo.Builder builder = Query.SmartContractInfo.newBuilder();
            builder.setName(data.getName());
            builder.setVersion(data.getVersion());
            builder.setPath(path);
            builder.setInput(input);
            builder.setEssc(data.getEssc());
            builder.setVssc(data.getVssc());
            Query.SmartContractInfo scInfo=builder.build();
            scInfoList.add(scInfo);
        }

        Query.SmartContractQueryResponse scqr = Query.SmartContractQueryResponse.newBuilder().addAllSmartContracts(scInfoList).build();
        byte[] scqrBytes = scqr.toByteArray();
        return newSuccessResponse(scqrBytes);
    }

    /**
     * getInstalledChaincodes returns all smartcontracts installed on the node
     * @return
     */
    private SmartContractResponse getInstalledSmartContracts(){
        // get smartcontract query response proto which contains information about all
        // installed smartcontracts
        Query.SmartContractQueryResponse scqr =null;
        try {
            scqr = support.getSmartContractsFromLocalStorage();
        }catch (JavaChainException e){
            return newErrorResponse(e.getMessage());
        }
        byte[] scqrBytes = scqr.toByteArray();

        return newSuccessResponse(scqrBytes);
    }


    /**
     * check validity of group name
     * @param group
     * @return
     */
    private void isValidGroupName(String group) throws SysSmartContractException{
         if(group==null || group.isEmpty()){
             String msg=String.format("EmptyStringErr");
             throw new SysSmartContractException(msg);
         }
    }


    /**
     * checkSmartcontractName checks the validity of smartcontract name. Smartcontract names
     * should never be blank and should only consist of alphanumerics, '_', and '-'
     * @param contractName
     * @return
     */
    private void checkSmartContractName(String contractName) throws SysSmartContractException{
        if (contractName == "") {
            String msg=String.format("EmptySmartContractNameErr");
            throw new SysSmartContractException(msg);
        }
        if(isValidSmartContractNameOrVersion(contractName,allowedCharsSmartContractName)==false){
            String msg=String.format("InvalidChaincodeNameErr:%s",contractName);
            throw new SysSmartContractException(msg);
        }
    }


    /**
     * checkSmartcontractVersion checks the validity of smartcontract version. Versions
     * should never be blank and should only consist of alphanumerics, '_',  '-',
     * '+', and '.'
     * @param contractName
     * @param version
     * @return
     */
    private void checkSmartContractVersion(String contractName,String version) throws SysSmartContractException{
        if (version == "") {
            String msg=String.format("EmptySmartContractVersionErr");
            throw new SysSmartContractException(msg);
        }
        if(isValidSmartContractNameOrVersion(version,allowedCharsVersion)==false){
            String msg=String.format("InvalidChaincodeVersionErr:%s",version);
            throw new SysSmartContractException(msg);
        }

    }

    private boolean isValidSmartContractNameOrVersion(String scNameOrVersion,
                                                      String regExp){
        Pattern p= Pattern.compile(regExp);
        Matcher m=p.matcher(scNameOrVersion);
        return m.matches();
    }

    /**
     * executeInstall implements the "install" Invoke transaction
     * @param stub
     * @param scBytes
     */
    private void executeInstall(ISmartContractStub stub,byte[] scBytes) throws SysSmartContractException{
        log.debug("Execute install.");
        ISmartContractPackage scPack=null;
        try {
            scPack=SmartContractProvider.getSmartContractPackage(scBytes);
        } catch (JavaChainException e) {
            throw new SysSmartContractException(e.getMessage());
        }
        if(scPack==null){
            String message="Get Null SC package";
            throw new SysSmartContractException(message);
        }

        SmartContractPackage.SmartContractDeploymentSpec scds = scPack.getDepSpec();
        if(scds==null){
            String message="Null deployment spec from from the SC package";
            throw new SysSmartContractException(message);
        }

        try{
            checkSmartContractName(scds.getSmartContractSpec().getSmartContractId().getName());
        }catch(SysSmartContractException e){
            throw e;
        }

        try{
            checkSmartContractVersion(scds.getSmartContractSpec().getSmartContractId().getName(),
                    scds.getSmartContractSpec().getSmartContractId().getVersion());
        }catch(SysSmartContractException e){
            throw e;
        }

        // Get any statedb artifacts from the chaincode package, e.g. couchdb index definitions
        byte[] statedbArtifactsTar=null;
        try {
            statedbArtifactsTar=SmartContractProvider.extractStateDBArtifactsFromSCPackage(scPack);
        } catch (JavaChainException e) {
            throw new SysSmartContractException(e.getMessage());
        }

        SmartContractDefinition smartContractDefinition=new SmartContractDefinition(
                scPack.getSmartContractData().getName(),
                scPack.getSmartContractData().getVersion(),
                scPack.getId());
        // HandleChaincodeInstall will apply any statedb artifacts (e.g. couchdb indexes) to
        // any channel's statedb where the chaincode is already instantiated
        // Note - this step is done prior to PutChaincodeToLocalStorage() since this step is idempotent and harmless until endorsements start,
        // that is, if there are errors deploying the indexes the chaincode install can safely be re-attempted later.
        try {
            ScEventManager.getMgr().handleSmartContractInstall(smartContractDefinition,statedbArtifactsTar);
        } catch (JavaChainException e) {
            throw new SysSmartContractException(e);
        }
        // Finally, if everything is good above, install the chaincode to local peer file system so that endorsements can start

        //TODO:add by zhouhui for test,保证测试通过，因正式环境未使用Spring,所以未自动填充值，导致值为null
        if(support == null){
            support = new LsscSupport();
        }
        support.putSmartContractToLocalStorage(scPack);

        log.info("Installed Smartcontract {} Version {} to node",
                scPack.getSmartContractData().getName(),
                scPack.getSmartContractData().getVersion());
    }


    /**
     * executeDeployOrUpgrade routes the code path either to executeDeploy or executeUpgrade
     * depending on its function argument
     * @param stub
     * @param groupName
     * @param scds
     * @param policy
     * @param essc
     * @param vssc
     * @param collectionConfigBytes
     * @param function
     * @return
     * @throws SysSmartContractException
     */
    private SmartContractDataPackage.SmartContractData executeDeployOrUpgrade(ISmartContractStub stub,
                                                     String groupName,
                                                     SmartContractPackage.SmartContractDeploymentSpec scds,
                                                     byte [] policy,
                                                     byte [] essc,
                                                     byte [] vssc,
                                                     byte [] collectionConfigBytes,
                                                     String function)
            throws  SysSmartContractException{
        try{
            checkSmartContractName(scds.getSmartContractSpec().getSmartContractId().getName());
            checkSmartContractVersion(scds.getSmartContractSpec().getSmartContractId().getName(),
                    scds.getSmartContractSpec().getSmartContractId().getVersion());
        }catch (SysSmartContractException e){
            throw e;
        }

        ISmartContractPackage scPack =null;
        String name=scds.getSmartContractSpec().getSmartContractId().getName();
        String version=scds.getSmartContractSpec().getSmartContractId().getVersion();
        try {
            scPack=support.getSmartContractFromLocalStorage(name,version);
        } catch (JavaChainException e) {
            String msg=String.format("Get smartcontract %s from localstorage failed:%s",name ,e.getMessage());
            throw new SysSmartContractException(msg);
        }
        SmartContractDataPackage.SmartContractData smartcontractData = scPack.getSmartContractData();

        switch (function){
            case DEPLOY:
                return executeDeploy(stub,groupName,scds,policy,essc,vssc,smartcontractData,scPack,collectionConfigBytes);
            case UPGRADE:
                return executeUpgrade(stub,groupName,scds,policy,essc,vssc,smartcontractData,scPack);
            default:
                log.error("Programming error, unexpected function '%s'",function);
                return null;
        }
    }


    /**
     * executeDeploy implements the "instantiate" Invoke transaction
     * @param stub
     * @param groupName
     * @param scds
     * @param policy
     * @param essc
     * @param vssc
     * @param scdata
     * @param scPackage
     * @param collectionConfigBytes
     * @return
     */
    private SmartContractDataPackage.SmartContractData executeDeploy(
            ISmartContractStub stub,
            String groupName,
            SmartContractPackage.SmartContractDeploymentSpec scds,
            byte[] policy,
            byte[] essc,
            byte[] vssc,
            SmartContractDataPackage.SmartContractData scdata,
            ISmartContractPackage scPackage,
            byte[] collectionConfigBytes
    )throws SysSmartContractException{
        //just test for existence of the smartcontract in the LSSC
        byte[] instanceBytes=getSmartContractInstance(stub, scds.getSmartContractSpec().getSmartContractId().getName());
        if(instanceBytes!=null && instanceBytes.length>0){
            String msg=String.format("The smartcontract %s has existed",scds.getSmartContractSpec().getSmartContractId().getName());
            throw new SysSmartContractException(msg);
        }
        SmartContractDataPackage.SmartContractData.Builder builder = scdata.toBuilder();
        builder.setEsscBytes(ByteString.copyFrom(essc));
        builder.setVsscBytes(ByteString.copyFrom(vssc));
        builder.setPolicy(ByteString.copyFrom(policy));
        builder.setInstantiationPolicy(ByteString.copyFrom(support.getInstantiationPolicy(groupName,scPackage)));
        SmartContractDataPackage.SmartContractData builtScData=builder.build();

        ProposalPackage.SignedProposal signedProposal = stub.getSignedProposal();
        support.checkInstantiationPolicy(signedProposal,groupName,builtScData.getInstantiationPolicy().toByteArray());

        putSmartContractData(stub,builtScData);
        // TODO: 5/21/18 modify return scdata to builtScData
        putSmartContractCollectionData(stub,builtScData,collectionConfigBytes);
        return builtScData;
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
    private SmartContractDataPackage.SmartContractData executeUpgrade(
            ISmartContractStub stub,
            String groupName,
            SmartContractPackage.SmartContractDeploymentSpec scds,
            byte[] policy,
            byte[] essc,
            byte[] vssc,
            SmartContractDataPackage.SmartContractData scdata,
            ISmartContractPackage scPackage
    )throws  SysSmartContractException{
        String smartcontractName=scds.getSmartContractSpec().getSmartContractId().getName();
        // check for existence of chaincode instance only (it has to exist on the channel)
        // we dont care about the old chaincode on the FS. In particular, user may even
        // have deleted it
        byte[] instanceBytes=getSmartContractInstance(stub, scds.getSmartContractSpec().getSmartContractId().getName());
        if(instanceBytes==null){
            String msg=String.format("The smartcontract %s not found",smartcontractName);
            throw new SysSmartContractException(msg);
        }
        //we need the cd to compare the version
        SmartContractDataPackage.SmartContractData scdLedger = getSmartContractData(smartcontractName, instanceBytes);
        if(scdLedger==null){
            String msg=String.format("SmartContractData for the smartcontract %s is null",smartcontractName);
            throw new SysSmartContractException(msg);
        }
        if(scdLedger.getVersion()==scds.getSmartContractSpec().getSmartContractId().getVersion()){
            String msg=String.format("The smartcontract %s to upgrade has the same version as the deployed code",smartcontractName);
            throw new SysSmartContractException(msg);
        }
        //do not upgrade if instantiation policy is violated
        if(scdLedger.getInstantiationPolicy()==null){
            String msg=String.format("Instantiation Policy Missing",smartcontractName);
            throw new SysSmartContractException(msg);
        }
        // get the signed instantiation proposal
        ProposalPackage.SignedProposal sp = stub.getSignedProposal();
        support.checkInstantiationPolicy(sp,groupName,scdLedger.getInstantiationPolicy().toByteArray());
        //retain chaincode specific data and fill channel specific ones
        SmartContractDataPackage.SmartContractData.Builder builder = scdata.toBuilder();
        builder.setEssc(ByteString.copyFrom(essc).toStringUtf8());
        builder.setVssc(ByteString.copyFrom(vssc).toStringUtf8());
        builder.setPolicy(ByteString.copyFrom(policy));
        builder.setInstantiationPolicy(ByteString.copyFrom(support.getInstantiationPolicy(groupName,scPackage)));
        SmartContractDataPackage.SmartContractData builtScData=builder.build();
        support.checkInstantiationPolicy(sp,groupName,builtScData.getInstantiationPolicy().toByteArray());

        putSmartContractData(stub,builtScData);
        return builtScData;
    }


}

