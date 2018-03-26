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
import org.bcia.javachain.core.smartcontract.service.SmartContractMessageGrpcService;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.io.IOException;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/22
 * @company Dingxuan
 */
public class SmartContractMessageGrpcServer {

    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractMessageGrpcServer.class);

    private Server server;

    private Integer port;

    private SmartContractMessageGrpcService service;

    public static void main(String[] args) throws IOException, InterruptedException {
        SmartContractMessageGrpcServer server = new SmartContractMessageGrpcServer(50051);
        server.start();
        server.blockUntilShutdown();
    }

    public SmartContractMessageGrpcServer(Integer port) {
        this.port = port;
        this.service = new SmartContractMessageGrpcService();
        this.server = ServerBuilder.forPort(port).addService(this.service).build();
    }

    private void start() throws IOException {
        this.server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                log.error("*** shutting down gRPC server since JVM is shutting down");
                SmartContractMessageGrpcServer.this.stop();
            }
        });
    }

    private void stop() {
        if(server != null) {
            server.shutdown();
        }
        log.info("*** server shut down");
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }

    public void send(SmartcontractShim.SmartContractMessage message) {
        log.info("gRPC server send message to client:" + message);
        service.getResponseObserver().onNext(message);
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SmartContractMessageGrpcService getService() {
        return service;
    }

    public void setService(SmartContractMessageGrpcService service) {
        this.service = service;
    }
}
