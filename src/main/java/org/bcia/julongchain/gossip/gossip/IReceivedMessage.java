package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.protos.gossip.Message;

public interface IReceivedMessage {

    public void respond(Message.GossipMessage msg);

    public SignedGossipMessage getGossipMessage();

    public Message.Envelope getSourceEnvelope();

    public ConnectionInfo getConnectionInfo();

    public void ack(String error);

}
