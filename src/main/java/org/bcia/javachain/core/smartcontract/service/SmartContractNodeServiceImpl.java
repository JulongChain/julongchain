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

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.leveldb.StateLevelDBFactory;
import org.bcia.javachain.protos.node.SmartContractNodeServiceGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class SmartContractNodeServiceImpl extends
        SmartContractNodeServiceGrpc.SmartContractNodeServiceImplBase {

    private static final JavaChainLog log = JavaChainLogFactory.getLog
            (SmartContractNodeServiceImpl.class);

    @Override
    public void register(SmartcontractShim.SmartContractMessage request,
                         StreamObserver<SmartcontractShim
                                 .SmartContractMessage> responseObserver) {
        log.info("start register");
        //TODO 先返回空对象
        responseObserver.onNext(SmartcontractShim.SmartContractMessage
                .newBuilder().build());
        responseObserver.onCompleted();

        log.info("end register");
    }

    @Override
    public void getState(SmartcontractShim.SmartContractMessage request,
                         StreamObserver<SmartcontractShim
                                 .SmartContractMessage> responseObserver) {

        log.info("start get state");

        SmartcontractShim.SmartContractMessage response;

        //从request中的event中取smart contract id
        String smartContractId = request.getSmartcontractEvent()
                .getSmartContractId();

        SmartcontractShim.PutState putState = null;
        try {
            //还原putState
            putState = SmartcontractShim.PutState
                    .parseFrom(request.getPayload());

            //获取其中的key
            String key = putState.getKey();

            //根据key值查找value
            byte[] value = StateLevelDBFactory.getState(smartContractId, key);

            //value放到返回的putState中
            putState = SmartcontractShim.PutState.newBuilder().setKey(key)
                    .setValue(ByteString.copyFrom(value))
                    .build();

            //response对象
            response = SmartcontractShim.SmartContractMessage.newBuilder()
                    .setPayload(putState.getValue()).build();

            //返回给客户端
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response = SmartcontractShim.SmartContractMessage.newBuilder()
                    .setType(SmartcontractShim.SmartContractMessage.Type
                            .ERROR).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        log.info("end get state");
    }

    @Override
    public void putState(SmartcontractShim.SmartContractMessage request,
                         StreamObserver<SmartcontractShim
                                 .SmartContractMessage> responseObserver) {

        log.info("start put state");

        //获取smart contract id
        String smartContractId = request.getSmartcontractEvent()
                .getSmartContractId();

        try {
            //获取putState
            SmartcontractShim.PutState putState = SmartcontractShim.PutState
                    .parseFrom(request.getPayload());
            //获取Key
            String key = putState.getKey();
            //获取value
            ByteString value = putState.getValue();
            //写入level db
            StateLevelDBFactory.putState(smartContractId, key, value
                    .toByteArray());

            //type completed
            SmartcontractShim.SmartContractMessage response = SmartcontractShim
                    .SmartContractMessage.newBuilder().setType
                            (SmartcontractShim.SmartContractMessage.Type
                                    .COMPLETED)
                    .build();

            //返回客户端
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("end put state");
        } catch (Exception e) {
            //返回error
            responseObserver.onNext(SmartcontractShim.SmartContractMessage
                    .newBuilder().setType(SmartcontractShim
                            .SmartContractMessage.Type.ERROR).build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void deleteState(SmartcontractShim.SmartContractMessage request,
                            StreamObserver<SmartcontractShim
                                    .SmartContractMessage> responseObserver) {

        log.info("start delete state");

        String smartContractId = request.getSmartcontractEvent()
                .getSmartContractId();

        try {
            SmartcontractShim.PutState putState = SmartcontractShim.PutState
                    .parseFrom(request.getPayload());
            String key = putState.getKey();
            StateLevelDBFactory.deleteState(smartContractId, key);
            SmartcontractShim.SmartContractMessage response = SmartcontractShim
                    .SmartContractMessage.newBuilder().setType
                            (SmartcontractShim.SmartContractMessage.Type
                                    .COMPLETED).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("end delete state");
        } catch (Exception e) {
            //返回error
            responseObserver.onNext(SmartcontractShim.SmartContractMessage
                    .newBuilder().setType(SmartcontractShim
                            .SmartContractMessage.Type.ERROR).build());
            responseObserver.onCompleted();
        }

    }

}
