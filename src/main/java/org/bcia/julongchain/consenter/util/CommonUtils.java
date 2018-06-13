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
package org.bcia.julongchain.consenter.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/11
 * @company Dingxuan
 */
public class CommonUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CommonUtils.class);

    public static byte[] marshlOrPanic(Message message){
        return marshal(message);
    }
    public static byte[] marshal(Message message){
        return  message.toByteArray();
    }

    public static Common.Envelope extractEnvelop(Common.Block block, int index) {
        if (block.getData() == null) {
            log.error("No data in block");
        }
        int envelopCount = block.getData().getDataList().size();
        if (index < 0 || index >= envelopCount) {
            log.error("Envelope index out of bounds");
        }
        Common.Envelope envelope = TxUtils.getEnvelopeFromBlock(block.getData().getData(index).toByteArray());
        return envelope;
    }

    public static Common.SignatureHeader newSignatureHeaderOrPanic(ILocalSigner signer) {
        if (signer == null) {
            log.error("Invalid signer. Must be different from nil.");
        }
        Common.SignatureHeader signatureHeader = signer.newSignatureHeader();
        return signatureHeader;
    }

    public static byte[] signOrPanic(ILocalSigner signer, byte[] msg) {
        if (signer == null) {
            log.error("Invalid signer. Must be different from nil.");
        }
        byte[] sigma = signer.sign(msg);
        return sigma;
    }

    public static Common.Envelope extractEnvelopeOrPanic(Common.Block block, int index) {
        Common.Envelope envelope = extractEnvelop(block, index);
        return envelope;
    }

    public static Common.Payload unmarshalPayload(byte[] encoded) throws InvalidProtocolBufferException {
        Common.Payload payload = null;
        payload = Common.Payload.parseFrom(encoded);
        return payload;
    }

    public static Common.GroupHeader unmarshalGroupHeader(byte[] bytes) {
        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return groupHeader;
    }

    public static Common.GroupHeader unmarshalEnvelopeOfType(Common.Envelope envelope, Common.HeaderType headerType, Message message) throws InvalidProtocolBufferException {
        return unmarshalEnvelopeOfTypes(envelope, new Common.HeaderType[]{headerType}, message);
    }

    public static Common.GroupHeader unmarshalEnvelopeOfTypes(Common.Envelope envelope, Common.HeaderType[] expectedHeaderTypes, Message message) throws InvalidProtocolBufferException {
        Common.Payload payload = unmarshalPayload(envelope.getPayload().toByteArray());
        if (payload.getHeader() == null) {
            log.error("Envelope must have a Header");
        }
        Common.GroupHeader chdr = unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());

        boolean headerTypeMatched = false;
        for (int i = 0; i < expectedHeaderTypes.length; i++) {
            if (chdr.getType() == expectedHeaderTypes[i].getNumber()) {
                headerTypeMatched = true;
                break;
            }
        }
        return chdr;

    }

    public static Common.Header makePayloadHeader(Common.GroupHeader ch,Common.SignatureHeader sh){
        Common.Header header=Common.Header.newBuilder()
                .setGroupHeader(ByteString.copyFrom(Utils.marshalOrPanic(ch)))
                .setSignatureHeader(ByteString.copyFrom(Utils.marshalOrPanic(sh))).build();
                return header;
    }

    public static Common.GroupHeader makeGroupHeader(int headerType, int version, String groupId, long epoch) {
        Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder()
                .setType(headerType)
                .setGroupId(groupId)
                .setVersion(version)
                .setEpoch(epoch)
                .setTimestamp(nowTimestamp()).build();
        return groupHeader;
    }

    public static Common.GroupHeader groupHeader(Common.Envelope env) throws InvalidProtocolBufferException {
        Common.Payload  envPayload=  unmarshalPayload(env.getPayload().toByteArray());
        if(envPayload.getHeader()==null){
            log.error("no header was set");
        }
        if(envPayload.getHeader().getGroupHeader()==null){
            log.error("no Group header was set");
        }
        Common.GroupHeader  chdr=  unmarshalGroupHeader(envPayload.getHeader().getGroupHeader().toByteArray());
        return chdr;
    }

    public static Timestamp nowTimestamp() {
        long millis = System.currentTimeMillis();
        //完成秒和纳秒（即10亿分之一秒）的设置
        return Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
    }

    public static String groupId(Common.Envelope env) throws InvalidProtocolBufferException {
        Common.GroupHeader chdr=groupHeader(env);
        return chdr.getGroupId();
    }


    public static Common.Envelope unmarshalEnvelope(byte[] encoded ){
        Common.Envelope envelope=null;
        try {
          envelope=Common.Envelope.parseFrom(encoded);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return envelope;
    }
}
