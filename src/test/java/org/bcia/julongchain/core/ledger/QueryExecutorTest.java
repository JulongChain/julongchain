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

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		byte[] as = queryExecutor.getState(ns, "a");
		System.out.println(new String(as));
	}

    @Test
    public void testGetStateMultipleKeys() throws Exception{
		List<byte[]> states = queryExecutor.getStateMultipleKeys(ns, new ArrayList<String>() {{
			add("key0");
			add("key1");
			add("key2");
			add("key3");
			add("key4");
			add("key5");
		}});
		states.forEach((b) -> {
			Assert.assertTrue(new String(b).startsWith("pub value"));
		});
	}

    @Test
    public void testGetStateRangeScanIterator() throws Exception{
        IResultsIterator itr = queryExecutor.getStateRangeScanIterator(ns, "key", null);
		for (int i = 0; i < 6; i++) {
			QueryResult n = itr.next();
			VersionedKV kv = (VersionedKV) n.getObj();
			Assert.assertEquals(kv.getCompositeKey().getKey(), "key" + i);
			Assert.assertEquals(kv.getCompositeKey().getNamespace(), ns);
			Assert.assertSame(kv.getVersionedValue().getVersion().getBlockNum(), (long) 1);
			Assert.assertSame(kv.getVersionedValue().getVersion().getTxNum(), (long) i);
			Assert.assertTrue(Arrays.equals(kv.getVersionedValue().getValue(), ("pub value" + i).getBytes()));
		}
	}

    @Test
    public void testGetPrivateData() throws Exception{
		for (int i = 0; i < 6; i++) {
			byte[] privateData = queryExecutor.getPrivateData(ns, "coll", "key" + i);
			Assert.assertTrue(Arrays.equals(privateData, ("pvt value" + i).getBytes()));
		}
	}

    @Test
    public void testGetPrivateDataMultipleKeys() throws Exception{
        List<byte[]> privateDatas = queryExecutor.getPrivateDataMultipleKeys(ns, "coll", new ArrayList<String>() {{
			add("key0");
			add("key1");
			add("key2");
			add("key3");
			add("key4");
			add("key5");
        }});
		for (int i = 0; i < 6; i++) {
			Assert.assertTrue(Arrays.equals(privateDatas.get(i), ("pvt value" + i).getBytes()));
		}
	}

    @Test
    public void testGetPrivateDataRangeScanIterator() throws Exception{
        IResultsIterator itr = queryExecutor.getPrivateDataRangeScanIterator(ns, "coll", "key", "l");
		for (int i = 0; i < 6; i++) {
			QueryResult qr = itr.next();
			KvQueryResult.KV kv = ((KvQueryResult.KV) qr.getObj());
			Assert.assertEquals(kv.getNamespace(), ns);
			Assert.assertEquals(kv.getKey(), "key" + i);
			Assert.assertEquals(kv.getValue(),ByteString.copyFromUtf8("pvt value" + i));
		}
	}

    @After
    public void after() throws Exception{

    }
}
