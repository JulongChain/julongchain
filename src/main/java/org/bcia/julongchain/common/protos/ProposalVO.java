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
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;

/**
 * 提案业务对象
 *
 * @author zhouhui
 * @date 2018/09/30
 * @company Dingxuan
 */
public class ProposalVO implements IProtoVO<ProposalPackage.Proposal> {
    private HeaderVO headerVO;
    private SmartContractProposalPayloadVO payloadVO;
    private SmartContractActionVO extensionVO;

    @Override
    public void parseFrom(ProposalPackage.Proposal proposal) throws InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(proposal, "Proposal can not be null");
        ValidateUtils.isNotNull(proposal.getHeader(), "Proposal.getHeader can not be null");
        ValidateUtils.isNotNull(proposal.getPayload(), "Proposal.getPayload can not be null");

        headerVO = new HeaderVO();
        Common.Header header = Common.Header.parseFrom(proposal.getHeader());
        headerVO.parseFrom(header);

        payloadVO = new SmartContractProposalPayloadVO();
        ProposalPackage.SmartContractProposalPayload smartContractProposalPayload = ProposalPackage
                .SmartContractProposalPayload.parseFrom(proposal.getPayload());
        payloadVO.parseFrom(smartContractProposalPayload);

        extensionVO = new SmartContractActionVO();
        ProposalPackage.SmartContractAction smartContractAction = ProposalPackage.SmartContractAction.parseFrom
                (proposal.getExtension());
        extensionVO.parseFrom(smartContractAction);
    }

    @Override
    public ProposalPackage.Proposal toProto() {
        ProposalPackage.Proposal.Builder proposalBuilder = ProposalPackage.Proposal.newBuilder();
        proposalBuilder.setHeader(headerVO.toProto().toByteString());
        proposalBuilder.setPayload(payloadVO.toProto().toByteString());

        if (extensionVO != null) {
            proposalBuilder.setExtension(extensionVO.toProto().toByteString());
        }

        return proposalBuilder.build();
    }

    public HeaderVO getHeaderVO() {
        return headerVO;
    }

    public SmartContractProposalPayloadVO getPayloadVO() {
        return payloadVO;
    }

    public SmartContractActionVO getExtensionVO() {
        return extensionVO;
    }
}
