package org.bcia.julongchain.gossip.gossip.channel;

import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.api.ISubChannelSelectionCriteria;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.filter.IRoutingFilter;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

public interface IGossipChannel {

    public NetworkMember[] getPeers();

    public IRoutingFilter peerFilter(ISubChannelSelectionCriteria subChannelSelectionCriteria);

    public Boolean isMemberInChan(NetworkMember member);

    public void updateStateInfo(SignedGossipMessage msg);

    public Boolean isOrgInChannel(byte[] membersOrg);

    public Boolean eligibleForChannel(NetworkMember member);

    public void handleMessage(IReceivedMessage receivedMessage);

    public void addToMsgStore(SignedGossipMessage msg);

    public void configureChannel(IJoinChannelMessage joinMsg);

    public void leaveChannel();

    public void stop();

}
