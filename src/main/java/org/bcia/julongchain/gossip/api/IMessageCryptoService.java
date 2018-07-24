package org.bcia.julongchain.gossip.api;

import org.bcia.julongchain.common.exception.GossipException;

import java.sql.Timestamp;

public interface IMessageCryptoService {

    public byte[] getPKIidOFCert(byte[] peerIdentity);

    public void verifyBlock(byte[] chainID, Long seqNum, byte[] signedBlock) throws GossipException;

    public byte[] sign(byte[] msg) throws GossipException;

    public void verify(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException;

    public void verifyByChannel(byte[] chainID, byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException;

    public void validateIdentity(byte[] peerIdentity) throws GossipException;

    public Timestamp expiration(byte[] peerIdentity) throws GossipException;

}
