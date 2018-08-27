package org.bcia.julongchain.node.cmd.server;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 节点开启服务命令单元测试
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 * @deprecated
 */
public class ServerStartCmdTest extends BaseJunit4Test {
    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ServerStartCmd serverStartCmd = new ServerStartCmd(node);
        serverStartCmd.execCmd(new String[0]);
    }
}