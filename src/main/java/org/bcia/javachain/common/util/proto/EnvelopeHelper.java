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
package org.bcia.javachain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.node.common.helper.ConfigChildHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;

import java.io.IOException;
import java.util.Map;

/**
 * 信封对象帮助类
 *
 * @author zhouhui
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EnvelopeHelper.class);

    public static Common.Envelope makeGroupCreateTx(String groupId, LocalSigner signer, Configtx.ConfigChild
            consenterSystemGroupChild, GenesisConfig.Profile profile) {
        return null;

    }

    public static void sendCreateGroupTransaction() {

    }

    public static void sendTransaction() {

    }

    public static Configtx.ConfigUpdate buildConfigUpdate(String groupId, Configtx.ConfigChild
            consenterSystemGroupChild, GenesisConfig.Profile profile) throws NodeException {
        if (profile.getApplication() == null) {
            throw new NodeException("No Application in profile");
        }

        if (profile.getConsortium() == null) {
            throw new NodeException("No Consortium in profile");
        }

        Configtx.ConfigChild configChild = ConfigChildHelper.buildApplicationChild(profile.getApplication());


        return null;
    }

    /**
     * 从文件中读取成一个Envelope对象
     *
     * @param filePath
     * @return
     * @throws NodeException
     */
    public static Common.Envelope readFromFile(String filePath) throws NodeException {
        Common.Envelope envelope = null;
        try {
            byte[] bytes = FileUtils.readFileBytes(filePath);
            envelope = Common.Envelope.parseFrom(bytes);
            return envelope;
        } catch (IOException e) {
            throw new NodeException("Can not read Group File");
        }
    }

    /**
     * 对原始Envelope对象进行完整性检查，并生成带签名的Envelope对象
     *
     * @param envelope
     * @param groupId
     * @param signer
     * @return
     * @throws NodeException
     */
    public static Common.Envelope sanityCheckAndSignConfigTx(Common.Envelope envelope, String groupId, ILocalSigner signer)
            throws NodeException {
        //检查Payload字段是否有误
        if (envelope.getPayload() == null || envelope.getPayload().isEmpty()) {
            //检查是否为空
            throw new NodeException("Missing payload");
        }
        Common.Payload payload = null;
        try {
            //从Envelope解析出Payload对象
            payload = Common.Payload.parseFrom(envelope.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong payload");
        }

        //检查Payload->Header->GroupHeader字段是否有误
        if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
            throw new NodeException("Missing header");
        }
        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong header");
        }

        //检查消息类型
        if (groupHeader.getType() != Common.HeaderType.CONFIG_UPDATE_VALUE) {
            throw new NodeException("Wrong header type");
        }

        //检查群组ID
        if (StringUtils.isBlank(groupHeader.getGroupId())) {
            throw new NodeException("Missing group id");
        }
        if (!groupHeader.getGroupId().equals(groupId)) {
            throw new NodeException("Wrong group id");
        }

        //检查Data字段是否是ConfigUpdateEnvelope类型
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = null;

        try {
            configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.parseFrom(payload.getData());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong config update envelope");
        }

        Configtx.ConfigUpdateEnvelope signedConfigUpdateEnvelope = signConfigUpdateEnvelope(configUpdateEnvelope,
                signer);
        Common.Envelope signedEnvelope = buildSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, groupHeader.getVersion(),
                groupId, signer, signedConfigUpdateEnvelope, groupHeader.getEpoch());

        return signedEnvelope;
    }

    /**
     * 对一个ConfigUpdateEnvelope对象进行签名
     *
     * @param originalEnvelope
     * @param signer
     * @return
     */
    public static Configtx.ConfigUpdateEnvelope signConfigUpdateEnvelope(Configtx.ConfigUpdateEnvelope originalEnvelope,
                                                                         ILocalSigner signer) {
        //获取ConfigUpdateEnvelope对象的构造器,拷贝原对象
        Configtx.ConfigUpdateEnvelope.Builder envelopeBuilder = Configtx.ConfigUpdateEnvelope.newBuilder(originalEnvelope);

        //构造签名对象,由两个字段构成SignatureHeader和Signature（其中Signature是针对SignatureHeader+ConfigUpdate的签名）
        Configtx.ConfigSignature.Builder configSignatureBuilder = Configtx.ConfigSignature.newBuilder();
        Common.SignatureHeader signatureHeader = signer.newSignatureHeader();
        //由SignatureHeader+ConfigUpdate合成原始字节数组
        byte[] original = ArrayUtils.addAll(signatureHeader.toByteArray(), originalEnvelope.getConfigUpdate().toByteArray());
        //对原始数组进行签名
        byte[] signature = signer.sign(original);

        configSignatureBuilder.setSignatureHeader(signatureHeader.toByteString());
        configSignatureBuilder.setSignature(ByteString.copyFrom(signature));
        Configtx.ConfigSignature configSignature = configSignatureBuilder.build();

        //ConfigUpdateEnvelope对象由ConfigUpdate和若干个ConfigSignature组成。增加一个签名即可
        envelopeBuilder.addSignatures(configSignature);

        return envelopeBuilder.build();
    }

    /**
     * 构建带签名的信封对象
     *
     * @param type    消息类型
     * @param version 消息协议版本
     * @param groupId 群组ID
     * @param signer  签名者
     * @param data    数据对象
     * @param epoch   所属纪元
     * @return
     */
    public static Common.Envelope buildSignedEnvelope(int type, int version, String groupId, ILocalSigner signer,
                                                      Message data, long epoch) {
        //获取Envelope对象的构造器
        Common.Envelope.Builder envelopeBuilder = Common.Envelope.newBuilder();

        //构造Payload
        Common.Payload payload = buildPayload(type, version, groupId, signer, data, epoch);

        //Signature字段由Payload字段签名而成
        byte[] signatureBytes = signer.sign(payload.toByteArray());

        envelopeBuilder.setPayload(payload.toByteString());
        envelopeBuilder.setSignature(ByteString.copyFrom(signatureBytes));

        return envelopeBuilder.build();
    }

    /**
     * 构造Payload对象
     *
     * @param type    消息类型
     * @param version 消息协议版本
     * @param groupId 群组ID
     * @param signer  签名者
     * @param data    数据字段
     * @param epoch   所属纪元
     * @return
     * @throws NodeException
     */
    public static Common.Payload buildPayload(int type, int version, String groupId, ILocalSigner signer, Message
            data, long epoch) {
        //获取Payload对象的构造器
        Common.Payload.Builder payloadBuilder = Common.Payload.newBuilder();

        //构造头部,包含GroupHeader和SignatureHeader两个字段
        Common.GroupHeader groupHeader = buildGroupHeader(type, version, groupId, epoch);
        Common.SignatureHeader signatureHeader = signer.newSignatureHeader();

        Common.Header.Builder headerBuilder = Common.Header.newBuilder();
        headerBuilder.setGroupHeader(groupHeader.toByteString());
        headerBuilder.setSignatureHeader(signatureHeader.toByteString());
        Common.Header header = headerBuilder.build();

        //Payload对象包含头部Header和Data两个字段
        payloadBuilder.setHeader(header);
        payloadBuilder.setData(data.toByteString());

        return payloadBuilder.build();
    }

    /**
     * 构造Header对象
     *
     * @param type      消息类型
     * @param version   消息协议的版本
     * @param groupId   群组ID
     * @param txId      交易ID
     * @param epoch     所属纪元，目前以所需区块的高度值填充
     * @param extension 智能合约扩展对象
     * @param creator   消息创建者
     * @param nonce     随机数，仅可使用一次。用于防止重播攻击
     * @return
     */
    public static Common.Header buildHeader(int type, int version, String groupId, String txId, long epoch,
                                            ProposalPackage.SmartContractHeaderExtension extension, byte[]
                                                    creator, byte[] nonce) {
        //构造GroupHeader对象
        Common.GroupHeader groupHeader = buildGroupHeader(type, 0, groupId, txId, 0, extension);
        //构造SignatureHeader对象
        Common.SignatureHeader SignatureHeader = buildSignatureHeader(creator, nonce);

        //构造Header对象
        Common.Header.Builder headerBuilder = Common.Header.newBuilder();
        headerBuilder.setGroupHeader(groupHeader.toByteString());
        headerBuilder.setSignatureHeader(SignatureHeader.toByteString());
        return headerBuilder.build();
    }

    /**
     * 构造GroupHeader对象
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
     * 构造GroupHeader对象
     *
     * @param type      消息类型
     * @param version   消息协议的版本
     * @param groupId   群组ID
     * @param txId      交易ID
     * @param epoch     所属纪元，目前以所需区块的高度值填充
     * @param extension 智能合约扩展对象
     * @return
     */
    public static Common.GroupHeader buildGroupHeader(int type, int version, String groupId, String txId, long epoch,
                                                      ProposalPackage.SmartContractHeaderExtension extension) {
        //首先构造GroupHeader对象
        Common.GroupHeader.Builder groupHeaderBuilder = Common.GroupHeader.newBuilder();
        groupHeaderBuilder.setType(type);
        groupHeaderBuilder.setVersion(version);
        //TODO:是否采用UTC的时间或更精准的时间
        groupHeaderBuilder.setTimestamp(EnvelopeHelper.nowTimestamp());
        groupHeaderBuilder.setGroupId(groupId);
        groupHeaderBuilder.setTxId(txId);
        groupHeaderBuilder.setEpoch(epoch);
        groupHeaderBuilder.setExtension(extension.toByteString());

        return groupHeaderBuilder.build();
    }

    /**
     * 构造SignatureHeader对象
     *
     * @param creator 消息创建者
     * @param nonce   随机数，仅可使用一次。用于防止重播攻击
     * @return
     */
    public static Common.SignatureHeader buildSignatureHeader(byte[] creator, byte[] nonce) {
        //构造SignatureHeader对象
        Common.SignatureHeader.Builder signatureHeaderBuilder = Common.SignatureHeader.newBuilder();
        signatureHeaderBuilder.setCreator(ByteString.copyFrom(creator));
        signatureHeaderBuilder.setNonce(ByteString.copyFrom(nonce));
        return signatureHeaderBuilder.build();
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