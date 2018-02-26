package org.bcia.javachain.orderer.broadcast;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.orderer.Ab;
import org.bcia.javachain.protos.orderer.AtomicBroadcastGrpc;


public class BroadCastClient  {
    public static void main(String[] args) throws Exception {
        ManagedChannel managedChannel=ManagedChannelBuilder.forAddress("localhost",50050).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub= AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver=stub.broadcast(new StreamObserver<Ab.BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse broadcastResponse) {
                System.out.println(broadcastResponse.getStatusValue());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompled!");
            }
        });
       for (int i = 0; i <10 ; i++) {
            //客户端以流式的形式向服务器发送数据
          envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(ByteString.copyFrom("wowoowowff".getBytes())).build());
                    Thread.sleep(1000);
        }
        Thread.sleep(5000);

    }


    public void send(String ip, int port, String message) throws Exception {
        ManagedChannel managedChannel=ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub= AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver=stub.broadcast(new StreamObserver<Ab.BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse broadcastResponse) {
                System.out.println(broadcastResponse.getStatusValue());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompled!");
            }
        });
//        for (int i = 0; i <10 ; i++) {
//            //客户端以流式的形式向服务器发送数据
            envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(ByteString.copyFrom(message.getBytes())).build());
//            Thread.sleep(1000);
//        }
//        Thread.sleep(5000);

    }

}
