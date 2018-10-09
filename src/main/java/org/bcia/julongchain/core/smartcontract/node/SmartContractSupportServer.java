/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract.node;

import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 智能合约server
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

    public void start() throws IOException, InterruptedException {
        server.start();
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread() {
                            @Override
                            public void run() {
                                logger.info("*** shutting down gRPC server since JVM is shutting down ");
                                SmartContractSupportServer.this.stop();
                                logger.info("*** server shut down ");
                            }
                        });
        blockUntilShutdown();
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

    private static SmartContractPackage.SmartContractDeploymentSpec constructDeploySpec(String smartcontractName, String path, String version, List<String> initArgs, boolean bCreateFS) {
        SmartContractPackage.SmartContractInput input = null;
        for (String initArg : initArgs) {
            ByteString arg = ByteString.copyFromUtf8(initArg);
            input = SmartContractPackage.SmartContractInput.newBuilder()
                    .addArgs(arg)
                    .build();
        }
        SmartContractPackage.SmartContractDeploymentSpec depSpec = SmartContractPackage.SmartContractDeploymentSpec.newBuilder()
                .setSmartContractSpec(SmartContractPackage.SmartContractSpec.newBuilder()
                        .setTypeValue(1)
                        .setSmartContractId(SmartContractPackage.SmartContractID.newBuilder()
                                .setName(smartcontractName)
                                .setPath(path)
                                .setVersion(version)
                                .build())
                        .setInput(input)
                        .build())
                .build();

        return depSpec;
    }
}
