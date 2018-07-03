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
package org.bcia.julongchain.core.ledger.leveldb;

import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.TxPvtData;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/06/05
 * @company Dingxuan
 */
public class KvLedgerTest {
    INodeLedger l;
	byte[] blockHashAtBlock1 = new byte[]{(byte) 90, (byte) 28, (byte) 92, (byte) -26, (byte) -23, (byte) -105, (byte) -103, (byte) 2, (byte) -127, (byte) -120, (byte) 69, (byte) -38, (byte) -29, (byte) -35, (byte) 85, (byte) 42, (byte) 81, (byte) 15, (byte) 82, (byte) 77, (byte) -18, (byte) 89, (byte) 46, (byte) -13, (byte) 16, (byte) 94, (byte) 0, (byte) -27, (byte) 86, (byte) 118, (byte) -123, (byte) 2};
    String txIDAtBlock1 = "567253d44b49608ca8f0fbb751892dfa200dc6218046132a6d5497b77ffe7de1";

    @Before
    public void before() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("myGroup");
    }

    @Test
    public void testGetTransactionByID() throws Exception{
		TransactionPackage.ProcessedTransaction tx = l.getTransactionByID(txIDAtBlock1);
		Assert.assertNotNull(tx.getTransactionEnvelope().getPayload());
		Assert.assertSame(0, tx.getValidationCode());
		tx = l.getTransactionByID("hahahah");
		Assert.assertNull(tx);
	}

    @Test
    public void testGetBlockByHash() throws Exception{
		Common.Block block = l.getBlockByHash(blockHashAtBlock1);
		Assert.assertSame((long) 1, block.getHeader().getNumber());
		Assert.assertNotNull(block);
		System.out.println(block);
		byte[] bytes = blockHashAtBlock1;
		bytes[0] = (byte) 0;
		block = l.getBlockByHash(bytes);
		Assert.assertNull(block);
	}

    @Test
    public void testGetBlockByTxID() throws Exception{
		Common.Block block = l.getBlockByTxID(txIDAtBlock1);
		Assert.assertNotNull(block);
		Assert.assertSame((long) 1, block.getHeader().getNumber());
		System.out.println(block);
		block = l.getBlockByTxID(txIDAtBlock1 + "1");
		Assert.assertNull(block);
    }

    @Test
    public void testGetTxValidationCodeByTxID() throws Exception {
		TransactionPackage.TxValidationCode txValidationCode = l.getTxValidationCodeByTxID(txIDAtBlock1);
		Assert.assertSame(TransactionPackage.TxValidationCode.VALID, txValidationCode);
		txValidationCode = l.getTxValidationCodeByTxID(txIDAtBlock1 + "1");
		Assert.assertNull(txValidationCode);
	}

	/*-------------------------------------------------------
	pvtdata暂时没有...
    @Test
    public void testGetPvtDataAndBlockByNum() throws Exception{
		BlockAndPvtData bap = l.getPvtDataAndBlockByNum(1, null);
		Common.Block block = bap.getBlock();
		Assert.assertEquals(block, l.getBlockByNumber(1));
		for (int i = 0; i < 6; i++) {
			TxPvtData txPvtData = bap.getBlockPvtData().get(((long) i));
			Assert.assertSame(txPvtData.getSeqInBlock(), (long) i);
		}
	}

    @Test
    public void testGetPvtDataByNum() throws Exception {
        List<TxPvtData> pvtDataByNum = l.getPvtDataByNum(0, null);
		for (int i = 0; i < 6; i++) {
			TxPvtData txPvtData = pvtDataByNum.get(i);
			Assert.assertSame(txPvtData.getSeqInBlock(), (long) i);
		}
	}
	-------------------------------------------------------*/
}
