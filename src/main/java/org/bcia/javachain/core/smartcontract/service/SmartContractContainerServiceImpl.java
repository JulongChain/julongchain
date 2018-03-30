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

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.bcia.javachain.core.ssc.essc.ESSC;
import org.bcia.javachain.protos.node.SmartContractContainerServiceGrpc;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class SmartContractContainerServiceImpl extends
        SmartContractContainerServiceGrpc
                .SmartContractContainerServiceImplBase {

    private static final JavaChainLog log = JavaChainLogFactory.getLog
            (SmartContractContainerServiceImpl.class);

    @Override
    public void invoke(SmartcontractShim.SmartContractMessage request,
                       StreamObserver<SmartcontractShim.SmartContractMessage>
                               responseObserver) {
        log.info("start invoke.");

        //根据传入参数中的链码名称执行相应的程序
        String smartContractId = request.getSmartcontractEvent()
                .getSmartContractId();

        //用户链码
        String mysc = "mysc001";

        if (StringUtils.equals(smartContractId, CommConstant.ESSC)) {
            //系统链码ESCC

            try {
                Smartcontract.SmartContractInput smartContractInput =
                        Smartcontract.SmartContractInput.parseFrom(request
                                .getPayload());
                SmartContractStub smartContractStub = new SmartContractStub
                        (null, null, null, smartContractInput.getArgsList(), null);
                new ESSC().invoke(smartContractStub);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage(), e);
            }

        } else if (StringUtils.equals(smartContractId, mysc)) {
            //用户链码
        }

        SmartcontractShim.SmartContractMessage response = SmartcontractShim
                .SmartContractMessage.newBuilder().setType
                        (SmartcontractShim.SmartContractMessage.Type
                                .COMPLETED).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("end invoke");
    }

}
