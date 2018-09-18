/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.bcia.julongchain.protos.node.SmartContractSupportGrpc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * smart contract test
 *
 * @author wanliangbing
 * @date 18-9-13
 * @company Dingxuan
 */
public class SmartContractShimTest {

    private static JulongChainLog log = JulongChainLogFactory.getLog(SmartContractSupportServiceTest.class);

    private BlockingQueue<SmartContractShim.SmartContractMessage> queue = new LinkedBlockingQueue<SmartContractShim.SmartContractMessage>();
    private String host = "localhost";
    private Integer port = 7052;
    private StreamObserver<SmartContractShim.SmartContractMessage> receiveObserver;
    private StreamObserver<SmartContractShim.SmartContractMessage> sendObserver;

    @Before
    public void init() {
        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        ManagedChannel managedChannel = nettyChannelBuilder.build();
        SmartContractSupportGrpc.SmartContractSupportStub smartContractSupportStub = SmartContractSupportGrpc.newStub(managedChannel);
        receiveObserver = new StreamObserver<SmartContractShim.SmartContractMessage>() {
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
        sendObserver = smartContractSupportStub.register(receiveObserver);
    }

    @Test
    public void testGetState() throws InterruptedException {
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
        log.info(value);
        Assert.assertEquals(value, "10");
    }

    @Test
    public void testPutState() throws InvalidProtocolBufferException, InterruptedException {
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
        log.info(receivePutState.getKey());
        log.info(receivePutState.getValue().toStringUtf8());
        Assert.assertEquals(receivePutState.getKey(), "a");
        Assert.assertEquals(receivePutState.getValue().toStringUtf8(), "20");
    }

    @Test
    public void testGetStateByRange() throws InterruptedException, InvalidProtocolBufferException {
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
        Assert.assertNotNull(resultsList);
        log.info(resultsList.size() + "");
        Assert.assertTrue(resultsList.size() > 0);
    }

    @Test
    public void testDelState() throws InterruptedException, InvalidProtocolBufferException {
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
