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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.NetAddress;
import org.bcia.julongchain.core.ssc.qssc.QSSC;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.util.NodeConstant;
import org.bcia.julongchain.protos.common.Ledger;
import org.bouncycastle.util.encoders.Hex;


/**
 * 完成节点查看群组链结构命令的解析
 * node group info -t 127.0.0.1:7051 -g myGroup
 *
 * @author zhouhui
 * @date 2018/08/07
 * @company Dingxuan
 */
public class GroupInfoCmd extends AbstractNodeGroupCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(GroupInfoCmd.class);

    /**
     * Target地址(Node)
     */
    private static final String ARG_TARGET = "t";
    /**
     * groupId
     */
    private static final String ARG_GROUP_ID = "g";

    public GroupInfoCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");
        options.addOption(ARG_GROUP_ID, true, "Input group id");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }

        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId: " + groupId);
        }

        if (StringUtils.isBlank(groupId)) {
            log.error("GroupId should not be null, Please input it");
            return;
        }

        Ledger.BlockchainInfo blockchainInfo = null;

        if (StringUtils.isBlank(targetAddress)) {
            log.info("TargetAddress is empty, use 127.0.0.1:7051");
            blockchainInfo = nodeGroup.getGroupInfo(NodeConstant.DEFAULT_NODE_HOST, NodeConstant.DEFAULT_NODE_PORT,
                    groupId);
        } else {
            try {
                NetAddress targetNetAddress = new NetAddress(targetAddress);
                blockchainInfo = nodeGroup.getGroupInfo(targetNetAddress.getHost(), targetNetAddress.getPort(),
                        groupId);
            } catch (ValidateException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (blockchainInfo != null) {
            log.info("The height of the block chain is: "
                    + blockchainInfo.getHeight());
            log.info("The current block hash of the block chain is: "
                    + Hex.toHexString(blockchainInfo.getCurrentBlockHash().toByteArray()));
            log.info("The previous block hash of the block chain is: "
                    + Hex.toHexString(blockchainInfo.getPreviousBlockHash().toByteArray()));
        } else {
            log.info("Get blockchainInfo fail");
        }
    }
}
