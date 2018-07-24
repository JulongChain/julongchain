package org.bcia.julongchain.node.gossip;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.gossip.api.IMessageCryptoService;

import java.sql.Timestamp;

public class MspMessageCryptoService implements IMessageCryptoService{

    private static final JavaChainLog log = JavaChainLogFactory.getLog(MspMessageCryptoService.class);

    @Override
    public byte[] getPKIidOFCert(byte[] peerIdentity) {
        return new byte[0];
    }

    @Override
    public void verifyBlock(byte[] chainID, Long seqNum, byte[] signedBlock) throws GossipException {

    }

    @Override
    public byte[] sign(byte[] msg) throws GossipException {
        return new byte[0];
    }

    @Override
    public void verify(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException {

    }

    @Override
    public void verifyByChannel(byte[] chainID, byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException {

    }

    @Override
    public void validateIdentity(byte[] peerIdentity) throws GossipException {

    }

    @Override
    public Timestamp expiration(byte[] peerIdentity) throws GossipException {
        return null;
    }
}
