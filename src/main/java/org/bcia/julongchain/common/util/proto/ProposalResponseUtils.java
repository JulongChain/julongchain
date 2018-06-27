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
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.Utils;
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
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(ByteString payload) {
        //首先构造响应主内容
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setPayload(payload);
        ProposalResponsePackage.Response response = responseBuilder.build();

        //构造提案响应
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        proposalResponseBuilder.setResponse(response);
        return proposalResponseBuilder.build();
    }

    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(ProposalResponsePackage.Response response) {
        //构造提案响应
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        proposalResponseBuilder.setResponse(response);
        return proposalResponseBuilder.build();
    }

    public static ProposalResponsePackage.Response buildResponse(ByteString payload) {
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(Common.Status.SUCCESS_VALUE);
        responseBuilder.setPayload(payload);
        return responseBuilder.build();
    }

    /**
     * 构造带错误消息的提案响应
     *
     * @param errorMsg
     * @return
     */
    public static ProposalResponsePackage.ProposalResponse buildErrorProposalResponse(Common.Status status, String errorMsg) {
        //首先构造响应主内容
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(status.getNumber());
        responseBuilder.setMessage(errorMsg);
        ProposalResponsePackage.Response response = responseBuilder.build();

        //构造提案响应
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        proposalResponseBuilder.setResponse(response);
        return proposalResponseBuilder.build();
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
     * 创建提案响应
     *
     * @param headerBytes
     * @param payloadBytes
     * @param response
     * @param results
     * @param events
     * @param smartContractID
     * @param visibility
     * @param signingIdentity
     * @return
     * @author sunianle
     */
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(
            byte[] headerBytes, byte[] payloadBytes, ProposalResponsePackage.Response response,
            byte[] results,
            byte[] events,
            SmartContractPackage.SmartContractID smartContractID,
            byte[] visibility,
            ISigningIdentity signingIdentity) throws InvalidProtocolBufferException, JavaChainException {
        Common.Header header = Common.Header.parseFrom(headerBytes);
        //obtain the proposal hash given proposal header, payload and the requested visibility

        byte[] pHashBytes = null;
        try {
            pHashBytes = getProposalHash1(header, payloadBytes, visibility);
        } catch (Exception e) {
            String msg = String.format("Could not compute proposal hash: err %s", e.getMessage());
            JavaChainException exception = new JavaChainException(msg);
            throw exception;
        }
        //get the bytes of the proposal smartContractResponse payload - we need to sign them
        byte[] prpBytes = null;
        try {
            prpBytes = getBytesProposalResponsePayload(pHashBytes, response, results, events, smartContractID);
        } catch (Exception e) {
            String msg = String.format("Failure while marshaling the ProposalResponsePayload: err %s", e.getMessage());
            JavaChainException exception = new JavaChainException(msg);
            throw exception;
        }

        byte[] endorser = signingIdentity.serialize();
        if (endorser == null || endorser.length == 0) {
            String msg = String.format("Could not serialize the signing identity for %s", signingIdentity.getMSPIdentifier());
            JavaChainException exception = new JavaChainException(msg);
            throw exception;
        }

        //sign the concatenation of the proposal smartContractResponse and the serialized endorser identity with this endorser's key
        byte[] signature = signingIdentity.sign(Utils.appendBytes(prpBytes, endorser));
        if (signature == null || signature.length == 0) {
            String msg = String.format("Could not sign the proposal response payload");
            JavaChainException exception = new JavaChainException(msg);
            throw exception;
        }

        ProposalResponsePackage.ProposalResponse.Builder builder = ProposalResponsePackage.ProposalResponse.newBuilder();
        builder.setVersion(1);
        ProposalResponsePackage.Endorsement endorsement = ProposalResponsePackage.Endorsement.newBuilder()
                .setSignature(ByteString.copyFrom(signature))
                .setEndorser(ByteString.copyFrom(endorser)).build();
        builder.setEndorsement(endorsement);
        builder.setPayload(ByteString.copyFrom(prpBytes));
        ProposalResponsePackage.Response response1 = ProposalResponsePackage.Response.newBuilder().setStatus(200).setMessage("OK").build();
        builder.setResponse(response1);

        return builder.build();
    }

    /**
     * GetBytesProposalResponsePayload gets proposal smartContractResponse payload
     *
     * @param pHashBytes
     * @param response
     * @param results
     * @param events
     * @param smartContractID
     * @return
     */
    private static byte[] getBytesProposalResponsePayload(byte[] pHashBytes, ProposalResponsePackage.Response
            response, byte[] results, byte[] events, SmartContractPackage.SmartContractID smartContractID) {
        //构造SmartContractAction
        ProposalPackage.SmartContractAction.Builder actionBuilder = ProposalPackage.SmartContractAction.newBuilder();
        if (events != null) {
            actionBuilder.setEvents(ByteString.copyFrom(events));
        }
        if (results != null) {
            actionBuilder.setResults(ByteString.copyFrom(results));
        }
        actionBuilder.setResponse(response);
        actionBuilder.setSmartContractId(smartContractID);
        ProposalPackage.SmartContractAction smartContractAction = actionBuilder.build();

        //构造ProposalResponsePayload
        ProposalResponsePackage.ProposalResponsePayload.Builder responsePayloadBuilder = ProposalResponsePackage
                .ProposalResponsePayload.newBuilder();
        responsePayloadBuilder.setExtension(smartContractAction.toByteString());
        if (pHashBytes != null) {
            responsePayloadBuilder.setProposalHash(ByteString.copyFrom(pHashBytes));
        }
        ProposalResponsePackage.ProposalResponsePayload responsePayload = responsePayloadBuilder.build();

        return responsePayload.toByteArray();
    }

    /**
     * GetProposalHash1 gets the proposal hash bytes after sanitizing the
     * chaincode proposal payload according to the rules of visibility
     *
     * @param header
     * @param payloadBytes
     * @param visibility
     * @return
     */
    private static byte[] getProposalHash1(Common.Header header, byte[] payloadBytes, byte[] visibility) throws
            InvalidProtocolBufferException, ValidateException, JavaChainException {
        if (header == null || header.getGroupHeader() == null || header.getSignatureHeader() == null || payloadBytes
                == null) {
            throw new ValidateException("Missing arguments");
        }

        //强制转换，如果失败抛出异常
        ProposalPackage.SmartContractProposalPayload smartContractProposalPayload = ProposalPackage
                .SmartContractProposalPayload.parseFrom(payloadBytes);

        byte[] proposalPayloadForTxBytes = getBytesProposalPayloadForTx(smartContractProposalPayload, visibility);

        //TODO:应当用哪个CSP
        ICsp defaultCsp = CspManager.getDefaultCsp();

        byte[] bytes = ArrayUtils.addAll(ArrayUtils.addAll(header.getGroupHeader().toByteArray(), header
                .getSignatureHeader().toByteArray()), proposalPayloadForTxBytes);
        return defaultCsp.hash(bytes, null);

//        //TODO:应当用哪个CSP，用哪个HashOpts
//        IHash hash = CspManager.getDefaultCsp().getHash(new SM3HashOpts());
//
//        hash.write(header.getGroupHeader().toByteArray());
//        hash.write(header.getSignatureHeader().toByteArray());
//        hash.write(proposalPayloadForTxBytes);
//
//        return hash.sum(null);
    }

    private static byte[] getBytesProposalPayloadForTx(ProposalPackage.SmartContractProposalPayload proposalPayload,
                                                       byte[] visibility) throws ValidateException {
        if (proposalPayload == null) {
            throw new ValidateException("Missing smartContractProposalPayload");
        }

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
}
