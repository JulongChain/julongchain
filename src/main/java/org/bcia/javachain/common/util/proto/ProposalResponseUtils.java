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
import org.bcia.javachain.common.util.Utils;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractResponse;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;

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
     * @author sunianle
     * @param headerBytes
     * @param payloadBytes
     * @param smartContractResponse
     * @param results
     * @param events
     * @param smartContractID
     * @param visibility
     * @param signingIdentity
     * @return
     */
    public static ProposalResponsePackage.ProposalResponse buildProposalResponse(
            byte[] headerBytes, byte[] payloadBytes, SmartContractResponse smartContractResponse,
            byte[] results,
            byte[]events,
            Smartcontract.SmartContractID smartContractID,
            byte[] visibility,
            ISigningIdentity signingIdentity) throws InvalidProtocolBufferException {
        Common.Header header=Common.Header.parseFrom(headerBytes);
        //obtain the proposal hash given proposal header, payload and the requested visibility
        byte []pHashBytes=getProposalHash1(header,payloadBytes,visibility);
        //get the bytes of the proposal smartContractResponse payload - we need to sign them
        byte []prpBytes=getBytesProposalResponsePayload(pHashBytes, smartContractResponse,results,events,smartContractID);
        byte[] endorser=signingIdentity.serialize();
        // sign the concatenation of the proposal smartContractResponse and the serialized endorser identity with this endorser's key
        byte[] signature=signingIdentity.sign(Utils.appendBytes(prpBytes,endorser));
        ProposalResponsePackage.ProposalResponse.Builder builder = ProposalResponsePackage.ProposalResponse.newBuilder();
        builder.setVersion(1);
        ProposalResponsePackage.Endorsement endorsement=ProposalResponsePackage.Endorsement.newBuilder()
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
     * @param pHashBytes
     * @param smartContractResponse
     * @param results
     * @param events
     * @param smartContractID
     * @return
     */
    private static byte[] getBytesProposalResponsePayload(byte[] pHashBytes, SmartContractResponse smartContractResponse, byte[] results, byte[] events,
                                                          Smartcontract.SmartContractID smartContractID) {
        //后面实现逻辑
        return new byte[]{3,5,4};
    }

    /**
     * GetProposalHash1 gets the proposal hash bytes after sanitizing the
     * chaincode proposal payload according to the rules of visibility
     * @param header
     * @param payloadBytes
     * @param visibility
     * @return
     */
    private static byte[] getProposalHash1(Common.Header header, byte[] payloadBytes, byte[] visibility) {
        //后面实现逻辑
        return new byte[]{2,3,4};
    }
}
