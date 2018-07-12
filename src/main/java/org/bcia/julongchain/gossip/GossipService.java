/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.gossip;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.gossip.GossipGrpc;
import org.bcia.julongchain.protos.gossip.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/07/01
 * @company Dingxuan
 */
public class GossipService extends GossipGrpc.GossipImplBase {

    private static JavaChainLog log = JavaChainLogFactory.getLog(GossipService.class);

    private static Set<StreamObserver<Message.Envelope>> responseObservers = new HashSet<StreamObserver<Message.Envelope>>();

    @Override
    public StreamObserver<Message.Envelope> gossipStream(StreamObserver<Message.Envelope> responseObserver) {
        return new StreamObserver<Message.Envelope>() {
            @Override
            public void onNext(Message.Envelope envelope) {
                responseObservers.add(responseObserver);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                log.info("gossip complete");
            }
        };
    }

    public static void deliver(Message.Envelope envelope) {
        for (StreamObserver<Message.Envelope> responseObserver : responseObservers) {
            responseObserver.onNext(envelope);
        }
    }

    public static Message.Envelope newGossipEnvelope(String group, Long seqNum, Common.Block block) {

        Message.Payload payload = Message.Payload.newBuilder().setSeqNum(seqNum).setData(block.toByteString()).build();

        Message.DataMessage dataMessage = Message.DataMessage.newBuilder().setPayload(payload).build();

        Message.GossipMessage gossipMessage = Message.GossipMessage.newBuilder().setGroup(ByteString.copyFromUtf8(group)).setDataMsg(dataMessage).build();

        Message.Envelope envelope = Message.Envelope.newBuilder().setPayload(gossipMessage.toByteString()).build();

        return envelope;

    }
}
