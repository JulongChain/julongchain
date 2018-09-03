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
package org.bcia.julongchain.tools.configtxgen.cmd;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.tools.configtxgen.ConfigtxGenConstant;

/**
 * 交易生成工具版本命令
 *
 * @author zhouhui
 * @date 2018/06/05
 * @company Dingxuan
 */
public class ConfigtxVersionCmd implements IConfigtxGenCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(ConfigtxVersionCmd.class);

    @Override
    public void execCmd(String[] args) throws ParseException, ConfigtxToolsException {
        log.info("ConfigtxGen version: " + ConfigtxGenConstant.CURRENT_VERSION);
    }
}
