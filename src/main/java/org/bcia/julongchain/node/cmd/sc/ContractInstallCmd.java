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
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.NetAddress;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.node.Node;

/**
 * 完成节点安装智能合约的解析
 * node contract install -t 127.0.0.1:7051 -n mycc -v 1.0 -p /root/julongchain/mycc_src
 *
 * @author zhouhui wanglei
 * @date 2018/2/24
 * @company Dingxuan
 */
public class ContractInstallCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);
    /**
     * Target地址(Node)
     */
    private static final String ARG_TARGET = "t";
    /**
     * 参数：智能合约的名称
     */
    private static final String ARG_SC_NAME = "n";
    /**
     * 参数：智能合约的版本
     */
    private static final String ARG_SC_VERSION = "v";
    /**
     * 参数：智能合约的Path
     */
    private static final String ARG_PATH = "p";

    public ContractInstallCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");
        options.addOption(ARG_SC_NAME, true, "Input contract name");
        options.addOption(ARG_SC_VERSION, true, "Input contract version");
        options.addOption(ARG_PATH, true, "Input contract path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        //-----------------------------------解析参数值-------------------------------//
        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }
        //解析出合约名称
        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Smart Contract Name: " + scName);
        }
        //合约版本
        String scVersion = null;
        if (cmd.hasOption(ARG_SC_VERSION)) {
            scVersion = cmd.getOptionValue(ARG_SC_VERSION, defaultValue);
            log.info("Smart Contract version: " + scVersion);
        }

        //合约路径
        String scPath = null;
        if (cmd.hasOption(ARG_PATH)) {
            scPath = cmd.getOptionValue(ARG_PATH, defaultValue);
            log.info("Smart Contract path: " + scPath);
        }

        //-----------------------------------校验入参--------------------------------//
        if (StringUtils.isBlank(scName)) {
            log.error("SmartContract's name should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(scVersion)) {
            log.error("SmartContract's version should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(scPath)) {
            log.error("SmartContract's path should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(targetAddress)) {
            log.info("TargetAddress is empty, use 127.0.0.1:7051");
            nodeSmartContract.install(LSSC.DEFAULT_HOST, LSSC.DEFAULT_PORT, scName, scVersion, scPath);
        } else {
            try {
                NetAddress targetNetAddress = new NetAddress(targetAddress);
                nodeSmartContract.install(targetNetAddress.getHost(), targetNetAddress.getPort(), scName, scVersion,
                        scPath);
            } catch (ValidateException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
