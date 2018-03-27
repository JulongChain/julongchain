package org.bcia.javachain.node.cmd.group;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author
 * @date 18-3-27
 * @company Dingxuan
 */
public class GroupUpdateCmdTest {
    private GroupUpdateCmd groupUpdateCmd;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void TestExecCmd() throws ParseException {
        String[] caseArgs1 = new String[]{"-c", "localhost:7050", "-g", "mygroup", "-f", "filefile"};

        groupUpdateCmd.execCmd(caseArgs1);
    }
}