package org.bcia.javachain.consenter.deliver;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.orderer.Ab;
import org.bcia.javachain.protos.orderer.AtomicBroadcastGrpc;
import org.springframework.stereotype.Component;

/**
 * deliver客户端
 *
 * @author zhangmingyang
 * @date 2018-02-23
 * @company Dingxuan
 */
@Component
public class DeliverClient {
    public void send(String ip, int port, String message){
        ManagedChannel managedChannel= ManagedChannelBuilder.forAddress(ip,port).usePlaintext(true).build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub=AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver=stub.deliver(new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse deliverResponse) {
                if(deliverResponse.getStatusValue()==500){
                    System.out.println("INTERNAL_SERVER_ERROR");
                }
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
       // for (int i = 0; i <10 ; i++) {
            //客户端以流式的形式向服务器发送数据
            envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(ByteString.copyFrom(message.getBytes())).build());
            //Thread.sleep(1000);
        //}
        //Thread.sleep(5000);

    }
}
