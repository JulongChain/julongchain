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
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.node.Node;

/**
 * 完成节点创建群组命令的解析
 * node group create -c localhost:7050 -g mygroup
 * node group create -c localhost:7050 -g mygroup -f /home/julongchain/group.tx
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
public class GroupCreateCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupCreateCmd.class);

    /**
     * consenter地址
     */
    private static final String ARG_CONSENTER = "c";
    /**
     * groupId
     */
    private static final String ARG_GROUP_ID = "g";
    /**
     * group配置文件路径
     */
    private static final String ARG_FILE_PATH = "f";

    public GroupCreateCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_FILE_PATH, true, "Input group config file path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter: " + consenter);
        }

        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId: " + groupId);
        }

        String groupConfigFile = null;
        if (cmd.hasOption(ARG_FILE_PATH)) {
            groupConfigFile = cmd.getOptionValue(ARG_FILE_PATH, defaultValue);
            log.info("GroupId config File: " + groupConfigFile);
        }

        if (StringUtils.isBlank(groupId)) {
            log.error("GroupId should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(consenter)) {
            log.error("Consenter should not be null, Please input it");
            return;
        }

        String[] consenterHostPort = consenter.split(":");
        if (consenterHostPort.length <= 1) {
            log.error("Consenter is not valid");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(consenterHostPort[1]);
        } catch (NumberFormatException ex) {
            log.error("Consenter's port is not valid");
            return;
        }

        nodeGroup.createGroup(consenterHostPort[0], port, groupId, groupConfigFile);

        log.info("Send Group create success");
    }

}
