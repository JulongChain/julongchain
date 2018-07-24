package org.bcia.julongchain.gossip.api;

public interface IJoinChannelMessage {

    public Long sequenceNumber();

    public byte[][] members = new byte[][]{};

    public AnchorPeer[] anchorPeersOf(byte[] orgIdentity);

}
