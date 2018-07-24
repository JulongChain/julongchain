package org.bcia.julongchain.gossip.api;

public interface ISubChannelSelectionCriteria {

    public Boolean execute(PeerSignature signature);

}
