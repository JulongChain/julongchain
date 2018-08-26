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

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.deliver.DeliverDeliverHandler;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.consenter.common.broadcast.BroadCastBroadcastHandler;
import org.bcia.julongchain.consenter.common.cmd.IConsenterCmd;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfig;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory;
import org.bcia.julongchain.consenter.common.multigroup.Registrar;
import org.bcia.julongchain.consenter.common.server.ConsenterServer;
import org.bcia.julongchain.consenter.common.server.PreStart;
import org.bcia.julongchain.consenter.util.ConsenterConstants;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/3/2
 * @company Dingxuan
 */
public class StartCmd implements IConsenterCmd {
    private static JulongChainLog log = JulongChainLogFactory.getLog(StartCmd.class);
    private ConsenterServer consenterServer;

    public StartCmd() {
        Registrar registrar=null;
        ConsenterConfig consenterConfig=ConsenterConfigFactory.getConsenterConfig();
        try {
             registrar= new PreStart().initializeMultichannelRegistrar(consenterConfig,new LocalSigner());
        } catch (JulongChainException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DeliverDeliverHandler  deliverHandler=new DeliverDeliverHandler(registrar,consenterConfig.getGeneral().getAuthentication().get(ConsenterConstants.TIMEWONDW));

        consenterServer = new ConsenterServer(Integer.valueOf(consenterConfig.getGeneral().getListenPort()));

        BroadCastBroadcastHandler broadCastHandle = new BroadCastBroadcastHandler(registrar);

        consenterServer.bindBroadcastServer(broadCastHandle);

        consenterServer.bindDeverServer(deliverHandler);

    }

    @Override
    public void execCmd(String[] args) throws ParseException {
        for (String str : args) {
            log.info("Arg: " + str);
        }
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        consenterServer.start();
                        consenterServer.blockUntilShutdown();
                    } catch (IOException ex) {
                        log.error(ex.getMessage(), ex);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            }.start();
            Thread.sleep(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
