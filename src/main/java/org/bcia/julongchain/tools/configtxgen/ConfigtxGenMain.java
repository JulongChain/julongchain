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
package org.bcia.julongchain.tools.configtxgen;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.tools.configtxgen.cmd.ConfigtxGenCmd;
import org.bcia.julongchain.tools.configtxgen.cmd.IConfigtxGenCmd;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/06
 * @company Dingxuan
 */
public class ConfigtxGenMain {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConfigtxGenMain.class);

    public static void main(String[] args) {
        try {
            execCmd(args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 执行命令行
     *
     * @param args
     * @return
     * @throws ParseException
     * @throws ConfigtxToolsException
     */
    public static IConfigtxGenCmd execCmd(String[] args) throws ParseException, ConfigtxToolsException,
            ValidateException {
        log.info("ConfigtxGen Command Start");

        if (args.length <= 0) {
            log.warn("ConfigtxGen command need more args-----");
            return null;
        }

        IConfigtxGenCmd configtxGenCmd = new ConfigtxGenCmd();
        configtxGenCmd.execCmd(args);

        return configtxGenCmd;
    }
}
