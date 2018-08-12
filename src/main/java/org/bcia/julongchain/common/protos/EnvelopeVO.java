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
import org.bcia.julongchain.protos.common.Common;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class EnvelopeVO implements IProtoVO<Common.Envelope> {
    private PayloadVO payloadVO;
    private ByteString signature;

    @Override
    public void parseFrom(Common.Envelope envelope) throws InvalidProtocolBufferException, ValidateException {
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        this.payloadVO = new PayloadVO();
        payloadVO.parseFrom(payload);

        this.signature = envelope.getSignature();
    }

    @Override
    public Common.Envelope toProto() {
        Common.Envelope.Builder builder = Common.Envelope.newBuilder();
        builder.setPayload(payloadVO.toProto().toByteString());

        if (signature != null) {
            builder.setSignature(signature);
        }
        return builder.build();
    }

    public PayloadVO getPayloadVO() {
        return payloadVO;
    }

    public ByteString getSignature() {
        return signature;
    }
}
