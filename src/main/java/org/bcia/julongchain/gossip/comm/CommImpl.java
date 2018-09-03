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
package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.common.TLSCertificates;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;
import org.bcia.julongchain.gossip.identity.IMapper;
import org.bcia.julongchain.gossip.util.PubSub;

import java.time.Duration;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class CommImpl implements IComm, IConnFactory{

    public static final Long defDialTimeout = 3000L;
    public static final Long defConnTimeout = 2000L;
    public static final Integer defReceBuffSize = 20;
    public static final Integer defSendBuffSize = 20;

    private static final JulongChainLog log = JulongChainLogFactory.getLog(CommImpl.class);
    private TLSCertificates tlsCerts;
    private PubSub pubSub;
    private byte[] nodeIdentity;
    private IMapper idMapper;
    private ConnectionStore connStore;
    private byte[] PKIID;
    private Channel<byte[]> deadEndpoints;
    private ChannelDeMultiplexer msgPublisher;
    private Channel<Object> exitChan;
    private Channel<IReceivedMessage>[] subscriptions;
    private Integer port;
    private Boolean stopping;
    private Duration dialTimeout;

    @Override
    public byte[] getPKIid() {
        return new byte[0];
    }

    @Override
    public void send(SignedGossipMessage msg, RemoteNode... nodes) {

    }

    @Override
    public SendResult[] sendWithAck(SignedGossipMessage msg, Duration timeout, Integer minAck, RemoteNode... nodes) {
        return new SendResult[0];
    }

    @Override
    public void probe(RemoteNode node) throws GossipException {

    }

    @Override
    public byte[] handshake(RemoteNode node) throws GossipException {
        return new byte[0];
    }

    @Override
    public Channel<IReceivedMessage> accept(IMessageAcceptor messageAcceptor) {
        return null;
    }

    @Override
    public Channel<byte[]> presumedDead() {
        return null;
    }

    @Override
    public void closeConn(RemoteNode node) {

    }

    @Override
    public Connection createConnection(String endpoint, byte[] pkiID) {
        return null;
    }

    @Override
    public void stop() {

    }

    public static Long getDefDialTimeout() {
        return defDialTimeout;
    }

    public static Long getDefConnTimeout() {
        return defConnTimeout;
    }

    public static Integer getDefReceBuffSize() {
        return defReceBuffSize;
    }

    public static Integer getDefSendBuffSize() {
        return defSendBuffSize;
    }

    public static JulongChainLog getLog() {
        return log;
    }

    public TLSCertificates getTlsCerts() {
        return tlsCerts;
    }

    public void setTlsCerts(TLSCertificates tlsCerts) {
        this.tlsCerts = tlsCerts;
    }

    public PubSub getPubSub() {
        return pubSub;
    }

    public void setPubSub(PubSub pubSub) {
        this.pubSub = pubSub;
    }

    public byte[] getNodeIdentity() {
        return nodeIdentity;
    }

    public void setNodeIdentity(byte[] nodeIdentity) {
        this.nodeIdentity = nodeIdentity;
    }

    public IMapper getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(IMapper idMapper) {
        this.idMapper = idMapper;
    }

    public ConnectionStore getConnStore() {
        return connStore;
    }

    public void setConnStore(ConnectionStore connStore) {
        this.connStore = connStore;
    }

    public byte[] getPKIID() {
        return PKIID;
    }

    public void setPKIID(byte[] PKIID) {
        this.PKIID = PKIID;
    }

    public Channel<byte[]> getDeadEndpoints() {
        return deadEndpoints;
    }

    public void setDeadEndpoints(Channel<byte[]> deadEndpoints) {
        this.deadEndpoints = deadEndpoints;
    }

    public ChannelDeMultiplexer getMsgPublisher() {
        return msgPublisher;
    }

    public void setMsgPublisher(ChannelDeMultiplexer msgPublisher) {
        this.msgPublisher = msgPublisher;
    }

    public Channel<Object> getExitChan() {
        return exitChan;
    }

    public void setExitChan(Channel<Object> exitChan) {
        this.exitChan = exitChan;
    }

    public Channel<IReceivedMessage>[] getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Channel<IReceivedMessage>[] subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getStopping() {
        return stopping;
    }

    public void setStopping(Boolean stopping) {
        this.stopping = stopping;
    }

    public Duration getDialTimeout() {
        return dialTimeout;
    }

    public void setDialTimeout(Duration dialTimeout) {
        this.dialTimeout = dialTimeout;
    }
}
