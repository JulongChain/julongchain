package org.bcia.julongchain.gossip.gossip;

public class EmittedGossipMessage {

    private SignedGossipMessage signedGossipMessage;
    private IFilter filter;

    public SignedGossipMessage getSignedGossipMessage() {
        return signedGossipMessage;
    }

    public void setSignedGossipMessage(SignedGossipMessage signedGossipMessage) {
        this.signedGossipMessage = signedGossipMessage;
    }

    public IFilter getFilter() {
        return filter;
    }

    public void setFilter(IFilter filter) {
        this.filter = filter;
    }
}
