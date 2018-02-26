/**
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.peer.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.peer.entity.PeerNode;
import org.bcia.javachain.peer.util.PeerCLIParser;

/**
 * Peer节点命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class PeerNodeCmd implements IPeerCmd {
	private static JavaChainLog log = JavaChainLogFactory.getLog(PeerNodeCmd.class);

	private PeerNode peerNode;

	private static final String CMD_START = "start";
	private static final String CMD_STATUS = "status";

    public PeerNodeCmd() {
        peerNode = new PeerNode();
    }

	@Override
	public void execCmd(String[] args) throws ParseException {

		for (String str : args) {
			log.info("arg-----$" + str);
		}

        Options options = new Options();
        //需要支持peer node start, 无需参数
        options.addOption(CMD_START, false, "start peer node");
        //需要支持peer node start/peer node status
        options.addOption(CMD_STATUS, false, "display peer node status");

        CommandLineParser parser = new PeerCLIParser();
        CommandLine cmd = parser.parse(options, args);

		String defaultValue = "UnKown";
        if (cmd.hasOption(CMD_START)) {
            log.info("peer node start !!!");

            peerNode.start();
        }

        if (cmd.hasOption(CMD_STATUS)) {
            log.info("peer node status!!!");

            peerNode.status();
        }
	}
}
