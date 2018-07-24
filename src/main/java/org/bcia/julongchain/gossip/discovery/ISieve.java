package org.bcia.julongchain.gossip.discovery;

import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public interface ISieve {

    public Boolean execute(SignedGossipMessage message);

}
