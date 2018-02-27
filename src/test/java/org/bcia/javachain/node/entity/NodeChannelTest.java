package org.bcia.javachain.node.entity;

import org.bcia.javachain.BaseJunit4Test;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class NodeChannelTest extends BaseJunit4Test {
    @Autowired
    private NodeChannel nodeChannel;

    @Test
    public void createChannel() {
        NodeChannel result = nodeChannel.createChannel("localhost", 7050, "myChannel");
        Assert.assertNotNull(result);
    }
}