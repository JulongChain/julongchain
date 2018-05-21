package org.bcia.javachain.node.cmd.group;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.server.ServerStartCmd;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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