/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.node.cmd.server;

import org.apache.commons.cli.*;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.node.Node;

/**
 * 完成节点开启服务命令的解析
 * node server start
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
public class ServerStartCmd extends AbstractNodeServerCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ServerStartCmd.class);

    public ServerStartCmd(Node node) {
        super(node);
    }

    //参数：开发模式
    private static final String ARG_DEVMODE = "dev";

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_DEVMODE, false, "dev mode");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        boolean devMode = false;
        if (cmd.hasOption(ARG_DEVMODE)) {
            devMode = true;
        }

        nodeServer.start(devMode);
    }

}
