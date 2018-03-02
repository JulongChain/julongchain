/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.consenter.common.cmd.impl;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.cmd.IConsenterCmd;
import org.bcia.javachain.consenter.common.server.ConsenterServer;
import org.bcia.javachain.node.util.NodeCLIParser;

/**
 * @author zhangmingyang
 * @Date: 2018/3/2
 * @company Dingxuan
 */
public class StartCmd implements IConsenterCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(StartCmd.class);
    public ConsenterServer consenterServer;
    //private static final String START = "start";

    public StartCmd() {
        consenterServer=new ConsenterServer();
    }

    @Override
    public void execCmd(String[] args) throws ParseException {
        for (String str : args) {
            log.info("arg-----$" + str);
        }
        Options options = new Options();
        //需要支持peer node start, 无需参数
//        options.addOption(START, false, "start peer node");
//        //需要支持peer node start/peer node status
//        //options.addOption(CMD_STATUS, false, "display peer node status");
//
//        CommandLineParser parser = new NodeCLIParser();
//        CommandLine cmd = parser.parse(options, args);

            log.info("peer node start !!!");
            try {
                consenterServer.start();
                consenterServer.blockUntilShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }




    }
}
