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
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.blockledger.Util;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
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
                handPullRequest(envelope, responseObserver);
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

    private void handPullRequest(Message.Envelope envelope, StreamObserver<Message.Envelope> responseObserver) {
        try {
            ByteString payload = envelope.getPayload();
            if (payload == null) {
                return;
            }
            Message.GossipMessage gossipMessage = Message.GossipMessage.parseFrom(payload);
            if (gossipMessage == null) {
                return;
            }
            Message.RemoteStateRequest stateRequest = gossipMessage.getStateRequest();
            if (stateRequest == null) {
                return;
            }
            String group = gossipMessage.getGroup().toStringUtf8();
            long startSeqNum = stateRequest.getStartSeqNum();
            new Thread() {
                @Override
                public void run() {
                    String location = ConsenterConfigFactory.loadConsenterConfig().getFileLedger().getLocation();
                    FileLedgerFactory fileLedgerFactory = null;
                    try {
                        fileLedgerFactory = new FileLedgerFactory(location);
                        ReadWriteBase readWriteBase = fileLedgerFactory.getOrCreate(group);
                        log.info("receive pull request:"+ group + " " +startSeqNum);
                        Common.Block block = Util.getBlock(readWriteBase, startSeqNum);
                        log.info("get block:" + block.getHeader().getNumber());
                        Message.Envelope responseEnvelope = newGossipEnvelope(group, block.getHeader().getNumber(), block);
                        responseObserver.onNext(responseEnvelope);
                    } catch (LedgerException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }.start();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Message.Envelope newGossipEnvelope(String group, Long seqNum, Common.Block block) {
        Message.Payload payload = Message.Payload.newBuilder().setSeqNum(seqNum).setData(block.toByteString()).build();
        Message.DataMessage dataMessage = Message.DataMessage.newBuilder().setPayload(payload).build();
        Message.GossipMessage gossipMessage = Message.GossipMessage.newBuilder().setGroup(ByteString.copyFromUtf8(group)).setDataMsg(dataMessage).build();
        Message.Envelope envelope = Message.Envelope.newBuilder().setPayload(gossipMessage.toByteString()).build();
        return envelope;
    }

    public static void saveBlock(Message.Envelope envelope) {
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

}
