package org.bcia.julongchain.consenter.common.broadcast;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc;
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


    /**
     * broadcast 发送方法
     * @param ip
     * @param port
     * @throws Exception
     */
    public void send(String ip,int port) throws Exception {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.broadcast(new StreamObserver<Ab
                .BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse broadcastResponse) {
                System.out.println(broadcastResponse.getStatusValue());
                //System.out.println(broadcastResponse.getInfo());
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

       //客户端以流式的形式向服务器发送数据
        Ab.SeekInfo.Builder seekInfoBuilder = Ab.SeekInfo.newBuilder();
        seekInfoBuilder.setBehavior(Ab.SeekInfo.SeekBehavior.BLOCK_UNTIL_READY);
        Ab.SeekInfo seekInfo = seekInfoBuilder.build();
        Common.Envelope.Builder envelope= Common.Envelope.newBuilder();
        envelopeStreamObserver.onNext(envelope.build());
        Thread.sleep(9000);

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
