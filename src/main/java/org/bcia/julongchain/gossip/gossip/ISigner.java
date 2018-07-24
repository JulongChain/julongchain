package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.common.exception.GossipException;

public interface ISigner {

    public byte[] execute(byte[] msg) throws GossipException;

}
