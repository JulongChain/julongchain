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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * node contract invoke -c consenter.example.com:7050 -g groupId -n mycc -l java -ctor '{"Args":["query","a"]}'
 * contract invoke -c 127.0.0.1:7050 -g myGroup -n mycc -l java -ctor "{'Args':['query','a']}"
 * consenter节点 群组名称 语言 执行信息
 *
 * @author wanglei zhouhui
 * @date 2018/03/14
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
    //参数
    private static final String KEY_ARGS = "args";

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

        String defaultValue = "unknown";
        //consenter信息
        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter-----$" + consenter);
        }
        //群组信息
        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId-----$" + groupId);
        }
        //解析出合约名称
        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Contract name-----$" + scName);
        }

        SmartContractPackage.SmartContractInput input = null;
        if (cmd.hasOption(ARG_SC_CTOR)) {
            String ctor = cmd.getOptionValue(ARG_SC_CTOR, defaultValue);
            log.info("ctor-----$" + ctor);
            JSONObject ctorJson = JSONObject.parseObject(ctor);

            SmartContractPackage.SmartContractInput.Builder inputBuilder = SmartContractPackage.SmartContractInput.newBuilder();

            JSONArray argsJSONArray = ctorJson.getJSONArray(KEY_ARGS);
            for (int i = 0; i < argsJSONArray.size(); i++) {
                inputBuilder.addArgs(ByteString.copyFrom(argsJSONArray.getString(i).getBytes()));
            }

            input = inputBuilder.build();
            //打印一下参数，检查是否跟预期一致
            for (int i = 0; i < input.getArgsCount(); i++) {
                log.info("input.getArg-----$" + input.getArgs(i).toStringUtf8());
            }
        }

        //合约语言
        String scLanguage = null;
        if (cmd.hasOption(ARG_LANGUAGE)) {
            scLanguage = cmd.getOptionValue(ARG_LANGUAGE, defaultValue);
            log.info("Smart Contract language-----$" + scLanguage);
        }

        //-----------------------------------校验入参--------------------------------//
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
        nodeSmartContract.invoke(ipAndPort[0], port, groupId, scName, scLanguage, input);
    }
}