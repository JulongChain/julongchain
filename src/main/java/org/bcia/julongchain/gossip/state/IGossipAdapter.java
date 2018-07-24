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
package org.bcia.julongchain.gossip.state;

import org.bcia.julongchain.gossip.NetworkMember;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.protos.gossip.Message;

public interface IGossipAdapter {

    public void send(Message.GossipMessage msg, RemotePeer... peers);

    public Object[] accept(IMessageAcceptor acceptor, Boolean passThrough);

    public void updateChannelMetadata(byte[] metadata, byte[] chainID);

    public NetworkMember[] peersOfChannel(byte[] chainID);

}
