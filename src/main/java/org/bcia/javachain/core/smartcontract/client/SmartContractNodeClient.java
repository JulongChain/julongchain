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
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractNodeServiceGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.util.concurrent.TimeUnit;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class SmartContractNodeClient {
    
    private static final JavaChainLog log = JavaChainLogFactory.getLog
            (SmartContractNodeClient.class);

    private ManagedChannel channel;

    private SmartContractNodeServiceGrpc
            .SmartContractNodeServiceBlockingStub blockingStub;

    public SmartContractNodeClient(String host, Integer port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(host, port).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = SmartContractNodeServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void register(SmartcontractShim.SmartContractMessage
                                 smartContractMessage) {
        blockingStub.register(smartContractMessage);
    }

    public byte[] getState(SmartcontractShim.SmartContractMessage
                                   smartContractMessage) {
        blockingStub.getState(smartContractMessage);
        return null;
    }

    public void putState(SmartcontractShim.SmartContractMessage
                                 smartContractMessage) {
        blockingStub.putState(smartContractMessage);
    }

    public void deleteState(SmartcontractShim.SmartContractMessage
                                    smartContractMessage) {
        blockingStub.deleteState(smartContractMessage);
    }

    public static void main(String[] args) throws Exception {
        SmartContractNodeClient client = new SmartContractNodeClient
                ("localhost", 50051);
        client.register(SmartcontractShim.SmartContractMessage.newBuilder()
                .build());
        client.getState(SmartcontractShim.SmartContractMessage.newBuilder()
                .build());
        client.putState(SmartcontractShim.SmartContractMessage.newBuilder()
                .build());
        client.deleteState(SmartcontractShim.SmartContractMessage.newBuilder
                ().build());
    }

}
