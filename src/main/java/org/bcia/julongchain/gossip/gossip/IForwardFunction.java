package org.bcia.julongchain.gossip.gossip;

public interface IForwardFunction {

    public void execute(IReceivedMessage receivedMessage);

}
