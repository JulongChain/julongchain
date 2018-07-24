package org.bcia.julongchain.gossip.comm;

public class RemotePeer {

    public String endpoint;
    public byte[] PKIID;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public byte[] getPKIID() {
        return PKIID;
    }

    public void setPKIID(byte[] PKIID) {
        this.PKIID = PKIID;
    }
}
