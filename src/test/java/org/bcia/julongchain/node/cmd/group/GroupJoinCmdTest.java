package org.bcia.julongchain.node.cmd.group;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 加入群组测试类
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 * @deprecated
 */
public class GroupJoinCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        GroupJoinCmd groupJoinCmd = new GroupJoinCmd(node);
        groupJoinCmd.execCmd(new String[]{"-b", "/home/julongchain/mygroup.block"});
    }
}