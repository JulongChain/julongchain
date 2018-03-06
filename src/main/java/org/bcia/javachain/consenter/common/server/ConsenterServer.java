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
package org.bcia.javachain.consenter.common.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;

import org.bcia.javachain.protos.consenter.Ab;
import org.bcia.javachain.protos.consenter.AtomicBroadcastGrpc;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2/27/18 5:14 PM *
 * @company Dingxuan
 */
@Component
public class ConsenterServer {
    private int port = 7050;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ConsenterServerImpl())
                .build()
                .start();

        System.out.println("consenter service start...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ConsenterServer.this.stop();
                System.err.println("***consenter server shut down");
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

        final ConsenterServer server = new ConsenterServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类
    private class ConsenterServerImpl extends AtomicBroadcastGrpc.AtomicBroadcastImplBase {
        @Override
        public StreamObserver<Common.Envelope> deliver(StreamObserver<Ab.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope envelope) {
                    System.out.println("envelope:"+envelope.getPayload());
                    responseObserver.onNext(Ab.DeliverResponse.newBuilder().setStatusValue(500).build());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }
        @Override
        public StreamObserver<Common.Envelope> broadcast(StreamObserver<Ab.BroadcastResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>(){

                @Override
                public void onNext(Common.Envelope envelope) {
                    System.out.println("envelope:"+envelope.getPayload());
                    responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatusValue(200).build());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println(throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
