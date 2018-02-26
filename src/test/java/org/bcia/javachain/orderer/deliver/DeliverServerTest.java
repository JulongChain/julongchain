package org.bcia.javachain.orderer.deliver;


import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DeliverServerTest extends BaseJunit4Test{
@Autowired
    public DeliverServer deliverServer;

    @Test
    public void start() throws IOException {
        deliverServer.start();
    }

    @Test
    public void blockUntilShutdown() throws InterruptedException {
        deliverServer.blockUntilShutdown();
    }
}
