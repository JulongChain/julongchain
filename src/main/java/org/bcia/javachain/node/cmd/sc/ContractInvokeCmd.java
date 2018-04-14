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
import org.bcia.javachain.node.Node;

/**
 * node contract invoke -c consenter.example.com:7050 -g groupId -n mycc -l java -ctor '{"Args":["query","a"]}'
 * consenter节点 群组 名称 语言 执行信息
 *
 * @author  wanglei
 * @date 18/3/14
 * @company Dingxuan
 */
public class ContractInvokeCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

    //参数：consenter地址
    private static final String ARG_CONSENTER = "c";
    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    //参数：smart Contract name
    private static final String ARG_SC_NAME = "n";
    //参数：smartContract parameter
    private static final String ARG_SC_CTOR = "ctor";
    //参数：language
    private static final String ARG_LANGUAGE = "l";


    //参数：超时时间
    private static final String ARG_TIMEOUT = "t";
    //参数：是否使用TLS传输
    private static final String ARG_USE_TLS = "tls";
    //参数：CA文件位置
    private static final String ARG_CA = "ca";

    public ContractInvokeCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input contract name");
        options.addOption(ARG_SC_CTOR, true, "Input contract parameter");
        options.addOption(ARG_LANGUAGE, true, "Input contract language");


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";
        //consenter 信息  ip:port
        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter-----$" + consenter);
        }
        //群组信息
        String groupId = null;
        if(cmd.hasOption(ARG_GROUP_ID)){
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("Grout ID-----$" + groupId);
        }
        //解析出合约名称
        String name = null;
        if(cmd.hasOption(ARG_SC_NAME)){
            name = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Contract name-----$" + name);
        }
        //合约具体执行参数
        String ctor = null;
        if(cmd.hasOption(ARG_SC_CTOR)){
            ctor = cmd.getOptionValue(ARG_SC_CTOR, defaultValue);
            log.info("Contract ctor-----$" + ctor);
        }
        //合约语言
        String scLanguage = null;
        if (cmd.hasOption(ARG_LANGUAGE)) {
            scLanguage = cmd.getOptionValue(ARG_LANGUAGE, defaultValue);
            log.info("Smart Contract language-----$" + scLanguage);
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

        //-----------------------------------参数校验--------------------------------//
        if (StringUtils.isBlank(groupId)) {
            log.error("groupId should not be null, Please input it");
            return;
        }
        if (StringUtils.isBlank(name)) {
            log.error("smart contract name should not be null, Please input it");
            return;
        }
        if (StringUtils.isBlank(ctor)) {
            log.error("smart contract ctor should not be null, Please input it");
            return;
        }

        //
        nodeSmartContract.invoke(ipAndPort[0], port, groupId, name, ctor, scLanguage);

    }

}

