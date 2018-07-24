package org.bcia.julongchain.gossip.identity;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.gossip.api.IPeerSuspector;

public interface IMapper {

    public void put(byte[] pkiID, byte[] identity) throws GossipException;

    public byte[] get(byte[] pkiID) throws GossipException;

    public byte[] sign(byte[] msg) throws GossipException;

    public void verify(byte[] vkID, byte[] signature, byte[] message) throws GossipException;

    public byte[] getPKIidOfCert(byte[] peerIdentity);

    public void suspectPeers(IPeerSuspector isSuspected);

    public void stop();

}
