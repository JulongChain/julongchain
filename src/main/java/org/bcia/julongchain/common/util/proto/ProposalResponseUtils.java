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
package org.bcia.julongchain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 提案响应工具类
 *
 * @author zhouhui
 * @date 2018/3/22
 * @company Dingxuan
 */
public class ProposalResponseUtils {
    private static JulongChainLog log = JulongChainLogFactory.getLog(ProposalResponseUtils.class);

    private static final String OK = "OK";
    private static final int RESPONSE_VERSION = 1;

    /**
     * 构造标准提案响应
     *
     * @param payload
     * @param signingIdentity
     * @return
     * @throws ValidateException
     */
    public static ProposalResponsePackage.ProposalResponse buildNormalProposalResponse(
            ProposalResponsePackage.ProposalResponsePayload payload, ISigningIdentity signingIdentity)
            throws ValidateException {
        //首先构造响应主内容
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(Common.Status.SUCCESS_VALUE);
        responseBuilder.setMessage(OK);
        ProposalResponsePackage.Response response = responseBuilder.build();

        byte[] payloadBytes = payload.toByteArray();
        ValidateUtils.isNotNull(payloadBytes, "PayloadBytes can not be null");

        byte[] endorser = signingIdentity.getIdentity().serialize();
        byte[] signature = signingIdentity.sign(ArrayUtils.addAll(payloadBytes, endorser));

        //构造Endorsement
        ProposalResponsePackage.Endorsement.Builder endorsementBuilder =
                ProposalResponsePackage.Endorsement.newBuilder();
        endorsementBuilder.setEndorser(ByteString.copyFrom(endorser));
        endorsementBuilder.setSignature(ByteString.copyFrom(signature));
        ProposalResponsePackage.Endorsement endorsement = endorsementBuilder.build();

        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder =
                ProposalResponsePackage.ProposalResponse.newBuilder();
        proposalResponseBuilder.setVersion(RESPONSE_VERSION);
        proposalResponseBuilder.setTimestamp(EnvelopeHelper.nowTimestamp());

        proposalResponseBuilder.setResponse(response);
        proposalResponseBuilder.setPayload(ByteString.copyFrom(payloadBytes));
        proposalResponseBuilder.setEndorsement(endorsement);

        return proposalResponseBuilder.build();
    }

    /**
     * 构造提案响应
     *
     * @param payload
     * @param message
     * @return
     */
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(ByteString payload, String message) {
        //首先构造响应主内容
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(Common.Status.SUCCESS_VALUE);
        responseBuilder.setPayload(payload);
        if (StringUtils.isNotBlank(message)) {
            responseBuilder.setMessage(message);
        }

        ProposalResponsePackage.Response response = responseBuilder.build();

        //构造提案响应
        return buildProposalResponse(response);
    }

    /**
     * 构造提案响应
     *
     * @param response
     * @return
     */
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(
            ProposalResponsePackage.Response response) {
        //构造提案响应
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        proposalResponseBuilder.setResponse(response);
        return proposalResponseBuilder.build();
    }

    /**
     * 构造带错误消息的提案响应
     *
     * @param errorMsg
     * @return
     */
    public static ProposalResponsePackage.ProposalResponse buildErrorProposalResponse(
            Common.Status status, String errorMsg) {
        //首先构造响应主内容
        ProposalResponsePackage.Response response = buildErrorResponse(status, errorMsg);

        //构造提案响应
        return buildProposalResponse(response);
    }

    /**
     * 构造带错误消息的响应
     *
     * @param errorMsg
     * @return
     */
    public static ProposalResponsePackage.Response buildErrorResponse(Common.Status status, String errorMsg) {
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(status.getNumber());
        responseBuilder.setMessage(errorMsg);
        return responseBuilder.build();
    }

    /**
     * 构造ProposalResponse
     *
     * @param headerBytes
     * @param payloadBytes
     * @param response
     * @param results
     * @param events
     * @param scId
     * @param visibility
     * @param signingIdentity
     * @return
     * @throws InvalidProtocolBufferException
     * @throws JulongChainException
     */
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(
            byte[] headerBytes, byte[] payloadBytes, ProposalResponsePackage.Response response, byte[] results,
            byte[] events, SmartContractPackage.SmartContractID scId, byte[] visibility,
            ISigningIdentity signingIdentity) throws InvalidProtocolBufferException, JulongChainException {
        ValidateUtils.isNotNull(headerBytes, "HeaderBytes can not be null");
        ValidateUtils.isNotNull(payloadBytes, "PayloadBytes can not be null");

        //强转仅仅是为了校验
        Common.Header header = Common.Header.parseFrom(headerBytes);

        ValidateUtils.isNotNull(header.getGroupHeader(), "Header.GroupHeader can not be null");
        ValidateUtils.isNotNull(header.getSignatureHeader(), "Header.SignatureHeader can not be null");

        ProposalPackage.SmartContractProposalPayload smartContractProposalPayload = ProposalPackage
                .SmartContractProposalPayload.parseFrom(payloadBytes);
        byte[] proposalPayloadForTxBytes = getBytesProposalPayloadForTx(smartContractProposalPayload, visibility);

        //计算提案Hash
        byte[] proposalHash = hashBytes(headerBytes, proposalPayloadForTxBytes);

        //构造ProposalResponsePayload
        ProposalResponsePackage.ProposalResponsePayload responsePayload = buildProposalResponsePayload(results,
                events, response, scId, proposalHash);
        return buildNormalProposalResponse(responsePayload, signingIdentity);
    }

    /**
     * 将多个byte数组拼接后进行哈希
     *
     * @param bytesArray
     * @return
     * @throws JulongChainException
     */
    private static byte[] hashBytes(byte[]... bytesArray) throws JulongChainException {
        byte[] bytes = null;
        for (int i = 0; i < bytesArray.length; i++) {
            if (i == 0) {
                bytes = bytesArray[0];
            } else {
                bytes = ArrayUtils.addAll(bytes, bytesArray[i]);
            }
        }

        return CspManager.getDefaultCsp().hash(bytes, null);
    }

    /**
     * 获取ProposalPayload的字节流(去临时信息)
     *
     * @param proposalPayload
     * @param visibility
     * @return
     * @throws ValidateException
     */
    private static byte[] getBytesProposalPayloadForTx(ProposalPackage.SmartContractProposalPayload proposalPayload,
                                                       byte[] visibility) throws ValidateException {
        ValidateUtils.isNotNull(proposalPayload, "SmartContractProposalPayload can not be null");
        //TODO:visibility在将来的版本中实现

        //将SmartContractProposalPayload去transientMap属性
        ProposalPackage.SmartContractProposalPayload.Builder newProposalPayloadBuilder = ProposalPackage
                .SmartContractProposalPayload.newBuilder(proposalPayload);
        newProposalPayloadBuilder.clearTransientMap();
        ProposalPackage.SmartContractProposalPayload newProposalPayload = newProposalPayloadBuilder.build();

        return newProposalPayload.toByteArray();
    }

    public static ProposalResponsePackage.ProposalResponsePayload buildProposalResponsePayload(
            ByteString event, ByteString result, ProposalResponsePackage.Response response, SmartContractPackage
            .SmartContractID smartContractId, ByteString hash) {
        ProposalPackage.SmartContractAction.Builder actionBuilder = ProposalPackage.SmartContractAction.newBuilder();
        actionBuilder.setEvents(event);
        actionBuilder.setResults(result);
        actionBuilder.setResponse(response);
        actionBuilder.setSmartContractId(smartContractId);
        ProposalPackage.SmartContractAction action = actionBuilder.build();

        ProposalResponsePackage.ProposalResponsePayload.Builder payloadBuilder = ProposalResponsePackage
                .ProposalResponsePayload.newBuilder();
        payloadBuilder.setExtension(action.toByteString());
        payloadBuilder.setProposalHash(hash);
        return payloadBuilder.build();
    }

    /**
     * 构造ProposalResponsePayload
     *
     * @param results
     * @param events
     * @param response
     * @param scId
     * @param hash
     * @return
     */
    public static ProposalResponsePackage.ProposalResponsePayload buildProposalResponsePayload(
            byte[] results, byte[] events, ProposalResponsePackage.Response response,
            SmartContractPackage.SmartContractID scId, byte[] hash) {
        ProposalResponsePackage.ProposalResponsePayload.Builder payloadBuilder = ProposalResponsePackage
                .ProposalResponsePayload.newBuilder();
        if (hash != null) {
            payloadBuilder.setProposalHash(ByteString.copyFrom(hash));
        }

        ProposalPackage.SmartContractAction smartContractAction = buildSmartContractAction(results, events, response,
                scId);
        payloadBuilder.setExtension(smartContractAction.toByteString());

        return payloadBuilder.build();
    }

    /**
     * 构造SmartContractAction
     *
     * @param results
     * @param events
     * @param response
     * @param scId
     * @return
     */
    public static ProposalPackage.SmartContractAction buildSmartContractAction(
            byte[] results, byte[] events, ProposalResponsePackage.Response response,
            SmartContractPackage.SmartContractID scId) {
        ProposalPackage.SmartContractAction.Builder actionBuilder = ProposalPackage.SmartContractAction.newBuilder();
        if (results != null) {
            actionBuilder.setResults(ByteString.copyFrom(results));
        }
        if (events != null) {
            actionBuilder.setEvents(ByteString.copyFrom(events));
        }

        actionBuilder.setResponse(response);
        actionBuilder.setSmartContractId(scId);
        return actionBuilder.build();
    }
}