/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.consenter.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/10
 * @company Dingxuan
 */
public class TxUtils {
    public static Common.Envelope getEnvelopeFromBlock(byte[] data) {
        Common.Envelope.Builder envelope = Common.Envelope.newBuilder();
        try {
            envelope.mergeFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return envelope.build();
    }

    public static Common.Envelope createSignedEnvelope (int txType, String groupId, ILocalSigner signer, Message dataMsg, int msgVersion, long epoch) {
        return createSignedEnvelopeWithTLSBinding(txType, groupId, signer, dataMsg, msgVersion, epoch, null);
    }

    public static Common.Envelope createSignedEnvelopeWithTLSBinding(int txType, String groupId, ILocalSigner signer, Message dataMsg, int msgVersion, long epoch, byte[] tlsCertHash) {
        Common.GroupHeader groupHeader = CommonUtils.makeGroupHeader(txType, msgVersion, groupId, epoch);
        groupHeader.toBuilder().setTlsCertHash(ByteString.copyFrom(tlsCertHash));

        Common.SignatureHeader signatureHeader = null;
        if (signer != null) {
            signatureHeader = signer.newSignatureHeader();
        }
        Common.Payload payload = Common.Payload.newBuilder()
                .setHeader(CommonUtils.makePayloadHeader(groupHeader, signatureHeader))
                .setData(dataMsg.toByteString()).build();
        byte[] paylBytes = Utils.marshalOrPanic(payload);

        byte[] sig = new byte[0];
        if (signer != null) {
            sig = signer.sign(paylBytes);
        }
        Common.Envelope envelope = Common.Envelope.newBuilder()
                .setPayload(ByteString.copyFrom(paylBytes))
                .setSignature(ByteString.copyFrom(sig)).build();
        return envelope;
    }
}
