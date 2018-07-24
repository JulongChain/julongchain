package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.gossip.filter.IRoutingFilter;

import java.time.Duration;

public class SendCriteria {

    private Duration timeout;
    private Integer minAck;
    private Integer maxPeers;
    private IRoutingFilter isEligible;
    private byte[] group;

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Integer getMinAck() {
        return minAck;
    }

    public void setMinAck(Integer minAck) {
        this.minAck = minAck;
    }

    public Integer getMaxPeers() {
        return maxPeers;
    }

    public void setMaxPeers(Integer maxPeers) {
        this.maxPeers = maxPeers;
    }

    public IRoutingFilter getIsEligible() {
        return isEligible;
    }

    public void setIsEligible(IRoutingFilter isEligible) {
        this.isEligible = isEligible;
    }

    public byte[] getGroup() {
        return group;
    }

    public void setGroup(byte[] group) {
        this.group = group;
    }
}
