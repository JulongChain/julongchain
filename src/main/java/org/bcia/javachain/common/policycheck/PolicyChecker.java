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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;

/**
 * 类描述
 * 策略检查器，通过通达及策略名获得策略，并使用此策略对签名进行验证
 * @author yuanjun
 * @date 20/04/20
 * @company Aisino
 */
public class PolicyChecker implements IPolicyChecker{
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyChecker.class);
    private org.bcia.javachain.common.policycheck.bean.PolicyChecker policyChecker;
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
        PolicyManager policyManager = policyChecker.getChannelPolicyManagerGetter().Manager(channelID);
         if(policyManager == null){
             log.info("Failed to get policy manager for channel ["+channelID+"]");
         }
        Common.Header header = Common.Header.getDefaultInstance();
    }

    @Override
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

    }

    @Override
    public void CheckPolicyNoChannel(String policyName, SignedProposal signedProposal) {
        if(policyName == ""){
            log.info("Invalid policy name during channelless check policy. Name must be different from nil.");
        }
        if(signedProposal == null){
            log.info("Invalid signed proposal during channelless check policy with policy ["+policyName+"]");
        }
        //获取提案
        //如果提案为空则打印
        ProposalPackage proposal;
        if(signedProposal == null){
            log.info("Failing extracting proposal during channelless check policy with policy ["+policyName+"]");
        }
        Common.Header header = Common.Header.getDefaultInstance();


        /**
         *

         header, err := utils.GetHeader(proposal.Header)
         if err != nil {
         return fmt.Errorf("Failing extracting header during channelless check policy with policy [%s]: [%s]", policyName, err)
         }

         shdr, err := utils.GetSignatureHeader(header.SignatureHeader)
         if err != nil {
         return fmt.Errorf("Invalid Proposal's SignatureHeader during channelless check policy with policy [%s]: [%s]", policyName, err)
         }

         // Deserialize proposal's creator with the local MSP
         id, err := p.localMSP.DeserializeIdentity(shdr.Creator)
         if err != nil {
         return fmt.Errorf("Failed deserializing proposal creator during channelless check policy with policy [%s]: [%s]", policyName, err)
         }

         // Load MSPPrincipal for policy
         principal, err := p.principalGetter.Get(policyName)
         if err != nil {
         return fmt.Errorf("Failed getting local MSP principal during channelless check policy with policy [%s]: [%s]", policyName, err)
         }

         // Verify that proposal's creator satisfies the principal
         err = id.SatisfiesPrincipal(principal)
         if err != nil {
         return fmt.Errorf("Failed verifying that proposal's creator satisfies local MSP principal during channelless check policy with policy [%s]: [%s]", policyName, err)
         }

         // Verify the signature
         return id.Verify(signedProp.ProposalBytes, signedProp.Signature)
         */



    }
}
