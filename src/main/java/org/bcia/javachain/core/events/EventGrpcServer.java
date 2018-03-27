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
package org.bcia.javachain.core.events;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.DeliverGrpc;
import org.bcia.javachain.protos.node.EventsGrpc;
import org.bcia.javachain.protos.node.EventsPackage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 节点事件GRPC服务
 *
 * @author zhouhui
 * @Date: 2018/3/21
 * @company Dingxuan
 */
@Component
public class EventGrpcServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EventGrpcServer.class);
    /**
     * 监听的端口
     */
    private int port = 7053;
    /**
     * grpc框架定义的服务器抽象
     */
    private Server server;
    /**
     * 业务服务1:事件处理服务
     */
    private IEventHubServer eventHubServer;

    public EventGrpcServer() {
    }

    public EventGrpcServer(int port) {
        this.port = port;
    }

    /**
     * 绑定事件处理服务
     *
     * @param eventHubServer
     */
    public void bindEventHubServer(IEventHubServer eventHubServer) {
        this.eventHubServer = eventHubServer;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new EventServerImpl())
                .build()
                .start();
        log.info("EventGrpcServer start, port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("shutting down EventGrpcServer since JVM is shutting down");
                EventGrpcServer.this.stop();
                log.error("EventGrpcServer shut down");
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


    public static void main(String[] args) throws IOException, InterruptedException {
        EventGrpcServer server = new EventGrpcServer(7053);
        server.start();
        server.blockUntilShutdown();
    }

    private class EventServerImpl extends EventsGrpc.EventsImplBase {
        @Override
        public StreamObserver<EventsPackage.SignedEvent> chat(StreamObserver<EventsPackage.Event> responseObserver) {
            return new StreamObserver<EventsPackage.SignedEvent>() {
                @Override
                public void onNext(EventsPackage.SignedEvent value) {
                    if (eventHubServer != null) {
                        EventsPackage.Event resultEvent = eventHubServer.chat(value);
                        responseObserver.onNext(resultEvent);
                    } else {
                        log.error("eventHubServer is not ready, but client sent some message: " + value);
                        responseObserver.onError(new NodeException("eventHubServer is not ready"));
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }
    }


}
