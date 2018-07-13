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
package org.bcia.julongchain.node.cmd.group;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.NetAddress;
import org.bcia.julongchain.node.Node;

/**
 * 完成节点加入群组命令的解析
 * node group join -t 127.0.0.1:7051 -b $groupid.block
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
public class GroupJoinCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupJoinCmd.class);

    /**
     * Target地址(Node)
     */
    private static final String ARG_TARGET = "t";

    /**
     * 参数：创世区块的地址
     */
    private static final String ARG_BLOCK_PATH = "b";

    public GroupJoinCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");
        options.addOption(ARG_BLOCK_PATH, true, "Input genesis block path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }

        String blockPath = null;
        if (cmd.hasOption(ARG_BLOCK_PATH)) {
            blockPath = cmd.getOptionValue(ARG_BLOCK_PATH, defaultValue);
            log.info("BlockPath: " + blockPath);
        }

        if (StringUtils.isBlank(blockPath)) {
            log.error("BlockPath should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(targetAddress)) {
            log.error("TargetAddress should not be null, Please input it");
            return;
        }

        try {
            NetAddress targetNetAddress = new NetAddress(targetAddress);
            nodeGroup.joinGroup(targetNetAddress.getHost(), targetNetAddress.getPort(), blockPath);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
        }
    }
}
