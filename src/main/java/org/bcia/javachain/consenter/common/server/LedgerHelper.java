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
package org.bcia.javachain.consenter.common.server;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.common.ledger.blockledger.IFactory;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.javachain.common.ledger.blockledger.json.JsonLedgerFactory;
import org.bcia.javachain.common.ledger.blockledger.ram.RamLedgerFactory;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.localconfig.ConsenterConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class LedgerHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LedgerHelper.class);
    public static IFactory createLedgerFactroy(ConsenterConfig consenterConfig) throws LedgerException {
        IFactory lf = null;
        String ld;
        switch (consenterConfig.getGeneral().getLedgerType()){
            case "file":
                ld=consenterConfig.getFileLedger().getLocation();
                if(ld==""){
                    ld=createTempDir(consenterConfig.getFileLedger().getPrefix());
                }
                log.debug(String.format("Ledger dir:",ld));
                try {
                    lf=new FileLedgerFactory(ld);
                } catch (LedgerException e) {
                    e.printStackTrace();
                }
                createSubDir(ld, Config.CHAINS_DIR);
                break;
            case "json":
                ld=consenterConfig.getFileLedger().getLocation();
                if(ld==""){
                    ld=createTempDir(consenterConfig.getFileLedger().getPrefix());
                }
                log.debug(String.format("Ledger dir:",ld));
                lf=new JsonLedgerFactory(ld);
                break;
            case "ram":
                break;
                default:
                    //TODO fabric源码中传入conf.RAMLedger.HistorySize
                lf=new RamLedgerFactory(consenterConfig.getRamLedger().getHistorySize());
        }
        return lf;
    }

    public static String createTempDir(String dirPrefix){
        return "";
    }

    public static String createSubDir(String parentDirPath,String subDir){

        return "";
    }
}
