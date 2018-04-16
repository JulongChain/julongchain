package org.bcia.javachain.node.cmd.version;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.cmd.server.ServerStartCmd;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 */
public class VersionCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        VersionCmd versionCmd = new VersionCmd(node);
        versionCmd.execCmd(new String[0]);
    }
}