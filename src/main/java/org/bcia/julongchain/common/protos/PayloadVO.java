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
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class PayloadVO implements IProtoVO<Common.Payload> {
    private GroupHeaderVO groupHeaderVO;
    private Common.SignatureHeader signatureHeader;
    private IProtoVO dataVO;

    @Override
    public void parseFrom(Common.Payload payload) throws InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(payload, "payload can not be null");
        ValidateUtils.isNotNull(payload.getHeader(), "payload.header can not be null");
        ValidateUtils.isNotNull(payload.getHeader().getGroupHeader(), "payload.groupHeader can not be null");
        ValidateUtils.isNotNull(payload.getHeader().getSignatureHeader(), "payload.signatureHeader can not be null");
        ValidateUtils.isNotNull(payload.getData(), "payload.data can not be null");

        groupHeaderVO = new GroupHeaderVO();
        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        groupHeaderVO.parseFrom(groupHeader);

        this.signatureHeader = Common.SignatureHeader.parseFrom(payload.getHeader().getSignatureHeader());

        if (groupHeaderVO.getType() == Common.HeaderType.ENDORSER_TRANSACTION_VALUE) {
            this.dataVO = new TransactionVO();
            TransactionPackage.Transaction transaction = TransactionPackage.Transaction.parseFrom(payload.getData());
            this.dataVO.parseFrom(transaction);
        } else if (groupHeaderVO.getType() == Common.HeaderType.CONFIG_VALUE) {
            this.dataVO = new ConfigEnvelopeVO();
            Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
            this.dataVO.parseFrom(configEnvelope);
        } else if (groupHeaderVO.getType() == Common.HeaderType.CONFIG_UPDATE_VALUE) {
            this.dataVO = new ConfigUpdateEnvelopeVO();
            Configtx.ConfigUpdateEnvelope configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.parseFrom(payload
                    .getData());
            this.dataVO.parseFrom(configUpdateEnvelope);
        }
    }

    @Override
    public Common.Payload toProto() {
        Common.Payload.Builder builder = Common.Payload.newBuilder();
        builder.getHeaderBuilder().setGroupHeader(groupHeaderVO.toProto().toByteString());
        builder.getHeaderBuilder().setSignatureHeader(signatureHeader.toByteString());

        if (dataVO instanceof TransactionVO) {
            builder.setData(((TransactionVO) dataVO).toProto().toByteString());
        }

        return builder.build();
    }

    public GroupHeaderVO getGroupHeaderVO() {
        return groupHeaderVO;
    }

    public Common.SignatureHeader getSignatureHeader() {
        return signatureHeader;
    }

    public IProtoVO getDataVO() {
        return dataVO;
    }
}