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
import org.springframework.stereotype.Component;

/**
 * 完成节点安装智能合约的解析
 * node contract install -n mycc -v 1.0 -p /home/javachian/contract_file
 *
 * @author zhouhui wanglei
 * @date 2018/2/24
 * @company Dingxuan
 */
@Component
public class ContractInstallCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

    //参数：groupId
    private static final String ARG_PATH = "p";
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
        options.addOption(ARG_PATH, true, "Input contract path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String consenter = null;

        //TODO 待完成
        nodeSmartContract.install();
    }

}
