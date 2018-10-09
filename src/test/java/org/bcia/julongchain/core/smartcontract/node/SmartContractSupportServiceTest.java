package org.bcia.julongchain.core.smartcontract.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;
import org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage;
import org.bcia.julongchain.protos.node.SmartContractSupportGrpc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SmartContractSupportServiceTest
 *
 * @author wanliangbing
 * @date 18-9-12
 * @company Dingxuan
 */
public class SmartContractSupportServiceTest {

    private static JulongChainLog log = JulongChainLogFactory.getLog(SmartContractSupportServiceTest.class);

    private BlockingQueue<SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractMessage>();
    private String host = "localhost";
    private Integer port = 7052;
    private StreamObserver<SmartContractMessage> receiveObserver;
    private StreamObserver<SmartContractMessage> sendObserver;

    @Before
    public void init() {
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        receiveObserver = new StreamObserver<SmartContractMessage>() {
            @Override
            public void onNext(SmartContractMessage message) {
                queue.add(message);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                managedChannel.shutdown();
            }
        };
        sendObserver = smartContractSupportStub.register(receiveObserver);
    }

    @Test
    public void testGetState() throws InterruptedException {
        SmartContractMessage.Builder builder = SmartContractMessage.newBuilder();
        builder.setPayload(ByteString.copyFromUtf8("a"));
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractMessage.Type.GET_STATE);
        SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);
        SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        String value = payload.toStringUtf8();
        log.info(value);
        Assert.assertEquals(value, "10");
    }

    @Test
    public void testPutState() throws InvalidProtocolBufferException, InterruptedException {
        SmartContractMessage.Builder builder = SmartContractMessage.newBuilder();
        SmartContractShim.PutState putState = SmartContractShim.PutState.newBuilder().setKey("a").setValue(ByteString.copyFromUtf8("20")).build();
        builder.setPayload(putState.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractMessage.Type.PUT_STATE);
        SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);
        SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.PutState receivePutState = SmartContractShim.PutState.parseFrom(payload);
        log.info(receivePutState.getKey());
        log.info(receivePutState.getValue().toStringUtf8());
        Assert.assertEquals(receivePutState.getKey(), "a");
        Assert.assertEquals(receivePutState.getValue().toStringUtf8(), "20");
    }

    @Test
    public void testGetStateByRange() throws InterruptedException, InvalidProtocolBufferException {
        SmartContractMessage.Builder builder = SmartContractMessage.newBuilder();
        SmartContractShim.GetStateByRange getStateByRange = SmartContractShim.GetStateByRange.newBuilder().setStartKey("a").setEndKey("b").build();
        builder.setPayload(getStateByRange.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractMessage.Type.GET_STATE_BY_RANGE);
        SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);
        SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.QueryResponse queryResponse = SmartContractShim.QueryResponse.parseFrom(payload);
        List<SmartContractShim.QueryResultBytes> resultsList = queryResponse.getResultsList();
        Assert.assertNotNull(resultsList);
        log.info(resultsList.size() + "");
        Assert.assertTrue(resultsList.size() > 0);
    }

    @Test
    public void testDelState() throws InterruptedException, InvalidProtocolBufferException {
        SmartContractMessage.Builder builder = SmartContractMessage.newBuilder();
        SmartContractShim.DelState delState = SmartContractShim.DelState.newBuilder().setKey("a").build();
        builder.setPayload(delState.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractMessage.Type.DEL_STATE);
        SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);
        SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.DelState receiveDelState = SmartContractShim.DelState.parseFrom(payload);
        Assert.assertEquals(receiveDelState.getKey(), "a");
    }

    @Test
    public void testGetHistoryByKey() {

    }

}