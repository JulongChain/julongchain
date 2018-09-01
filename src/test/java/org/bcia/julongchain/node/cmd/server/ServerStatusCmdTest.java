package org.bcia.julongchain.node.cmd.server;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务器状态查询测试类
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 * @deprecated
 */
public class ServerStatusCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ServerStatusCmd serverStatusCmd = new ServerStatusCmd(node);
        serverStatusCmd.execCmd(new String[0]);
    }
}