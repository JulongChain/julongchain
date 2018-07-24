package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.api.IPeerSuspector;
import org.bcia.julongchain.gossip.api.ISubChannelSelectionCriteria;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.filter.IRoutingFilter;
import org.bcia.julongchain.protos.gossip.Message;

import java.util.Map;

public interface IGossip {

    public void send(Message.GossipMessage msg, RemotePeer... peers);

    public void sendByCriteria(SignedGossipMessage msg, SendCriteria sendCriteria);

    public NetworkMember[] peers();

    public NetworkMember[] peersOfChannel(byte[] groupID);

    public void updateMetadata(byte[] metadata);

    public void updateGroupMetadata(byte[] metadata, byte[] groupID);

    public void gossip(Message.GossipMessage gossipMessage);

    public IRoutingFilter peerFilter(byte[] groupID, ISubChannelSelectionCriteria messagePredicate);

    public Map<String, Object> accept(IMessageAcceptor acceptor, Boolean passThrough);

    public void joinGroup(IJoinChannelMessage joinMsg, byte[] groupID);

    public void leaveGroup(byte[] groupID);

    public void suspectPeers(IPeerSuspector peerSuspector);

    public void stop();

}
