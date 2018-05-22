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
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LedgerManagerTest {
    public static String groupID = "mytestgroupid2";
    public static byte[] b1 = null;
    public static byte[] b2 = null;
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

    @After
    public void after(){
        System.out.println(((KvLedger) l).getLedgerID());
    }

    @Test
    public void delete(){
        System.out.println(deleteDir(new File(Config.getPath())));
    }

    @Test
    public void createLedger() throws Exception {
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        System.out.println(deleteDir(new File(Config.getPath())));
        long before = System.currentTimeMillis();
        LedgerManager.initialize(null);
        Common.Block block = factory.getGenesisBlock("mytestgroupid1");
        l = LedgerManager.createLedger(block);
        block = factory.getGenesisBlock("mytestgroupid2");
        l = LedgerManager.createLedger(block);
        long after = System.currentTimeMillis();
        System.out.println("耗时： " + (after - before));
//        l = LedgerManager.createLedger(block);
//        List<String> list = LedgerManager.getLedgerIDs();
//        list.forEach((s) -> {
//            System.out.println(s);
//        });
//        ITxSimulator simulator = l.newTxSimulator("MyGroup");
//        simulator.
    }

    @Test
    public void openLedger() throws Exception{
        LedgerManager.initialize(null);
        String ledgerId1 = "mytestgroupid1";
        String ledgerId2 = "mytestgroupid2";
        l = LedgerManager.openLedger(ledgerId1);
        System.out.println(l.getTransactionByID("8"));
    }

    @Test
    public void commitBlock() throws Exception {
//        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
//        System.out.println(deleteDir(new File(Config.getPath())));
//        long before = System.currentTimeMillis();
//        LedgerManager.initialize(null);
//        Common.Block block = factory.getGenesisBlock("MyGroup");
//        l = LedgerManager.createLedger(block);
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("mytestgroupid1");
        long i = 0;
        while(true){
            if(l.getBlockByNumber(i) == null){
                break;
            }
            i++;
        }
        System.out.println("Start Block Number is " + i);
        long startTime = System.currentTimeMillis();
        Common.BlockData data = null;
        ByteString preHash = ByteString.copyFrom(new SM3().hash(l.getBlockByNumber(i - 1).getData().toByteArray()));
        for (int j = 0; j < 9; j++) {
            BlockAndPvtData bap = new BlockAndPvtData();

            Common.Envelope envelope = Common.Envelope.newBuilder()
                    .setPayload(Common.Payload.newBuilder()
                            .setHeader(Common.Header.newBuilder()
                                    .setGroupHeader(Common.GroupHeader.newBuilder()
                                            .setTxId(String.valueOf(j))
                                            .build().toByteString())
                                    .build())
                            .build().toByteString())
                    .build();
            System.out.println();
            System.out.println("Enter envelope " + String.valueOf(j) + " with txID " + j);
            System.out.println();
            Common.Envelope envelope2 = Common.Envelope.newBuilder()
                    .setPayload(Common.Payload.newBuilder()
                            .setHeader(Common.Header.newBuilder()
                                    .setGroupHeader(Common.GroupHeader.newBuilder()
                                            .setTxId(String.valueOf(j * 10))
                                            .build().toByteString())
                                    .build())
                            .build().toByteString())
                    .build();
            System.out.println();
            System.out.println("Enter envelope " + String.valueOf(j + 1) + " with txID " + j * 10);
            System.out.println();
            data = Common.BlockData.newBuilder()
                    .addData(envelope.toByteString())
                    .addData(envelope2.toByteString())
                    .build();

            bap.setBlock(Common.Block.newBuilder()
                    .setHeader(Common.BlockHeader.newBuilder()
                            .setNumber(i + j)
                            .setDataHash(ByteString.copyFrom(new SM3().hash(data.toByteArray())))
                            .setPreviousHash(preHash)
                            .build())
                    .setData(data)
                    .setMetadata(Common.BlockMetadata.newBuilder()
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .build())
                    .build());
            soutBytes(bap.getBlock().toByteArray());
            preHash = ByteString.copyFrom(new SM3().hash(data.toByteArray()));
            l.commitWithPvtData(bap);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时: " + String.valueOf(endTime - startTime) + "ms");
    }

    @Test
    public void showBlocks() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        for (int i = 0;; i++) {
            Common.Block block = l.getBlockByNumber(i);
            if(block == null){
                break;
            }
            System.out.println(block);
        }
    }

    @Test
    public void newTxSimulator() throws Exception {
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockchainInfo());
    }

    @Test
    public void getTxById()throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        Common.Payload payload = Common.Payload.parseFrom(l.getTransactionByID("4").getTransactionEnvelope().getPayload());
        Common.GroupHeader header = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        System.out.println(header);
        System.out.println(header.getTxId());
    }

    @Test
    public void getBlockByNumber() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockByNumber(3));
    }

    @Test
    public void getChainInfo() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockchainInfo());
    }

    @Test
    public void showfs() throws Exception{
        File file = new File(LedgerConfig.getBlockStorePath() + File.separator + "chains/MyGroup/blockfile000000");
        FileInputStream is = new FileInputStream(file);
        byte[] b = new byte[(int) file.length()];
        is.read(b);
        soutBytes(b);
//        byte[] b = new byte[70];
//        is.skip(46);
//        is.read(b);
//        System.out.println(b[0]);
//        Common.Envelope envelope = Common.Envelope.parseFrom(b);
//        System.out.println(envelope);
    }

    @Test
    public void getBlockByTxId() throws Exception {
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println("Result " + l.getBlockByTxID("80"));
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
    }
}
