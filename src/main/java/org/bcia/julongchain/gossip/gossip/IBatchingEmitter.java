package org.bcia.julongchain.gossip.gossip;

public interface IBatchingEmitter {

    public void add(Object object);

    public void stop();

    public Integer size();

}
