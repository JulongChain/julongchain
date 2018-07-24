package org.bcia.julongchain.gossip.gossip;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.gossip.NetworkMember;
import org.bcia.julongchain.gossip.comm.RemotePeer;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.discovery.Discovery;
import org.bcia.julongchain.gossip.gossip.channel.Config;
import org.bcia.julongchain.gossip.gossip.channel.IAdapter;
import org.bcia.julongchain.gossip.state.IGossipAdapter;
import org.bcia.julongchain.protos.gossip.Message;

import java.util.Arrays;

public class GossipAdapterImpl implements IGossipAdapter, IAdapter{

    private static final JavaChainLog log = JavaChainLogFactory.getLog(GossipAdapterImpl.class);
    private GossipServiceImpl gossipServiceImpl;
    private Discovery discovery;

    @Override
    public Config getConf() {
        Config config = new Config();
        org.bcia.julongchain.gossip.gossip.Config gossipServiceConf = this.getGossipServiceImpl().getConf();
        config.setID(gossipServiceConf.getID());
        config.setMaxBlockCountToStore(gossipServiceConf.getMaxBlockCountToStore());
        config.setPublishStateInfoInterval(gossipServiceConf.getPublishStateInfoInterval());
        config.setPullInterval(gossipServiceConf.getPullInterval());
        config.setPullPeerNum(gossipServiceConf.getPullPeerNum());
        config.setRequestStateInfoInterval(gossipServiceConf.getRequestStateInfoInterval());
        config.setBlockExpirationInterval(gossipServiceConf.getPullInterval().multipliedBy(100));
        config.setStateInfoCacheSweepInterval(gossipServiceConf.getPullInterval().multipliedBy(5));
        return config;
    }

    @Override
    public void gossip(SignedGossipMessage msg) {
        EmittedGossipMessage emittedGossipMessage = new EmittedGossipMessage();
        emittedGossipMessage.setSignedGossipMessage(msg);
        emittedGossipMessage.setFilter(new IFilter() {
            @Override
            public Boolean filter(byte[] bytes2) {
                return true;
            }
        });
        IBatchingEmitter emitter = this.getGossipServiceImpl().getEmitter();
        emitter.add(emittedGossipMessage);
    }

    @Override
    public void forward(IReceivedMessage msg) {
        EmittedGossipMessage emittedGossipMessage = new EmittedGossipMessage();
        emittedGossipMessage.setSignedGossipMessage(msg.getGossipMessage());
        IFilter filter = new IFilter() {
            @Override
            public Boolean filter(byte[] bytes2) {
                return !Arrays.equals(getBytes1(), bytes2);
            }
        };
        filter.setBytes1(msg.getConnectionInfo().getPkiID());
        emittedGossipMessage.setFilter(filter);
        IBatchingEmitter emitter = this.getGossipServiceImpl().getEmitter();
        emitter.add(emittedGossipMessage);
    }

    @Override
    public void send(SignedGossipMessage msg, RemotePeer... peers) {
        this.getGossipServiceImpl().getComm().send(msg, peers);
    }

    @Override
    public void validateStateInfoMessage(SignedGossipMessage msg) throws GossipException {
        this.getGossipServiceImpl().validateStateInfoMsg(msg);
    }

    @Override
    public byte[] getOrgOfPeer(byte[] pkiID) {
        return getGossipServiceImpl().getOrgOfPeer(pkiID);
    }

    @Override
    public byte[] getIdentityByPKIID(byte[] pkiID) {
        try {
            byte[] identity = getGossipServiceImpl().getIdMapper().get(pkiID);
            return identity;
        } catch (GossipException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public org.bcia.julongchain.gossip.discovery.NetworkMember[] getMembership() {
        return new org.bcia.julongchain.gossip.discovery.NetworkMember[0];
    }

    @Override
    public org.bcia.julongchain.gossip.discovery.NetworkMember lookup(byte[] pkiID) {
        return null;
    }

    @Override
    public void send(Message.GossipMessage msg, RemotePeer... peers) {

    }

    @Override
    public Object[] accept(IMessageAcceptor acceptor, Boolean passThrough) {
        return new Object[0];
    }

    @Override
    public void updateChannelMetadata(byte[] metadata, byte[] chainID) {

    }

    @Override
    public NetworkMember[] peersOfChannel(byte[] chainID) {
        return new NetworkMember[0];
    }

    public GossipServiceImpl getGossipServiceImpl() {
        return gossipServiceImpl;
    }

    public void setGossipServiceImpl(GossipServiceImpl gossipServiceImpl) {
        this.gossipServiceImpl = gossipServiceImpl;
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    public void setDiscovery(Discovery discovery) {
        this.discovery = discovery;
    }
}
