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
        TransactionPackage.ProcessedTransaction tx = l.getTransactionByID("1");
        Assert.assertNotNull(tx);
        tx = l.getTransactionByID("2");
        Assert.assertNotNull(tx);
    }

    @Test
    public void testGetBlockByHash() throws Exception{
        Common.Block block = null;
        block = l.getBlockByNumber(0);
        block = l.getBlockByHash(block.getHeader().getDataHash().toByteArray());
        Assert.assertSame(block.getHeader().getNumber(), ((long) 0));
        block = l.getBlockByNumber(1);
        block = l.getBlockByHash(block.getHeader().getDataHash().toByteArray());
        Assert.assertSame(block.getHeader().getNumber(), (long) 1);
    }

    @Test
    public void testGetBlockByTxID() throws Exception{
        Common.Block block = null;
        block = l.getBlockByTxID("1");
        Assert.assertSame(block.getHeader().getNumber(), (long) 1);
    }

    @Test
    public void testGetTxValidationCodeByTxID() throws Exception {
        TransactionPackage.TxValidationCode code = l.getTxValidationCodeByTxID("1");
        Assert.assertSame(code.getNumber(), 0);
    }

    @Test
    public void testGetPvtDataAndBlockByNum() throws Exception{
        BlockAndPvtData bap = l.getPvtDataAndBlockByNum(1, null);
        Common.Block block = bap.getBlock();
        TxPvtData txPvtData = bap.getBlockPvtData().get(((long) 0));
        Assert.assertEquals(block, l.getBlockByNumber(1));
        Assert.assertSame(txPvtData.getSeqInBlock(), (long) 0);
    }

    @Test
    public void testGetPvtDataByNum() throws Exception {
        List<TxPvtData> pvtDataByNum = l.getPvtDataByNum(0, null);
        TxPvtData txPvtData = pvtDataByNum.get(0);
        Assert.assertSame(txPvtData.getSeqInBlock(), (long) 0);
    }
}
