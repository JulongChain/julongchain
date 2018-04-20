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
package org.bcia.javachain.core.smartcontract.node;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.io.IOException;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportServer {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(SmartContractSupportServer.class);

    private final int port;

    private final Server server;

    public SmartContractSupportServer(int port) {
        this.port = port; server = ServerBuilder.forPort(port).addService(new SmartContractSupportService()).build();
    }

    public void start() throws IOException {
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM " + "is shutting down ");
                SmartContractSupportServer.this.stop();
                System.err.println("*** server shut down ");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        SmartContractSupportServer server = new SmartContractSupportServer(7051);
        server.start();
        server.blockUntilShutdown();
    }

}
