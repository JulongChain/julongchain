package org.bcia.javachain.consenter.deliver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.orderer.Ab;
import org.bcia.javachain.protos.orderer.AtomicBroadcastGrpc;
import org.springframework.stereotype.Service;


import java.io.IOException;
/**
 * deliver 服务
 *
 * @author zhangmingyang
 * @date 2018-02-23
 * @company Dingxuan
 */
@Service
public class DeliverServer {
    private int port = 7051;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new DeliverServerImpl())
                .build()
                .start();

        System.out.println("deliver service start...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                DeliverServer.this.stop();
                System.err.println("*** deliver server shut down");
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

        final DeliverServer server = new DeliverServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类
    private class DeliverServerImpl extends AtomicBroadcastGrpc.AtomicBroadcastImplBase{
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
    }

}
