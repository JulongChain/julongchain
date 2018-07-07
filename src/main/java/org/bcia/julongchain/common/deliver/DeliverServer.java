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
package org.bcia.julongchain.common.deliver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class DeliverServer  implements ISender{
    StreamObserver<Ab.DeliverResponse> responseObserver;
    private IPolicyChecker policyChecker;
    private ISender sender;
    private Common.Envelope envelope;

    public DeliverServer(StreamObserver<Ab.DeliverResponse> responseObserver, Common.Envelope envelope) {
        this.responseObserver = responseObserver;
        this.envelope = envelope;
    }

    public StreamObserver<Ab.DeliverResponse> getResponseObserver() {
        return responseObserver;
    }

    public IPolicyChecker getPolicyChecker() {
        return policyChecker;
    }

    public ISender getSend() {
        return sender;
    }

    public Common.Envelope getEnvelope() {
        return envelope;
    }

    @Override
    public void send(Message msg) throws InvalidProtocolBufferException {
        Ab.DeliverResponse deliverResponse= Ab.DeliverResponse.parseFrom(msg.toByteArray());
        responseObserver.onNext(deliverResponse);
    }
}
