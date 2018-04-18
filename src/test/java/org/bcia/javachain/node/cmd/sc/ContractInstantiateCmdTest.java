package org.bcia.javachain.node.cmd.sc;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/29
 * @company Dingxuan
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