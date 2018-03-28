package org.bcia.javachain.node;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.group.GroupCreateCmd;
import org.bcia.javachain.node.cmd.group.GroupJoinCmd;
import org.bcia.javachain.node.cmd.group.GroupUpdateCmd;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeTest extends BaseJunit4Test {
    @Autowired
    private Node node;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execCmd() throws ParseException, NodeException {
        //----------------------------------------正常用例集--------------------------------------------//
        String[] caseArgs1 = new String[]{"group", "create", "-c", "localhost:7050", "-g", "mygroup", "-f",
                "/home/javachain/group1.tx"};
        INodeCmd nodeCmd1 = node.execCmd(caseArgs1);
        Assert.assertThat(nodeCmd1, Matchers.instanceOf(GroupCreateCmd.class));

        String[] caseArgs2 = new String[]{"group", "join", "-b", "/home/javachain/mygroup.block"};
        INodeCmd nodeCmd2 = node.execCmd(caseArgs2);
        Assert.assertThat(nodeCmd2, Matchers.instanceOf(GroupJoinCmd.class));

        String[] caseArgs3 = new String[]{"group", "update", "-c", "localhost:7050", "-g", "mygroup", "-f",
                "/home/javachain/group1.tx"};
        INodeCmd nodeCmd3 = node.execCmd(caseArgs3);
        Assert.assertThat(nodeCmd3, Matchers.instanceOf(GroupUpdateCmd.class));

        //----------------------------------------异常用例集--------------------------------------------//
        String[] caseArgs11 = null;
        INodeCmd nodeCmd11 = node.execCmd(caseArgs11);
        Assert.assertNull(nodeCmd11);

        String[] caseArgs12 = new String[]{"group"};
        INodeCmd nodeCmd12 = node.execCmd(caseArgs12);
        Assert.assertNull(nodeCmd12);

        String[] caseArgs13 = new String[]{"group"};
        INodeCmd nodeCmd13 = node.execCmd(caseArgs13);
        Assert.assertNull(nodeCmd13);


    }
}