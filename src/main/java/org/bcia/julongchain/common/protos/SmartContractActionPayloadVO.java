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
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class SmartContractActionPayloadVO implements IProtoVO<TransactionPackage.SmartContractActionPayload> {
    private SmartContractProposalPayloadVO smartContractProposalPayloadVO;
    private SmartContractEndorsedActionVO smartContractEndorsedActionVO;

    @Override
    public void parseFrom(TransactionPackage.SmartContractActionPayload smartContractActionPayload) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(smartContractActionPayload, "smartContractActionPayload can not be null");
        ValidateUtils.isNotNull(smartContractActionPayload.getSmartContractProposalPayload(),
                "smartContractActionPayload.getSmartContractProposalPayload can not be null");
        ValidateUtils.isNotNull(smartContractActionPayload.getAction(), "smartContractActionPayload.getAction can " +
                "not be null");

        this.smartContractProposalPayloadVO = new SmartContractProposalPayloadVO();
        ProposalPackage.SmartContractProposalPayload smartContractProposalPayload = ProposalPackage
                .SmartContractProposalPayload.parseFrom(smartContractActionPayload.getSmartContractProposalPayload());
        this.smartContractProposalPayloadVO.parseFrom(smartContractProposalPayload);

        this.smartContractEndorsedActionVO = new SmartContractEndorsedActionVO();
        if (smartContractActionPayload.getAction() != null) {
            this.smartContractEndorsedActionVO.parseFrom(smartContractActionPayload.getAction());
        }
    }

    @Override
    public TransactionPackage.SmartContractActionPayload toProto() {
        TransactionPackage.SmartContractActionPayload.Builder builder = TransactionPackage.SmartContractActionPayload
                .newBuilder();
        builder.setSmartContractProposalPayload(smartContractProposalPayloadVO.toProto().toByteString());
        builder.setAction(smartContractEndorsedActionVO.toProto());
        return builder.build();
    }

    public SmartContractProposalPayloadVO getSmartContractProposalPayloadVO() {
        return smartContractProposalPayloadVO;
    }

    public SmartContractEndorsedActionVO getSmartContractEndorsedActionVO() {
        return smartContractEndorsedActionVO;
    }
}
