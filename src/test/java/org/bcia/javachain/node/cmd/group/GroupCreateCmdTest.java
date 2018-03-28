package org.bcia.javachain.node.cmd.group;

import org.bcia.javachain.node.cmd.INodeCmd;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public class GroupCreateCmdTest {

    @Autowired
    private GroupCreateCmd groupCreateCmd;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execCmd() {
        //----------------------------------------正常用例集--------------------------------------------//
//        String[] caseArgs1 = new String[]{"group", "create", "-c", "localhost:7050", "-g", "mygroup", "-f",
//                "/home/javachain/group1.tx"};
//        INodeCmd nodeCmd1 = node.execCmd(caseArgs1);
//        Assert.assertThat(nodeCmd1, Matchers.instanceOf(GroupCreateCmd.class));
//
//        String[] caseArgs2 = new String[]{"group", "join", "-b", "/home/javachain/mygroup.block"};
//        INodeCmd nodeCmd2 = node.execCmd(caseArgs2);
//        Assert.assertThat(nodeCmd2, Matchers.instanceOf(GroupJoinCmd.class));
//
//        String[] caseArgs3 = new String[]{"group", "update", "-c", "localhost:7050", "-g", "mygroup", "-f",
//                "/home/javachain/group1.tx"};
//        INodeCmd nodeCmd3 = node.execCmd(caseArgs3);
//        Assert.assertThat(nodeCmd3, Matchers.instanceOf(GroupUpdateCmd.class));

        //----------------------------------------异常用例集--------------------------------------------//
//        String[] caseArgs11 = null;
//        INodeCmd nodeCmd11 = node.execCmd(caseArgs11);
//        Assert.assertNull(nodeCmd11);
    }
}