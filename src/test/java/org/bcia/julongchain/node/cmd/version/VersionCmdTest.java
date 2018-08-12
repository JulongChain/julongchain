package org.bcia.julongchain.node.cmd.version;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 * @deprecated
 */
public class VersionCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        NodeVersionCmd versionCmd = new NodeVersionCmd(node);
        versionCmd.execCmd(new String[0]);
    }
}