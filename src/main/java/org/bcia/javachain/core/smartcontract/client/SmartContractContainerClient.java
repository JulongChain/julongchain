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
import org.bcia.javachain.core.common.smartcontractprovider
        .SmartContractContext;
import org.bcia.javachain.protos.node.SmartContractContainerServiceGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.util.concurrent.TimeUnit;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class SmartContractContainerClient {

    private static final JavaChainLog log = JavaChainLogFactory.getLog
            (SmartContractContainerClient.class);

    private final ManagedChannel channel;

    private final SmartContractContainerServiceGrpc
            .SmartContractContainerServiceBlockingStub blockingStub;

    public SmartContractContainerClient(String host, Integer port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(host, port).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = SmartContractContainerServiceGrpc.newBlockingStub
                (channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public SmartcontractShim.SmartContractMessage invoke(SmartContractContext
                                                                 smartContractContext,
                                                         SmartcontractShim
                                                                 .SmartContractMessage smartContractMessage, long timeout) {
        SmartcontractShim.SmartContractMessage result = null;
        try {
            result = blockingStub
                    .invoke(smartContractMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        SmartContractContainerClient client = new
                SmartContractContainerClient("localhost", 50053);
        client.invoke(null, null, 1000l);
        client.shutdown();
    }

}
