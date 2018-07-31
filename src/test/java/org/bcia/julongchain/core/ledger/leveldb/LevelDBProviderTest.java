/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.ledger.leveldb;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.iq80.leveldb.DBIterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDBProviderTest {
    LevelDBProvider provider;
    @Before
    public void before() throws LedgerException {
        provider = new LevelDBProvider("/home/bcia/test");
    }

    @After
    public void after() throws LedgerException {
//        provider.close();
    }

    @Test
    public void getDbHandler(){

    }

    @Test
    public void add() throws LedgerException {
        provider.put(new byte[]{0x00}, "0".getBytes(), true);
        provider.put(new byte[]{0x00, 0x00}, "00".getBytes(), true);
        provider.put(new byte[]{0x01}, "1".getBytes(), true);
        provider.put(new byte[]{0x00, 0x01}, "01".getBytes(), true);
    }

    @Test
    public void get() throws LedgerException {
        byte[] key0 = {0x00};
        byte[] key1 = {0x00, 0x00};
        byte[] key2 = {0x01};
        byte[] key3 = {0x00, 0x01};
        byte[] value0 = provider.get(key0);
        Assert.assertTrue(Arrays.equals(value0, "0".getBytes()));
        byte[] value1 = provider.get(key1);
	    Assert.assertTrue(Arrays.equals(value1, "00".getBytes()));
	    byte[] value2 = provider.get(key2);
	    Assert.assertTrue(Arrays.equals(value2, "1".getBytes()));
	    byte[] value3 = provider.get(key3);
	    Assert.assertTrue(Arrays.equals(value3, "01".getBytes()));
    }

    @Test
    public void getIterator() throws LedgerException {
	    Iterator<Map.Entry<byte[], byte[]>> iterator = provider.getIterator(new byte[]{0x00, 0x00});
	    Map.Entry<byte[], byte[]> entry0 = iterator.next();
	    byte[] key0 = entry0.getKey();
	    byte[] value0 = entry0.getValue();
	    Assert.assertTrue(Arrays.equals(key0, new byte[]{0x00, 0x00}));
	    Assert.assertTrue(Arrays.equals(value0, "00".getBytes()));
    }

    @Test
    public void writeBatch() throws LedgerException {
    	UpdateBatch batch = new UpdateBatch();
    	batch.put(new byte[]{0x02}, new byte[]{0x03});
    	batch.delete(new byte[]{0x00});
    	provider.writeBatch(batch, true);
    	Assert.assertNull(provider.get(new byte[]{0x00}));
	    Assert.assertNotNull(provider.get(new byte[]{0x02}));
    }
}
