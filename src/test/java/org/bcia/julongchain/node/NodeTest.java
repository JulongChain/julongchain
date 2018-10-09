package org.bcia.julongchain.node;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.cmd.group.GroupCreateCmd;
import org.bcia.julongchain.node.cmd.group.GroupJoinCmd;
import org.bcia.julongchain.node.cmd.group.GroupListCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInstallCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInstantiateCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInvokeCmd;
import org.bcia.julongchain.node.cmd.sc.ContractQueryCmd;
import org.bcia.julongchain.node.cmd.server.ServerStartCmd;
import org.bcia.julongchain.node.cmd.server.ServerStatusCmd;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 节点对象单元测试
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 */
public class NodeTest extends BaseJunit4Test {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execCmd() throws ParseException, NodeException {
        //----------------------------------------正常用例集--------------------------------------------//
        Node node = Node.getInstance();

        //创建群组
        String[] caseArgs1 = new String[]{"group", "create", "-c", "localhost:7050", "-g", "mygroup1"};
        INodeCmd nodeCmd1 = node.execCmd(caseArgs1);
        Assert.assertThat(nodeCmd1, Matchers.instanceOf(GroupCreateCmd.class));

        //加入群组
        String[] caseArgs2 = new String[]{"group", "join", "-b", "/opt/BCIA/julongchain/mygroup1.block"};
        INodeCmd nodeCmd2 = node.execCmd(caseArgs2);
        Assert.assertThat(nodeCmd2, Matchers.instanceOf(GroupJoinCmd.class));

        //更新群组
//        String[] caseArgs3 = new String[]{"group", "update", "-c", "localhost:7050", "-g", "mygroup", "-f",
//                "/opt/BCIA/julongchain/group1.tx"};
//        INodeCmd nodeCmd3 = node.execCmd(caseArgs3);
//        Assert.assertThat(nodeCmd3, Matchers.instanceOf(GroupUpdateCmd.class));

        //列出群组
        String[] caseArgs4 = new String[]{"group", "list"};
        INodeCmd nodeCmd4 = node.execCmd(caseArgs4);
        Assert.assertThat(nodeCmd4, Matchers.instanceOf(GroupListCmd.class));

        //安装智能合约
        String[] caseArgs5 = new String[]{"contract", "install", "-n", "mycc", "-v", "1.0", "-p", "/root/julongchain/mycc_src"};
        INodeCmd nodeCmd5 = node.execCmd(caseArgs5);
        Assert.assertThat(nodeCmd5, Matchers.instanceOf(ContractInstallCmd.class));

        //实例化智能合约
        String[] caseArgs6 = new String[]{"contract", "instantiate", "-c", "localhost:7050", "-g", "mygroup1", "-n",
                "mycc",
                "-v", "1.0", "-ctor", "{'args':['init','a','100','b','200']}", "-P", "OR	('Org1MSP.member'," +
                "'Org2MSP.member')"};
        INodeCmd nodeCmd6 = node.execCmd(caseArgs6);
        Assert.assertThat(nodeCmd6, Matchers.instanceOf(ContractInstantiateCmd.class));

        //查询智能合约
        String[] caseArgs7 = new String[]{"contract", "query", "-g", "mygroup", "-n", "mycc", "-ctor",
                "{'args':['query','a']}"};
        INodeCmd nodeCmd7 = node.execCmd(caseArgs7);
        Assert.assertThat(nodeCmd7, Matchers.instanceOf(ContractQueryCmd.class));

        //调用智能合约
        String[] caseArgs8 = new String[]{"contract", "invoke", "-c", "localhost:7050", "-g", "mygroup", "-n", "mycc", "-ctor",
                "{'args':['invoke','a', 'b', '10']}"};
        INodeCmd nodeCmd8 = node.execCmd(caseArgs8);
        Assert.assertThat(nodeCmd8, Matchers.instanceOf(ContractInvokeCmd.class));

        //服务启动
        String[] caseArgs9 = new String[]{"server", "start"};
        INodeCmd nodeCmd9 = node.execCmd(caseArgs9);
        Assert.assertThat(nodeCmd9, Matchers.instanceOf(ServerStartCmd.class));

        //服务状态
        String[] caseArgs10 = new String[]{"server", "status"};
        INodeCmd nodeCmd10 = node.execCmd(caseArgs10);
        Assert.assertThat(nodeCmd10, Matchers.instanceOf(ServerStatusCmd.class));

        //----------------------------------------异常用例集--------------------------------------------//
        String[] caseArgs11 = null;
        INodeCmd nodeCmd11 = node.execCmd(caseArgs11);
        Assert.assertNull(nodeCmd11);

        String[] caseArgs12 = new String[]{"group"};
        INodeCmd nodeCmd12 = node.execCmd(caseArgs12);
        Assert.assertNull(nodeCmd12);

        String[] caseArgs13 = new String[]{"contract"};
        INodeCmd nodeCmd13 = node.execCmd(caseArgs13);
        Assert.assertNull(nodeCmd13);


    }

    @Test
    public void testMockCreateGroup() throws JulongChainException {
        Node.getInstance().mockInitialize();
        Node.getInstance().mockCreateGroup("myGroup");
    }
}