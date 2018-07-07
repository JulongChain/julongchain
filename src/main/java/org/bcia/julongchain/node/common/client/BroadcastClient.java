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
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc;

import java.util.concurrent.TimeUnit;

/**
 * 广播客户端实现
 *
 * @author zhouhui
 * @date 2018/3/7
 * @company Dingxuan
 */
public class BroadcastClient implements IBroadcastClient {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BroadcastClient.class);
    /**
     * IP地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    private ManagedChannel managedChannel;

    public BroadcastClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void send(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) {
        managedChannel =
                NettyChannelBuilder.forAddress(host, port).maxInboundMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE)
                        .usePlaintext().build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.broadcast(responseObserver);
        envelopeStreamObserver.onNext(envelope);
    }

    @Override
    public void close() {
        log.info("BroadcastClient close-----");

        managedChannel.shutdown();
//        try {
//            managedChannel.awaitTermination(1000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            log.error(e.getMessage(), e);
//        }
    }
}
