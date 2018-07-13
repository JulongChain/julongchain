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
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.NetAddress;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * node contract invoke -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -i "{'Args':['query','a']}"
 *
 * @author wanglei zhouhui
 * @date 2018/03/14
 * @company Dingxuan
 */
public class ContractInvokeCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

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
     * 参数：智能合约的Input
     */
    private static final String ARG_INPUT = "i";
    /**
     * 参数
     */
    private static final String KEY_ARGS = "args";

    public ContractInvokeCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();
        options.addOption(ARG_TARGET, true, "Input target address");
        options.addOption(ARG_CONSENTER, true, "Input consenter's IP and port");
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input contract name");
        options.addOption(ARG_INPUT, true, "Input contract parameter");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        String targetAddress = null;
        if (cmd.hasOption(ARG_TARGET)) {
            targetAddress = cmd.getOptionValue(ARG_TARGET, defaultValue);
            log.info("TargetAddress: " + targetAddress);
        }

        //consenter信息
        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("Consenter: " + consenter);
        }
        //群组信息
        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId: " + groupId);
        }
        //解析出合约名称
        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Contract name: " + scName);
        }

        SmartContractPackage.SmartContractInput input = null;
        if (cmd.hasOption(ARG_INPUT)) {
            String inputStr = cmd.getOptionValue(ARG_INPUT, defaultValue);
            log.info("InputStr: " + inputStr);
            JSONObject inputJson = JSONObject.parseObject(inputStr);

            SmartContractPackage.SmartContractInput.Builder inputBuilder = SmartContractPackage.SmartContractInput.newBuilder();

            JSONArray argsJSONArray = inputJson.getJSONArray(KEY_ARGS);
            for (int i = 0; i < argsJSONArray.size(); i++) {
                inputBuilder.addArgs(ByteString.copyFrom(argsJSONArray.getString(i).getBytes()));
            }

            input = inputBuilder.build();
            //打印一下参数，检查是否跟预期一致
            for (int i = 0; i < input.getArgsCount(); i++) {
                log.info("Input.getArg: " + input.getArgs(i).toStringUtf8());
            }
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

        //invoke smart contract
        if (StringUtils.isBlank(targetAddress)) {
            log.info("TargetAddress is empty, use 127.0.0.1:7051");
            nodeSmartContract.invoke(LSSC.DEFAULT_HOST, LSSC.DEFAULT_PORT, consenterHostPort[0], consenterPort,
                    groupId, scName, input);
        } else {
            try {
                NetAddress targetNetAddress = new NetAddress(targetAddress);
                nodeSmartContract.invoke(targetNetAddress.getHost(), targetNetAddress.getPort(), consenterHostPort[0]
                        , consenterPort, groupId, scName, input);
            } catch (ValidateException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}