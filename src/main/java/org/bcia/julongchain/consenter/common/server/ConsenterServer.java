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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.broadcast.BroadCastHandler;
import org.bcia.julongchain.consenter.common.deliver.DeliverHandler;
import org.bcia.julongchain.consenter.common.multigroup.Registrar;
import org.bcia.julongchain.protos.common.Common;

import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc;

import java.io.IOException;

import static org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory.loadConsenterConfig;

/**
 * @author zhangmingyang
 * @Date: 2/27/18 5:14 PM *
 * @company Dingxuan
 */
public class ConsenterServer {
    private int port = 7050;
    private Server server;
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConsenterServer.class);
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ConsenterServerImpl())
                .build()
                .start();
        log.info("consenter service start, port: 7050");

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


    public static void main(String[] args) throws IOException, InterruptedException {
        ConsenterServer server = new ConsenterServer();
        server.start();
        server.blockUntilShutdown();


    }

    // 定义一个实现服务接口的类
    private class ConsenterServerImpl extends AtomicBroadcastGrpc.AtomicBroadcastImplBase {
        LocalSigner localSigner = new LocalSigner();
        Registrar registrar;
        {
            try {
                registrar = PreStart.initializeMultichannelRegistrar(loadConsenterConfig(),localSigner);
            } catch (JavaChainException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public StreamObserver<Common.Envelope> deliver(StreamObserver<Ab.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                DeliverHandler deliverHandler = new DeliverHandler();
                @Override
                public void onNext(Common.Envelope envelope) {
                  //  log.info("envelop:" + envelope.getPayload().toStringUtf8());
                    deliverHandler.handle(envelope);
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
            return new StreamObserver<Common.Envelope>() {
                BroadCastHandler broadCastHandle = new BroadCastHandler(registrar);
                @Override
                public void onNext(Common.Envelope envelope) {

                    try {
                        broadCastHandle.handle(envelope, responseObserver);
                    } catch (IOException e) {
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


    }


}
