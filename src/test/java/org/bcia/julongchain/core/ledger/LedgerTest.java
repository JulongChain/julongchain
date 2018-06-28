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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileReader;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.HistoryDBHelper;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
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
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getStateLevelDBPath());
        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getHistoryLevelDBPath());
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getLedgerProviderPath());
//        LevelDBProvider provider = new LevelDBProvider(LedgerConfig.getPvtDataStorePath());
//        for (int i = 0; i < 100; i++) {
//            Height height = new Height();
//            height.setTxNum((long) i);
//            height.setBlockNum((long) i * 10);
//            byte[] value = height.toBytes();
//            provider.put(ArrayUtils.addAll(ArrayUtils.addAll(("ns" + i).getBytes(), COMPOSITE_KEY_SEP), ("key" + i).getBytes()), value, true);
//        }
	    System.out.println(provider.getDBPath());
	    Iterator<Map.Entry<byte[], byte[]>> itr =  provider.getIterator(null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getTxNum());
//            System.out.println(Height.newHeightFromBytes(entry.getValue()).getBlockNum());
//            System.out.println(new String(entry.getValue()));
            soutBytes(entry.getKey());
            System.out.println(new String(entry.getKey()));
            System.out.println(new String(entry.getValue()));
//	        System.out.println(entry.getValue().length == 0);
//            soutBytes(entry.getValue());
//            soutBytes(entry.getKey());
//            soutBytes(entry.getValue());
            System.out.println("_____________________________________");
        }
    }

    @Test
    public void getValuesFromFS() throws Exception {
    	Map<String, String> map = new HashMap<>();
	    System.out.println(map.entrySet());

	    String filePath = LedgerConfig.getChainsPath() + "/chains/myGroup/blockfile_000000";
	    File file = new File(filePath);
	    System.out.println(filePath);
	    int len = 0;
    	int i = 0;
//    	File file = new File(filePath);

	    while (len < file.length()) {
		    BlockFileReader reader = new BlockFileReader(filePath);
		    byte[] blockLen = reader.read(len, 8);
		    len += 8;
		    long l = Util.bytesToLong(blockLen, 0, 8);
		    System.out.println("block" + i++ + " length : " + l);
		    byte[] blockBytes = reader.read(len, l);
		    len += l;
		    Common.Block block = Common.Block.parseFrom(blockBytes);
		    System.out.println(block);
	    }
//	    FileInputStream fis = new FileInputStream(file);
//	    byte[] b = new byte[(int) file.length()];
//	    fis.read(b);
//	    soutBytes(b);
    }

    @Test
    public void test() throws Exception {
    	String a = "123 234 345";
	    String[] split = a.split(" ");
	    System.out.println(split[split.length - 1]);
    }

    public static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
            if (i++ % 10 == 9) {
                System.out.println();
//                System.out.println(i);
            }
        }
        System.out.println();
    }
}
