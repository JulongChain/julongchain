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
package org.bcia.javachain.core.smartcontract.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportClient {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(SmartContractSupportClient.class);

    private final ManagedChannel channel;
    private final SmartContractSupportGrpc.SmartContractSupportStub asyncStub;
    private StreamObserver<SmartcontractShim.SmartContractMessage> streamObserver;

    private LinkedBlockingQueue<SmartcontractShim.SmartContractMessage> receiveMessages = new LinkedBlockingQueue<>();

    public SmartContractSupportClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        asyncStub = SmartContractSupportGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void start() {
        logger.info("*** register");

        streamObserver = asyncStub.register(new StreamObserver<SmartcontractShim.SmartContractMessage>() {

            @Override
            public void onNext(SmartcontractShim.SmartContractMessage smartContractMessage) {
                logger.info("Got message:" + smartContractMessage.toString());
                receiveMessages.add(smartContractMessage);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("Finished register");
            }
        });

        SmartcontractShim.SmartContractMessage message = SmartcontractShim.SmartContractMessage.newBuilder().setType
                (SmartcontractShim.SmartContractMessage.Type.REGISTER)
                .build();
        send(message);

        while (true) {
            try {
                logger.info(new Date().toString());
                SmartcontractShim.SmartContractMessage receive = receive();
                Thread.sleep(1000);
                send(receive);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SmartContractSupportClient client = new SmartContractSupportClient("localhost", 7051);
        client.start();
    }

    public void send(SmartcontractShim.SmartContractMessage message) {
        streamObserver.onNext(message);
    }

    public SmartcontractShim.SmartContractMessage receive() throws InterruptedException {
        SmartcontractShim.SmartContractMessage message = receiveMessages.take();
        logger.info(message.toString());
        return message;
    }

}
