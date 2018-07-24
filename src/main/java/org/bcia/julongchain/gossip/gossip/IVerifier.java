package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.common.exception.GossipException;

public interface IVerifier {

    public void execute(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException;

}
