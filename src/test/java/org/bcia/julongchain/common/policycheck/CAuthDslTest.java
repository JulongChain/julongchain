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

import afu.org.checkerframework.checker.igj.qual.I;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.policies.config.SignaturePolicy;
import org.bcia.julongchain.common.policycheck.cauthdsl.CAuthDsl;
import org.bcia.julongchain.common.policycheck.cauthdsl.PolicyParser;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.msp.mgmt.Identity;
import org.bcia.julongchain.msp.mgmt.Msp;
import org.bcia.julongchain.msp.mgmt.MspManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 11/05/18
 * @company Aisino
 */
public class CAuthDslTest {
    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void deduplicate() throws JulongChainException, UnsupportedEncodingException {
        List<SignedData> sds = new ArrayList<SignedData>();
        ByteString.copyFrom("id1".getBytes()).toByteArray();
        String policyName = "Admins";
        ProposalPackage.Proposal proposal = null;
        ProposalPackage.SignedProposal signedProposal = TxUtils.mockSignedEndorserProposalOrPanic("",
                SmartContractPackage.SmartContractSpec.newBuilder().build());
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
        SignedData sd1 = new SignedData("A".getBytes(),signatureHeader.getCreator().toByteArray(),"A".getBytes());
        SignedData sd2 = new SignedData("B".getBytes(),signatureHeader.getCreator().toByteArray(),"B".getBytes());
        SignedData sd3 = new SignedData("C".getBytes(),signatureHeader.getCreator().toByteArray(),"C".getBytes());
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        IIdentityDeserializer localMsp = GlobalMspManagement.getLocalMsp();
        CAuthDsl.deduplicate(sds,localMsp);
    }
    @Test
    public void compile() throws PolicyException {

        List<SignedData> sds = new ArrayList<SignedData>();
        SignedData sd1 = new SignedData("A".getBytes(),"id111111111".getBytes(),"A".getBytes());
        SignedData sd2 = new SignedData("B".getBytes(),"id222222222".getBytes(),"B".getBytes());
        SignedData sd3 = new SignedData("C".getBytes(),"id333333333".getBytes(),"C".getBytes());
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        MspManager deserializer = new MspManager();//mock(MspManager.class);
        String policy = "OR(AND('A.member', 'B.member'), OR('C.admin', 'D.member'))";
        String str = PolicyParser.checkPolicyStr(policy);
        Policies.SignaturePolicyEnvelope envelope = PolicyParser.fromString(policy);

        List<MspPrincipal.MSPPrincipal> identities = new ArrayList<MspPrincipal.MSPPrincipal>();
        IIdentityDeserializer deserializers = GlobalMspManagement.getLocalMsp();
        CAuthDsl.compile(envelope.getRule(),envelope.getIdentitiesList(),deserializers);


    }
}
