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

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/22
 * @company Dingxuan
 */
public class SmartContractMessageGrpcClient {

    private static final JavaChainLog log = JavaChainLogFactory.getLog(SmartContractMessageGrpcClient.class);

    private static final String serverAddress = "localhost";

    private static final Integer serverPort = 50051;

    private StreamObserver<SmartcontractShim.SmartContractMessage> requestStreamObserver;

    private StreamObserver<SmartcontractShim.SmartContractMessage> responseStreamObserver;

    private static final SmartContractMessageGrpcClient instance = new SmartContractMessageGrpcClient();

    private ManagedChannel channel;

    private SmartContractMessageGrpcClient() {
        try {
            init();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void init() throws InterruptedException {
        channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext(true).build();

        SmartContractSupportGrpc.SmartContractSupportStub asyncStub = SmartContractSupportGrpc.newStub(channel);

        this.responseStreamObserver = new StreamObserver<SmartcontractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartcontractShim.SmartContractMessage message) {
                receive(message);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };

        this.requestStreamObserver = asyncStub.register(this.responseStreamObserver);
    }

    public void send(SmartcontractShim.SmartContractMessage message) {
        requestStreamObserver.onNext(message);
    }

    private void receive(SmartcontractShim.SmartContractMessage message) {
        log.debug("message:" + message);

        if(message.getType() == SmartcontractShim.SmartContractMessage.Type.KEEPALIVE) {
            log.debug("type:keepalive");
        }else if(message.getType() == SmartcontractShim.SmartContractMessage.Type.INVOKE_CHAINCODE) {
            log.debug("type:invoke_chaincode");
        }else {
            log.debug("type:other");
        }
    }

    public static void main(String[] args ) throws Exception {
        SmartContractMessageGrpcClient client = SmartContractMessageGrpcClient.getInstance();

        int i = 1;
        while(true) {
            if(i%5 > 0) {
                client.send(SmartcontractShim.SmartContractMessage.newBuilder().setTxid("keepalive").build());
            } else {
                client.send(SmartcontractShim.SmartContractMessage.newBuilder().setTxid("invoke").build());
            }
            i++;
            log.debug("i:" + i );
            Thread.sleep(2000);
        }
    }

    public static SmartContractMessageGrpcClient getInstance() {
        return instance;
    }
}
