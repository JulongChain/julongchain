package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

import java.time.Duration;

public interface IComm {

    public byte[] getPKIid();

    public void send(SignedGossipMessage msg, RemotePeer... peers);

    public SendResult[] sendWithAck(SignedGossipMessage msg, Duration timeout, Integer minAck, RemotePeer... peers);

    public void probe(RemotePeer peer) throws GossipException;

    public byte[] handshake(RemotePeer peer) throws GossipException;

    public Channel<IReceivedMessage> accept(IMessageAcceptor messageAcceptor);

    public Channel<byte[]> presumedDead();

    public void closeConn(RemotePeer peer);

    public void stop();

}
