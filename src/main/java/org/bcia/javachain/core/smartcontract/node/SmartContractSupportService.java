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

import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportService extends SmartContractSupportGrpc
        .SmartContractSupportImplBase {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(SmartContractSupportService.class);

    @Override
    public StreamObserver<SmartcontractShim.SmartContractMessage> register
            (StreamObserver<SmartcontractShim.SmartContractMessage>
                     responseObserver) {

        return new StreamObserver<SmartcontractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartcontractShim.SmartContractMessage smartContractMessage) {
                logger.info("onNext");
                logger.info(smartContractMessage.getType().toString());
                SmartcontractShim.SmartContractMessage response = SmartcontractShim.SmartContractMessage.newBuilder()
                        .setType(SmartcontractShim.SmartContractMessage
                                .Type.REGISTERED).build();
                responseObserver.onNext(response);

            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("onError");
            }

            @Override
            public void onCompleted() {
                logger.info("onCompleted");
            }
        };

    }

}