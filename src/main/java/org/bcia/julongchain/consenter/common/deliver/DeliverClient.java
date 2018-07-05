package org.bcia.julongchain.consenter.common.deliver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.protos.common.Common;

import org.bcia.julongchain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

import static org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc.*;

/**
 * deliver客户端
 *
 * @author zhangmingyang
 * @date 2018-02-23
 * @company Dingxuan
 */
@Component
public class DeliverClient {
    private static JavaChainLog log = JavaChainLogFactory.getLog(DeliverClient.class);
    /**
     * deliver 发送方法
     * @param ip
     * @param port
     */
    public  void send(String ip,int port) {
        ManagedChannel managedChannel= ManagedChannelBuilder.forAddress(ip,port).usePlaintext(true).build();
        AtomicBroadcastStub stub= newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver=stub.deliver(new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse deliverResponse) {
                if(deliverResponse.getStatusValue()==200){
                    log.info("success");
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
            //客户端以流式的形式向服务器发送数据
           // envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(ByteString.copyFrom(message.getBytes())).build());
        ILocalSigner localSigner = new LocalSigner();
        Common.GroupHeader  data = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
                "myGroup", 30);
        Common.Payload payload = EnvelopeHelper.buildPayload(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "myGroup", localSigner, data, 30);
        envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(payload.toByteString()).build());
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
