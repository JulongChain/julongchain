package org.bcia.julongchain.gossip.discovery;

public interface Discovery {

    public NetworkMember lookup(byte[] PKIID);

    public NetworkMember self();

    public void updateMetadata(byte[] metadata);

    public void updateEndpoint(String string);

    public void stop();

    public NetworkMember[] getMembership();

    public void initiateSync(Integer peerNum);

    public void connect(NetworkMember member, IIdentifier id);


}
