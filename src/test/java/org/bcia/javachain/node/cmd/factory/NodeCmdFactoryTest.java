package org.bcia.javachain.node.cmd.factory;

import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.group.GroupCreateCmd;
import org.bcia.javachain.node.cmd.group.GroupJoinCmd;
import org.bcia.javachain.node.cmd.group.GroupListCmd;
import org.bcia.javachain.node.cmd.group.GroupUpdateCmd;
import org.bcia.javachain.node.cmd.sc.ContractInstallCmd;
import org.bcia.javachain.node.cmd.sc.ContractInstantiateCmd;
import org.bcia.javachain.node.cmd.sc.ContractInvokeCmd;
import org.bcia.javachain.node.cmd.sc.ContractQueryCmd;
import org.bcia.javachain.node.cmd.server.ServerStartCmd;
import org.bcia.javachain.node.cmd.server.ServerStatusCmd;
import org.bcia.javachain.node.cmd.version.VersionCmd;
import org.bcia.javachain.node.entity.NodeVersion;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 节点命令工厂单元测试
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 */
public class NodeCmdFactoryTest extends BaseJunit4Test {
    @Autowired
    private Node node;

    @Test
    public void getInstance() {
        //----------------------------------------正常用例集--------------------------------------------//
        //创建群组命令
        INodeCmd nodeCmd1 = NodeCmdFactory.getInstance(node, "group", "create");
        Assert.assertThat(nodeCmd1, Matchers.instanceOf(GroupCreateCmd.class));

        //加入群组命令
        INodeCmd nodeCmd2 = NodeCmdFactory.getInstance(node, "group", "join");
        Assert.assertThat(nodeCmd2, Matchers.instanceOf(GroupJoinCmd.class));

        //更新群组命令
        INodeCmd nodeCmd3 = NodeCmdFactory.getInstance(node, "group", "update");
        Assert.assertThat(nodeCmd3, Matchers.instanceOf(GroupUpdateCmd.class));

        //列出群组命令
        INodeCmd nodeCmd4 = NodeCmdFactory.getInstance(node, "group", "list");
        Assert.assertThat(nodeCmd4, Matchers.instanceOf(GroupListCmd.class));

        //安装智能合约命令
        INodeCmd nodeCmd5 = NodeCmdFactory.getInstance(node, "contract", "install");
        Assert.assertThat(nodeCmd5, Matchers.instanceOf(ContractInstallCmd.class));

        //实例化智能合约命令
        INodeCmd nodeCmd6 = NodeCmdFactory.getInstance(node, "contract", "instantiate");
        Assert.assertThat(nodeCmd6, Matchers.instanceOf(ContractInstantiateCmd.class));

        //查询智能合约命令
        INodeCmd nodeCmd7 = NodeCmdFactory.getInstance(node, "contract", "query");
        Assert.assertThat(nodeCmd7, Matchers.instanceOf(ContractQueryCmd.class));

        //调用智能合约命令
        INodeCmd nodeCmd8 = NodeCmdFactory.getInstance(node, "contract", "invoke");
        Assert.assertThat(nodeCmd8, Matchers.instanceOf(ContractInvokeCmd.class));

        //服务启动命令
        INodeCmd nodeCmd9 = NodeCmdFactory.getInstance(node, "server", "start");
        Assert.assertThat(nodeCmd9, Matchers.instanceOf(ServerStartCmd.class));

        //服务状态命令
        INodeCmd nodeCmd10 = NodeCmdFactory.getInstance(node, "server", "status");
        Assert.assertThat(nodeCmd10, Matchers.instanceOf(ServerStatusCmd.class));

        //服务状态命令
        INodeCmd nodeCmd20 = NodeCmdFactory.getInstance(node, "version", null);
        Assert.assertThat(nodeCmd20, Matchers.instanceOf(VersionCmd.class));

        //----------------------------------------异常用例集--------------------------------------------//
        INodeCmd nodeCmd11 = NodeCmdFactory.getInstance(node, "group", null);
        Assert.assertNull(nodeCmd11);

        INodeCmd nodeCmd12 = NodeCmdFactory.getInstance(node, "contract", null);
        Assert.assertNull(nodeCmd12);

        INodeCmd nodeCmd13 = NodeCmdFactory.getInstance(node, "server", null);
        Assert.assertNull(nodeCmd13);
    }
}