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
package org.bcia.javachain.consenter.common.cmd.impl;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.cmd.IConsenterCmd;
import org.bcia.javachain.consenter.common.server.ConsenterServer;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/3/2
 * @company Dingxuan
 */
public class StartCmd implements IConsenterCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(StartCmd.class);
    public ConsenterServer consenterServer;

    public StartCmd() {
        consenterServer=new ConsenterServer();
    }

    @Override
    public void execCmd(String[] args) throws ParseException {
        for (String str : args) {
            log.info("arg-----$" + str);
        }
            try {
                new Thread() {
                    @Override
                    public void run() {
                       ConsenterServer server = new ConsenterServer();
                        try {
                            server.start();
                            server.blockUntilShutdown();
                        } catch (IOException ex) {
                            log.error(ex.getMessage(), ex);
                        } catch (InterruptedException ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                }.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
