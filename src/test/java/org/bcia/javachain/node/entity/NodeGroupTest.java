package org.bcia.javachain.node.entity;

import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeGroupTest extends BaseJunit4Test {
    @Autowired
    private Node node;

    @Test
    public void createGroup() throws NodeException {
        NodeGroup nodeGroup = new NodeGroup(node);
        nodeGroup.createGroup("localhost", 7050, "myGroup", null);
    }
}