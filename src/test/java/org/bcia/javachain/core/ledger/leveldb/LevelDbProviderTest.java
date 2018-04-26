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
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.util.Util;
import org.iq80.leveldb.DBIterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.Map;

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
        provider = LevelDbProvider.newProvider("/home/bcia/test");
    }

    @After
    public void after() throws LedgerException {
        provider.close();
    }

    @Test
    public void getDbHandler(){

    }

    @Test
    public void newUpdateBatch(){
        Assert.assertNotNull(LevelDbProvider.newUpdateBatch());
    }

    @Test
    public void add() throws LedgerException {
        for (int i = 0; i < 100; i++) {
            byte[] key = ("key" + i).getBytes();
            byte[] value = ("value" + i).getBytes();
            provider.put(key, value, true);
        }
    }

    @Test
    public void get() throws LedgerException {
        provider = LevelDbProvider.newProvider("/home/bcia/javachain-606067328/ledgersData/ledgerProvider");
//        byte[] key = {0x01};
//        byte[] value = provider.get(key);
//        System.out.println(new String(value));
//        byte[] value1 = provider.get(key);
//        System.out.println(new String(value));
//        provider.close();
        byte[] key = "underConstructionLedgerKey".getBytes();
        byte[] value = provider.get(key);
        System.out.println(new String(value));
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

    @Test
    public void getIterator() throws LedgerException {
        for (int i = 0; i < 10; i++) {
            byte[] key = ("key" + i).getBytes();
            soutByte(provider.get(key));
        }
    }

    public static void soutByte(byte[] bytes){
        for (byte aByte : bytes) {
            System.out.print(aByte + " ");
        }
        System.out.println();
    }
}
