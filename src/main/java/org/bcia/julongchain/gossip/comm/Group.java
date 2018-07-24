package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;

public class Group {

    private IMessageAcceptor pred;
    private Channel<Object> ch;

    public IMessageAcceptor getPred() {
        return pred;
    }

    public void setPred(IMessageAcceptor pred) {
        this.pred = pred;
    }

    public Channel<Object> getCh() {
        return ch;
    }

    public void setCh(Channel<Object> ch) {
        this.ch = ch;
    }
}
