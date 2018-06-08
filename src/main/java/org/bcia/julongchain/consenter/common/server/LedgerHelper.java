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
package org.bcia.julongchain.consenter.common.server;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.julongchain.common.ledger.blockledger.json.JsonLedgerFactory;
import org.bcia.julongchain.common.ledger.blockledger.ram.RamLedgerFactory;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.FileUtil;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        switch (consenterConfig.getGeneral().getLedgerType()) {
            case "file":
                ld = consenterConfig.getFileLedger().getLocation();
                if (ld == ""||ld==null) {
                    ld = createTempDir(consenterConfig.getFileLedger().getPrefix());
                }
                log.debug(String.format("Ledger dir:", ld));
                try {
                    lf = new FileLedgerFactory(ld);
                } catch (LedgerException e) {
                    e.printStackTrace();
                }
                createSubDir(ld, Config.CHAINS_DIR);
                break;
            case "json":
                ld = consenterConfig.getFileLedger().getLocation();
                if (ld == "") {
                    ld = createTempDir(consenterConfig.getFileLedger().getPrefix());
                }
                log.debug(String.format("Ledger dir:", ld));
                lf = new JsonLedgerFactory(ld);
                break;
            case "ram":
                break;
            default:
                lf = new RamLedgerFactory(consenterConfig.getRamLedger().getHistorySize());
        }
        return lf;
    }

    public static String createTempDir(String dirPrefix) {
        Path tempDirPath = null;
        String  testDir=null;
        try {
            tempDirPath = Files.createTempDirectory(null);
            testDir  = Paths.get(tempDirPath.toString()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // String dirPath = Paths.get(dirPrefix, "").toString();
        return testDir;
    }

    public static String createSubDir(String parentDirPath, String subDir) {
        String subDirPath = join(parentDirPath, subDir);
        FileUtil.mkdirAll(Paths.get(subDirPath));
        return subDirPath;
    }

    public static void main(String[] args) {
        String a = createSubDir("F:\\", "msp");
        System.out.println(a);
    }

    private static String join(String... itms){
        StringBuffer buffer = new StringBuffer("");
        for(String itm : itms){
            buffer.append(itm);
            buffer.append(File.separator);
        }
        String result = buffer.toString();
        return result.substring(0, result.length() - 1);
    }

}
