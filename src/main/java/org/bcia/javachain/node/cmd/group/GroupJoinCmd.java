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
package org.bcia.javachain.node.cmd.group;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;

/**
 * 完成节点加入群组命令的解析
 * node group join -b $groupid.block
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
@Component
public class GroupJoinCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupJoinCmd.class);

    //参数：创世区块的地址
    private static final String ARG_BLOCK_PATH = "b";

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {

        Options options = new Options();
        options.addOption(ARG_BLOCK_PATH, true, "Imput genesis block path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String blockPath = null;
        if (cmd.hasOption(ARG_BLOCK_PATH)) {
            blockPath = cmd.getOptionValue(ARG_BLOCK_PATH, defaultValue);
            log.info("blockPath-----$" + blockPath);
        }

        if (StringUtils.isBlank(blockPath)) {
            log.error("blockPath should not be null, Please input it");
            return;
        }

        nodeGroup.joinGroup(blockPath);
    }

}
