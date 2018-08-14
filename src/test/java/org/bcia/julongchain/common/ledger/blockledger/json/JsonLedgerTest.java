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
package org.bcia.julongchain.common.ledger.blockledger.json;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.blockledger.Util;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Ab;
import org.junit.*;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/28
 * @company Dingxuan
 */
public class JsonLedgerTest {
    static final String dir = "/tmp/julongchain/jsonLedger";
    static IFactory jsonLedgerFactory;
    static ReadWriteBase jsonLedger;
    static Common.Block block;
    @BeforeClass
    public static void before() throws Exception{
        //重置目录
        System.out.println(deleteDir(new File(dir)));
        //重新生成fileLedgerFactory
        jsonLedgerFactory = new JsonLedgerFactory(dir);
        //创建file ledger
        jsonLedger = jsonLedgerFactory.getOrCreate("myGroup");
        //提交创世区块
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		block = factory.getGenesisBlock("myGroup");
		jsonLedger.append(block);
    }

    @Test
    public void testGetOrCreate() throws Exception{
        Assert.assertNotNull(jsonLedger);
        Assert.assertTrue(new File(dir).exists());
    }

    @Test
    public void testGroupIDS() throws Exception{
        Assert.assertSame(jsonLedgerFactory.groupIDs().size(), 1);
        jsonLedgerFactory.getOrCreate("myGroup1");
        Assert.assertSame(jsonLedgerFactory.groupIDs().size(), 2);
        jsonLedgerFactory.getOrCreate("myGroup2");
        Assert.assertSame(jsonLedgerFactory.groupIDs().size(), 3);
    }

    @Test
    public void testAppend() throws Exception{
		long height = jsonLedger.height();

        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getHeader().toByteArray(), null)))
                        .setNumber(height)
                        .build())
				.setData(Common.BlockData.newBuilder()
						.addData(ByteString.copyFromUtf8("BlockData" + height))
						.build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        jsonLedger.append(block);
        Assert.assertSame(jsonLedger.height(), ++height);

        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getHeader().toByteArray(), null)))
                        .setNumber(height)
                        .build())
				.setData(Common.BlockData.newBuilder()
						.addData(ByteString.copyFromUtf8("BlockData" + height))
						.build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        jsonLedger.append(block);
        Assert.assertSame(jsonLedger.height(), ++height);
    }

    @Test
    public void testIterator() throws Exception{
        IIterator itr = null;

        itr = jsonLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        itr = jsonLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        try {
            jsonLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        } catch (LedgerException e) {
//            Assert.assertEquals(e, Util.NOT_FOUND_ERROR_ITERATOR);
        }
        itr = jsonLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
        Assert.assertNotNull(itr);
    }

    @Test
    public void testNext() throws Exception{
        JsonCursor cursor = new JsonCursor((JsonLedger) jsonLedger, jsonLedger.height() - 1);
        Assert.assertNotNull(cursor);

        QueryResult qr = cursor.next();
        Assert.assertNotNull(qr);
    }

//    @Test public void testReadyChain() throws Exception{
//        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
//        Common.Block block = factory.getGenesisBlock("testGroup");
//        jsonLedger.append(block);
//        Assert.assertNotNull(new JsonCursor((JsonLedger) jsonLedger, jsonLedger.height() - 1).readyChain());
//    }

    @After
    public void after() throws Exception{
	}

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
