package org.bcia.julongchain.gossip.filter;

import org.bcia.julongchain.gossip.discovery.NetworkMember;

public interface IRoutingFilter {

    public Boolean routingFilter(NetworkMember networkMember);

}
