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
 * 完成节点实例化智能合约的解析
 * node contract instantiate -c localhost:7050 -g $group_id -n mycc -v 1.0
 * -ctor "{'args':['init','a','100','b','200']}" -P "OR	('Org1MSP.member','Org2MSP.member')"
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
public class ContractInstantiateCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstantiateCmd.class);

    //参数：consenter地址
    private static final String ARG_CONSENTER = "c";
    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    //参数：智能合约的名称
    private static final String ARG_SC_NAME = "n";
    //参数：智能合约的版本
    private static final String ARG_SC_VERSION = "v";
    //参数：内容
    private static final String ARG_CTOR = "ctor";
    //参数：背书策略
    private static final String ARG_POLICY = "P";
    //参数
    private static final String KEY_ARGS = "args";

    public ContractInstantiateCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {

        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input smart contract's name");
        options.addOption(ARG_SC_VERSION, true, "Input smart contract's version");
        options.addOption(ARG_CTOR, true, "Input content");
        options.addOption(ARG_POLICY, true, "policy");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "unknown";

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

        SmartContractPackage.SmartContractInput input = null;
        if (cmd.hasOption(ARG_CTOR)) {
            String ctor = cmd.getOptionValue(ARG_CTOR, defaultValue);
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

        nodeSmartContract.instantiate(ipAndPort[0], port, groupId, scName, scVersion, input);
    }

}
