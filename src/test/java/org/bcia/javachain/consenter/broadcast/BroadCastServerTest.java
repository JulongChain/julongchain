package org.bcia.javachain.consenter.broadcast;


import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class BroadCastServerTest extends BaseJunit4Test{
    @Autowired
    public BroadCastServer broadCastServer;
    @Test
    public void start() throws IOException {
        broadCastServer.start();
    }

    @Test
    public void blockUntilShutdown() throws InterruptedException {
    broadCastServer.blockUntilShutdown();
    }


}
