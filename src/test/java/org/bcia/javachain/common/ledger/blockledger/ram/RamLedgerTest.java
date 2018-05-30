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
package org.bcia.javachain.common.ledger.blockledger.ram;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.blockledger.IFactory;
import org.bcia.javachain.common.ledger.blockledger.IIterator;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.ledger.blockledger.Util;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.csp.factory.CspManager;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.consenter.Ab;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/28
 * @company Dingxuan
 */
public class RamLedgerTest {
    static final String dir = "/tmp/javachain/ramLedger";
    IFactory ramLedgerFactory;
    ReadWriteBase ramLedger;

    @Before
    public void before() throws Exception{
        //重置目录
        System.out.println(deleteDir(new File(dir)));
        //重新生成fileLedgerFactory
        ramLedgerFactory = new RamLedgerFactory(10);
        //创建file ledger
        ramLedger = ramLedgerFactory.getOrCreate("myGroup");
    }

    @Test
    public void testGetOrCreate() throws Exception{
        Assert.assertNotNull(ramLedger);
        Assert.assertSame(ramLedger.height(), (long) 0);
        Assert.assertEquals(ramLedgerFactory.groupIDs().get(0), "myGroup");
    }

    @Test
    public void testGroupIDs() throws Exception{
        Assert.assertEquals(ramLedgerFactory.groupIDs().get(0), "myGroup");
        ramLedgerFactory.getOrCreate("myGroup");
        Assert.assertEquals(ramLedgerFactory.groupIDs().get(0), "myGroup");
        ramLedgerFactory.getOrCreate("myGroup1");
        Assert.assertEquals(ramLedgerFactory.groupIDs().get(1), "myGroup");
        ramLedgerFactory.getOrCreate("myGroup2");
        Assert.assertEquals(ramLedgerFactory.groupIDs().get(2), "myGroup");
    }

    @Test
    public void testAppend() throws Exception{
        Common.Block block = null;
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        block = factory.getGenesisBlock("myGroup");
        ramLedger.append(block);
        Assert.assertSame(ramLedger.height(), (long) 1);

        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getData().toByteArray(), null)))
                        .setNumber(1)
                        .build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        ramLedger.append(block);
        Assert.assertSame(ramLedger.height(), (long) 2);
    }

    @Test
    public void testIterator() throws Exception{
        IIterator itr = null;

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        try {
            ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        } catch (LedgerException e) {
            Assert.assertEquals(e, Util.NOT_FOUND_ERROR_ITERATOR);
        }
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        Common.Block block = factory.getGenesisBlock("myTestGroup");
        ramLedger.append(block);
        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getData().toByteArray(), null)))
                        .setNumber(1)
                        .build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        ramLedger.append(block);
        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        Assert.assertNotNull(itr);
    }

//    @Test
//    public void testReadyChain() throws Exception{
//        IIterator itr = null;
//        Channel<Object> channel = null;
//
//        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
//        channel = itr.readyChain();
//        Assert.assertNotNull(channel);
//
//        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
//        channel = itr.readyChain();
//        Assert.assertNotNull(channel);
//
//        try {
//            ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
//        } catch (LedgerException e) {
//            Assert.assertEquals(e, Util.NOT_FOUND_ERROR_ITERATOR);
//        }
//        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
//        Common.Block block = factory.getGenesisBlock("myTestGroup");
//        ramLedger.append(block);
//        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
//        channel = itr.readyChain();
//        Assert.assertNotNull(channel);
//
//        block = Common.Block.newBuilder()
//                .setHeader(Common.BlockHeader.newBuilder()
//                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getData().toByteArray(), null)))
//                        .setNumber(1)
//                        .build())
//                .setMetadata(Common.BlockMetadata.newBuilder()
//                        .addMetadata(ByteString.EMPTY)
//                        .addMetadata(ByteString.EMPTY)
//                        .addMetadata(ByteString.EMPTY)
//                        .addMetadata(ByteString.EMPTY)
//                        .build())
//                .build();
//        ramLedger.append(block);
//        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
//        channel = itr.readyChain();
//        Assert.assertNotNull(channel);
//    }

    @Test
    public void testNext() throws Exception{
        IIterator itr = null;
        Channel<Object> channel = null;

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
        itr.next();

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
        itr.next();

        try {
            ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        } catch (LedgerException e) {
            Assert.assertEquals(e, Util.NOT_FOUND_ERROR_ITERATOR);
        }
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        Common.Block block = factory.getGenesisBlock("myTestGroup");
        ramLedger.append(block);
        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        // TODO: 5/29/18 等待新区块
//        itr.next();

        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getData().toByteArray(), null)))
                        .setNumber(1)
                        .build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        ramLedger.append(block);
        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        itr.next();
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
