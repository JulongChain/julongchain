/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.cmd.impl;

import org.apache.commons.cli.*;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.consenter.common.cmd.IConsenterCmd;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfig;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory;
import org.bcia.julongchain.consenter.common.server.ConsenterServer;

/**
 * @author zhangmingyang
 * @Date: 2018/3/2
 * @company Dingxuan
 */
public class VersionCmd implements IConsenterCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(VersionCmd.class);
    public ConsenterServer consenterServer;
    private static final String VERSION = "version";

    public VersionCmd() {
        consenterServer = new ConsenterServer();
    }

    @Override
    public void execCmd(String[] args) throws ParseException {
        for (String str : args) {
            log.info("Arg: " + str);
        }
        ConsenterConfig consenterConfig= ConsenterConfigFactory.loadConsenterConfig();
        String consenterVersion=consenterConfig.getGeneral().getVersion();
        if (args[0].equalsIgnoreCase(VERSION)) {
            log.info(String.format("consnter version is %s",consenterVersion));
        }


    }
}
