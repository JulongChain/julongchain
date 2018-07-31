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
package org.bcia.julongchain.gossip;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.gossip.GossipGrpc;
import org.bcia.julongchain.protos.gossip.Message;

public class GossipClientStream implements StreamObserver<Message.Envelope> {

    private static JavaChainLog log = JavaChainLogFactory.getLog(GossipClientStream.class);

    private final ManagedChannel connection;
    private StreamObserver<Message.Envelope> streamObserver;

    public GossipClientStream(ManagedChannel connection) {
        GossipGrpc.GossipStub stub = GossipGrpc.newStub(connection);
        log.info("Connecting to gossip consenter.");
        try {
            this.streamObserver = stub.gossipStream(this);
        } catch (Exception e) {
            log.error("Unable to connect to gossip consenter server", e);
            System.exit(-1);
        }
        this.connection = connection;
    }

    public synchronized void serialSend(Message.Envelope envelope) {
        try {
            this.streamObserver.onNext(envelope);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error sending %s: %s", envelope.getPayload(), e));
        }
    }

    @Override
    public void onNext(Message.Envelope envelope) {

        if (envelope == null) {
            return;
        }

        if (envelope.getPayload() == null) {
            return;
        }

        try {
            Message.GossipMessage gossipMessage = Message.GossipMessage.parseFrom(envelope.getPayload());

            if (gossipMessage == null) {
                return;
            }

            if (gossipMessage.getDataMsg() == null) {
                return;
            }

            if (gossipMessage.getGroup() == null) {
                return;
            }

            String group = gossipMessage.getGroup().toStringUtf8();

            if (StringUtils.isEmpty(group)) {
                return;
            }

            Message.Payload payload = gossipMessage.getDataMsg().getPayload();

            if (payload == null) {
                return;
            }

            ByteString data = payload.getData();

            if (data == null) {
                return;
            }

            Common.Block block = Common.Block.parseFrom(data);

            if (block == null) {
                return;
            }


            BlockAndPvtData blockAndPvtData = new BlockAndPvtData(block, null, null);

            if (blockAndPvtData == null) {
                return;
            }

            LedgerManager.openLedger(group).commitWithPvtData(blockAndPvtData);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onCompleted() {
        log.info("completed");
    }

}
