package org.bcia.julongchain.node.cmd.group;

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
public class GroupListCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        GroupListCmd groupListCmd = new GroupListCmd(node);
        groupListCmd.execCmd(new String[0]);
    }
}