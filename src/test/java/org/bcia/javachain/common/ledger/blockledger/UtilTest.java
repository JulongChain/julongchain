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
package org.bcia.javachain.common.ledger.blockledger;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedger;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.javachain.common.ledger.blockledger.json.JsonLedgerFactory;
import org.bcia.javachain.common.ledger.blockledger.ram.RamLedgerFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.consenter.Ab;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/29
 * @company Dingxuan
 */
public class UtilTest {
    String dir;
    String groupID;

    @Before
    public void before() throws Exception{
        dir = "/tmp/javachain/util";
        groupID = "myGroup";
        //重置目录
        System.out.println(deleteDir(new File(dir)));
    }

    @Test
    public void testCreateNextBlockUsingFile() throws Exception{
        IFactory factory = new FileLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        Common.Block block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
            add(Common.Envelope.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("My Group"))
                    .build());
        }});
        Assert.assertNotNull(block);
        Assert.assertSame(block.getHeader().getNumber(), (long) 0);
    }

    @Test
    public void testGetBlockUsingFile() throws Exception{
        IFactory factory = new FileLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        final Common.Block block = Util.createNextBlock(reader, null);
//        ((ReadWriteBase) reader).append(block);
        new Thread(() -> {
            try {
                Thread.sleep(1001);
                ((ReadWriteBase) reader).append(block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                IFactory factory1 = new FileLedgerFactory(dir);
                IReader reader1 = factory1.getOrCreate(groupID);
                Assert.assertEquals(Util.getBlock(reader1, 0), block);
            } catch (LedgerException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testCreateNextBlockUsingJson() throws Exception{
        IFactory factory = new JsonLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        Common.Block block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
            add(Common.Envelope.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("My Group"))
                    .build());
        }});
        Assert.assertNotNull(block);
        System.out.println(block);
        Assert.assertSame(block.getHeader().getNumber(), (long) 0);
    }

    @Test
    public void testGetBlockUsingJson() throws Exception{
        IFactory factory = new JsonLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        final Common.Block block = Util.createNextBlock(reader, null);
        new Thread(() -> {
            try {
                Thread.sleep(1002);
                ((ReadWriteBase) reader).append(block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                IFactory factory1 = new JsonLedgerFactory(dir);
                IReader reader1 = factory1.getOrCreate(groupID);
                Assert.assertEquals(Util.getBlock(reader1, 0), block);
            } catch (LedgerException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println(1);
    }

    @Test
    public void testCreateNextBlockUsingRam() throws Exception{
        IFactory factory = new RamLedgerFactory(10);
        IReader reader = factory.getOrCreate(groupID);
        Common.Block block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
            add(Common.Envelope.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("My Group"))
                    .build());
        }});
        Assert.assertNotNull(block);
        System.out.println(block);
        Assert.assertSame(block.getHeader().getNumber(), (long) 0);
    }

    @Test
    public void testGetBlockUsingRam() throws Exception{
        IFactory factory = new RamLedgerFactory(10);
        IReader reader = factory.getOrCreate(groupID);
        List<Common.Envelope> messages = new ArrayList<>();
        messages.add(Common.Envelope.newBuilder()
                .setPayload(ByteString.copyFromUtf8("Test Ram"))
                .build());
        Common.Block myGroup = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance()).getGenesisBlock("MyGroup");
        ((ReadWriteBase) reader).append(myGroup);
        new Thread(() -> {
            try {
                Common.Block tmpBlock = null;
                while (true) {
                    Thread.sleep(500);
                    tmpBlock = Util.createNextBlock(reader, messages);
                    ((ReadWriteBase) reader).append(tmpBlock);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                Common.Envelope envelope = null;
                Common.Envelope.parseFrom(Util.getBlock(reader, 0).getData().getData(0));
                envelope = Common.Envelope.parseFrom(Util.getBlock(reader, 1).getData().getData(0));
                Assert.assertEquals(envelope.getPayload().toStringUtf8(), "Test Ram");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println(1);
    }
    @After
    public void after() throws Exception{}

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void soutBytes(byte[] bytes) throws Exception{
        int i = 0;
        for (byte aByte : bytes) {
            i++;
            System.out.print(aByte + "\t");
            if(i > 30){
                System.out.println();
                i = 0;
            }
        }
    }
}
