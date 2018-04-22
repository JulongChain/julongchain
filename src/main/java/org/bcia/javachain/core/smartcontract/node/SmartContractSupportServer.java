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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;

import java.io.IOException;
import java.util.Date;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportServer {

    private static Log logger = LogFactory.getLog(SmartContractSupportServer.class);

    private final int port;

    private final Server server;

    public SmartContractSupportServer(int port) {
        this.port = port;
        server = ServerBuilder.forPort(port).addService(new SmartContractSupportService()).build();
    }

    public void start() throws IOException {
        server.start();
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
        new Thread() {
            public void run() {
                SmartContractSupportServer server = new SmartContractSupportServer(7052);
                try {
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    server.blockUntilShutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        while(true) {
            try {
                Thread.sleep(5000);
                logger.info(new Date().toString());
                Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder().setType(Common.HeaderType
                        .ENDORSER_TRANSACTION.getNumber()).build();

                Common.Header header = Common.Header.newBuilder().setGroupHeader(groupHeader.toByteString())
                        .build();

                ProposalPackage.Proposal proposal = ProposalPackage.Proposal.newBuilder().setHeader(header
                        .toByteString()).build();

                ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                        .setProposalBytes
                                (proposal.toByteString()).build();

                SmartContractSupportService.invoke("MyChaincode1", signedProposal);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

}
