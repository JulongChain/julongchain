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


package org.bcia.javachain.common.tools.cryptogen;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.tools.cryptogen.cmd.CryptogenCmdFactory;
import org.bcia.javachain.common.tools.cryptogen.cmd.ICryptoGenCmd;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class Main {

    private static JavaChainLog log = JavaChainLogFactory.getLog(Main.class);

    public static void main(String[] args) throws JavaChainException {

        if (args.length == 0) {
            log.error("please input command");
            return;
        }

        ICryptoGenCmd cryptoGenCmd = CryptogenCmdFactory.getInstance(args[0]);

        if (cryptoGenCmd == null) {
            log.error(args[0] + " command not found");
            return;
        }

        String[] destArgs = new String[args.length - 1];
        System.arraycopy(args, args.length == 1 ? 0 : 1, destArgs, 0, destArgs.length);
        cryptoGenCmd.execCmd(destArgs);
    }

}
