/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.gossip.common.TLSCertificates;
import org.bouncycastle.asn1.x509.Certificate;

import java.time.Duration;

public class Config {

    private Integer bindPort;
    private String ID;
    private String[] bootstrapPeers;
    private Integer propagateIterations;
    private Integer propagatePeerNum;
    private Integer maxBlockCountToStore;
    private Integer maxPropagationBurstSize;
    private Duration maxPropagationBurstLatency;
    private Duration pullInterval;
    private Integer pullPeerNum;
    private Boolean skipBlockVerification;
    private Duration publishCertPeriod;
    private Duration publishStateInfoInterval;
    private Duration requestStateInfoInterval;
    private TLSCertificates tlsCerts;
    private String internalEndpoint;
    private String externalEndpoint;

    public Integer getBindPort() {
        return bindPort;
    }

    public void setBindPort(Integer bindPort) {
        this.bindPort = bindPort;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String[] getBootstrapPeers() {
        return bootstrapPeers;
    }

    public void setBootstrapPeers(String[] bootstrapPeers) {
        this.bootstrapPeers = bootstrapPeers;
    }

    public Integer getPropagateIterations() {
        return propagateIterations;
    }

    public void setPropagateIterations(Integer propagateIterations) {
        this.propagateIterations = propagateIterations;
    }

    public Integer getPropagatePeerNum() {
        return propagatePeerNum;
    }

    public void setPropagatePeerNum(Integer propagatePeerNum) {
        this.propagatePeerNum = propagatePeerNum;
    }

    public Integer getMaxBlockCountToStore() {
        return maxBlockCountToStore;
    }

    public void setMaxBlockCountToStore(Integer maxBlockCountToStore) {
        this.maxBlockCountToStore = maxBlockCountToStore;
    }

    public Integer getMaxPropagationBurstSize() {
        return maxPropagationBurstSize;
    }

    public void setMaxPropagationBurstSize(Integer maxPropagationBurstSize) {
        this.maxPropagationBurstSize = maxPropagationBurstSize;
    }

    public Duration getMaxPropagationBurstLatency() {
        return maxPropagationBurstLatency;
    }

    public void setMaxPropagationBurstLatency(Duration maxPropagationBurstLatency) {
        this.maxPropagationBurstLatency = maxPropagationBurstLatency;
    }

    public Duration getPullInterval() {
        return pullInterval;
    }

    public void setPullInterval(Duration pullInterval) {
        this.pullInterval = pullInterval;
    }

    public Integer getPullPeerNum() {
        return pullPeerNum;
    }

    public void setPullPeerNum(Integer pullPeerNum) {
        this.pullPeerNum = pullPeerNum;
    }

    public Boolean getSkipBlockVerification() {
        return skipBlockVerification;
    }

    public void setSkipBlockVerification(Boolean skipBlockVerification) {
        this.skipBlockVerification = skipBlockVerification;
    }

    public Duration getPublishCertPeriod() {
        return publishCertPeriod;
    }

    public void setPublishCertPeriod(Duration publishCertPeriod) {
        this.publishCertPeriod = publishCertPeriod;
    }

    public Duration getPublishStateInfoInterval() {
        return publishStateInfoInterval;
    }

    public void setPublishStateInfoInterval(Duration publishStateInfoInterval) {
        this.publishStateInfoInterval = publishStateInfoInterval;
    }

    public Duration getRequestStateInfoInterval() {
        return requestStateInfoInterval;
    }

    public void setRequestStateInfoInterval(Duration requestStateInfoInterval) {
        this.requestStateInfoInterval = requestStateInfoInterval;
    }

    public TLSCertificates getTlsCerts() {
        return tlsCerts;
    }

    public void setTlsCerts(TLSCertificates tlsCerts) {
        this.tlsCerts = tlsCerts;
    }

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public void setInternalEndpoint(String internalEndpoint) {
        this.internalEndpoint = internalEndpoint;
    }

    public String getExternalEndpoint() {
        return externalEndpoint;
    }

    public void setExternalEndpoint(String externalEndpoint) {
        this.externalEndpoint = externalEndpoint;
    }
}
