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
package org.bcia.julongchain.core.ssc.vssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policies.IPolicyProvider;
import org.bcia.julongchain.common.policycheck.policies.PolicyProvider;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.core.common.privdata.CollectionStoreSupport;
import org.bcia.julongchain.core.common.privdata.ICollectionStore;
import org.bcia.julongchain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.RwSetUtil;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.protos.common.Collection;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 验证对LSSC的调用而设立的支持类
 *
 * @author sunianle
 * @date 4/28/18
 * @company Dingxuan
 */
public class VSSCSupportForLsscInvocation {
    public static void validateLSSCInvocation(
            ISmartContractStub stub,
            String groupID,
            Common.Envelope envelope,
            TransactionPackage.SmartContractActionPayload scap,
            Common.Payload payload,
            IApplicationCapabilities ac,
            ICollectionStore collectionStore,
            ISystemSmartContractProvider sscProvider,
            JavaChainLog log
    )throws SysSmartContractException{
        ProposalPackage.SmartContractProposalPayload scpp=null;
        try {
            scpp=ProposalPackage.SmartContractProposalPayload.parseFrom(scap.getSmartContractProposalPayload());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("VSSC error: GetSmartContractProposalPayload failed, err %s",e.getMessage());
            throw new SysSmartContractException(msg);
        }
        SmartContractPackage.SmartContractInvocationSpec scis=null;
        try {
            SmartContractPackage.SmartContractInvocationSpec.parseFrom(scpp.getInput());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("VSSC error: Unmarshal SmartContractInvocationSpec failed, err %s",e.getMessage());
            throw new SysSmartContractException(msg);
        }

        if(scis.getSmartContractSpec()==null ||
                scis.getSmartContractSpec().getInput()==null ||
                scis.getSmartContractSpec().getInput().getArgsList()==null){
            log.error("VSSC error: committing invalid vssc invocation");
            throw new SysSmartContractException("VSSC error: committing invalid vssc invocation");
        }

        String lsscFunc=scis.getSmartContractSpec().getInput().getArgs(0).toString();
        List<ByteString> argsList = scis.getSmartContractSpec().getInput().getArgsList();
        //去除函数的纯参数列表
        List<ByteString> argsListWithoutFunction=new ArrayList<ByteString>();
        int num=argsList.size();
        for(int i=1;i<num;i++){
            argsListWithoutFunction.add(argsList.get(i));
        }
        log.debug("VSSC info: ValidateLSSCInvocation acting on %s %s",lsscFunc,argsListWithoutFunction.toString());

        switch (lsscFunc){
            case LSSC.UPGRADE:
                ;
            case LSSC.DEPLOY:
                log.debug("VSSC info: validating invocation of lssc function %s on arguments %s",lsscFunc,argsListWithoutFunction.toString());
                int size=argsListWithoutFunction.size();
                if(size<2){
                    String msg=String.format("Wrong number of arguments for invocation lssc(%s): expected at least 2, received %d",lsscFunc,size);
                    throw new SysSmartContractException(msg);
                }
                if((ac.isPrivateGroupData()==false && size>5  ||
                        (ac.isPrivateGroupData()==true) && size>6 )){
                    String msg=String.format("Wrong number of arguments for invocation lssc(%s): expected at least 2, received %d",lsscFunc,size);
                    throw new SysSmartContractException(msg);
                }
                SmartContractPackage.SmartContractDeploymentSpec scds;
                try {
                    scds=SmartContractPackage.SmartContractDeploymentSpec.parseFrom(argsListWithoutFunction.get(1));
                } catch (InvalidProtocolBufferException e) {
                    String msg=String.format("GetSmartContractDeploymentSpec error %s",e.getMessage());
                    throw new SysSmartContractException(msg);
                }

                if(scds==null || scds.getSmartContractSpec()==null || scds.getSmartContractSpec().getSmartContractId()==null||
                        scap.getAction()==null || scap.getAction().getProposalResponsePayload()==null){
                    String msg=String.format("VSSC error: invocation of lssc(%s) does not have appropriate arguments",lsscFunc);
                    throw new SysSmartContractException(msg);
                }

                //get the rwset
                ProposalResponsePackage.ProposalResponsePayload pRespPayload =null;
                try {
                    pRespPayload =ProposalResponsePackage.ProposalResponsePayload.parseFrom(scap.getAction().getProposalResponsePayload());
                } catch (InvalidProtocolBufferException e) {
                    String msg=String.format("GetProposalResponsePayload error %s",e.getMessage());
                    throw new SysSmartContractException(msg);
                }
                if(pRespPayload.getExtension()==null){
                    throw new SysSmartContractException("null pRespPayload.Extension");
                }

                ProposalPackage.SmartContractAction respPayload = null;
                try {
                    respPayload=ProposalPackage.SmartContractAction.parseFrom(pRespPayload.getExtension());
                } catch (InvalidProtocolBufferException e) {
                    throw new SysSmartContractException(String.format("GetSmartContractAction error %s",e.getMessage()));
                }
                Rwset.TxReadWriteSet rwSetProto=null;
                try {
                    rwSetProto=Rwset.TxReadWriteSet.parseFrom(respPayload.getResults());
                } catch (InvalidProtocolBufferException e) {
                    throw new SysSmartContractException(e.getMessage());
                }
                TxRwSet txRwSet=null;
                // TODO: 6/4/18 modify by sunzongyu, catch exception when get TxRwSet from proto message.
                try {
                    txRwSet=RwSetUtil.txRwSetFromProtoMsg(rwSetProto);
                } catch (LedgerException e) {
                    String msg=String.format("Could not unmarshal from reSetProto, with error %s" + e.getMessage());
                    throw new SysSmartContractException(msg);
                }
                List<NsRwSet> nsSet = txRwSet.getNsRwSets();

                //extract the rwset for lscc
                KvRwset.KVRWSet lsscRwSet=null;
                for(NsRwSet ns:nsSet){
                    log.debug("Namespace %s",ns.getNameSpace());
                    if(ns.getNameSpace()=="lssc"){
                        lsscRwSet = ns.getKvRwSet();
                        break;
                    }
                }

                //retrieve from the ledger the entry for the smartcontract at hand
                InstantiatedSCResult instantiatedSCResult=getInstantiatedSC(sscProvider,groupID,scds.getSmartContractSpec().getSmartContractId().getName());

                /******************************************/
                /* security check 0 - validation of rwset */
                /******************************************/
                // there has to be a write-set
                if(lsscRwSet==null){
                    String msg=String.format("No read write set for lssc was found");
                    throw new SysSmartContractException(msg);
                }
                // there must be at least one write
                if(lsscRwSet.getWritesCount()<1){
                    String msg="LSSC must issue at least one single putState upon deploy/upgrade";
                    throw new SysSmartContractException(msg);
                }
                // the first key name must be the smartcontract id
                if(lsscRwSet.getWrites(0).getKey()!=scds.getSmartContractSpec().getSmartContractId().getName()){
                    String msg=String.format("Expected key %s, found %s",scds.getSmartContractSpec().getSmartContractId().getName(),
                            lsscRwSet.getWrites(0).getKey());
                    throw new SysSmartContractException(msg);
                }

                // the value must be a SmartContractData struct
                SmartContractDataPackage.SmartContractData cdRwSet=null;
                try {
                    cdRwSet=SmartContractDataPackage.SmartContractData.parseFrom(lsscRwSet.getWrites(0).getValue());
                } catch (InvalidProtocolBufferException e) {
                    String msg=String.format("Unmarhsalling of ChaincodeData failed, error %s",e.getMessage());
                    throw new SysSmartContractException(msg);
                }
                // the name must match
                if(cdRwSet.getName()!=scds.getSmartContractSpec().getSmartContractId().getName()){
                    String msg=String.format("Expected sc name %s,found %s",scds.getSmartContractSpec().getSmartContractId().getName(),
                            cdRwSet.getName());
                    throw new SysSmartContractException(msg);
                }
                // the version must match
                if(cdRwSet.getVersion()!=scds.getSmartContractSpec().getSmartContractId().getVersion()){
                    String msg=String.format("Expected sc version %s,found %s",scds.getSmartContractSpec().getSmartContractId().getVersion(),
                            cdRwSet.getVersion());
                    throw new SysSmartContractException(msg);
                }
                // it must only write to 2 namespaces: LSSC's and the sc that we are deploying/upgrading
                for(NsRwSet ns:nsSet){
                    if(ns.getNameSpace()!="lssc" && ns.getNameSpace()!=cdRwSet.getName()
                            && ns.getKvRwSet().getWritesCount()>0){
                        String msg=String.format("LSSC invocation is attempting to write to namespace %s",ns.getNameSpace());
                        throw new SysSmartContractException(msg);
                    }
                }

                log.debug("Validating {} for sc {} version {}",lsscFunc,cdRwSet.getName(),cdRwSet.getVersion());

                switch(lsscFunc){
                    case LSSC.DEPLOY:
                        /****************************************************************************/
                        /* security check 0.a - validation of rwset (and of collections if enabled) */
                        /****************************************************************************/
                        if(ac.isPrivateGroupData()){
                            //do extra validation for collections
                            validateDeployRWSetAndCollection(lsscRwSet,cdRwSet,argsListWithoutFunction,groupID,
                                    scds.getSmartContractSpec().getSmartContractId().getName(),collectionStore);
                        }
                        else {
                            // there can only be a single ledger write
                            if(lsscRwSet.getWritesCount()!=1){
                                String msg=String.format("LSSC can only issue a single putState upon deploy/upgrade");
                                throw new SysSmartContractException(msg);
                            }
                        }
                        /*****************************************************/
                        /* security check 1 - check the instantiation policy */
                        /*****************************************************/
                        ByteString policy = cdRwSet.getInstantiationPolicy();
                        if(policy==null){
                            String msg=String.format("No instantiation policy was specified");
                            throw new SysSmartContractException(msg);
                        }
                        // FIXME: could we actually pull the cds package from the
                        // file system to verify whether the policy that is specified
                        // here is the same as the one on disk?
                        // PROS: we prevent attacks where the policy is replaced
                        // CONS: this would be a point of non-determinism
                        checkInstantiationPolicy(groupID,envelope,policy.toByteArray(),payload,log);

                        /******************************************************************/
                        /* security check 2 - cc not in the LSSC table of instantiated cc */
                        /******************************************************************/
                        if(instantiatedSCResult.bSCExistsOnLedger){
                            String msg=String.format("SmartContract %s is already instantiated",
                                    scds.getSmartContractSpec().getSmartContractId().getName());
                            throw new SysSmartContractException(msg);
                        }
                        break;

                    case LSSC.UPGRADE:
                        /********************************************/
                        /* security check 0.a - validation of rwset */
                        /********************************************/
                        // there can only be a single ledger write
                        if(lsscRwSet.getWritesCount()!=1){
                            throw new SysSmartContractException("LSSC can only issue one putState upon upgrade");
                        }
                        /**************************************************************/
                        /* security check 1 - cc in the LCCC table of instantiated cc */
                        /**************************************************************/
                        if(instantiatedSCResult.bSCExistsOnLedger==false){
                            String msg=String.format("Upgrading non-existent smartcontract %s",
                                    scds.getSmartContractSpec().getSmartContractId().getName());
                            throw new SysSmartContractException(msg);
                        }
                        /*****************************************************/
                        /* security check 2 - check the instantiation policy */
                        /*****************************************************/
                        ByteString pol = instantiatedSCResult.scd.getInstantiationPolicy();
                        if(pol==null){
                            throw new SysSmartContractException("No instantiation policy was specified");
                        }
                        // FIXME: could we actually pull the cds package from the
                        // file system to verify whether the policy that is specified
                        // here is the same as the one on disk?
                        // PROS: we prevent attacks where the policy is replaced
                        // CONS: this would be a point of non-determinism
                        checkInstantiationPolicy(groupID,envelope,pol.toByteArray(),payload,log);

                        /**********************************************************/
                        /* security check 3 - existing cc's version was different */
                        /**********************************************************/
                        if(instantiatedSCResult.scd.getVersion()==scds.getSmartContractSpec().getSmartContractId().getVersion()){
                            String msg=String.format("Existing version of the sc on the ledger (%s) should be different from the upgraded one",
                                    scds.getSmartContractSpec().getSmartContractId().getVersion());
                            throw new SysSmartContractException(msg);
                        }

                        /******************************************************************/
                        /* security check 4 - check the instantiation policy in the rwset */
                        /******************************************************************/
                        if(ac.isValidation()){
                            ByteString polNew = cdRwSet.getInstantiationPolicy();
                            if(polNew==null){
                                throw new SysSmartContractException("No instantiation policy was specified");
                            }
                            //no point in checking it again if they are the same policy
                            if(Arrays.equals(polNew.toByteArray(),pol.toByteArray())==false){
                                checkInstantiationPolicy(groupID,envelope,polNew.toByteArray(),payload,log);
                            }

                        }

                        //all is good!
                    default:
                        throw new SysSmartContractException(String.format("VSSC error: committing an invocation of function %s of lssc is invalid",lsscFunc));
                }
        }
    }

    /**
     *checkInstantiationPolicy evaluates an instantiation policy against a signed proposal
     * @param groupID
     * @param env
     * @param instantiationPolicy
     * @param payload
     * @return
     */
    private static void checkInstantiationPolicy(String groupID, Common.Envelope env, byte []instantiationPolicy,
                                          Common.Payload payload,JavaChainLog log)throws SysSmartContractException{
        IMspManager mspManager = GlobalMspManagement.getManagerForChain(groupID);
        if(mspManager==null){
            String msg=String.format("MSP getPolicyManager for group %s is null, aborting",groupID);
            throw new SysSmartContractException(msg);
        }
        IPolicyProvider policyProvider =new PolicyProvider(mspManager);
        IPolicy policy=null;
        try {
            policy= policyProvider.makePolicy(instantiationPolicy);
        } catch (PolicyException e) {
            throw new SysSmartContractException(e.getMessage());
        }
        log.debug("VSSC info:check instantiationPolicy starts");
        //get the signature header
        Common.SignatureHeader signatureHeader =null;
        try {
            signatureHeader=Common.SignatureHeader.parseFrom(payload.getHeader().getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            throw new SysSmartContractException(e.getMessage());
        }
        SignedData signedData=new SignedData(env.getPayload().toByteArray(),
                signatureHeader.getCreator().toByteArray(),
                env.getSignature().toByteArray());
        List<SignedData> datas=new ArrayList<SignedData>();
        datas.add(signedData);
        try {
            policy.evaluate(datas);
        } catch (PolicyException e) {
            String msg=String.format("instantiation policy violation:%s",e.getMessage());
            throw new SysSmartContractException(msg);
        }
    }

    private static InstantiatedSCResult getInstantiatedSC(ISystemSmartContractProvider sscProvider,
                      String groupID, String name) throws SysSmartContractException{
        IQueryExecutor qe=null;
        try {
            qe=sscProvider.getQueryExecutorForLedger(groupID);
        } catch (JavaChainException e) {
            String msg=String.format("Could not retrieve QueryExecutor for group %s, error %s",
                                            groupID,e.getMessage());
            throw new SysSmartContractException(msg);
        }
        byte[] bytes =null;
        try {
            bytes=qe.getState("lssc", name);
        } catch (LedgerException e) {
            String msg=String.format("Could not retrieve state for smartcontract %s on group %s, error %s",
                    name,groupID,e.getMessage());
            throw new SysSmartContractException(msg);
        }

        if(bytes==null){
            return null;
        }


        SmartContractDataPackage.SmartContractData scd=null;
        try {
            scd=SmartContractDataPackage.SmartContractData.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Unmarshalling SmartContractQueryResponse failed, error %s",
                   e.getMessage());
            throw new SysSmartContractException(msg);
        }

        InstantiatedSCResult result=new InstantiatedSCResult();
        result.scd=scd;
        result.bSCExistsOnLedger=true;

        return result;
    }

    /**
     * validateDeployRWSetAndCollection performs validation of the rwset
     * of an LSSC deploy operation and then it validates any collection
     * configuration
     * @param lsscrwset
     * @param scdRWSet
     * @param lsscArgs
     * @param groupID
     * @param smartcontractName
     * @return
     */
    private static void validateDeployRWSetAndCollection(
            KvRwset.KVRWSet lsscrwset,
            SmartContractDataPackage.SmartContractData scdRWSet,
            List<ByteString> lsscArgs,
            String groupID,
            String smartcontractName,
            ICollectionStore collectionStore
    ) throws SysSmartContractException{
        /********************************************/
        /* security check 0.a - validation of rwset */
        /********************************************/
        // there can only be one or two writes
        if(lsscrwset.getWritesCount()>2){
            throw new SysSmartContractException("LSSC can only issue one or two putState upon deploy");
        }
        /**********************************************************/
        /* security check 0.b - validation of the collection data */
        /**********************************************************/
        byte[] collectionsConfigArgs=null;
        if(lsscArgs.size()>5){
            collectionsConfigArgs=lsscArgs.get(5).toByteArray();
        }
        byte[] collectionsConfigLedger=null;
        if(lsscrwset.getWritesCount()==2){
            CollectionStoreSupport support=new CollectionStoreSupport();
            String key=support.buildCollectionKVSKey(smartcontractName);
            if(lsscrwset.getWrites(1).getKey()!=key){
                String msg=String.format("invalid key for the collection of smartcontract %s:%s; expected '%s', received '%s'",
                        scdRWSet.getName(),scdRWSet.getVersion(),key,lsscrwset.getWrites(1).getKey());
                throw new SysSmartContractException(msg);
            }
            collectionsConfigLedger=lsscrwset.getWrites(1).getValue().toByteArray();
        }

        if(Arrays.equals(collectionsConfigArgs,collectionsConfigLedger)==false){
            String msg=String.format("collection configuration mismatch for chaincode %s:%s",
                    scdRWSet.getName(),scdRWSet.getVersion());
            throw new SysSmartContractException(msg);
        }

        Collection.CollectionCriteria cc =Collection.CollectionCriteria.newBuilder().
                setChannel(groupID).setNamespace(smartcontractName).build();

        Collection.CollectionConfigPackage ccp=null;
        try {
            ccp = collectionStore.retrieveCollectionConfigPackage(cc);
        } catch (JavaChainException e) {
            String msg=e.getMessage();
            // fail if we get any error other than NoSuchCollectionError
            // because it means something went wrong while looking up the
            // older collection
            if(!msg.equals("NoSuchCollectionError")){
                String message=String.format("unable to check whether collection existed earlier for smartcontract %s:%s",
                        scdRWSet.getName(),scdRWSet.getVersion());
                throw new SysSmartContractException(message);
            }
            e.printStackTrace();
        }
        if(ccp!=null){
            String msg=String.format("collection data should not exist for smartcontract %s:%s",
                    scdRWSet.getName(),scdRWSet.getVersion());
            throw new SysSmartContractException(msg);
        }

        if(collectionsConfigArgs!=null){
            Collection.CollectionConfigPackage collectionConfigPackage =null;
            try {
                collectionConfigPackage=Collection.CollectionConfigPackage.parseFrom(collectionsConfigArgs);
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("invalid collection configuration supplied for smartcontract %s:%s",
                        scdRWSet.getName(),scdRWSet.getVersion());
                throw new SysSmartContractException(msg);
            }
        }

        // TODO: FAB-6526 - to add validation of the collections object
    }

    static class InstantiatedSCResult{
        public SmartContractDataPackage.SmartContractData scd;
        public boolean bSCExistsOnLedger;
    }
}
