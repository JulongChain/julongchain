package org.bcia.julongchain.core.smartcontract.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;
import org.bcia.julongchain.protos.node.SmartContractSupportGrpc;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * class description
 *
 * @author
 * @date 18-9-12
 * @company Dingxuan
 */
public class SmartContractSupportServiceTest {

    @Test
    public void testGetState() throws InterruptedException {

        BlockingQueue<SmartContractShim.SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractShim.SmartContractMessage>();

        String host = "localhost";
        Integer port = 7052;
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        StreamObserver<SmartContractShim.SmartContractMessage> receiveObserver = new StreamObserver<SmartContractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartContractShim.SmartContractMessage message) {
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

        StreamObserver<SmartContractShim.SmartContractMessage> sendObserver = smartContractSupportStub.register(receiveObserver);

        SmartContractShim.SmartContractMessage.Builder builder = SmartContractShim.SmartContractMessage.newBuilder();
        builder.setPayload(ByteString.copyFromUtf8("a"));
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractShim.SmartContractMessage.Type.GET_STATE);
        SmartContractShim.SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);

        SmartContractShim.SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        String value = payload.toStringUtf8();

        Assert.assertEquals(value, "10");

    }

    @Test
    public void testPutState() throws InvalidProtocolBufferException, InterruptedException {
        BlockingQueue<SmartContractShim.SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractShim.SmartContractMessage>();

        String host = "localhost";
        Integer port = 7052;
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        StreamObserver<SmartContractShim.SmartContractMessage> receiveObserver = new StreamObserver<SmartContractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartContractShim.SmartContractMessage message) {
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

        StreamObserver<SmartContractShim.SmartContractMessage> sendObserver = smartContractSupportStub.register(receiveObserver);

        SmartContractShim.SmartContractMessage.Builder builder = SmartContractShim.SmartContractMessage.newBuilder();
        SmartContractShim.PutState putState = SmartContractShim.PutState.newBuilder().setKey("a").setValue(ByteString.copyFromUtf8("20")).build();
        builder.setPayload(putState.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractShim.SmartContractMessage.Type.PUT_STATE);
        SmartContractShim.SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);

        SmartContractShim.SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.PutState receivePutState = SmartContractShim.PutState.parseFrom(payload);
        Assert.assertEquals(receivePutState.getKey(), "a");
        Assert.assertEquals(receivePutState.getValue().toStringUtf8(), "20");
    }

    @Test
    public void testGetStateByRange() throws InterruptedException, InvalidProtocolBufferException {
        BlockingQueue<SmartContractShim.SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractShim.SmartContractMessage>();

        String host = "localhost";
        Integer port = 7052;
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        StreamObserver<SmartContractShim.SmartContractMessage> receiveObserver = new StreamObserver<SmartContractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartContractShim.SmartContractMessage message) {
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

        StreamObserver<SmartContractShim.SmartContractMessage> sendObserver = smartContractSupportStub.register(receiveObserver);

        SmartContractShim.SmartContractMessage.Builder builder = SmartContractShim.SmartContractMessage.newBuilder();
        SmartContractShim.GetStateByRange getStateByRange = SmartContractShim.GetStateByRange.newBuilder().setStartKey("a").setEndKey("b").build();
        builder.setPayload(getStateByRange.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractShim.SmartContractMessage.Type.GET_STATE_BY_RANGE);
        SmartContractShim.SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);

        SmartContractShim.SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.QueryResponse queryResponse = SmartContractShim.QueryResponse.parseFrom(payload);
        List<SmartContractShim.QueryResultBytes> resultsList = queryResponse.getResultsList();
        Assert.assertTrue(resultsList.size() > 0);
    }

    @Test
    public void testDelState() throws InterruptedException, InvalidProtocolBufferException {
        BlockingQueue<SmartContractShim.SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractShim.SmartContractMessage>();

        String host = "localhost";
        Integer port = 7052;
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        StreamObserver<SmartContractShim.SmartContractMessage> receiveObserver = new StreamObserver<SmartContractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartContractShim.SmartContractMessage message) {
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

        StreamObserver<SmartContractShim.SmartContractMessage> sendObserver = smartContractSupportStub.register(receiveObserver);

        SmartContractShim.SmartContractMessage.Builder builder = SmartContractShim.SmartContractMessage.newBuilder();
        SmartContractShim.DelState delState = SmartContractShim.DelState.newBuilder().setKey("a").build();
        builder.setPayload(delState.toByteString());
        builder.setTxid("txid");
        builder.setGroupId("myGroup");
        builder.setSmartContractEvent(SmartContractEventPackage.SmartContractEvent.newBuilder().setSmartContractId("mycc").build());
        builder.setType(SmartContractShim.SmartContractMessage.Type.DEL_STATE);
        SmartContractShim.SmartContractMessage sendMessage = builder.build();
        sendObserver.onNext(sendMessage);

        SmartContractShim.SmartContractMessage receiveMessage = queue.take();
        ByteString payload = receiveMessage.getPayload();
        SmartContractShim.DelState receiveDelState = SmartContractShim.DelState.parseFrom(payload);
        Assert.assertEquals(receiveDelState.getKey(), "a");
    }

    @Test
    public void testGetHistoryByKey() {

    }

}