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
package org.bcia.julongchain.common.ledger.blockledger.file;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Ab;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.Map;

/**
 * File ledger 测试类
 *
 * @author sunzongyu
 * @date 2018/05/24
 * @company Dingxuan
 */
public class FileLedgerTest {
    static final String dir = "/tmp/julongchain/fileLedger";
    static FileLedgerFactory fileLedgerFactory;
    static Common.Block block;
    static ReadWriteBase fileLedger;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void beforeClass() throws Exception {
		//重置目录
		Utils.rmrf(dir);
		//重新生成fileLedgerFactory
		fileLedgerFactory = new FileLedgerFactory(dir);
		//生成fileLedger
		fileLedger = fileLedgerFactory.getOrCreate("myGroup");
		//提交创世区块
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		block = factory.getGenesisBlock("myGroup");
		fileLedger.append(block);
	}

    @Test
    public void testGetOrCreate() throws Exception{
    	fileLedger = fileLedgerFactory.getOrCreate("myGroup");
        Assert.assertNotNull(fileLedger);
    }

    @Test
    public void testAppend() throws Exception {
		long height = fileLedger.height();
		//正确用例
		block = Common.Block.newBuilder()
				.setHeader(Common.BlockHeader.newBuilder()
						.setNumber(height)
						.build())
				.setMetadata(Common.BlockMetadata.newBuilder()
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.build())
				.build();
		fileLedger.append(block);
		Assert.assertSame(++height, fileLedger.height());

		Map<String, File> fileRelativePath;
        fileRelativePath = IoUtil.getFileRelativePath(dir);
		Assert.assertNotSame(fileRelativePath.get("chains/myGroup/blockfile_000000").length(), (long) 0);
        //错误用例
		block = Common.Block.newBuilder()
				.setHeader(Common.BlockHeader.newBuilder()
						//区块号错误
						.setNumber(height + 1)
						.build())
				.setMetadata(Common.BlockMetadata.newBuilder()
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.addMetadata(ByteString.EMPTY)
						.build())
				.build();
		thrown.expect(LedgerException.class);
		fileLedger.append(block);
    }

    @Test
    public void testHeight() throws Exception{
		long height = fileLedger.height();
        block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(height)
                        .build())
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
        fileLedger.append(block);
        Assert.assertSame(fileLedger.height(), ++height);
    }

    @Test
    public void testIterator() throws Exception {
        IIterator itr = null;

        itr = fileLedger.iterator(Ab.SeekPosition.newBuilder().setOldest(Ab.SeekOldest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

        itr = fileLedger.iterator(Ab.SeekPosition.newBuilder().setNewest(Ab.SeekNewest.getDefaultInstance()).build());
        Assert.assertNotNull(itr);

		itr = fileLedger.iterator(Ab.SeekPosition.newBuilder().setSpecified(Ab.SeekSpecified.getDefaultInstance()).build());
		Assert.assertNotNull(itr);
    }

    @After
    public void after() throws Exception{}
}
