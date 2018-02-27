package org.bcia.javachain.node.cmd.channel;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChannelCreateCmdTest extends BaseJunit4Test {
    @Autowired
    private ChannelCreateCmd channelCreateCmd;

    @Test
    public void execCmd() throws ParseException {
        String[] caseArgs1 = new String[]{"-c","localhost:7050","-ci","mychannel","-f","filefile"};

        channelCreateCmd.execCmd(caseArgs1);
    }
}