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
package org.bcia.julongchain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.consenter.util.Utils;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 类描述
 *
 * @author sunianle,zhangmingyang
 * @date 3/29/18
 * @company Dingxuan
 */
public class TxUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(TxUtils.class);

    // MockSignedEndorserProposalOrPanic creates a SignedProposal with the passed arguments
    public static ProposalPackage.SignedProposal mockSignedEndorserProposalOrPanic(
            String groupID,
            SmartContractPackage.SmartContractSpec spec
    ) throws JavaChainException {
        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.serialize();
        SmartContractPackage.SmartContractInvocationSpec invocationSpec=SmartContractPackage.SmartContractInvocationSpec.newBuilder().build();
        ProposalPackage.Proposal proposal = ProposalUtils.createSmartcontractProposalWithTransient(Common.HeaderType.ENDORSER_TRANSACTION,
                groupID,invocationSpec,creator,null);
        byte[] proBytes =proposal.toByteArray();
        byte[] signature=identity.sign(proBytes);
        ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder().setProposalBytes(ByteString.copyFrom(proBytes))
                .setSignature(ByteString.copyFrom(signature)).build();
        return signedProposal;
    }

    public static Common.Envelope createSignedTx(ProposalPackage.Proposal proposal, ISigningIdentity signer,
                                                 ProposalResponsePackage.ProposalResponse resps){
        //先占位，后面补充逻辑
        Common.Envelope.Builder builder=Common.Envelope.newBuilder();
        builder.setPayload(ByteString.copyFrom("Payload".getBytes()));
        builder.setSignature(ByteString.copyFrom("Signature".getBytes()));
        Common.Envelope  envelope=builder.build();
        return envelope;
    }

    public static byte[] getBytesEnvelope(Common.Envelope tx) {
        return tx.toByteArray();
    }

    public static Common.Envelope getEnvelopeFromBlock(byte[] data) {
//        Common.Envelope.Builder envelope = Common.Envelope.newBuilder();
//        try {
//            envelope.mergeFrom(data);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
        Common.Envelope envelope= null;
        try {
            envelope = Common.Envelope.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return envelope;
    }

    public static Common.Envelope createSignedEnvelope (int txType, String groupId, ILocalSigner signer, Message dataMsg, int msgVersion, long epoch) {
        return createSignedEnvelopeWithTLSBinding(txType, groupId, signer, dataMsg, msgVersion, epoch, null);
    }

    public static Common.Envelope createSignedEnvelopeWithTLSBinding(int txType, String groupId, ILocalSigner signer, Message dataMsg, int msgVersion, long epoch, byte[] tlsCertHash) {
        Common.GroupHeader groupHeader = CommonUtils.makeGroupHeader(txType, msgVersion, groupId, epoch);
        if(tlsCertHash != null) {
            groupHeader.toBuilder().setTlsCertHash(ByteString.copyFrom(tlsCertHash));
        }

        Common.SignatureHeader signatureHeader = null;
        if (signer != null) {
            signatureHeader = signer.newSignatureHeader();
        }
        Common.Payload payload = Common.Payload.newBuilder()
                .setHeader(CommonUtils.makePayloadHeader(groupHeader, signatureHeader))
                .setData(dataMsg.toByteString()).build();
        byte[] paylBytes = Utils.marshalOrPanic(payload);

        byte[] sig = new byte[0];
        if (signer != null) {
            sig = signer.sign(paylBytes);
        }
        Common.Envelope envelope = Common.Envelope.newBuilder()
                .setPayload(ByteString.copyFrom(paylBytes))
                .setSignature(ByteString.copyFrom(sig)).build();
        return envelope;
    }

}
