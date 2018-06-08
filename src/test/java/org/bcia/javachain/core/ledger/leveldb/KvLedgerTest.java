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
package org.bcia.javachain.core.ledger.leveldb;

import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/06/05
 * @company Dingxuan
 */
public class KvLedgerTest {
    INodeLedger l;

    @Before
    public void before() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("myGroup");
    }

    @Test
    public void testGetTransactionByID() throws Exception{
		for (int i = 0; i < 6; i++) {
			TransactionPackage.ProcessedTransaction tx = l.getTransactionByID(String.valueOf(i));
			Assert.assertNotNull(tx.getTransactionEnvelope().getPayload());
			Assert.assertSame(tx.getValidationCode(), 0);
		}
	}

    @Test
    public void testGetBlockByHash() throws Exception{
        Common.Block block = null;
		for (int i = 0; i < 6; i++) {
			block = l.getBlockByNumber(1);
			block = l.getBlockByHash(block.getHeader().getDataHash().toByteArray());
			Assert.assertSame(block.getHeader().getNumber(), ((long) 1));
		}
    }

    @Test
    public void testGetBlockByTxID() throws Exception{
		Common.Block expected = l.getBlockByNumber(1);
        Common.Block block = null;
		for (int i = 0; i < 6; i++) {
			block = l.getBlockByTxID(String.valueOf(i));
			Assert.assertEquals(expected, block);
		}
    }

    @Test
    public void testGetTxValidationCodeByTxID() throws Exception {
		for (int i = 0; i < 6; i++) {
			TransactionPackage.TxValidationCode code = l.getTxValidationCodeByTxID(String.valueOf(i));
			Assert.assertSame(code.getNumber(), 0);
		}
    }

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
}
