package org.bcia.javachain.orderer.broadcast;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.orderer.Ab;
import org.bcia.javachain.protos.orderer.AtomicBroadcastGrpc;


import java.io.IOException;

public class BroadCastServer {
    private int port = 7050;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new BroadCastServerImpl())
                .build()
                .start();

        System.out.println("service start...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                BroadCastServer.this.stop();
                System.err.println("*** server shut down");
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

        final BroadCastServer server = new BroadCastServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类
    private class BroadCastServerImpl extends AtomicBroadcastGrpc.AtomicBroadcastImplBase {

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
