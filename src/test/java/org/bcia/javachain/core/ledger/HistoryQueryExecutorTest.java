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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.junit.After;
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
    String ns = "Default";
    IHistoryQueryExecutor queryExecutor;

    @Before
    public void before() throws Exception{
        LedgerManager.initialize(null);
        queryExecutor = LedgerManager.openLedger(ns).newHistoryQueryExecutor();
    }

    @Test
    public void testGetHistoryForKey() throws Exception{
        QueryResult qr = queryExecutor.getHistoryForKey(ns, "key").next();
        System.out.println(qr);
    }

    @After
    public void after() throws Exception{

    }
}
