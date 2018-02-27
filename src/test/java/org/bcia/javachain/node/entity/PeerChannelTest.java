package org.bcia.javachain.node.entity;

import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.consenter.broadcast.BroadCastServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class PeerChannelTest extends BaseJunit4Test {

    @Autowired
    private NodeChannel nodeChannel;

    @Before
    public void setUp() throws Exception {
        BroadCastServer server = new BroadCastServer();
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createChannel() {
    }
}