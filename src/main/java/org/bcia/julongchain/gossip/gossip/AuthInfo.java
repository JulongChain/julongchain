package org.bcia.julongchain.gossip.gossip;

public class AuthInfo {

    private byte[] signedData;
    private byte[] signature;

    public byte[] getSignedData() {
        return signedData;
    }

    public void setSignedData(byte[] signedData) {
        this.signedData = signedData;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
