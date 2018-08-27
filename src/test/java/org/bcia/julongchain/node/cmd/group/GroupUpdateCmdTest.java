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
public class GroupUpdateCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        GroupUpdateCmd groupUpdateCmd = new GroupUpdateCmd(node);
        groupUpdateCmd.execCmd(new String[]{"-c", "localhost:7050", "-g", "mygroup", "-f",
                "/home/julongchain/group1.tx"});
    }
}