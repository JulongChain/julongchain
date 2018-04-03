package org.bcia.javachain.consenter.common.broadcast;

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
    public static void main(String[] args) throws Exception {
        String ip="localhost";
        String message="aba";
        int port=7050;

        System.out.println("begin");

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

//            //客户端以流式的形式向服务器发送数据
     //   ILocalSigner localSigner = new LocalSigner();
      //  SM2Impl signer=new SM2Impl();
//        Common.GroupHeader  data = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
//                "myGroup", 30);
//
//        Common.Payload payload = EnvelopeHelper.buildPayload(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "myGroup", signer, data, 30);
        Ab.SeekInfo.Builder seekInfoBuilder = Ab.SeekInfo.newBuilder();
        seekInfoBuilder.setBehavior(Ab.SeekInfo.SeekBehavior.BLOCK_UNTIL_READY);
        Ab.SeekInfo seekInfo = seekInfoBuilder.build();
      //  Common.Envelope envelope=EnvelopeHelper.buildSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "123",new SM2Impl(),seekInfo,0L);
        Common.Envelope.Builder envelope= Common.Envelope.newBuilder();
        envelopeStreamObserver.onNext(envelope.build());
        
        Thread.sleep(1000);

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
