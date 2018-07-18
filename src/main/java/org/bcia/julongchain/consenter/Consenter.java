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
package org.bcia.julongchain.consenter;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.cmd.IConsenterCmd;
import org.bcia.julongchain.consenter.common.cmd.factory.ConsenterCmdFactory;
import org.bcia.julongchain.consenter.util.Constant;

import java.io.File;
import java.nio.file.Files;

/**
 * @author zhangmingyang
 * @Date: 2018/3/1 *
 * @company Dingxuan
 */
public class Consenter {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Consenter.class);
    private IConsenterCmd iConsenterCmd;

    public void execCmd(String[] args) {
        if (args.length <= 0) {
            log.warn("PolicyNode command need more args");
            return;
        }
        String command = args[0];
        if (args.length == 1 && Constant.VERSION.equalsIgnoreCase(command)) {
            log.info("Consentor version is V0.25!..");
            iConsenterCmd = ConsenterCmdFactory.getInstance(command);
            String[] arg = new String[]{command};
            try {
                iConsenterCmd.execCmd(arg);
            } catch (org.apache.commons.cli.ParseException e) {
                e.printStackTrace();
            }
        } else if (args.length == 1 && Constant.START.equalsIgnoreCase(command)) {
            iConsenterCmd = ConsenterCmdFactory.getInstance(command);
            String[] argment = new String[]{command};
            try {
                iConsenterCmd.execCmd(argment);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if (args.length == 1 && Constant.BENCHMARK.equalsIgnoreCase(command)) {
            iConsenterCmd = ConsenterCmdFactory.getInstance(command);
            String[] argment = new String[]{command};
            try {
                iConsenterCmd.execCmd(argment);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            log.info("args is error!");
        }
        return;
    }

    public static void main(String[] args) {
        Consenter consenter = new Consenter();
        consenter.execCmd(args);
    }

}

