package org.bcia.julongchain.gossip.discovery;

public interface IDisclosurePolicy {

    public Object[] execute(NetworkMember remotePeer);

}
