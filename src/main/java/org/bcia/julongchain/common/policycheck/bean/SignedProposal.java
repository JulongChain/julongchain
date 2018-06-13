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

package org.bcia.julongchain.common.policycheck.bean;

/**
 * 用ProposalPackage.SignedProposal替代,苏年乐
 *
 * @author yuanjun
 * @date 14/05/18
 * @company Aisino
 * @deprecated sunianle
 */
public class SignedProposal {
    private byte[] ProposalBytes;
    private byte[] Signature;

    public SignedProposal(byte[] proposalBytes, byte[] signature) {
        ProposalBytes = proposalBytes;
        Signature = signature;
    }

    public byte[] getProposalBytes() {
        return ProposalBytes;
    }

    public void setProposalBytes(byte[] proposalBytes) {
        ProposalBytes = proposalBytes;
    }

    public byte[] getSignature() {
        return Signature;
    }

    public void setSignature(byte[] signature) {
        Signature = signature;
    }
}
