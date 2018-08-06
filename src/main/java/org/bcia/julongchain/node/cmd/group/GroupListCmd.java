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
import org.bcia.julongchain.core.ssc.cssc.CSSC;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.node.Query;

import java.util.List;


/**
 * 完成节点查看群组列表命令的解析,List命令无参数
 * node group list -t 127.0.0.1:7051
 *
 * @author wanglei zhouhui
 * @date 2018/3/22
 * @company Dingxuan
 */
public class GroupListCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupListCmd.class);

    /**
     * Target地址(Node)
     */
    private static final String ARG_TARGET = "t";

    public GroupListCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }

        List<Query.GroupInfo> groupsList = null;

        if (StringUtils.isBlank(targetAddress)) {
            log.info("TargetAddress is empty, use 127.0.0.1:7051");
            groupsList = nodeGroup.listGroups(CSSC.DEFAULT_HOST, CSSC.DEFAULT_PORT);
        } else {
            try {
                NetAddress targetNetAddress = new NetAddress(targetAddress);
                groupsList = nodeGroup.listGroups(targetNetAddress.getHost(), targetNetAddress.getPort());
            } catch (ValidateException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (groupsList != null) {
            int groupSize = groupsList.size();
            log.info("Group List size: " + groupSize);
            for (int i = 0; i < groupSize; i++) {
                String groupId = groupsList.get(i).getGroupId();
                log.info("Group List info: " + groupId);
            }
        } else {
            log.info("Group List size: 0");
        }
    }
}
