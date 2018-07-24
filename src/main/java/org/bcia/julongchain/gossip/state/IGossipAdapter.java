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
