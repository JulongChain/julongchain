package org.bcia.julongchain.node.cmd.sc;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查询智能合约测试类
 *
 * @author zhouhui
 * @date 2018/4/13
 * @company Dingxuan
 * @deprecated
 */
public class ContractQueryCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ContractQueryCmd contractQueryCmd = new ContractQueryCmd(node);
        contractQueryCmd.execCmd(new String[]{"-g", "mygroup", "-n", "mycc", "-ctor",
                "{'args':['query','a']}"});
    }
}