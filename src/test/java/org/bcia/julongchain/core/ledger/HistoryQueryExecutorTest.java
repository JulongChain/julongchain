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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/31
 * @company Dingxuan
 */
public class HistoryQueryExecutorTest {
    String ns = "myGroup";
    IHistoryQueryExecutor queryExecutor;

    @Before
    public void before() throws Exception{
        LedgerManager.initialize(null);
        queryExecutor = LedgerManager.openLedger(ns).newHistoryQueryExecutor();
    }

    @Test
    public void testGetHistoryForKey() throws Exception{
        QueryResult qr = queryExecutor.getHistoryForKey(ns, "key").next();
        KvQueryResult.KeyModification result = (KvQueryResult.KeyModification) qr.getObj();
        Assert.assertEquals(result.getTxId(), "1");
    }

    @After
    public void after() throws Exception{

    }
}
