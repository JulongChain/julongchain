package org.bcia.javachain.consenter.common.broadcast;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;

import org.bcia.javachain.protos.consenter.Ab;
import org.bcia.javachain.protos.consenter.AtomicBroadcastGrpc;
import org.springframework.stereotype.Component;

/**
 * broadcast客户端
 *
 * @author zhangmingyang
 * @date 2018-02-23
 * @company Dingxuan
 */
@Component
public class BroadCastClient {
    public void send(String ip, int port, String message) throws Exception {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.broadcast(new StreamObserver<Ab
                .BroadcastResponse>() {
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

    /**
     * 有回调的发送方法
     *
     * @param ip
     * @param port
     * @param envelope
     * @param responseObserver
     */
    public void send(String ip, int port, Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.broadcast(responseObserver);
        envelopeStreamObserver.onNext(envelope);
    }

}
