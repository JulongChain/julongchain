package org.bcia.julongchain.gossip.comm;

public class SendResult {

    private Boolean sendSuccess;
    private String errorMessage;
    private RemotePeer remotePeer;

    public Boolean getSendSuccess() {
        return sendSuccess;
    }

    public void setSendSuccess(Boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public RemotePeer getRemotePeer() {
        return remotePeer;
    }

    public void setRemotePeer(RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
    }
}
