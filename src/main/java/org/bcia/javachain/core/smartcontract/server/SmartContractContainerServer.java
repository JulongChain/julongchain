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
package org.bcia.javachain.core.smartcontract.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.smartcontract.service
        .SmartContractContainerServiceImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
@Component
public class SmartContractContainerServer {

    private static final JavaChainLog log = JavaChainLogFactory.getLog
            (SmartContractContainerServer.class);

    private Server server;
    private Integer port = 50053;

    public SmartContractContainerServer() {
        try {
            this.start();
            log.info("server start success.");
            this.blockUntilShutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(port).addService(new
                SmartContractContainerServiceImpl()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.error("*** shutting down gRPC server since JVM " +
                        "is shutting down");
                SmartContractContainerServer.this.stop();
                log.error("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) {
        new SmartContractContainerServer();
    }

}
