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
package org.bcia.javachain.core.ssc.vssc;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.cauthdsl.CAuthDslBuilder;
import org.bcia.javachain.common.cauthdsl.PolicyProvider;
import org.bcia.javachain.common.channelconfig.ApplicationCapabilities;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.policies.IPolicyProvider;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.core.common.privdata.*;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.protos.common.Collection;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractDataPackage;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 验证系统智能合约　Validator System Smart Contract,VSSC
 * ValidatorOneValidSignature implements the default transaction validation policy,
 * which is to check the correctness of the read-write set and the endorsement
 * signatures against an endorsement policy that is supplied as argument to
 * every invoke
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class VSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(VSSC.class);

    public final static String DUPLICATED_IDENTITY_ERROR="Endorsement policy evaluation failure might be caused by duplicated identities";
    // sscProvider is the interface with which we call
    // methods of the system chaincode package without
    // import cycles
    private ISystemSmartContractProvider sscProvider;
    // collectionStore provides support to retrieve
    // collections from the ledger
    private ICollectionStore collectionStore;
    // collectionStoreSupport implements privdata.Support
    private IPrivDataSupport collectionStoreSupport;

    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        this.sscProvider= SystemSmartContractFactory.getSystemSmartContractProvider();
        this.collectionStoreSupport=new CollectionStoreSupport();
        this.collectionStore= CollectionStoreFactory.getCollectionStore(collectionStoreSupport);
        log.info("Successfully initialized VSSC");
        return newSuccessResponse();
    }

    // Invoke is called to validate the specified block of transactions
    // This validation system chaincode will check that the transaction in
    // the supplied envelope contains endorsements (that is. signatures
    // from entities) that comply with the supplied endorsement policy.
    // @return a successful SmartContractResponse (code 200) in case of success, or
    // an error otherwise
    // Note that Peer calls this function with 3 arguments, where args[0] is the
    // function name, args[1] is the Envelope and args[2] is the validation policy
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub)  {
        // TODO: document the argument in some white paper or design document
        // args[0] - function name (not used now)
        // args[1] - serialized Envelope
        // args[2] - serialized policy
        log.debug("Enter VSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<3){
            return newErrorResponse(String.format("Incorrect number of arguments , %d)",args.size()));
        }

        byte[] blockBytes=args.get(1);
        if(blockBytes.length==0){
            return newErrorResponse(String.format("No block to validate"));
        }
        byte[] policyBytes=args.get(2);
        if(policyBytes.length==0){
            return newErrorResponse(String.format("No policy supplied"));
        }

        // get the envelope...
        Common.Envelope envelope=null;
        try {
            envelope=ProtoUtils.getEnvelopeFromBlock(blockBytes);
        } catch (Exception e) {
            newErrorResponse(String.format("VSSC error: GetEnvelope failed, err %s",e.getMessage()));
        }

        // ...and the payload...
        // get the envelope...
        Common.Payload payload=null;
        try {
            payload=ProtoUtils.getPayload(envelope);
        } catch (Exception e) {
            newErrorResponse(String.format("VSSC error: GetPayload failed, err %s",e.getMessage()));
        }

        // get the policy
        Common.GroupHeader groupHeader=null;
        try {
            groupHeader=ProtoUtils.unMarshalGroupHeader(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            newErrorResponse(String.format("VSSC error: GetGroupHeader failed, err %s",e.getMessage()));
        }
        IApplicationConfig ac = this.sscProvider.getApplicationConfig(groupHeader.getGroupId());

        IMspManager manager=GlobalMspManagement.getManagerForChain(groupHeader.getGroupId());
        PolicyProvider policyProvider=new PolicyProvider(manager);
        IPolicy policy = policyProvider.newPolicy(policyBytes);

        // validate the payload type
        if(groupHeader.getType()!= Common.HeaderType.ENDORSER_TRANSACTION.getNumber()){
            log.error("Only Endorser Transactions are supported, provided type %d",groupHeader.getType());
            return newErrorResponse(String.format("Only Endorser Transactions are supported, provided type %d",groupHeader.getType()));
        }
        // ...and the transaction...
        TransactionPackage.Transaction transaction =null;
        try {
            transaction = ProtoUtils.getTransaction(payload.getData());
        } catch (InvalidProtocolBufferException e) {
            log.error("VSSC error: GetTransaction failed, err %s",e.getMessage());
            return newErrorResponse(String.format("VSSC error: GetTransaction failed, err %s",e.getMessage()));
        }

        // loop through each of the actions within
        List<TransactionPackage.TransactionAction> list = transaction.getActionsList();
        for (TransactionPackage.TransactionAction action:list) {
            TransactionPackage.SmartContractActionPayload scap =null;
            try {
                scap=TransactionPackage.SmartContractActionPayload.parseFrom(action.getPayload());
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("VSSC error: GetSmartContractActionPayload failed, err %s",e.getMessage());
                log.error(msg);
                return newErrorResponse(msg);
            }
            List<SignedData> signatureSet =null;
            try {
                signatureSet=deduplicateIdentity(scap);
            } catch (SysSmartContractException e) {
                return newErrorResponse(e.getMessage());
            }
            try {
                policy.evaluate(signatureSet);
            } catch (PolicyException e) {
                log.warn("Endorsement policy failure for transaction txid={}, err:{}",groupHeader.getTxId(),e.getMessage());
                if(signatureSet.size()<scap.getAction().getEndorsementsCount()){
                    // Warning: duplicated identities exist, endorsement failure might be cause by this reason
                    return newErrorResponse(DUPLICATED_IDENTITY_ERROR);
                }
                return newErrorResponse(String.format("VSSC error: policy evaluation failed, err %s",e.getMessage()));
            }

            ProposalPackage.SmartContractHeaderExtension hdrExt =null;
            try {
                hdrExt = ProposalPackage.SmartContractHeaderExtension.parseFrom(groupHeader.getExtension());
            } catch (InvalidProtocolBufferException e) {
                String msg=String.format("VSSC error: GetSmartContractHeaderExtension failed, err %s",e.getMessage());
                log.error(msg);
                return newErrorResponse(msg);
            }
            //do some extra validation that is specific to lssc
            if(hdrExt.getSmartContractId().getName()=="LSSC"){
                log.debug("VSSC info: doing special validation for LSSC");
                try {
                    validateLSSCInvocation(stub,groupHeader.getGroupId(),envelope,scap,payload,ac.getCapabilities());
                } catch (SysSmartContractException e) {
                    String msg=String.format("VSSC error: ValidateLSSCInvocation failed, err %s",e.getMessage());
                    return newErrorResponse(msg);
                }
            }
        }
        log.debug("VSSC exits successfully");
        return newSuccessResponse();
    }

    @Override
    public String getSmartContractStrDescription() {
        String description="与验证相关的系统智能合约";
        return description;
    }

    /**
     *checkInstantiationPolicy evaluates an instantiation policy against a signed proposal
     * @param groupID
     * @param env
     * @param instantiationPolicy
     * @param payload
     * @return
     */
    private void checkInstantiationPolicy(String groupID, Common.Envelope env, byte []instantiationPolicy,
                                             Common.Payload payload)throws SysSmartContractException{
        IMspManager mspManager = GlobalMspManagement.getManagerForChain(groupID);
        if(mspManager==null){
            String msg=String.format("MSP manager for group %s is null, aborting",groupID);
            throw new SysSmartContractException(msg);
        }
        IPolicyProvider policyProvider = CAuthDslBuilder.createPolicyProvider(mspManager);
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
    private void validateDeployRWSetAndCollection(
            KvRwset.KVRWSet lsscrwset,
            SmartContractDataPackage.SmartContractData scdRWSet,
            List<byte[]> lsscArgs,
            String groupID,
            String smartcontractName
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
            collectionsConfigArgs=lsscArgs.get(5);
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
        ICollectionConfigPackage ccp =null;
        try {
            ccp = collectionStore.retriveCollectionConfigPackage(cc);
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

    private void validateLSSCInvocation(
            ISmartContractStub stub,
            String groupID,
            Common.Envelope envelope,
            TransactionPackage.SmartContractActionPayload scap,
            Common.Payload payload,
            IApplicationCapabilities ac
            )throws SysSmartContractException{

    }

    private SmartContractDataPackage.SmartContractData getInstantiatedSmartContract(String groupID,String smartcontractID){
        return null;
    }

    private List<SignedData> deduplicateIdentity(TransactionPackage.SmartContractActionPayload scap)
                                   throws SysSmartContractException{
        return null;
    }
}
