package org.bcia.julongchain.gossip.discovery;

import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;
import org.bcia.julongchain.protos.gossip.Message;

public interface IEnvelopeFilter {

    public Message.Envelope execute(SignedGossipMessage message);

}
