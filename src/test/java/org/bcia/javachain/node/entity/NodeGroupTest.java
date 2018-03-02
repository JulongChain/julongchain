package org.bcia.javachain.node.entity;

import org.bcia.javachain.BaseJunit4Test;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeGroupTest extends BaseJunit4Test {
    @Autowired
    private NodeGroup nodeGroup;

    @Test
    public void createGroup() {
        NodeGroup result = nodeGroup.createGroup("localhost", 7050, "myGroup");
        Assert.assertNotNull(result);
    }
}