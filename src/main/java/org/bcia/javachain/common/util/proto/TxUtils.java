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
package org.bcia.javachain.common.util.proto;

import com.google.protobuf.ByteString;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/29/18
 * @company Dingxuan
 */
public class TxUtils {
    // MockSignedEndorserProposalOrPanic creates a SignedProposal with the passed arguments
    public static ProposalPackage.SignedProposal mockSignedEndorserProposalOrPanic(
            String groupID,
            Smartcontract.SmartContractSpec spec,
            byte[] creator,
            byte[] signature
    ){
        ProposalPackage.Proposal.Builder proposalBuilder = ProposalPackage.Proposal.newBuilder();
        ProposalPackage.Proposal proposal = proposalBuilder.build();
        byte[] proBytes = ProtoUtils.marshalOrPanic(proposal);
        ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder().setProposalBytes(ByteString.copyFrom(proBytes))
                .setSignature(ByteString.copyFrom(signature)).build();
        return signedProposal;
    }
}
