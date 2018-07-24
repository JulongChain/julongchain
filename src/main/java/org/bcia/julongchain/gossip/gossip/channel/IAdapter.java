package org.bcia.julongchain.gossip.gossip.channel;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public interface IAdapter {

    public Config getConf();

    public void gossip(SignedGossipMessage message);

    public void forward(IReceivedMessage message);

    public NetworkMember[] getMembership();

    public NetworkMember lookup(byte[] pkiID);

    public void send(SignedGossipMessage msg, RemotePeer... peers);

    public void validateStateInfoMessage(SignedGossipMessage message) throws GossipException;

    public byte[] getOrgOfPeer(byte[] pkiID);

    public byte[] getIdentityByPKIID(byte[] pkiID);

}
