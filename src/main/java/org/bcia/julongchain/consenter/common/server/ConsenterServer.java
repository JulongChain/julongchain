/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.server;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.*;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.deliver.DeliverServer;
import org.bcia.julongchain.common.deliver.IDeliverHandler;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.consenter.Consenter;
import org.bcia.julongchain.consenter.common.multigroup.Registrar;
import org.bcia.julongchain.consenter.util.RequestHeadersInterceptor;
import org.bcia.julongchain.gossip.GossipService;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * consenter服务管理
 *
 * @author zhangmingyang
 * @Date: 2018/2/17
 * @company Dingxuan
 */
public class ConsenterServer {
    private int port;
    private Server server;
    public static final AtomicReference<ServerCall<?, ?>> serverCallCapture = new AtomicReference<ServerCall<?, ?>>();
    private static JulongChainLog log = JulongChainLogFactory.getLog(ConsenterServer.class);
    private static IBroadcastHandler broadcastHandler;
    private static IDeliverHandler deliverHandler;

    public ConsenterServer(int port) {
        this.port = port;
    }


    public void bindBroadcastServer(IBroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }


    public void bindDeverServer(IDeliverHandler deliverHandler) {
        this.deliverHandler = deliverHandler;
    }


    public void start() throws IOException {
        List<ServerInterceptor> allInterceptors = ImmutableList.<ServerInterceptor>builder()
                .add(RequestHeadersInterceptor.recordServerCallInterceptor(serverCallCapture)).build();
        //server = ServerBuilder.forPort(port)
        server = NettyServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(new ConsenterServerImpl(), allInterceptors))
                .addService(new GossipService())
                .build()
                .start();
        log.info("consenter service start, port:" + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                log.info("*** shutting down gRPC server since JVM is shutting down");
                ConsenterServer.this.stop();
                log.error("***consenter server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // block 一直到退出程序
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }

    }

    private static class ConsenterServerImpl extends AtomicBroadcastGrpc.AtomicBroadcastImplBase {

        @Override
        public StreamObserver<Common.Envelope> broadcast(StreamObserver<Ab.BroadcastResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope envelope) {
                    try {
                        broadcastHandler.handle(envelope, responseObserver);
                    } catch (ConsenterException e) {
                        log.error(e.getMessage());
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<Common.Envelope> deliver(StreamObserver<Ab.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope envelope) {
                    DeliverServer deliverServer = new DeliverServer(responseObserver, envelope);
                    try {
                        deliverHandler.handle(deliverServer);
                    } catch (ConsenterException e) {
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getMessage());
                }

                @Override
                public void onCompleted() {

                }
            };
        }
    }


}
