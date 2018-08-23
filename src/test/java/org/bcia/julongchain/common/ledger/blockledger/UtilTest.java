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
package org.bcia.julongchain.common.ledger.blockledger;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.julongchain.common.ledger.blockledger.json.JsonLedgerFactory;
import org.bcia.julongchain.common.ledger.blockledger.ram.RamLedgerFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.bcia.julongchain.common.ledger.util.Utils.*;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/29
 * @company Dingxuan
 */
public class UtilTest {
    static String dir;
    static String groupID;

    @Rule
	public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void before() throws Exception{
        dir = "/tmp/julongchain/util";
        groupID = "myGroup";
        //重置目录
		rmrf(dir);
    }

    @Test
    public void testCreateNextBlockUsingFile() throws Exception{
        IFactory factory = new FileLedgerFactory(dir);
        ReadWriteBase reader = factory.getOrCreate(groupID);
        //正确用例
		Common.Block block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
			add(Common.Envelope.newBuilder()
					.setPayload(Common.Payload.newBuilder()
							.setHeader(Common.Header
									.newBuilder()
									.setGroupHeader(Common.GroupHeader.newBuilder()
											.setType(Common.HeaderType.ENDORSER_TRANSACTION.getNumber())
											.setTxId("txID")
											.setVersion(1)
											.setGroupId("myGroup")
											.build().toByteString()))
							.build().toByteString())

					.build());
		}});
        Assert.assertNotNull(block);
        Assert.assertSame(block.getHeader().getNumber(), 0L);
        reader.append(block);

		block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
			add(Common.Envelope.newBuilder()
					.setPayload(Common.Payload.newBuilder()
							.setHeader(Common.Header
									.newBuilder()
									.setGroupHeader(Common.GroupHeader.newBuilder()
											.setType(Common.HeaderType.ENDORSER_TRANSACTION.getNumber())
											.setTxId("txID")
											.setVersion(1)
											.setGroupId("myGroup")
											.build().toByteString()))
							.build().toByteString())

					.build());
		}});
		Assert.assertNotNull(block);
		Assert.assertSame(block.getHeader().getNumber(), 1L);
    }

    @Test
    public void testGetBlockUsingFile() throws Exception{
		IFactory factory = new FileLedgerFactory(dir);
		ReadWriteBase reader = factory.getOrCreate(groupID);
		List<Common.Envelope> messages = new ArrayList<>();
		Common.Envelope envelope = Common.Envelope.newBuilder()
				.setPayload(Common.Payload.newBuilder()
						.setHeader(Common.Header.newBuilder()
								.setGroupHeader(Common.GroupHeader.newBuilder()
										.setTxId(String.valueOf("File Test"))
										.build().toByteString())
								.build())
						.build().toByteString())
				.build();
		messages.add(envelope);
		new Thread(() -> {
			try {
				while (true) {
					Thread.sleep(1001);
					reader.append(Util.createNextBlock(reader, messages));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		Common.Block block = null;
		block = Util.getBlock(reader, 0);
		assertSame(0L, block.getHeader().getNumber());
		System.out.println(block);
		block = Util.getBlock(reader, 1);
		assertSame(1L, block.getHeader().getNumber());
		System.out.println(block);
		block = Util.getBlock(reader, 2);
		assertSame(2L, block.getHeader().getNumber());
		System.out.println(block);
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
        Assert.assertSame(block.getHeader().getNumber(), 0L);
    }

    @Test
    public void testGetBlockUsingJson() throws Exception{
        IFactory factory = new JsonLedgerFactory(dir);
        ReadWriteBase reader = factory.getOrCreate(groupID);
		ArrayList<Common.Envelope> messages = new ArrayList<Common.Envelope>() {{
			add(Common.Envelope.getDefaultInstance());
		}};
        new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000L);
					Common.Block nextBlock = Util.createNextBlock(reader, messages);
					reader.append(nextBlock);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		Common.Block block = Util.getBlock(reader, 0);
		System.out.println(block);
		assertSame(0L, block.getHeader().getNumber());
		block = Util.getBlock(reader, 1);
		System.out.println(block);
		assertSame(1L, block.getHeader().getNumber());
		block = Util.getBlock(reader, 2);
		System.out.println(block);
		assertSame(2L, block.getHeader().getNumber());
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
        ReadWriteBase reader = factory.getOrCreate(groupID);
		ArrayList<Common.Envelope> messages = new ArrayList<Common.Envelope>() {{
			add(Common.Envelope.newBuilder()
                .setPayload(ByteString.copyFromUtf8("Test Ram"))
                .build());
		}};
        new Thread(() -> {
            try {
                Common.Block tmpBlock = null;
                while (true) {
                    Thread.sleep(500);
                    tmpBlock = Util.createNextBlock(reader, messages);
                    reader.append(tmpBlock);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
		Common.Block block = Util.getBlock(reader, 0);
		System.out.println(block);
		assertSame(0L, block.getHeader().getNumber());
		block = Util.getBlock(reader, 1);
		System.out.println(block);
		assertSame(1L, block.getHeader().getNumber());
		block = Util.getBlock(reader, 2);
		System.out.println(block);
		assertSame(2L, block.getHeader().getNumber());
    }
    @After
    public void after() throws Exception{}
}
