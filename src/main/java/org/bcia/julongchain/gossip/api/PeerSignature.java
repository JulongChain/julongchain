package org.bcia.julongchain.gossip.api;

public class PeerSignature {

    private byte[] signature;
    private byte[] message;
    private byte[] peerIdentityType;

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public byte[] getPeerIdentityType() {
        return peerIdentityType;
    }

    public void setPeerIdentityType(byte[] peerIdentityType) {
        this.peerIdentityType = peerIdentityType;
    }
}
