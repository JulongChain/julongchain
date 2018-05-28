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

package org.bcia.javachain.common.policycheck;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.policycheck.cauthdsl.Policy;
import org.bcia.javachain.common.policycheck.policies.IChannelPolicyManagerGetter;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.node.ProposalPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 * 策略检查器，通过通达及策略名获得策略，并使用此策略对签名进行验证
 * @author yuanjun
 * @date 20/04/20
 * @company Aisino
 */
public class PolicyChecker implements IPolicyChecker{
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyChecker.class);

    private IChannelPolicyManagerGetter IChannelPolicyManagerGetter;
    private IIdentityDeserializer localMSP;
    private IMspPrincipalGetter principalGetter;


    public PolicyChecker(IChannelPolicyManagerGetter IChannelPolicyManagerGetter, IIdentityDeserializer localMSP, IMspPrincipalGetter principalGetter) {
        this.IChannelPolicyManagerGetter = IChannelPolicyManagerGetter;
        this.localMSP = localMSP;
        this.principalGetter = principalGetter;
    }

    /**
     * @param channelID
     * @param policyName
     * @param signedProposal
     */
    @Override
    public void CheckPolicy(String channelID, String policyName, SignedProposal signedProposal) {
         if(channelID == ""){
             CheckPolicyNoChannel(policyName,signedProposal);
         }

        if(policyName == ""){
             log.info("Invalid policy name during check policy on channel ["+channelID+"]. Name must be different from nil");
        }
        if(signedProposal == null){
             log.info("Invalid signed proposal during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
        }
        //get policy
        PolicyManager policyManager = null;

        try {
            policyManager = IChannelPolicyManagerGetter.Manager(channelID);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }

        if(policyManager == null){
             log.info("Failed to get policy manager for channel ["+channelID+"]");
         }
        ProposalPackage.Proposal proposal = null;
        try {

           proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());

        } catch (InvalidProtocolBufferException e) {
             log.info("Failing extracting proposal during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        }

        Common.Header header = null; //Common.Header.newBuilder();
        try {
            header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            log.info("Failing extracting header during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        };
        Common.SignatureHeader shdr = null;
        try {
            shdr = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            log.info("Invalid Proposal's SignatureHeader during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        }
        List<SignedData> sd = new ArrayList<SignedData>();
        sd.add(new SignedData(signedProposal.getProposalBytes(),shdr.getCreator().toByteArray(),signedProposal.getSignature()));
        this.CheckPolicyBySignedData(channelID,policyName,sd);
    }


    @Override
    public void CheckPolicyNoChannel(String policyName, SignedProposal signedProposal) {
        if(policyName == ""){
            log.info("Invalid policy name during channelless check policy. Name must be different from nil.");
        }
        if(signedProposal == null){
            log.info("Invalid signed proposal during channelless check policy with policy ["+policyName+"]");
        }
        ProposalPackage.Proposal proposal = null;
        try {
            proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
            log.info("Failing extracting proposal during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        Common.Header header = null;
        try {
            header = Common.Header.parseFrom(proposal.getHeader());

        } catch (InvalidProtocolBufferException e) {
            log.info("Failing extracting header during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        Common.SignatureHeader shdr = null;
        try {
            shdr = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            log.info("Invalid Proposal's SignatureHeader during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        IIdentity id = null;
        try {
            id = this.localMSP.deserializeIdentity(shdr.getCreator().toByteArray());
        }catch (Exception e){
            log.info("Failed deserializing proposal creator during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        MspPrincipal.MSPPrincipal principal = null;
        try {
            principal = this.principalGetter.get(policyName);
        }catch (Exception e){
            log.info("Failed getting local MSP principal during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        //MspPrincipal.MSPPrincipal m = MspPrincipal.MSPPrincipal.newBuilder().build();
        id.satisfiesPrincipal(principal);
        id.verify(signedProposal.getProposalBytes(),signedProposal.getSignature());

    }

    @Override
    /**
     * 检查传入的签名是否有效
     */
    public void CheckPolicyBySignedData(String channelID, String policyName, List<SignedData> signedDatas) {

        if(channelID == ""){
            log.info("Invalid channel ID name during check policy on signed data. Name must be different from nil");
        }
        if(policyName == ""){
            log.info("Invalid policy name during check policy on signed data on channel ["+channelID+"]. Name must be different from nil");
        }
        if(signedDatas == null){
            log.info("Invalid signed data during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
        }
        PolicyManager policyManager = null;
        try {
            policyManager = this.IChannelPolicyManagerGetter.Manager(channelID);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
        if(policyManager == null){
            log.info("Failed to get policy manager for channel ["+channelID+"]");
        }
        Policy policy = (Policy) policyManager.getPolicy(policyName);
        try {
            policy.evaluate(signedDatas);
        } catch (PolicyException e) {
            log.info("Failed evaluating policy on signed data during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        }

    }

}
