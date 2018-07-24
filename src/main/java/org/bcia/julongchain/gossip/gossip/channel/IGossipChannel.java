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

import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.api.ISubChannelSelectionCriteria;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.filter.IRoutingFilter;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public interface IGossipChannel {

    public NetworkMember[] getPeers();

    public IRoutingFilter peerFilter(ISubChannelSelectionCriteria subChannelSelectionCriteria);

    public Boolean isMemberInChan(NetworkMember member);

    public void updateStateInfo(SignedGossipMessage msg);

    public Boolean isOrgInChannel(byte[] membersOrg);

    public Boolean eligibleForChannel(NetworkMember member);

    public void handleMessage(IReceivedMessage receivedMessage);

    public void addToMsgStore(SignedGossipMessage msg);

    public void configureChannel(IJoinChannelMessage joinMsg);

    public void leaveChannel();

    public void stop();

}
