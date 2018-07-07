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
import org.bcia.julongchain.protos.node.SmartContractPackage;

import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class SmartContractProposalPayloadVO implements IProtoVO<ProposalPackage.SmartContractProposalPayload> {
    private Map<String, ByteString> transientMap;
    private SmartContractPackage.SmartContractInvocationSpec input;

    @Override
    public void parseFrom(ProposalPackage.SmartContractProposalPayload smartContractProposalPayload) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(smartContractProposalPayload, "smartContractProposalPayload can not be null");

        this.transientMap = smartContractProposalPayload.getTransientMapMap();

        if (smartContractProposalPayload.getInput() != null) {
            this.input = SmartContractPackage.SmartContractInvocationSpec.parseFrom(smartContractProposalPayload.getInput());
        }
    }

    @Override
    public ProposalPackage.SmartContractProposalPayload toProto() {
        ProposalPackage.SmartContractProposalPayload.Builder builder = ProposalPackage.SmartContractProposalPayload
                .newBuilder();
        builder.setInput(input.toByteString());
        builder.putAllTransientMap(transientMap);

        return builder.build();
    }

    public Map<String, ByteString> getTransientMap() {
        return transientMap;
    }

    public SmartContractPackage.SmartContractInvocationSpec getInput() {
        return input;
    }
}
