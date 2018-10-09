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
package org.bcia.julongchain.node.cmd.sc;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.NetAddress;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.common.util.NodeConstant;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 完成节点实例化智能合约的解析
 * node contract upgrade -t 127.0.0.1:7051 -c localhost:7050 -g $group_id -n mycc -v 1.1
 * -i "{'args':['init','a','100','b','200']}" -P "OR	('Org1MSP.member','Org2MSP.member')"
 *
 * @author zhouhui
 * @date 2018/10/01
 * @company Dingxuan
 */
public class ContractUpgradeCmd extends AbstractNodeContractCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(ContractUpgradeCmd.class);
    /**
     * Target地址(Node)
     */
    private static final String ARG_TARGET = "t";
    /**
     * 参数：Consenter地址
     */
    private static final String ARG_CONSENTER = "c";
    /**
     * 参数：groupId
     */
    private static final String ARG_GROUP_ID = "g";
    /**
     * 参数：智能合约的名称
     */
    private static final String ARG_SC_NAME = "n";
    /**
     * 参数：智能合约的版本
     */
    private static final String ARG_SC_VERSION = "v";
    /**
     * 参数：智能合约的Input
     */
    private static final String ARG_INPUT = "i";
    /**
     * 参数：背书策略
     */
    private static final String ARG_POLICY = "P";

    public ContractUpgradeCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input smart contract's name");
        options.addOption(ARG_SC_VERSION, true, "Input smart contract's version");
        options.addOption(ARG_INPUT, true, "Input content");
        options.addOption(ARG_POLICY, true, "Input policy");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }

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

        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("ScName: " + scName);
        }

        String scVersion = null;
        if (cmd.hasOption(ARG_SC_VERSION)) {
            scVersion = cmd.getOptionValue(ARG_SC_VERSION, defaultValue);
            log.info("ScVersion: " + scVersion);
        }

        //合约具体执行参数
        SmartContractPackage.SmartContractInput input = getSmartContractInput(cmd, ARG_INPUT, defaultValue);

        String policy = null;
        if (cmd.hasOption(ARG_POLICY)) {
            policy = cmd.getOptionValue(ARG_POLICY, defaultValue);
            log.info("Policy: " + policy);
        }

        if (StringUtils.isBlank(groupId)) {
            log.error("GroupId should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(consenter)) {
            log.error("Consenter should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(scName)) {
            log.error("Smart contract's name should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(scVersion)) {
            log.error("Smart contract's version should not be null, Please input it");
            return;
        }

        String[] consenterHostPort = consenter.split(":");
        if (consenterHostPort.length <= 1) {
            log.error("Consenter is not valid");
            return;
        }

        int consenterPort = 0;
        try {
            consenterPort = Integer.parseInt(consenterHostPort[1]);
        } catch (NumberFormatException ex) {
            log.error("Consenter's port is not valid");
            return;
        }

        if (StringUtils.isBlank(targetAddress)) {
            log.info("TargetAddress is empty, use 127.0.0.1:7051");
            nodeSmartContract.upgrade(NodeConstant.DEFAULT_NODE_HOST, NodeConstant.DEFAULT_NODE_PORT,
                    consenterHostPort[0], consenterPort, groupId, scName, scVersion, input);
        } else {
            try {
                NetAddress targetNetAddress = new NetAddress(targetAddress);
                nodeSmartContract.upgrade(targetNetAddress.getHost(), targetNetAddress.getPort(),
                        consenterHostPort[0], consenterPort, groupId, scName, scVersion, input);
            } catch (ValidateException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
