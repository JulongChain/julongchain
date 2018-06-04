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

import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/31
 * @company Dingxuan
 */
public class QueryExecutorTest {
    IQueryExecutor queryExecutor = null;
    INodeLedger ledger = null;
    final String ns = "myGroup";
    final String coll = "coll";

    @Before
    public void before() throws Exception{
        LedgerManager.initialize(null);
        ledger = LedgerManager.openLedger(ns);
        queryExecutor = ledger.newQueryExecutor();
    }

    @Test
    public void testGetState() throws Exception{
        Assert.assertTrue(Arrays.equals(queryExecutor.getState(ns, "key"), "value".getBytes()));
    }

    @Test
    public void testGetStateMultipleKeys() throws Exception{
        Assert.assertTrue(Arrays.equals(queryExecutor.getStateMultipleKeys(ns, new ArrayList<String>(){{
            add("key");
        }}).get(0), "value".getBytes()));
    }

    @Test
    public void testGetStateRangeScanIterator() throws Exception{
        IResultsIterator itr = queryExecutor.getStateRangeScanIterator(ns, "ke", null);
        QueryResult n = itr.next();
        System.out.println(n);
        VersionedKV kv = (VersionedKV) n.getObj();
        Assert.assertEquals(kv.getCompositeKey().getKey(), "key");
        Assert.assertEquals(kv.getCompositeKey().getNamespace(), "myGroup");
        Assert.assertSame(kv.getVersionedValue().getVersion().getBlockNum(), (long) 1);
        Assert.assertSame(kv.getVersionedValue().getVersion().getTxNum(), (long) 0);
        Assert.assertTrue(Arrays.equals(kv.getVersionedValue().getValue(), "value".getBytes()));
    }

    @After
    public void after() throws Exception{

    }
}
