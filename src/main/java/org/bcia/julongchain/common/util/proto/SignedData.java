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

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.consenter.common.msgprocessor.SigFilter;
import org.bcia.julongchain.consenter.util.Utils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.ProposalPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * SignedData is used to represent the general triplet required to verify a signature
 * This is intended to be generic across crypto schemes, while most crypto schemes will
 * include the signing identity and a nonce within the Data, this is left to the crypto
 * implementation
 *
 * @author sunianle
 * @date 3/21/18
 * @company Dingxuan
 */
public class SignedData {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SigFilter.class);
    private byte[] data;
    private byte[] identity;
    private byte[] signature;

    public SignedData(byte[] data, byte[] identity, byte[] signature) {
        this.data = data;
        this.identity = identity;
        this.signature = signature;
    }


    public static List<SignedData> asSingedData(Configtx.ConfigUpdateEnvelope ce) throws ValidateException {
        if (ce == null) {
            throw new ValidateException("No signatures for nil SignedConfigItem");
        }
        List<SignedData> result = new ArrayList<>();
        for (int i = 0; i < ce.getSignaturesCount(); i++) {
            Configtx.ConfigSignature configSignature = ce.getSignatures(i);
            try {
                Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(configSignature.getSignatureHeader());
                result.add(new SignedData(Utils.concatenateBytes(configSignature.toByteArray(), ce.getConfigUpdate().toByteArray()), signatureHeader.getCreator().toByteArray(), configSignature.toByteArray()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<SignedData> asSignedData(Common.Envelope envelope) throws ValidateException, InvalidProtocolBufferException {
        List<SignedData> result = new ArrayList<>();
        if (envelope == null) {
            log.error("No signatures for nil Envelope");
            throw new ValidateException("No signatures for nil Envelope");
        }
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        if (payload.getHeader() == null) {
            throw new ValidateException("Missing Header");
        }
        Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(payload.getHeader().getSignatureHeader());
        result.add(new SignedData(payload.toByteArray(), signatureHeader.getCreator().toByteArray(), envelope.getSignature().toByteArray()));

        return result;
    }

    public static List<SignedData> asSignedData(ProposalPackage.SignedProposal signedProposal) throws ValidateException,
            InvalidProtocolBufferException {
        ValidateUtils.isNotNull(signedProposal, "signedProposal can not be null");
        ValidateUtils.isNotNull(signedProposal.getProposalBytes(), "proposal can not be null");

        List<SignedData> result = new ArrayList<>();

        ProposalPackage.Proposal proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        Common.Header header = Common.Header.parseFrom(proposal.getHeader());
        Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());

        SignedData signedData = new SignedData(signedProposal.getProposalBytes().toByteArray(),
                signatureHeader.getCreator().toByteArray(), signedProposal.getSignature().toByteArray());
        result.add(signedData);

        return result;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getIdentity() {
        return identity;
    }

    public byte[] getSignature() {
        return signature;
    }


}
