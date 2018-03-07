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
package org.bcia.javachain.node.common;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSignerImpl;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

import java.io.IOException;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EnvelopeHelper.class);

    public static void sendCreateGroupTransaction() {

    }

    public static void sendTransaction() {

    }

    public static Common.Envelope sanityCheckAndSignConfigTx(Common.Envelope envelope, String gourpId) throws NodeException {
        Common.Payload payload = null;

        try {
            //从Envelope解析出Payload对象
            payload = Common.Payload.newBuilder().mergeFrom(envelope.getPayload()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong payload");
        }

        if (payload == null) {
            throw new NodeException("Missing payload");
        }

        if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
            throw new NodeException("Missing header");
        }

        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.newBuilder().mergeFrom(payload.getHeader().getGroupHeader()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong header");
        }

        if (groupHeader.getType() != Common.HeaderType.CONFIG_UPDATE_VALUE) {
            throw new NodeException("Wrong header type");
        }

        if (StringUtils.isBlank(groupHeader.getGroupId())) {
            throw new NodeException("Missing group id");
        }

        if (!groupHeader.getGroupId().equals(gourpId)) {
            throw new NodeException("Wrong group id");
        }

        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = null;

        try {
            //从Envelope解析出Payload对象
            configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.newBuilder().mergeFrom(payload.getData()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong config update envelope");
        }

        return null;


    }

    public static Common.Envelope buildSignedEnvelope(int type, int version, String groupId, ILocalSigner signer, byte[]
            data, long epoch) throws NodeException, IOException {
        Common.Envelope.Builder envelopeBuilder = Common.Envelope.newBuilder();

        Common.Payload payload = buildPayload(type, version, groupId, signer, data, epoch);
        MethodDescriptor.Marshaller<Common.Payload> payloadMarshaller = ProtoUtils
                .marshaller(Common.Payload.getDefaultInstance());
        byte[] payloadBytes = IOUtils.toByteArray(payloadMarshaller.stream(payload));

        byte[] signatureBytes = signer.sign(payloadBytes);

        envelopeBuilder.setPayload(ByteString.copyFrom(payloadBytes));
        envelopeBuilder.setSignature(ByteString.copyFrom(signatureBytes));

        return envelopeBuilder.build();
    }

    public static Common.Payload buildPayload(int type, int version, String groupId, ILocalSigner signer, byte[]
            data, long epoch) throws NodeException, IOException {
        Common.Payload.Builder payloadBuilder = Common.Payload.newBuilder();

        Common.GroupHeader groupHeader = buildGroupHeader(type, version, groupId, epoch);
        MethodDescriptor.Marshaller<Common.GroupHeader> groupHeaderMarshaller = ProtoUtils
                .marshaller(Common.GroupHeader.getDefaultInstance());
        byte[] groupHeaderBytes = IOUtils.toByteArray(groupHeaderMarshaller.stream(groupHeader));

        Common.SignatureHeader signatureHeader = new LocalSignerImpl().newSignatureHeader();
        MethodDescriptor.Marshaller<Common.SignatureHeader> signatureHeaderMarshaller = ProtoUtils
                .marshaller(Common.SignatureHeader.getDefaultInstance());
        byte[] signatureHeaderBytes = IOUtils.toByteArray(signatureHeaderMarshaller.stream(signatureHeader));

        Common.Header.Builder headerBuilder = Common.Header.newBuilder();
        headerBuilder.setGroupHeader(ByteString.copyFrom(groupHeaderBytes));
        headerBuilder.setSignatureHeader(ByteString.copyFrom(signatureHeaderBytes));
        Common.Header header = headerBuilder.build();

        payloadBuilder.setHeader(header);
        payloadBuilder.setData(ByteString.copyFrom(data));

        return payloadBuilder.build();
    }

    /**
     * 建造GroupHeader对象
     *
     * @param type    消息类型
     * @param version 消息协议的版本
     * @param groupId 群组ID
     * @param epoch   所属纪元，目前以所需区块的高度值填充
     * @return
     */
    public static Common.GroupHeader buildGroupHeader(int type, int version, String groupId, long epoch) {
        Common.GroupHeader.Builder groupHeaderBuilder = Common.GroupHeader.newBuilder();
        groupHeaderBuilder.setType(type);
        groupHeaderBuilder.setVersion(version);
        groupHeaderBuilder.setTimestamp(nowTimestamp());
        groupHeaderBuilder.setGroupId(groupId);
        groupHeaderBuilder.setEpoch(epoch);

        return groupHeaderBuilder.build();
    }

    /**
     * 获取当前的时间戳
     *
     * @return
     */
    public static Timestamp nowTimestamp() {
        long millis = System.currentTimeMillis();
        //完成秒和纳秒（即10亿分之一秒）的设置
        return Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
    }

}
