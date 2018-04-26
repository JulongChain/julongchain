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
package org.bcia.javachain.core.ledger.kvledger.historydb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.HistoryLevelDB;
import org.bcia.javachain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class HistoryDBTest {

    IHistoryDB db;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Before
    public void before() throws LedgerException {
        db = new HistoryLevelDB();
    }

    @Test
    //TODO 添加BlockMetadata
    public void commit() throws Throwable{
        Common.Block block = Common.Block.newBuilder()
                //Header
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(1)
                        .build())
                //Metadata
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .build())
                .build();
        db.commit(block);
    }

    @Test
    public void getLastSavepoint() throws LedgerException {
        Height h = db.getLastSavepoint();
        System.out.println(h.getTxNum());
        System.out.println(h.getBlockNum());
        Assert.assertEquals(new Long(1000), h.getBlockNum());
        Assert.assertEquals(new Long(10000), h.getTxNum());
    }

    @Test
    public void shouldRecover() throws LedgerException{
//        Assert.assertFalse(db.shouldRecover(new Long(1000)));
//        Assert.assertTrue(db.shouldRecover(new Long(10000)));
    }

    @Test
    public void recoverPoint() throws LedgerException {
        Assert.assertEquals(new Long(1001), new Long(db.recoverPoint((long)1)));
    }


}
