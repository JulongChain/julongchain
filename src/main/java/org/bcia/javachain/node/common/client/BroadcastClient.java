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
package org.bcia.javachain.node.common.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.bcia.javachain.protos.consenter.AtomicBroadcastGrpc;

/**
 * 广播客户端实现
 *
 * @author zhouhui
 * @date 2018/3/7
 * @company Dingxuan
 */
public class BroadcastClient implements IBroadcastClient {
    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

    public BroadcastClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void send(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.broadcast(responseObserver);
        envelopeStreamObserver.onNext(envelope);
    }

    @Override
    public void close() {

    }
}
