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
package org.bcia.julongchain.node.common.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.protos.node.EndorserGrpc;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;

/**
 * 背书客户端
 *
 * @author zhouhui
 * @date 2018/3/19
 * @company Dingxuan
 */
public class EndorserClient implements IEndorserClient {
    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

    public EndorserClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public ProposalResponsePackage.ProposalResponse sendProcessProposal(ProposalPackage.SignedProposal signedProposal) {
        ManagedChannel managedChannel =
                NettyChannelBuilder.forAddress(ip, port).maxInboundMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE)
                        .usePlaintext().build();
        EndorserGrpc.EndorserBlockingStub stub = EndorserGrpc.newBlockingStub(managedChannel);
        return stub.processProposal(signedProposal);
    }
}
