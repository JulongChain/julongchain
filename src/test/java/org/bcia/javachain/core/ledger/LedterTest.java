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

import com.google.protobuf.util.JsonFormat;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.json.JsonLedger;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.HistoryDBHelper;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.protos.common.Common;
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
public class LedterTest {
    public static final String AKSJDLAD = "aksjdlad";
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    @Test
    public void historyDBTest() throws LedgerException {
        LevelDBProvider provider = LevelDBProvider.newProvider(LedgerConfig.getHistoryLevelDBPath());
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
        LevelDBProvider provider = LevelDBProvider.newProvider("/home/bcia/javachain/ledgersData/chains/index");
//        LevelDBProvider provider = LevelDBProvider.newProvider(LedgerConfig.getHistoryLevelDBPath());
//        LevelDBProvider provider = LevelDBProvider.newProvider("/home/bcia/javachain/ledgersData/ledgerProvider");
//        LevelDBProvider provider = LevelDBProvider.newProvider("/home/bcia/javachain/ledgersData/stateLeveldb");
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
//            soutBytes(entry.getKey());
//            soutBytes(entry.getValue());
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getTxNum());
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getBlockNum());
//            System.out.println(new String(entry.getKey()));
//            System.out.println(new String(entry.getValue()));
            soutBytes(entry.getKey());
            soutBytes(entry.getValue());
            System.out.println("_____________________________________");
        }

    }

    @Test
    public void getValuesFromFS() throws Exception {
        File file = new File("/home/bcia/javachain/ledgersData/chains/chains/MyGroup/blockfile000000");
//        FileInputStream reader = new FileInputStream(file);
//        System.out.println(file.length());
//        byte[] bytes = new byte[(int) file.length()];
//        reader.read(bytes);
//        soutBytes(bytes);
//        byte[] blockByte = new byte[bytes.length - 8];
        System.out.println(file.length());
        String s = "chain_ajlkfjalkfj;alsjflkajsdlkfj";
        System.out.println(s.substring(JsonLedger.GROUP_DIRECTORY_FORMAT_STRING.length(), s.length()));
        System.out.println(64 * 1024 * 1024);
        System.out.println(Integer.MAX_VALUE);
    }

    public static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
        System.out.println();
    }
}
