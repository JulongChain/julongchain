/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.julongchain.common.policycheck;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policycheck.policies.IGroupPolicyManagerGetter;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.node.ProposalPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 * 策略检查器，通过通达及策略名获得策略，并使用此策略对签名进行验证
 * @author yuanjun,sunianle
 * @date 20/04/20
 * @company Aisino,Dingxuan
 */
public class PolicyChecker implements IPolicyChecker{
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyChecker.class);

    private IGroupPolicyManagerGetter groupPolicyManagerGetter;
    private IIdentityDeserializer localMSP;
    private IMspPrincipalGetter principalGetter;


    public PolicyChecker(IGroupPolicyManagerGetter groupPolicyManagerGetter, IIdentityDeserializer localMSP, IMspPrincipalGetter principalGetter) {
        this.groupPolicyManagerGetter = groupPolicyManagerGetter;
        this.localMSP = localMSP;
        this.principalGetter = principalGetter;
    }

    /**
     * @param groupID
     * @param policyName
     * @param signedProposal
     */
    @Override
    public void checkPolicy(String groupID, String policyName, ProposalPackage.SignedProposal signedProposal) throws PolicyException{
         if(groupID == ""){
             checkPolicyNoGroup(policyName,signedProposal);
         }

        if(policyName == ""){
             String msg=String.format("Invalid policy name during check policy on group [%s]. Name must be different from nil",groupID);
             throw new PolicyException(msg);
        }
        if(signedProposal == null){
             String msg=String.format("Invalid signed proposal during check policy on group [%s] with policy [%s]",groupID,policyName);
             throw new PolicyException(msg);
        }
        //get policy
        IPolicyManager policyManager = null;

        try {
            policyManager = groupPolicyManagerGetter.getPolicyManager(groupID);
            if(policyManager == null){
                log.error("Failed to get policy manager for channel [%s]",groupID);
            }
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Failed to get policy getPolicyManager for group [%s]:%s",groupID,e.getMessage());
            throw new PolicyException(msg);
        } catch (PolicyException e) {
            String msg=String.format("Failed to get policy getPolicyManager for group [%s]:%s",groupID,e.getMessage());
            throw new PolicyException(msg);
        }

        if(policyManager == null){
            String msg=String.format("Failed to get policy getPolicyManager for group [%s]",groupID);
            throw new PolicyException(msg);
         }
        ProposalPackage.Proposal proposal = null;
        try {
           proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
             String msg=String.format("Failing extracting proposal during check policy on group [%s] with policy [%s]:%s",groupID,policyName,e.getMessage());
             throw new PolicyException(msg);
        }

        Common.Header header = null; //Common.Header.newBuilder();
        try {
            header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Failing extracting header during check policy on group [%s] with policy [%s]:%s",groupID,policyName,e.getMessage());
            throw new PolicyException(msg);
        };
        Common.SignatureHeader signatureHeader = null;
        try {
            signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Invalid Proposal's SignatureHeader during check policy on group [%s] with policy [%s]:%s",groupID,policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        List<SignedData> sd = new ArrayList<SignedData>();
        sd.add(new SignedData(signedProposal.getProposalBytes().toByteArray(),
                signatureHeader.getCreator().toByteArray(),
                signedProposal.getSignature().toByteArray()));
        this.checkPolicyBySignedData(groupID,policyName,sd);
    }


    @Override
    public void checkPolicyNoGroup(String policyName, ProposalPackage.SignedProposal signedProposal)throws PolicyException {
        if(policyName == ""){
            String msg=String.format("Invalid policy name during groupless check policy. Name must be different from nil.");
            throw new PolicyException(msg);
        }
        if(signedProposal == null){
            log.info("Invalid signed proposal during channelless check policy with policy ["+policyName+"]");
            String msg=String.format("Invalid signed proposal during channelless check policy with policy [%s]",policyName);
            throw new PolicyException(msg);
        }
        ProposalPackage.Proposal proposal = null;
        try {
            proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Failing extracting proposal during channelless check policy with policy [%s]:%s",policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        Common.Header header = null;
        try {
            header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Failing extracting header during channelless check policy with policy [%s]:%s",policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        Common.SignatureHeader signatureHeader = null;
        try {
            signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Invalid Proposal's SignatureHeader during channelless check policy with policy [%s]:%s",policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        IIdentity id = null;
        try {
            log.info("signatureHeader.getCreator()-----$" + signatureHeader.getCreator());
            id = localMSP.deserializeIdentity(signatureHeader.getCreator().toByteArray());//
        }catch (Exception e){
            String msg=String.format("Failed deserializing proposal creator during channelless check policy with policy [%s]:%s",policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        if(id==null){
            String msg=String.format("proposal creator is null during channelless check policy with policy [%s]",policyName);
            throw new PolicyException(msg);
        }
        MspPrincipal.MSPPrincipal principal = null;
        try {
            principal = this.principalGetter.get(policyName);
        }catch (Exception e){
            String msg=String.format("Failed getting local MSP principal during channelless check policy with policy [%s]:%s",policyName,e.getMessage());
            throw new PolicyException(msg);
        }
        //MspPrincipal.MSPPrincipal m = MspPrincipal.MSPPrincipal.newBuilder().build();
        try {
            id.satisfiesPrincipal(principal);
        }catch (MspException e){
            String msg=String.format("Satisfies Principal get error:%s",e.getMessage());
            throw new PolicyException(msg);
        }
        try {
            id.verify(signedProposal.getProposalBytes().toByteArray(), signedProposal.getSignature().toByteArray());
        }catch(VerifyException e){
            String msg=String.format("Verify signature failed:%s",e.getMessage());
            throw new PolicyException(msg);
        }
    }

    @Override
    /**
     * 检查传入的签名是否有效
     */
    public void checkPolicyBySignedData(String groupID, String policyName, List<SignedData> signedDatas) throws PolicyException{
        if(groupID == ""){
            String msg=String.format("Invalid group ID name during check policy on signed data. Name must be different from nil");
            throw new PolicyException(msg);
        }
        if(policyName == ""){
            String msg=String.format("Invalid policy name during check policy on signed data on channel [%s]. Name must be different from nil",groupID);
            throw new PolicyException(msg);
        }
        if(signedDatas == null){
            String msg=String.format("Invalid signed data during check policy on group [%s] with policy [%s]",groupID,policyName);
            throw new PolicyException(msg);
        }
        IPolicyManager policyManager = null;
        try {
            policyManager = this.groupPolicyManagerGetter.getPolicyManager(groupID);
            if(policyManager == null){
                log.error("Failed to get policy manager for channel [%s]",groupID);
            }
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("Get policymanager error:%s",e.getMessage());
            throw new PolicyException(msg);
        } catch (PolicyException e) {
            String msg=String.format("Get policymanager error:%s",e.getMessage());
            throw new PolicyException(msg);
        }
        if(policyManager == null){
            String msg=String.format("Failed to get policymanager for group [%s]",groupID);
            throw new PolicyException(msg);
        }
        IPolicy policy = policyManager.getPolicy(policyName);
        policy.evaluate(signedDatas);
    }

}
