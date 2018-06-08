package org.bcia.julongchain.node.entity;

import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
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