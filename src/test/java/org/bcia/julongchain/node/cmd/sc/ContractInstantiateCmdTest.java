package org.bcia.julongchain.node.cmd.sc;

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
 * @date 2018/3/29
 * @company Dingxuan
 * @deprecated
 */
public class ContractInstantiateCmdTest extends BaseJunit4Test {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ContractInstantiateCmd contractInstantiateCmd = new ContractInstantiateCmd(node);
        contractInstantiateCmd.execCmd(new String[]{"-c", "localhost:7050", "-g", "mygroup", "-n", "mycc",
                "-v", "1.0", "-ctor", "{'args':['init','a','100','b','200']}", "-P", "OR	('Org1MSP.member'," +
                "'Org2MSP.member')"});
    }
}