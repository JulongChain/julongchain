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

import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.comm.IComm;
import org.bcia.julongchain.gossip.discovery.IDisclosurePolicy;

public class DiscoveryAdapter {

    private Boolean stopping;
    private IComm c;
    private Channel<byte[]> presumedDead;
    private Channel<IReceivedMessage> incChan;
    private IGossipFunction gossipFunction;
    private IForwardFunction forwardFunction;
    private IDisclosurePolicy disclosurePolicy;

    public Boolean getStopping() {
        return stopping;
    }

    public void setStopping(Boolean stopping) {
        this.stopping = stopping;
    }

    public IComm getC() {
        return c;
    }

    public void setC(IComm c) {
        this.c = c;
    }

    public Channel<byte[]> getPresumedDead() {
        return presumedDead;
    }

    public void setPresumedDead(Channel<byte[]> presumedDead) {
        this.presumedDead = presumedDead;
    }

    public Channel<IReceivedMessage> getIncChan() {
        return incChan;
    }

    public void setIncChan(Channel<IReceivedMessage> incChan) {
        this.incChan = incChan;
    }

    public IGossipFunction getGossipFunction() {
        return gossipFunction;
    }

    public void setGossipFunction(IGossipFunction gossipFunction) {
        this.gossipFunction = gossipFunction;
    }

    public IForwardFunction getForwardFunction() {
        return forwardFunction;
    }

    public void setForwardFunction(IForwardFunction forwardFunction) {
        this.forwardFunction = forwardFunction;
    }

    public IDisclosurePolicy getDisclosurePolicy() {
        return disclosurePolicy;
    }

    public void setDisclosurePolicy(IDisclosurePolicy disclosurePolicy) {
        this.disclosurePolicy = disclosurePolicy;
    }
}
