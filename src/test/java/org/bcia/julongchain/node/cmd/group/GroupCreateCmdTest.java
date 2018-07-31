package org.bcia.julongchain.node.cmd.group;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public class GroupCreateCmdTest extends BaseJunit4Test {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        GroupCreateCmd groupCreateCmd = new GroupCreateCmd(node);
        groupCreateCmd.execCmd(new String[]{"-c", "localhost:7050", "-g", "mygroup"});
    }
}