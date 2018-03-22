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
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

/**
 * node contract invoke -c consenter.example.com:7050 -g groupId -n mycc -s '{"Args":["invoke","a","b","10"]}'
 *
 * @author  wanglei
 * @date 18-3-14
 * @company Dingxuan
 */
public class ContractInvokeCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

    //参数：consenter地址
    private static final String ARG_CONSENTER = "c";
    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    //参数：smartContract
    private static final String ARG_SMART_CONTRACT = "s";

    //参数：超时时间
    private static final String ARG_TIMEOUT = "t";
    //参数：是否使用TLS传输
    private static final String ARG_USE_TLS = "tls";
    //参数：CA文件位置
    private static final String ARG_CA = "ca";

    @Override
    public void execCmd(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        //options.addOption(ARG_FILE_PATH, true, "Input group config file path");
        options.addOption(ARG_SMART_CONTRACT, true, "Input smartCintract");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter-----$" + consenter);
        }

        String groupId = null;
        if(cmd.hasOption(ARG_GROUP_ID)){
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("Grout ID-----$" + groupId);
        }

        String contract = null;
        if(cmd.hasOption(ARG_SMART_CONTRACT)){
            contract = cmd.getOptionValue(ARG_SMART_CONTRACT, defaultValue);
            log.info("Contract-----$" + contract);
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
        //
        nodeSmartContract.invoke();

    }

}

