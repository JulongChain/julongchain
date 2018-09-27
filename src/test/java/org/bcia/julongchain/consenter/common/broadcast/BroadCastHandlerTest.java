package org.bcia.julongchain.consenter.common.broadcast;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.consenter.Consenter;
import org.bcia.julongchain.consenter.common.server.ConsenterServer;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.cmd.group.GroupCreateCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInvokeCmd;
import org.bcia.julongchain.node.entity.NodeGroup;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 消息处理测试类
 *
 * @author zhangmingyang
 * @date 2018/07/03
 * @company Dingxuan
 */
public class BroadCastHandlerTest {

    @Test
    public void setup(){

    }

    @Test
    public void processConfigMessage() throws NodeException {
        Node node = Node.getInstance();
        NodeGroup nodeGroup=new NodeGroup(node);
        nodeGroup.createGroup("localhost",7050,"testGroup",null);
    }

    @Test
    public void processNormalMessage() throws NodeException, ParseException {
        String[] args={"-c", "localhost:7050", "-g", "myGroup", "-n", "mycc", "-ctor",
                "{'args':['invoke','a', 'b', '10']}"};
        Node node = Node.getInstance();
        ContractInvokeCmd contractInvokeCmd=new ContractInvokeCmd(node);
        contractInvokeCmd.execCmd(args);

    }

}