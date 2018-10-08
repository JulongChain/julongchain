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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;

/**
 * 提案响应业务对象
 *
 * @author zhouhui
 * @date 2018/09/30
 * @company Dingxuan
 */
public class ProposalResponseVO implements IProtoVO<ProposalResponsePackage.ProposalResponse> {
    private int version;
    private Timestamp timestamp;
    private ResponseVO responseVO;
    private ProposalResponsePayloadVO payloadVO;
    private ProposalResponsePackage.Endorsement endorsement;

    @Override
    public void parseFrom(ProposalResponsePackage.ProposalResponse proposalResponse) throws
            InvalidProtocolBufferException, ValidateException {
        this.version = proposalResponse.getVersion();
        this.timestamp = proposalResponse.getTimestamp();

        this.responseVO = new ResponseVO();

        this.payloadVO = new ProposalResponsePayloadVO();
        ProposalResponsePackage.ProposalResponsePayload proposalResponsePayload = ProposalResponsePackage
                .ProposalResponsePayload.parseFrom(proposalResponse.getPayload());
        payloadVO.parseFrom(proposalResponsePayload);

        this.endorsement = proposalResponse.getEndorsement();
    }

    @Override
    public ProposalResponsePackage.ProposalResponse toProto() {
        ProposalResponsePackage.ProposalResponse.Builder builder = ProposalResponsePackage.ProposalResponse
                .newBuilder();
        builder.setVersion(version);
        builder.setTimestamp(timestamp);
        builder.setResponse(responseVO.toProto());
        builder.setPayload(payloadVO.toProto().toByteString());
        builder.setEndorsement(endorsement);
        return builder.build();
    }
}
