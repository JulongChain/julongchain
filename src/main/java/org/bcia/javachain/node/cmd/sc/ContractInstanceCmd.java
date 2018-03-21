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
package org.bcia.javachain.node.cmd.sc;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;

/**
 * 完成节点实例化智能合约的解析
 * node contract instantiate -c localhost:7050 -g $group_id -n mycc -v 1.0
 * -content '{"Args":["init","a","100","b","200"]}' -P "OR	('Org1MSP.member','Org2MSP.member')"
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
@Component
public class ContractInstanceCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstanceCmd.class);

    //参数：consenter地址
    private static final String ARG_CONSENTER = "c";
    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    //参数：智能合约的名称
    private static final String ARG_SC_NAME = "n";
    //参数：智能合约的版本
    private static final String ARG_SC_VERSION = "v";
    //参数：内容
    private static final String ARG_CONTENT = "content";
    //参数：背书策略
    private static final String ARG_POLICY = "policy";

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {

        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input smart contract's name");
        options.addOption(ARG_SC_VERSION, true, "Input smart contract's version");
        options.addOption(ARG_CONTENT, true, "Input content");
        options.addOption(ARG_POLICY, true, "policy");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter-----$" + consenter);
        }

        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId-----$" + groupId);
        }

        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("scName-----$" + scName);
        }

        String scVersion = null;
        if (cmd.hasOption(ARG_SC_VERSION)) {
            scVersion = cmd.getOptionValue(ARG_SC_VERSION, defaultValue);
            log.info("scVersion-----$" + scVersion);
        }

        String content = null;
        if (cmd.hasOption(ARG_CONTENT)) {
            content = cmd.getOptionValue(ARG_CONTENT, defaultValue);
            log.info("content-----$" + content);
        }

        String policy = null;
        if (cmd.hasOption(ARG_POLICY)) {
            policy = cmd.getOptionValue(ARG_POLICY, defaultValue);
            log.info("policy-----$" + policy);
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

        if (StringUtils.isBlank(scVersion)) {
            log.error("Smart contract's version should not be null, Please input it");
            return;
        }

        String[] ipAndPort = consenter.split(":");
        if (ipAndPort.length <= 1) {
            log.error("Consenter is not valid");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(ipAndPort[1]);
        } catch (NumberFormatException ex) {
            log.error("Consenter's port is not valid");
            return;
        }

        nodeSmartContract.instantiate();
    }

}
