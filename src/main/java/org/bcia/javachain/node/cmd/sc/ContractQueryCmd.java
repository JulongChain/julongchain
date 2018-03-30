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
 * 完成节点查询智能合约的解析
 * node contract query -g $group_id -n mycc -ctor '{"Args":["query","a"]}'
 *
 * @author zhouhui
 * @date 2018/3/16
 * @company Dingxuan
 */
@Component
public class ContractQueryCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractQueryCmd.class);

    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    //参数：智能合约的名称
    private static final String ARG_SC_NAME = "n";
    //参数：解析出查询主体
    private static final String ARG_CTOR = "ctor";

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {

        Options options = new Options();
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_SC_NAME, true, "Input smart contract id");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        //-----------------------------------解析参数值-------------------------------//
        //解析出群组ID
        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId-----$" + groupId);
        }

        //解析出智能合约名称
        String scName = null;
        if (cmd.hasOption(ARG_SC_NAME)) {
            scName = cmd.getOptionValue(ARG_SC_NAME, defaultValue);
            log.info("Smart contract id-----$" + scName);
        }

        //解析出智能合约执行参数
        String ctor = null;
        if (cmd.hasOption(ARG_CTOR)) {
            ctor = cmd.getOptionValue(ARG_CTOR, defaultValue);
            log.info("ctor-----$" + ctor);
        }

        //-----------------------------------校验入参--------------------------------//
        if (StringUtils.isBlank(groupId)) {
            log.error("groupId should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(scName)) {
            log.error("smartContractName should not be null, Please input it");
            return;
        }

        //-----------------------------------默认值--------------------------------//
        //TODO:从-E中获取
        String essc = null;
        if (StringUtils.isBlank(essc)) {
            essc = "escc";
        }

        //TODO:从-V中获取
        String vssc = null;
        if (StringUtils.isBlank(vssc)) {
            essc = "vssc";
        }

        //TODO:从-P中获取
        String policy = null;
        if (StringUtils.isNotBlank(policy)) {
            //TODO:有一大堆逻辑
        }

        //TODO:从-collections-config中获取
        String collectionsConfigFile = null;
        if (StringUtils.isNotBlank(collectionsConfigFile)) {
            //TODO:有一大堆逻辑
        }

        //TODO:从-ctor中获取
        String ctorJSON = null;
        if (StringUtils.isNotBlank(ctorJSON)) {
            //TODO:有一大堆逻辑


        }

        nodeSmartContract.query(groupId, scName, ctor);
    }

}
