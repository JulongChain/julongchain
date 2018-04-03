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
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.ApplicationConfig;
import org.bcia.javachain.common.groupconfig.GroupConfigConstant;
import org.bcia.javachain.common.groupconfig.MSPConfigHandler;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.node.common.helper.ConfigChildHelper;
import org.bcia.javachain.node.common.helper.ConfigUpdateHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bouncycastle.util.Arrays;

import java.io.IOException;

/**
 * 信封对象帮助类
 *
 * @author zhouhui
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EnvelopeHelper.class);

    public static Common.Envelope makeGroupCreateTx(String groupId, ILocalSigner signer, Configtx.ConfigChild
            consenterSystemGroupChild, GenesisConfig.Profile profile) throws InvalidProtocolBufferException,
            NodeException, ValidateException {
        Configtx.ConfigUpdate configUpdate = buildConfigUpdate(groupId, consenterSystemGroupChild, profile);

        Configtx.ConfigUpdateEnvelope.Builder envelopeBuilder = Configtx.ConfigUpdateEnvelope.newBuilder();
        envelopeBuilder.setConfigUpdate(configUpdate.toByteString());
        Configtx.ConfigUpdateEnvelope envelope = envelopeBuilder.build();

        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = signConfigUpdateEnvelope(envelope, signer);

        return buildSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, groupId, signer, configUpdateEnvelope,
                0);

    }

    public static void sendCreateGroupTransaction() {

    }

    public static void sendTransaction() {

    }

    public static Configtx.ConfigUpdate buildConfigUpdate(String groupId, Configtx.ConfigChild
            consenterSystemGroupChild, GenesisConfig.Profile profile) throws NodeException,
            InvalidProtocolBufferException, ValidateException {
        if (profile.getApplication() == null) {
            throw new NodeException("No Application in profile");
        }

        if (profile.getConsortium() == null) {
            throw new NodeException("No Consortium in profile");
        }

        //构造应用子树
        Configtx.ConfigChild appChild = ConfigChildHelper.buildApplicationChild(profile.getApplication());
        //得到最终的应用配置
        ApplicationConfig appConfig = new ApplicationConfig(appChild, new MSPConfigHandler(0));

        if (consenterSystemGroupChild != null) {
            //TODO:要实现吗？
        } else {

        }

        Configtx.ConfigChild originalChild = Configtx.ConfigChild.newBuilder().build();

        Configtx.ConfigChild.Builder groupChildBuilder = Configtx.ConfigChild.newBuilder();
        groupChildBuilder.putChilds(GroupConfigConstant.APPLICATION, appChild);
        Configtx.ConfigChild pendingChild = groupChildBuilder.build();

        Configtx.Config original = Configtx.Config.newBuilder().setGroupChild(originalChild).build();
        Configtx.Config pending = Configtx.Config.newBuilder().setGroupChild(pendingChild).build();
        Configtx.ConfigUpdate configUpdate = ConfigUpdateHelper.compute(original, pending);

        //TODO:
//        if(appConfig.getCapabilities().)


        return configUpdate;
    }

    public static Common.Envelope createSignedTxEnvelope(ProposalPackage.Proposal originalProposal, ISigningIdentity
            identity, ProposalResponsePackage.ProposalResponse... endorserResponses) throws ValidateException {
        if (originalProposal == null || identity == null || endorserResponses == null) {
            log.warn("Args should not be null");
            throw new ValidateException("Args should not be null");
        }

        //校验并获取Proposal头部
        Common.Header header = null;
        try {
            header = Common.Header.parseFrom(originalProposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new ValidateException("Wrong proposal header");
        }

        //校验并获取Proposal负载，应为SmartContractProposalPayload对象
        ProposalPackage.SmartContractProposalPayload smartContractProposalPayload = null;
        try {
            smartContractProposalPayload = ProposalPackage.SmartContractProposalPayload.parseFrom(
                    originalProposal.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new ValidateException("Wrong proposal payload");
        }

        //校验并获取签名头部
        Common.SignatureHeader signatureHeader = null;
        try {
            signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new ValidateException("Wrong proposal header signatureHeader");
        }

        //签名头部的消息创建者字段应与身份一致
        if (Arrays.compareUnsigned(signatureHeader.getCreator().toByteArray(), identity.serialize()) != 0) {
            throw new ValidateException("Wrong signatureHeader creator");
        }

        ProposalPackage.SmartContractHeaderExtension extension = null;
        try {
            Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(header.getGroupHeader());
            extension = ProposalPackage.SmartContractHeaderExtension.parseFrom(groupHeader.getExtension());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            //不能成功转化，说明是错误的智能合约头部扩展
            throw new ValidateException("Wrong SmartContractHeaderExtension");
        }

        byte[] bytes = null;
        TransactionPackage.SmartContractEndorsedAction.Builder endorsedActionBuilder = TransactionPackage
                .SmartContractEndorsedAction.newBuilder();

        ProposalResponsePackage.Endorsement[] endorsements = new ProposalResponsePackage.Endorsement[endorserResponses
                .length];
        for (int i = 0; i < endorserResponses.length; i++) {
            ProposalResponsePackage.ProposalResponse endorserResponse = endorserResponses[i];

            if (endorserResponse.getResponse().getStatus() != 200) {
                throw new ValidateException("endorserResponse status error: " + endorserResponse);
            }

            if (i == 0) {
                bytes = endorserResponse.getPayload().toByteArray();
                endorsedActionBuilder.setProposalResponsePayload(endorserResponse.getPayload());
            } else {
                if (Arrays.compareUnsigned(bytes, endorserResponse.getPayload().toByteArray()) != 0) {
                    throw new ValidateException("Should be same payload");
                }
            }

            endorsements[i] = endorserResponse.getEndorsement();
            endorsedActionBuilder.addEndorsements(endorserResponse.getEndorsement());
        }
        TransactionPackage.SmartContractEndorsedAction endorsedAction = endorsedActionBuilder.build();

        //TODO:为什么要去掉transientMap属性？
        ProposalPackage.SmartContractProposalPayload.Builder proposalPayloadBuilder = ProposalPackage
                .SmartContractProposalPayload.newBuilder(smartContractProposalPayload);
        proposalPayloadBuilder.clearTransientMap();
        ProposalPackage.SmartContractProposalPayload clearProposalPayload = proposalPayloadBuilder.build();

        TransactionPackage.SmartContractActionPayload.Builder actionPayloadBuilder = TransactionPackage
                .SmartContractActionPayload.newBuilder();
        actionPayloadBuilder.setSmartContractProposalPayload(clearProposalPayload.toByteString());
        actionPayloadBuilder.setAction(endorsedAction);
        TransactionPackage.SmartContractActionPayload actionPayload = actionPayloadBuilder.build();

        TransactionPackage.TransactionAction.Builder transactionActionBuilder = TransactionPackage.TransactionAction
                .newBuilder();
        transactionActionBuilder.setHeader(header.getSignatureHeader());
        transactionActionBuilder.setPayload(actionPayload.toByteString());
        TransactionPackage.TransactionAction transactionAction = transactionActionBuilder.build();

        TransactionPackage.Transaction.Builder transactionBuilder = TransactionPackage.Transaction.newBuilder();
        transactionBuilder.addActions(transactionAction);
        TransactionPackage.Transaction transaction = transactionBuilder.build();

        Common.Payload.Builder payloadBuilder = Common.Payload.newBuilder();
        payloadBuilder.setHeader(header);
        payloadBuilder.setData(transaction.toByteString());
        Common.Payload payload = payloadBuilder.build();

        byte[] signature = identity.sign(payload.toByteArray());

        Common.Envelope.Builder envelopeBuilder = Common.Envelope.newBuilder();
        envelopeBuilder.setPayload(payload.toByteString());
        envelopeBuilder.setSignature(ByteString.copyFrom(signature));
        return envelopeBuilder.build();
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