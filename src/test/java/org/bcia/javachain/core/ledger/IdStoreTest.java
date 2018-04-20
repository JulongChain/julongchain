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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.core.ledger.kvledger.IdStore;
import org.bcia.javachain.protos.common.Common;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class IdStoreTest {

    IdStore idStore;
    LevelDbProvider provider;

    @Before
    public void before() throws LedgerException  {
        idStore = IdStore.openIDStore();
    }

    @Test
    public void openIDStore(){
        Assert.assertNotNull(idStore);
    }

    @Test
    public void setUnderConstructionFlag() throws LedgerException {
        idStore.setUnderConstructionFlag("1234");
        Assert.assertEquals("1234", idStore.getUnderConstructionFlag());
    }

    @Test
    public void unsetUnderConstructionFlag() throws LedgerException{
        Assert.assertEquals("1234", idStore.getUnderConstructionFlag());
        idStore.unsetUnderConstructionFlag();
        Assert.assertNull(idStore.getUnderConstructionFlag());
    }

    @Test
    public void getUnderConstructionFlag() throws LedgerException {
        idStore.setUnderConstructionFlag("1234");
        Assert.assertEquals("1234", idStore.getUnderConstructionFlag());
    }

    @Test
    public void createLedgerID() throws LedgerException {
        idStore.createLedgerID("kql", Common.Block.newBuilder().build());
        idStore.close();
        provider = LevelDbProvider.newProvider();
        System.out.println(provider.get(idStore.encodeLedgerKey("kql")));
    }
}
