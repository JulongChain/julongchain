/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.protos.common.Common;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LedgerManagerTest {
    INodeLedger l = null;
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Test
    public void createLedger() throws Exception {
//        deleteDir(new File(Config.getPath()));
        long before = System.currentTimeMillis();
        LedgerManager.initialize(null);
        Common.Block block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(0)
                        .setDataHash(ByteString.copyFromUtf8("DataHash"))
                        .setPreviousHash(ByteString.copyFromUtf8("PreviousHash"))
                        .build())
                .setData(Common.BlockData.newBuilder()
                        .addData(Common.Envelope.newBuilder()
                                .setPayload(Common.Payload.getDefaultInstance().toByteString())
                                .build().toByteString())
                        .addData(Common.Envelope.newBuilder()
                                .setPayload(Common.Payload.getDefaultInstance().toByteString())
                                .build().toByteString())
                        .build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
//        l = LedgerManager.createLedger(block);
        l = LedgerManager.openLedger("testGroup");
        List<String> list = LedgerManager.getLedgerIDs();
        list.forEach((s) -> {
            System.out.println(s);
        });
    }

    @Test
    public void newTxSimulator() throws Exception {
        LedgerManager.initialize(null);
        String groupId = BlockUtils.getGroupIDFromBlock(null);
        l = LedgerManager.openLedger(groupId);
        ITxSimulator txSimulator = l.newTxSimulator(groupId);
        for (int i = 0; i < 100; i++) {
            System.out.println(new String(txSimulator.getState("ns" + i, "keys" + i)));
        }
    }

    @Test
    public void openLedger() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(BlockUtils.getGroupIDFromBlock(null));
    }
}
