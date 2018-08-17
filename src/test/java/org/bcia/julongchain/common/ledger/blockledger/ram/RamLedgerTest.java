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
package org.bcia.julongchain.common.ledger.blockledger.ram;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Ab;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.bcia.julongchain.common.ledger.util.Utils.rmrf;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/28
 * @company Dingxuan
 */
public class RamLedgerTest {
    static final String dir = "/tmp/julongchain/ramLedger";
    static IFactory ramLedgerFactory;
    static ReadWriteBase ramLedger;
	static Common.Block block = null;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void before() throws Exception{
        //重置目录
		rmrf(dir);
        //重新生成fileLedgerFactory
        ramLedgerFactory = new RamLedgerFactory(10);
        //创建file ledger
        ramLedger = ramLedgerFactory.getOrCreate("myGroup");
        //提交创世区块
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		block = factory.getGenesisBlock("myGroup");
		ramLedger.append(block);
    }

    @Test
    public void testGetOrCreate() throws Exception{
        Assert.assertNotNull(ramLedger);
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
		long height = ramLedger.height();

		for (int i = 0; i < 15; i++) {
			block = Common.Block.newBuilder()
					.setHeader(Common.BlockHeader.newBuilder()
							.setPreviousHash(ByteString.copyFrom(CspManager.getDefaultCsp().hash(block.getHeader().toByteArray(), null)))
							.setNumber(height)
							.build())
					.setMetadata(Common.BlockMetadata.newBuilder()
							.addMetadata(ByteString.EMPTY)
							.addMetadata(ByteString.EMPTY)
							.addMetadata(ByteString.EMPTY)
							.addMetadata(ByteString.EMPTY)
							.build())
					.build();
			ramLedger.append(block);
			System.out.println(height);
			Assert.assertSame(ramLedger.height(), ++height);
		}
    }

    @Test
    public void testIterator() throws Exception{
		long height = ramLedger.height();
        IIterator itr = null;

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        itr = ramLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.newBuilder().setNumber(height / 2).build()).build());
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

    @After
    public void after() throws Exception{}
}
