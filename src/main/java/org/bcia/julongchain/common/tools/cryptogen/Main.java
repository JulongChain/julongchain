/**
 * Copyright BCIA. All Rights Reserved.
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


package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.cmd.CryptoGenCmdFactory;
import org.bcia.julongchain.common.tools.cryptogen.cmd.HelpCmd;
import org.bcia.julongchain.common.tools.cryptogen.cmd.ICryptoGenCmd;

/**
 * 辅助工具主入口
 *
 * @author chenhao, liuxifeng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class Main {

    private static JavaChainLog log = JavaChainLogFactory.getLog(Main.class);

    public static void main(String[] args) throws JavaChainException {
        if (args.length == 0) {
            new HelpCmd().execCmd(args);
            return;
        }

        // TODO CommandLine replace CryptoGenCmdFactory
        ICryptoGenCmd cryptoGenCmd = CryptoGenCmdFactory.getInstance(args[0]);
        if (cryptoGenCmd == null) {
            log.error(" expected command but got \"" + args[0] + "\", try --help");
            return;
        }
        cryptoGenCmd.execCmd(args);
    }
}
