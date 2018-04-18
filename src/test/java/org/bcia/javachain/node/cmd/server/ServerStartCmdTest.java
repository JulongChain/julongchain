package org.bcia.javachain.node.cmd.server;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 节点开启服务命令单元测试
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
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