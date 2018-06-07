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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.json.JsonLedger;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.HistoryDBHelper;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/02
 * @company Dingxuan
 */
public class LedgerTest {
    public static final String AKSJDLAD = "aksjdlad";
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    @Test
    public void historyDBTest() throws LedgerException {
        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getHistoryLevelDBPath());
        for (int i = 0; i < 100; i++) {
            String ns = "ns" + i;
            String key = "key" + i;
            long blockNum = i * 2;
            long tranNum = i * 10;
            byte[] empty = new byte[]{};
            provider.put(HistoryDBHelper.constructCompositeHistoryKey(ns, key, blockNum, tranNum), empty, true);
        }
    }

    @Test
    public void getKVFromLevelDB() throws Throwable {
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getIndexPath());
        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getStateLevelDBPath());
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getHistoryLevelDBPath());
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getLedgerProviderPath());
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getPvtDataStorePath());
//        for (int i = 0; i < 100; i++) {
//            Height height = new Height();
//            height.setTxNum((long) i);
//            height.setBlockNum((long) i * 10);
//            byte[] value = height.toBytes();
//            provider.put(ArrayUtils.addAll(ArrayUtils.addAll(("ns" + i).getBytes(), COMPOSITE_KEY_SEP), ("key" + i).getBytes()), value, true);
//        }
        Iterator<Map.Entry<byte[], byte[]>> itr =  provider.getIterator(null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getTxNum());
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getBlockNum());
//            System.out.println(new String(entry.getValue()));
            soutBytes(entry.getKey());
            System.out.println(new String(entry.getKey()));
//            soutBytes(entry.getValue());
//            soutBytes(entry.getKey());
//            soutBytes(entry.getValue());
            System.out.println("_____________________________________");
        }

    }

    @Test
    public void getValuesFromFS() throws Exception {
        File file = new File(LedgerConfig.getBlockStorePath() + "/chains/myGroup/blockfile_000000");
        FileInputStream reader = new FileInputStream(file);
        System.out.println(file.length());
        byte[] bytes = new byte[(int) file.length()];
        reader.read(bytes);
        soutBytes(bytes);
        byte[] blockByte = new byte[bytes.length - 8];
    }

    public static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
            if (i++ % 10 == 9) {
                System.out.println();
                System.out.println(i);
            }
        }
        System.out.println();
    }
}
