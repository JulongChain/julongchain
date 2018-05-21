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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.policycheck.policies.ChannelPolicyManagerGetter;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;

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

    private ChannelPolicyManagerGetter channelPolicyManagerGetter;
    private IIdentityDeserializer localMSP;
    private MspPrincipal.MSPPrincipal principalGetter;


    public PolicyChecker(ChannelPolicyManagerGetter channelPolicyManagerGetter, IIdentityDeserializer localMSP, MspPrincipal.MSPPrincipal principalGetter) {
        this.channelPolicyManagerGetter = channelPolicyManagerGetter;
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
         PolicyManager policyManager = channelPolicyManagerGetter.Manager(channelID);
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
        //ProposalResponsePackage.ProposalResponse proposal = ProtoUtils.getProposalResponse(signedProposal.getProposalBytes());

        Common.Header header = null; //Common.Header.newBuilder();
        try {
            header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            log.info("Failing extracting header during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        };
        Common.SignatureHeader shdr = Common.SignatureHeader.getDefaultInstance();//.getCreator();
        SignedData[] sd = new SignedData[1];
        sd[0] = new SignedData(signedProposal.getProposalBytes(),shdr.getCreator().toByteArray(),signedProposal.getSignature());
        this.CheckPolicyBySignedData(channelID,policyName,sd);
    }

    @Override
    /**
     * 检车传入的签名是否有效
     */
    public void CheckPolicyBySignedData(String channelID, String policyName, SignedData[] signedDatas) {

        if(channelID == ""){
            log.info("Invalid channel ID name during check policy on signed data. Name must be different from nil");
        }
        if(policyName == ""){
            log.info("Invalid policy name during check policy on signed data on channel ["+channelID+"]. Name must be different from nil");
        }
        if(signedDatas == null){
            log.info("Invalid signed data during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
        }
        PolicyManager policyManager = this.channelPolicyManagerGetter.Manager(channelID);
        if(policyManager == null){
            log.info("Failed to get policy manager for channel ["+channelID+"]");
        }
        IPolicy policy = policyManager.getPolicy(policyName);
        List<SignedData> sd = new ArrayList<SignedData>();
        for(int i=0;i<signedDatas.length;i++){
            sd.add(signedDatas[i]);
        }
        try {
            policy.evaluate(sd);
        } catch (PolicyException e) {
            log.info("Failed evaluating policy on signed data during check policy on channel ["+channelID+"] with policy ["+policyName+"]");
            e.printStackTrace();
        }

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
        try {
            Common.Header header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            log.info("Failing extracting header during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        Common.SignatureHeader shdr = Common.SignatureHeader.getDefaultInstance();
        IIdentity id = null;
        try {
           id = this.localMSP.deserializeIdentity(shdr.getCreator().toByteArray());
        }catch (Exception e){
            log.info("Failed deserializing proposal creator during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        MspPrincipal.MSPPrincipal principal = null;
        try {
         principal = this.principalGetter.getDefaultInstanceForType();
        }catch (Exception e){
            log.info("Failed getting local MSP principal during channelless check policy with policy ["+policyName+"]");
            e.printStackTrace();
        }
        MspPrincipal m = null; //TODO 待完善
        id.satisfiesPrincipal(m);
        id.verify(signedProposal.getProposalBytes(),signedProposal.getSignature());


    }
}
