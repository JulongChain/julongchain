package org.bcia.julongchain.gossip.gossip;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.deliverservice.blocksprovider.IGossipServcieAdapter;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.gossip.channel.IGossipChannel;
import org.bcia.julongchain.protos.gossip.Message;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ChannelState {

    private Boolean stopping;

    private Map<String, IGossipChannel> channels;

    private GossipServiceImpl gossipServiceImpl;

    private synchronized void stop() {
        if (BooleanUtils.isTrue(getStopping())) {
            return;
        }
        setStopping(true);
        Collection<IGossipChannel> values = channels.values();
        for (IGossipChannel gc : values) {
            gc.stop();
        }
    }

    private IGossipChannel lookupChannelForMsg(IReceivedMessage msg) {
        if (GossipMessageHelper.isStateInfoPullRequestMsg(msg.getGossipMessage().getGossipMessage())) {
            Message.StateInfoPullRequest sipr = msg.getGossipMessage().getGossipMessage().getStateInfoPullReq();
            ByteString mac = sipr.getGroupMAC();
            byte[] pkiID = msg.getConnectionInfo().getPkiID();

        }
        return null;
    }

    private synchronized IGossipChannel getGossipChannelByMAC(byte[] receivedMAC, byte[] pkiID) {
        Set<Map.Entry<String, IGossipChannel>> entries = channels.entrySet();
        for (Map.Entry<String, IGossipChannel> entry : entries) {
            String chanName = entry.getKey();
            IGossipChannel channel = entry.getValue();
            byte[] mac = generateMAC(pkiID, chanName.getBytes());
            if (Arrays.equals(mac, receivedMAC)) {
                return channel;
            }
        }
        return null;
    }

    private synchronized IGossipChannel getGossipChannelByChainID(byte[] chainID) {
        if (isStopping()) {
            return null;
        }
        IGossipChannel channel = channels.get(new String(chainID));
        return channel;
    }

    private synchronized void joinChannel(IJoinChannelMessage joinMsg, byte[] chainID) {
        if (isStopping()) {
            return;
        }
        IGossipChannel channel = channels.get(new String(chainID));
        if (channel == null) {
            byte[] pkIid = this.getGossipServiceImpl().getComm().getPKIid();
            GossipAdapterImpl gossipAdapter = new GossipAdapterImpl();
            gossipAdapter.setGossipServiceImpl(getGossipServiceImpl());
            gossipAdapter.setDiscovery(getGossipServiceImpl().getDisc());
        } else {

        }
    }

    private Boolean isStopping() {
        return BooleanUtils.isTrue(stopping);
    }

    public byte[] generateMAC(byte[] pkiID, byte[] channelID) {
        byte[] bytes = ArrayUtils.addAll(pkiID, channelID);
        try {
            return Util.getHashBytes(bytes);
        } catch (LedgerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getStopping() {
        return stopping;
    }

    public void setStopping(Boolean stopping) {
        this.stopping = stopping;
    }

    public Map<String, IGossipChannel> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, IGossipChannel> channels) {
        this.channels = channels;
    }

    public GossipServiceImpl getGossipServiceImpl() {
        return gossipServiceImpl;
    }

    public void setGossipServiceImpl(GossipServiceImpl gossipServiceImpl) {
        this.gossipServiceImpl = gossipServiceImpl;
    }
}
