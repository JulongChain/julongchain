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
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalResponsePackage;

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
}
