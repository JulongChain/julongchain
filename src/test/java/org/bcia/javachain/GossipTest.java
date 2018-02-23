package org.bcia.javachain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class GossipTest extends BaseJunit4Test{

    @Autowired
    private GossipService gossipService;

    @Test
    public void gossipTest() {
        System.out.println("开始测试");
        System.out.println(gossipService.gossip());
    }

}