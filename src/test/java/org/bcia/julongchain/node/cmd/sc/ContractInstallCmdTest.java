package org.bcia.julongchain.node.cmd.sc;

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
 */
public class ContractInstallCmdTest {

    @Autowired
    private Node node;

    @Test
    public void execCmd() throws ParseException, NodeException {
        ContractInstallCmd contractInstallCmd = new ContractInstallCmd(node);
        contractInstallCmd.execCmd(new String[]{"-n", "mycc", "-v", "1.0", "-p", "examples" +
                ".smartcontract.java.smartcontract_example02.Example02.java"});
    }
}