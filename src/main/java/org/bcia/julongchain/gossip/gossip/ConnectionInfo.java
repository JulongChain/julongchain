package org.bcia.julongchain.gossip.gossip;

public class ConnectionInfo {

    private byte[] pkiID;
    private AuthInfo auth;
    private byte[] peerIdentity;
    private String endpoint;

    public byte[] getPkiID() {
        return pkiID;
    }

    public void setPkiID(byte[] pkiID) {
        this.pkiID = pkiID;
    }

    public AuthInfo getAuth() {
        return auth;
    }

    public void setAuth(AuthInfo auth) {
        this.auth = auth;
    }

    public byte[] getPeerIdentity() {
        return peerIdentity;
    }

    public void setPeerIdentity(byte[] peerIdentity) {
        this.peerIdentity = peerIdentity;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
