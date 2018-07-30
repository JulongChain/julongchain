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

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.CertStore;
import org.bcia.julongchain.gossip.api.IJoinChannelMessage;
import org.bcia.julongchain.gossip.api.IPeerSuspector;
import org.bcia.julongchain.gossip.api.ISecurityAdvisor;
import org.bcia.julongchain.gossip.api.ISubChannelSelectionCriteria;
import org.bcia.julongchain.gossip.comm.GroupDeMultiplexer;
import org.bcia.julongchain.gossip.comm.IComm;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.discovery.Discovery;
import org.bcia.julongchain.gossip.discovery.IEnvelopeFilter;
import org.bcia.julongchain.gossip.discovery.ISieve;
import org.bcia.julongchain.gossip.discovery.NetworkMember;
import org.bcia.julongchain.gossip.filter.IRoutingFilter;
import org.bcia.julongchain.gossip.identity.IMapper;
import org.bcia.julongchain.protos.gossip.Message;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;

public class GossipServiceImpl implements  IGossip{

    private static final JavaChainLog log = JavaChainLogFactory.getLog(GossipServiceImpl.class);

    private byte[] selfIdentity;
    private Timestamp includeIdentityPeriod;
    private CertStore certStore;
    private IMapper idMapper;
    private Channel<byte[]> presumedDead;
    private Discovery disc;
    private IComm comm;
    private Timestamp incTime;
    private byte[] selfOrg;
    private GroupDeMultiplexer groupDeMultiplexer;
    private Config conf;
    private Channel<Object> toDieChan;
    private Boolean stopFlag;
    private IBatchingEmitter emitter;
    private DiscoveryAdapter discoveryAdapter;
    private ISecurityAdvisor secAdvisor;

    public void validateStateInfoMsg(SignedGossipMessage msg) throws GossipException {
        IVerifier verifier = new IVerifier() {
            @Override
            public void execute(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException {
                byte[] pkiID = getIdMapper().getPKIidOfCert(peerIdentity);
                if (pkiID == null) {
                    throw new GossipException("PKI_ID not found in identity mapper");
                }
                getIdMapper().verify(pkiID, signature,message);
            }
        };
        byte[] identity = getIdMapper().get(msg.getGossipMessage().getStateInfo().getPkiId().toByteArray());
        msg.verify(identity, verifier);
    }

    public byte[] getOrgOfPeer(byte[] pkiID) {
        byte[] cert = new byte[]{};
        try {
            cert = getIdMapper().get(pkiID);
        } catch (GossipException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return getSecAdvisor().orgByPeerIdentity(cert);
    }

    public Object[] disclosurePolicy(NetworkMember remotePeer) {
        byte[] remotePeerOrg = getOrgOfPeer(remotePeer.getPKIid());
        if (remotePeerOrg == null || remotePeerOrg.length == 0) {
            log.warn("Cannot determine organization of " + remotePeer.toString());
            ISieve iSieve = new ISieve() {
                @Override
                public Boolean execute(SignedGossipMessage message) {
                    return false;
                }
            };
            IEnvelopeFilter iEnvelopeFilter = new IEnvelopeFilter() {
                @Override
                public Message.Envelope execute(SignedGossipMessage message) {
                    return message.getEnvelope();
                }
            };
            return new Object[]{iSieve, iEnvelopeFilter};
        }
        ISieve iSieve = new ISieve() {
            @Override
            public Boolean execute(SignedGossipMessage msg) {
                if (GossipMessageHelper.isAliveMsg(msg.getGossipMessage())) {
                    log.error("Programming error, this should be used only on alive messages");
                }
                byte[] org = getOrgOfPeer(msg.getGossipMessage().getAliveMsg().getMembership().getPkiId().toByteArray());
                if (org.length == 0) {
                    log.warn("Unable to determine org of message " + msg.getGossipMessage().toString());
                    return false;
                }
                boolean fromSameForeignOrg = Arrays.equals(remotePeerOrg, org);
                boolean fromMyOrg = Arrays.equals(getSelfOrg(), org);
                if (!(fromSameForeignOrg || fromMyOrg)) {
                    return false;
                }
                return Arrays.equals(org, remotePeerOrg)
                        || msg.getGossipMessage().getAliveMsg().getMembership().getEndpoint() != ""
                        && remotePeer.getEndpoint() != "";
            }
        };
        IEnvelopeFilter iEnvelopeFilter = new IEnvelopeFilter() {
            @Override
            public Message.Envelope execute(SignedGossipMessage msg) {
                if (!Arrays.equals(getSelfOrg(), remotePeerOrg)) {
                    Message.SecretEnvelope secretEnvelope = Message.SecretEnvelope.newBuilder().build();
                    Message.Envelope newEnvelope = Message.Envelope.newBuilder().mergeFrom(msg.getEnvelope()).setSecretEnvelope(secretEnvelope).build();
                    msg.setEnvelope(newEnvelope);
                    return newEnvelope;
                }
                return null;
            }
        };
        return new Object[]{iSieve, iEnvelopeFilter};
    }

    public IRoutingFilter peersByOriginOrgPolicy(NetworkMember peer) {
        byte[] peersOrg = getOrgOfPeer(peer.getPKIid());
        if (peersOrg.length == 0) {
            log.warn("Unable to determine organization of peer " + peer.toString());
            return new IRoutingFilter() {
                @Override
                public Boolean routingFilter(NetworkMember networkMember) {
                    return false;
                }
            };
        }
        if (Arrays.equals(getSelfOrg(), peersOrg)) {
            return new IRoutingFilter() {
                @Override
                public Boolean routingFilter(NetworkMember networkMember) {
                    return true;
                }
            };
        }
        return new IRoutingFilter() {
            @Override
            public Boolean routingFilter(NetworkMember member) {
                byte[] memberOrg = getOrgOfPeer(member.getPKIid());
                if (memberOrg.length == 0) {
                    return false;
                }
                boolean isFromMyOrg = Arrays.equals(getSelfOrg(), memberOrg);
                return isFromMyOrg || Arrays.equals(memberOrg, peersOrg);
            }
        };
    }

    @Override
    public void send(Message.GossipMessage msg, RemotePeer... peers) {

    }

    @Override
    public void sendByCriteria(SignedGossipMessage msg, SendCriteria sendCriteria) {

    }

    @Override
    public NetworkMember[] peers() {
        return new NetworkMember[0];
    }

    @Override
    public NetworkMember[] peersOfChannel(byte[] groupID) {
        return new NetworkMember[0];
    }

    @Override
    public void updateMetadata(byte[] metadata) {

    }

    @Override
    public void updateGroupMetadata(byte[] metadata, byte[] groupID) {

    }

    @Override
    public void gossip(Message.GossipMessage gossipMessage) {

    }

    @Override
    public IRoutingFilter peerFilter(byte[] groupID, ISubChannelSelectionCriteria messagePredicate) {
        return null;
    }

    @Override
    public Map<String, Object> accept(IMessageAcceptor acceptor, Boolean passThrough) {
        return null;
    }

    @Override
    public void joinGroup(IJoinChannelMessage joinMsg, byte[] groupID) {

    }

    @Override
    public void leaveGroup(byte[] groupID) {

    }

    @Override
    public void suspectPeers(IPeerSuspector peerSuspector) {

    }

    @Override
    public void stop() {

    }

    public byte[] getSelfIdentity() {
        return selfIdentity;
    }

    public void setSelfIdentity(byte[] selfIdentity) {
        this.selfIdentity = selfIdentity;
    }

    public Timestamp getIncludeIdentityPeriod() {
        return includeIdentityPeriod;
    }

    public void setIncludeIdentityPeriod(Timestamp includeIdentityPeriod) {
        this.includeIdentityPeriod = includeIdentityPeriod;
    }

    public CertStore getCertStore() {
        return certStore;
    }

    public void setCertStore(CertStore certStore) {
        this.certStore = certStore;
    }

    public IMapper getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(IMapper idMapper) {
        this.idMapper = idMapper;
    }

    public Channel<byte[]> getPresumedDead() {
        return presumedDead;
    }

    public void setPresumedDead(Channel<byte[]> presumedDead) {
        this.presumedDead = presumedDead;
    }

    public Discovery getDisc() {
        return disc;
    }

    public void setDisc(Discovery disc) {
        this.disc = disc;
    }

    public IComm getComm() {
        return comm;
    }

    public void setComm(IComm comm) {
        this.comm = comm;
    }

    public Timestamp getIncTime() {
        return incTime;
    }

    public void setIncTime(Timestamp incTime) {
        this.incTime = incTime;
    }

    public byte[] getSelfOrg() {
        return selfOrg;
    }

    public void setSelfOrg(byte[] selfOrg) {
        this.selfOrg = selfOrg;
    }

    public GroupDeMultiplexer getGroupDeMultiplexer() {
        return groupDeMultiplexer;
    }

    public void setGroupDeMultiplexer(GroupDeMultiplexer groupDeMultiplexer) {
        this.groupDeMultiplexer = groupDeMultiplexer;
    }

    public Config getConf() {
        return conf;
    }

    public void setConf(Config conf) {
        this.conf = conf;
    }

    public Channel<Object> getToDieChan() {
        return toDieChan;
    }

    public void setToDieChan(Channel<Object> toDieChan) {
        this.toDieChan = toDieChan;
    }

    public Boolean getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(Boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public IBatchingEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(IBatchingEmitter emitter) {
        this.emitter = emitter;
    }

    public DiscoveryAdapter getDiscoveryAdapter() {
        return discoveryAdapter;
    }

    public void setDiscoveryAdapter(DiscoveryAdapter discoveryAdapter) {
        this.discoveryAdapter = discoveryAdapter;
    }

    public ISecurityAdvisor getSecAdvisor() {
        return secAdvisor;
    }

    public void setSecAdvisor(ISecurityAdvisor secAdvisor) {
        this.secAdvisor = secAdvisor;
    }
}
