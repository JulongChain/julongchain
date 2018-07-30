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
package org.bcia.julongchain.gossip.gossip.channel;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.api.ISubChannelSelectionCriteria;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.filter.IRoutingFilter;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public class GossipChannel implements IGossipChannel {

    private static final JavaChainLog log = JavaChainLogFactory.getLog(GossipChannel.class);
    private IAdapter adapter;
    private Boolean shouldGossipStateInfo;


    @Override
    public NetworkMember[] getPeers() {
        return new NetworkMember[0];
    }

    @Override
    public IRoutingFilter peerFilter(ISubChannelSelectionCriteria subChannelSelectionCriteria) {
        return null;
    }

    @Override
    public Boolean isMemberInChan(NetworkMember member) {
        return null;
    }

    @Override
    public void updateStateInfo(SignedGossipMessage msg) {

    }

    @Override
    public Boolean isOrgInChannel(byte[] membersOrg) {
        return null;
    }

    @Override
    public Boolean eligibleForChannel(NetworkMember member) {
        return null;
    }

    @Override
    public void handleMessage(IReceivedMessage receivedMessage) {

    }

    @Override
    public void addToMsgStore(SignedGossipMessage msg) {

    }

    @Override
    public void configureChannel(IJoinChannelMessage joinMsg) {

    }

    @Override
    public void leaveChannel() {

    }

    @Override
    public void stop() {

    }
}
