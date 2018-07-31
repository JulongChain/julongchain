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
package org.bcia.julongchain.common.protos;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class ProposalResponsePayloadVO implements IProtoVO<ProposalResponsePackage.ProposalResponsePayload> {
    private SmartContractActionVO extension;
    private ByteString proposalHash;

    @Override
    public void parseFrom(ProposalResponsePackage.ProposalResponsePayload proposalResponsePayload) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(proposalResponsePayload, "proposalResponsePayload can not be null");

        this.extension = new SmartContractActionVO();
        ProposalPackage.SmartContractAction smartContractAction = ProposalPackage.SmartContractAction.parseFrom
                (proposalResponsePayload.getExtension());
        this.extension.parseFrom(smartContractAction);

        this.proposalHash = proposalResponsePayload.getProposalHash();
    }

    @Override
    public ProposalResponsePackage.ProposalResponsePayload toProto() {
        ProposalResponsePackage.ProposalResponsePayload.Builder builder = ProposalResponsePackage
                .ProposalResponsePayload.newBuilder();
        builder.setExtension(extension.toProto().toByteString());
        if (proposalHash != null) {
            builder.setProposalHash(proposalHash);
        }
        return builder.build();
    }

    public SmartContractActionVO getExtension() {
        return extension;
    }

    public ByteString getProposalHash() {
        return proposalHash;
    }
}
