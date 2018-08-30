/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.core.node.grpc;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.core.smartcontract.node.SmartContractSupportService;

import java.io.IOException;

/**
 * 智能合约Grpc服务
 *
 * @author zhouhui
 * @Date: 2018/8/28
 * @company Dingxuan
 */
public class SmartContractGrpcServer {
    private static JulongChainLog log = JulongChainLogFactory.getLog(SmartContractGrpcServer.class);
    /**
     * 监听的端口
     */
    private int port;
    /**
     * grpc框架定义的服务器抽象
     */
    private Server server;

    public SmartContractGrpcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = NettyServerBuilder.forPort(port).maxMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE)
//        server = ServerBuilder.forPort(port)
                .addService(new SmartContractSupportService())
                .build()
                .start();
        log.info("SmartContractGrpcServer start, port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("shutting down SmartContractGrpcServer since JVM is shutting down");
                SmartContractGrpcServer.this.stop();
                log.info("SmartContractGrpcServer shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // block 一直到退出程序
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        SmartContractGrpcServer server = new SmartContractGrpcServer(7052);
        server.start();
        server.blockUntilShutdown();
    }
}
