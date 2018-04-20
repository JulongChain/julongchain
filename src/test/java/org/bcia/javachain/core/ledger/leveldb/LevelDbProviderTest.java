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
package org.bcia.javachain.core.ledger.leveldb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.core.ledger.util.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class LevelDbProviderTest {
    LevelDbProvider provider;
    @Before
    public void before() throws LedgerException {
        provider = LevelDbProvider.newProvider();
    }

    @After
    public void after() throws LedgerException {
        provider.close();
    }

    @Test
    public void getDbHandler(){
        Assert.assertNotNull(provider.getDbHandle("hahaha"));

    }

    @Test
    public void newUpdateBatch(){
        Assert.assertNotNull(LevelDbProvider.newUpdateBatch());
    }

    @Test
    public void add() throws LedgerException {
        byte[] key = {0x00};
//        byte[] key1 = {0x02};
        byte[] value1 = Util.longToBytes(1000, 8);
        byte[] value2 = Util.longToBytes(10000, 8);

        provider.put(key, ArrayUtils.addAll(value1, value2), true);
//        provider.put(key1, value, false);
//        System.out.println(new String(provider.get(key)));
//        System.out.println(new String(provider.get(key1)));
//        provider.close();
    }

    @Test
    public void get() throws LedgerException {
        byte[] key = {0x01};
        byte[] value = provider.get(key);
        System.out.println(new String(value));
        byte[] value1 = provider.get(key);
        System.out.println(new String(value));
        provider.close();
    }

    @Test
    public void constructLevelKey(){
        byte[] bytes = provider.constructLevelKey("123", "key".getBytes());
        System.out.println(new String(bytes));
    }

    @Test
    public void retrieveAppKey(){
        byte[] bytes = provider.retrieveAppKey(provider.constructLevelKey("123", "key".getBytes()));
        System.out.println(new String(bytes));
    }
}
