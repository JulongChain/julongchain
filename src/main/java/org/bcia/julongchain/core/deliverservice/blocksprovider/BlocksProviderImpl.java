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
package org.bcia.julongchain.core.deliverservice.blocksprovider;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang.StringUtils;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.gossip.Message;

import static org.bcia.julongchain.protos.common.Common.Status.BAD_REQUEST;
import static org.bcia.julongchain.protos.common.Common.Status.FORBIDDEN;
import static org.bcia.julongchain.protos.common.Common.Status.SUCCESS;

/**
 * @author zhangmingyang
 * @Date: 2018/5/31
 * @company Dingxuan
 */
public class BlocksProviderImpl implements IBlocksProvider {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BlocksProviderImpl.class);
    private final int WRONG_STATUS_THRESHOLD = 10;
    //10秒
    private final int MAX_WRONG_DELAY = 10000;

    private String groupId;
    private IStreamClient client;
    private IGossipServcieAdapter gossip;
    private IMessageCryptoService mcs;
    private int done;
    private int wrongStatusThreshold;

    public BlocksProviderImpl(String groupId, IStreamClient client, IGossipServcieAdapter gossip, IMessageCryptoService mcs) {
        this.groupId = groupId;
        this.client = client;
        this.gossip = gossip;
        this.mcs = mcs;
        this.wrongStatusThreshold = WRONG_STATUS_THRESHOLD;
    }

    @Override
    public void deliverBlocks() throws InvalidProtocolBufferException {
        int errorStatusCounter = 0;
        int statusCounter = 0;
        this.client.close();
        if (isDone()) {
            Ab.DeliverResponse msg = this.client.recv();
            Ab.DeliverResponse.TypeCase t = msg.getTypeCase();
            Common.Status status = msg.getStatus();
            Common.Block block = msg.getBlock();
            switch (t) {
                case STATUS:
                    if (status == SUCCESS) {
                        log.warn(String.format("[%s] ERROR! Received success for a seek that should never complete", groupId));
                        return;
                    }
                    if (status == BAD_REQUEST || status == FORBIDDEN) {
                        log.error(String.format("[%s] Got error %v", groupId, status));
                        errorStatusCounter++;
                    }
                    if (errorStatusCounter > wrongStatusThreshold) {
                        log.error(String.format("[%s] Wrong statuses threshold passed, stopping block provider", groupId));
                        return;
                    } else {
                        errorStatusCounter = 0;
                        log.warn(String.format("[%s] Got error %v", groupId, status));
                    }
                    // FIXME: 2018/5/31 处理逻辑是否错误
                    float maxDelay = Float.valueOf(MAX_WRONG_DELAY);
                    float currDelay = Float.valueOf((float) (Math.pow(2, statusCounter) * 100 * 1000));
                    try {
                        Thread.sleep((long) Math.min(maxDelay, currDelay));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (currDelay < maxDelay) {
                        statusCounter++;
                    }
                    if (status == BAD_REQUEST) {
                        client.disConnect(false);
                    } else {
                        client.disConnect(true);
                    }
                case BLOCK:
                    errorStatusCounter = 0;
                    statusCounter = 0;
                    long seqNum = block.getHeader().getNumber();
                    byte[] marshaledBlock = block.toByteArray();
                    //TODO err判断

                    int numberofNode = gossip.nodeOfGroup(groupId.getBytes()).length;
                    Message.Payload payload = createPayload(seqNum, marshaledBlock);
                    Message.GossipMessage gossipMsg = createGossipMsg(groupId, payload);
                    log.debug(String.format("[%s] Adding payload locally, buffer seqNum = [%d], peers number [%d]", groupId, seqNum, numberofNode));
                    gossip.addPayload(groupId, payload);
                    gossip.gossip(gossipMsg);
                default:
                    return;
            }

        }

    }

    @Override
    public void updateOrderingEndpoints(String[] endpoints) {
        if(!isEndpointsUpdated(endpoints)){
            return;
        }
        log.debug(String.format("Updating endpoint, to %s", endpoints));
        client.updateEndpoints(endpoints);
        log.debug("Disconnecting so endpoints update will take effect");
        client.disConnect(false);
    }

    @Override
    public void stop() {
        //TODO atomic.StoreInt32(&b.done, 1)
        client.close();
    }

    private boolean isEndpointsUpdated(String[] endpoints) {
        if (endpoints.length != client.getEndpoints().length) {
            return true;
        }
        for (String endopint : endpoints) {
            if (!StringUtils.contains(String.valueOf(client.getEndpoints()), endopint)) ;
            return true;
        }
        return false;
    }

    public Message.GossipMessage createGossipMsg(String groupId, Message.Payload payload) throws InvalidProtocolBufferException {
        Message.DataMessage dataMessage = Message.DataMessage.parseFrom(payload.toByteArray());
        Message.GossipMessage gossipMessage = Message.GossipMessage.newBuilder()
                .setNonce(0)
                .setGroup(ByteString.copyFrom(groupId.getBytes()))
                .setTag(Message.GossipMessage.Tag.CHAN_AND_ORG)
                .setDataMsg(dataMessage).build();
        return gossipMessage;
    }


    public Message.Payload createPayload(long seqNum, byte[] marshaledBlock) throws InvalidProtocolBufferException {
        Message.Payload gossipPayload = Message.Payload.newBuilder()
                .setData(ByteString.copyFrom(marshaledBlock))
                .setSeqNum(seqNum).build();
        return gossipPayload;
    }

    // Check whenever provider is stopped
    public boolean isDone() {
        //TODO atomic.LoadInt32(&b.done) == 1
        return true;
    }

    public String getGroupId() {
        return groupId;
    }

    public IStreamClient getClient() {
        return client;
    }

    public IGossipServcieAdapter getGossip() {
        return gossip;
    }

    public int getDone() {
        return done;
    }

    public int getWrongStatusThreshold() {
        return wrongStatusThreshold;
    }

    public IMessageCryptoService getMcs() {
        return mcs;
    }

    public static void main(String[] args) {
        boolean a = StringUtils.contains("2324", "32");
        System.out.println(a);
    }
}
