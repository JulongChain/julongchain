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

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;

/**
 * 完成节点安装智能合约的解析
 * node contract install -n mycc -v 1.0 -p /home/javachian/contract_file
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
@Component
public class ContractInstallCmd extends AbstractNodeContractCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ContractInstallCmd.class);

    @Override
    public void execCmd(String[] args) throws ParseException {

        //TODO 待完成
        nodeSmartContract.install();
    }

}
