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

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.proto.ProposalResponseUtils;
import org.bcia.julongchain.core.admin.IAdminServer;
import org.bcia.julongchain.core.endorser.IEndorserServer;
import org.bcia.julongchain.core.events.IDeliverEventsServer;
import org.bcia.julongchain.core.smartcontract.node.SmartContractSupportService;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.*;

import java.io.IOException;

/**
 * 节点GRPC服务
 *
 * @author zhouhui
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class NodeGrpcServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGrpcServer.class);
    /**
     * 监听的端口
     */
    private int port;
    /**
     * grpc框架定义的服务器抽象
     */
    private Server server;
    /**
     * 业务服务1:背书服务
     */
    private IEndorserServer endorserServer;
    /**
     * 业务服务2:Deliver事件处理服务
     */
    private IDeliverEventsServer deliverEventsServer;
    /**
     * 业务服务3:管理服务
     */
    private IAdminServer adminServer;

    public NodeGrpcServer(int port) {
        this.port = port;
    }

    /**
     * 绑定背书服务
     *
     * @param endorserServer
     */
    public void bindEndorserServer(IEndorserServer endorserServer) {
        this.endorserServer = endorserServer;
    }


    public void bindDeliverEventsServer(IDeliverEventsServer deliverEventsServer) {
        this.deliverEventsServer = deliverEventsServer;
    }

    public void bindAdminServer(IAdminServer adminServer) {
        this.adminServer = adminServer;
    }

    public void start() throws IOException {
        server = NettyServerBuilder.forPort(port).maxMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE)
//        server = ServerBuilder.forPort(port)
                .addService(new EndorserServerImpl())
                .addService(new SmartContractSupportService())
                .addService(new AdminServerImpl())
                .build()
                .start();
        log.info("NodeGrpcServer start, port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("shutting down NodeGrpcServer since JVM is shutting down");
                NodeGrpcServer.this.stop();
                log.info("NodeGrpcServer shut down");
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
        NodeGrpcServer server = new NodeGrpcServer(7051);
        server.start();
        server.blockUntilShutdown();
    }


    private class EndorserServerImpl extends EndorserGrpc.EndorserImplBase {
        @Override
        public void processProposal(ProposalPackage.SignedProposal request, StreamObserver<ProposalResponsePackage
                .ProposalResponse> responseObserver) {
            if (endorserServer != null) {
                ProposalResponsePackage.ProposalResponse proposalResponse = null;
                try {
                    proposalResponse = endorserServer.processProposal(request);
                } catch (NodeException e) {
                    log.error(e.getMessage(), e);
                    proposalResponse = ProposalResponseUtils.buildErrorProposalResponse(Common.Status
                            .INTERNAL_SERVER_ERROR, e.getMessage());
                }
                responseObserver.onNext(proposalResponse);
                responseObserver.onCompleted();
            } else {
                log.error("endorserServer is not ready, but client sent some message: " + request);
                responseObserver.onError(new NodeException("endorserServer is not ready"));
                responseObserver.onCompleted();
            }
        }
    }

    private class DeliverServerImpl extends DeliverGrpc.DeliverImplBase {
        @Override
        public StreamObserver<Common.Envelope> deliver(StreamObserver<EventsPackage.DeliverResponse> responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope value) {
                    if (deliverEventsServer != null) {
                        responseObserver.onNext(deliverEventsServer.deliver(value));
                        responseObserver.onCompleted();
                    } else {
                        log.error("deliverEventsServer is not ready, but client sent some message: " + value);
                        responseObserver.onError(new NodeException("deliverEventsServer is not ready"));
                        responseObserver.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error(t.getMessage(), t);
                    responseObserver.onError(t);
                }

                @Override
                public void onCompleted() {
                    log.info("DeliverGrpc.Deliver onCompleted");
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<Common.Envelope> deliverFiltered(StreamObserver<EventsPackage.DeliverResponse>
                                                                       responseObserver) {
            return new StreamObserver<Common.Envelope>() {
                @Override
                public void onNext(Common.Envelope value) {
                    if (deliverEventsServer != null) {
                        responseObserver.onNext(deliverEventsServer.deliverFiltered(value));
                        responseObserver.onCompleted();
                    } else {
                        log.error("deliverEventsServer is not ready, but client sent some message: " + value);
                        responseObserver.onError(new NodeException("deliverEventsServer is not ready"));
                        responseObserver.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error(t.getMessage(), t);
                    responseObserver.onError(t);
                }

                @Override
                public void onCompleted() {
                    log.info("DeliverGrpc.deliverFiltered onCompleted");
                    responseObserver.onCompleted();
                }
            };
        }
    }

    private class AdminServerImpl extends AdminGrpc.AdminImplBase {
        @Override
        public void getStatus(Empty request, StreamObserver<AdminPackage.ServerStatus>
                responseObserver) {
            if (adminServer != null) {
                responseObserver.onNext(adminServer.getStatus());
                responseObserver.onCompleted();
            } else {
                log.error("adminServer is not ready, but client sent some message");
                responseObserver.onError(new NodeException("adminServer is not ready"));
                responseObserver.onCompleted();
            }
        }

        @Override
        public void startServer(Empty request, StreamObserver<AdminPackage.ServerStatus> responseObserver) {

        }

        @Override
        public void getModuleLogLevel(AdminPackage.LogLevelRequest request, StreamObserver<AdminPackage
                .LogLevelResponse> responseObserver) {

        }

        @Override
        public void setModuleLogLevel(AdminPackage.LogLevelRequest request, StreamObserver<AdminPackage
                .LogLevelResponse> responseObserver) {

        }

        @Override
        public void revertLogLevels(Empty request, StreamObserver<Empty> responseObserver) {

        }
    }
}
