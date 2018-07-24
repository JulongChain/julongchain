package org.bcia.julongchain.gossip.gossip.channel;

import java.time.Duration;

public class Config {

    private String ID;
    private Duration publishStateInfoInterval;
    private Integer maxBlockCountToStore;
    private Integer pullPeerNum;
    private Duration pullInterval;
    private Duration requestStateInfoInterval;
    private Duration blockExpirationInterval;
    private Duration stateInfoCacheSweepInterval;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Duration getPublishStateInfoInterval() {
        return publishStateInfoInterval;
    }

    public void setPublishStateInfoInterval(Duration publishStateInfoInterval) {
        this.publishStateInfoInterval = publishStateInfoInterval;
    }

    public Integer getMaxBlockCountToStore() {
        return maxBlockCountToStore;
    }

    public void setMaxBlockCountToStore(Integer maxBlockCountToStore) {
        this.maxBlockCountToStore = maxBlockCountToStore;
    }

    public Integer getPullPeerNum() {
        return pullPeerNum;
    }

    public void setPullPeerNum(Integer pullPeerNum) {
        this.pullPeerNum = pullPeerNum;
    }

    public Duration getPullInterval() {
        return pullInterval;
    }

    public void setPullInterval(Duration pullInterval) {
        this.pullInterval = pullInterval;
    }

    public Duration getRequestStateInfoInterval() {
        return requestStateInfoInterval;
    }

    public void setRequestStateInfoInterval(Duration requestStateInfoInterval) {
        this.requestStateInfoInterval = requestStateInfoInterval;
    }

    public Duration getBlockExpirationInterval() {
        return blockExpirationInterval;
    }

    public void setBlockExpirationInterval(Duration blockExpirationInterval) {
        this.blockExpirationInterval = blockExpirationInterval;
    }

    public Duration getStateInfoCacheSweepInterval() {
        return stateInfoCacheSweepInterval;
    }

    public void setStateInfoCacheSweepInterval(Duration stateInfoCacheSweepInterval) {
        this.stateInfoCacheSweepInterval = stateInfoCacheSweepInterval;
    }
}
