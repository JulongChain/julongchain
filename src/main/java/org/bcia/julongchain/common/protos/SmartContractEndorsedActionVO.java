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
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.List;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class SmartContractEndorsedActionVO implements IProtoVO<TransactionPackage.SmartContractEndorsedAction> {
    private ProposalResponsePayloadVO proposalResponsePayloadVO;
    private List<ProposalResponsePackage.Endorsement> endorsements;

    @Override
    public void parseFrom(TransactionPackage.SmartContractEndorsedAction smartContractEndorsedAction) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(smartContractEndorsedAction, "smartContractEndorsedAction can not be null");
        ValidateUtils.isNotNull(smartContractEndorsedAction.getProposalResponsePayload(),
                "smartContractEndorsedAction.getProposalResponsePayload can not be null");

        this.proposalResponsePayloadVO = new ProposalResponsePayloadVO();
        ProposalResponsePackage.ProposalResponsePayload proposalResponsePayload = ProposalResponsePackage
                .ProposalResponsePayload.parseFrom(smartContractEndorsedAction.getProposalResponsePayload());
        this.proposalResponsePayloadVO.parseFrom(proposalResponsePayload);

        this.endorsements = smartContractEndorsedAction.getEndorsementsList();
    }

    @Override
    public TransactionPackage.SmartContractEndorsedAction toProto() {
        TransactionPackage.SmartContractEndorsedAction.Builder builder = TransactionPackage
                .SmartContractEndorsedAction.newBuilder();
        builder.setProposalResponsePayload(proposalResponsePayloadVO.toProto().toByteString());
        if (endorsements != null) {
            builder.addAllEndorsements(endorsements);
        }

        return builder.build();
    }

    public ProposalResponsePayloadVO getProposalResponsePayloadVO() {
        return proposalResponsePayloadVO;
    }

    public List<ProposalResponsePackage.Endorsement> getEndorsements() {
        return endorsements;
    }
}
