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
import org.bcia.javachain.common.cauthdsl.PolicyProvider;
import org.bcia.javachain.common.channelconfig.ApplicationCapabilities;
import org.bcia.javachain.common.groupconfig.IApplicationConfig;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.core.common.privdata.CollectionStoreFactory;
import org.bcia.javachain.core.common.privdata.CollectionStoreSupport;
import org.bcia.javachain.core.common.privdata.ICollectionStore;
import org.bcia.javachain.core.common.privdata.IPrivDataSupport;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractData;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractFactory;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.core.ssc.SystemSmartContractDescriptor;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.mgmt.Mgmt;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
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
    public Response init(ISmartContractStub stub) {
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
    // @return a successful Response (code 200) in case of success, or
    // an error otherwise
    // Note that Peer calls this function with 3 arguments, where args[0] is the
    // function name, args[1] is the Envelope and args[2] is the validation policy
    @Override
    public Response invoke(ISmartContractStub stub)  {
        // TODO: document the argument in some white paper or design document
        // args[0] - function name (not used now)
        // args[1] - serialized Envelope
        // args[2] - serialized policy
        log.debug("Enter VSSC invoke function");
        List<String> args = stub.getStringArgs();
        int size=args.size();
        if(size<3){
            return newErrorResponse(String.format("Incorrect number of arguments , %d)",args.size()));
        }

        String block=args.get(1);
        if(block==null || block.isEmpty()){
            return newErrorResponse(String.format("No block to validate"));
        }
        String strPolicy=args.get(2);
        if(strPolicy==null || strPolicy.isEmpty()){
            return newErrorResponse(String.format("No policy supplied"));
        }



        // get the envelope...
        Common.Envelope envelope=null;
        try {
            envelope=ProtoUtils.getEnvelopeFromBlock(block);
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

        Mgmt mgmt=new Mgmt();
        IMspManager manager=mgmt.getManagerForChain(groupHeader.getGroupId());
        PolicyProvider policyProvider=new PolicyProvider(manager);
        IPolicy policy = policyProvider.newPolicy(strPolicy);

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
             //待补充
        }

        log.debug("VSSC exits successfully");
        return newSuccessResponse();
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与验证相关的系统智能合约";
    }

    // checkInstantiationPolicy evaluates an instantiation policy against a signed proposal
    private boolean checkInstantiationPolicy(String groupID, Common.Envelope env, byte []instantiationPolicy [],Common.Payload payload){
        return false;
    }

    // validateDeployRWSetAndCollection performs validation of the rwset
    // of an LSCC deploy operation and then it validates any collection
    // configuration
    private boolean validateDeployRWSetAndCollection(
            KvRwset.KVRWSet lsccrwset,
            SmartContractData scdRWSet,
            byte[][] args,
            String groupID,
            String channelID
            ){
        return false;
    }

    private boolean validateLSSCInvocation(
            ISmartContractStub stub,
            String groupID,
            Common.Envelope envelope,
            TransactionPackage.SmartContractActionPayload scap,
            Common.Payload payload,
            ApplicationCapabilities ac
            ){
        return false;
    }

    private SmartContractData getInstantiatedSmartContract(String groupID,String smartcontractID){
        return null;
    }

    private SignedData deduplicateIdentity(TransactionPackage.SmartContractActionPayload scap){
        return null;
    }
}
