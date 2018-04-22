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
package org.bcia.javachain.core.smartcontract.service;

import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/22
 * @company Dingxuan
 */
public class SmartContractMessageGrpcService extends SmartContractSupportGrpc.SmartContractSupportImplBase{

    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractMessageGrpcService.class);

    private StreamObserver<SmartcontractShim.SmartContractMessage> responseObserver;

    @Override
    public StreamObserver<SmartcontractShim.SmartContractMessage> register(StreamObserver<SmartcontractShim.SmartContractMessage> responseObserver) {

        this.responseObserver = responseObserver;

        return new StreamObserver<SmartcontractShim.SmartContractMessage>() {
            @Override
            public void onNext(SmartcontractShim.SmartContractMessage message) {
                receive(message);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private void receive(SmartcontractShim.SmartContractMessage message) {
        log.debug("message:" + message);
        log.debug("message type:" + message.getType());
        log.debug("message type value:" + message.getTypeValue());
        log.debug("message type name:" + message.getType().name());
        log.debug("message type to string:" + message.getType().toString());

        if(StringUtils.equals(message.getTxid(), "invoke")) {
            responseObserver.onNext(SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.INVOKE_SMARTCONTRACT).build());
        }else if(StringUtils.equals(message.getTxid(), "keepalive")) {
            responseObserver.onNext(SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.KEEPALIVE).build());
        }
    }

    public StreamObserver<SmartcontractShim.SmartContractMessage> getResponseObserver() {
        return responseObserver;
    }

    public void setResponseObserver(StreamObserver<SmartcontractShim.SmartContractMessage> responseObserver) {
        this.responseObserver = responseObserver;
    }
}
