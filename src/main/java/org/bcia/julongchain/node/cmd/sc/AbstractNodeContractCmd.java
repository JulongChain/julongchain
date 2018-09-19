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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.entity.NodeSmartContract;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 节点合约命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public abstract class AbstractNodeContractCmd implements INodeCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(AbstractNodeContractCmd.class);

    protected Node node;

    protected NodeSmartContract nodeSmartContract;

    /**
     * 参数
     */
    private static final String KEY_ARGS = "args";

    public AbstractNodeContractCmd(Node node) {
        this.node = node;

        nodeSmartContract = new NodeSmartContract(node);
    }

    @Override
    public abstract void execCmd(String[] args) throws ParseException, NodeException;

    /**
     * 获取智能合约输入对象
     *
     * @param cmd
     * @param option
     * @param defaultText 默认文本
     * @return
     */
    protected SmartContractPackage.SmartContractInput getSmartContractInput(CommandLine cmd, String option, String
            defaultText) {
        SmartContractPackage.SmartContractInput input = null;

        if (cmd.hasOption(option)) {
            String inputStr = cmd.getOptionValue(option, defaultText);
            log.info("InputStr: " + inputStr);
            JSONObject inputJson = JSONObject.parseObject(inputStr);

            SmartContractPackage.SmartContractInput.Builder inputBuilder =
                    SmartContractPackage.SmartContractInput.newBuilder();

            JSONArray argsJSONArray = inputJson.getJSONArray(KEY_ARGS);
            for (int i = 0; i < argsJSONArray.size(); i++) {
                inputBuilder.addArgs(ByteString.copyFrom(argsJSONArray.getString(i).getBytes()));
            }

            input = inputBuilder.build();
            //打印一下参数，检查是否跟预期一致
            for (int i = 0; i < input.getArgsCount(); i++) {
                log.info("Input.getArg: " + input.getArgs(i).toStringUtf8());
            }

            return input;
        }

        return null;
    }
}
