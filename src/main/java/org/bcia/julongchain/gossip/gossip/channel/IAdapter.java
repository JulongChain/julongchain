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

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public interface IAdapter {

    public Config getConf();

    public void gossip(SignedGossipMessage message);

    public void forward(IReceivedMessage message);

    public NetworkMember[] getMembership();

    public NetworkMember lookup(byte[] pkiID);

    public void send(SignedGossipMessage msg, RemotePeer... peers);

    public void validateStateInfoMessage(SignedGossipMessage message) throws GossipException;

    public byte[] getOrgOfPeer(byte[] pkiID);

    public byte[] getIdentityByPKIID(byte[] pkiID);

}
