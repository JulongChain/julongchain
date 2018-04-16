package org.bcia.javachain.node.cmd.sc;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
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
public class ContractInvokeCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ContractInvokeCmd contractInvokeCmd = new ContractInvokeCmd(node);
        contractInvokeCmd.execCmd(new String[]{"-c", "localhost:7050", "-g", "mygroup", "-n", "mycc", "-ctor",
                "{'args':['invoke','a', 'b', '10']}"});
    }
}