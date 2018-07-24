package org.bcia.julongchain.gossip.discovery;

public class PeerIdentification {

    private byte[] ID;
    private Boolean selfOrg;

    public byte[] getID() {
        return ID;
    }

    public void setID(byte[] ID) {
        this.ID = ID;
    }

    public Boolean getSelfOrg() {
        return selfOrg;
    }

    public void setSelfOrg(Boolean selfOrg) {
        this.selfOrg = selfOrg;
    }
}
