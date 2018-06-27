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
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 完成节点安装智能合约的解析
 * node contract install -n mycc -l java -ctor '{"Args":["query","a"]}' -v 1.0 -p /home/javachian/contract_file
 * node contract install -n mycc -v 1.0 -p examples.smartcontract.java.smartcontract_example02.Example02.java
 * <p>
 * 名称 语言 执行信息 版本 路径
 *
 * @author zhouhui wanglei
 * @date 2018/2/24
 * @company Dingxuan
 */
public class ContractInstallCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

    //参数：smart Contract name
    private static final String ARG_SC_NAME = "n";
    //参数：language
    private static final String ARG_LANGUAGE = "l";
    //参数：version
    private static final String ARG_VERSION = "v";
    //参数：smartContract parameter
    private static final String ARG_SC_CTOR = "ctor";
    //参数：path
    private static final String ARG_PATH = "p";

    //参数
    private static final String KEY_ARGS = "args";

    public ContractInstallCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {

        Options options = new Options();
        options.addOption(ARG_SC_NAME, true, "Input contract name");
        options.addOption(ARG_LANGUAGE, true, "Input contract language");
        options.addOption(ARG_VERSION, true, "Input contract version");
        options.addOption(ARG_SC_CTOR, true, "Input contract parameter");
        options.addOption(ARG_PATH, true, "Input contract path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "unknown";

        //-----------------------------------解析参数值-------------------------------//
        //解析出合约名称
        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Smart Contract Name-----$" + scName);
        }
        //合约语言
        String scLanguage = null;
        if (cmd.hasOption(ARG_LANGUAGE)) {
            scLanguage = cmd.getOptionValue(ARG_LANGUAGE, defaultValue);
            log.info("Smart Contract language-----$" + scLanguage);
        }
        //合约版本
        String scVersion = null;
        if (cmd.hasOption(ARG_VERSION)) {
            scVersion = cmd.getOptionValue(ARG_VERSION, defaultValue);
            log.info("Smart Contract version-----$" + scVersion);
        }
        //合约具体执行参数
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

        //合约路径
        String scPath = null;
        if (cmd.hasOption(ARG_PATH)) {
            scPath = cmd.getOptionValue(ARG_PATH, defaultValue);
            log.info("Smart Contract path-----$" + scPath);
        }

        //TODO 待完成
        nodeSmartContract.install(scName, scVersion, scPath, scLanguage, input);
    }

}
