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
package org.bcia.javachain.core.ledger.blockfile;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.ledger.blockledger.ram.RamLedgerFactory;
import org.bcia.javachain.csp.gm.dxct.sm3.SM3;
import org.bcia.javachain.protos.common.Common;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/03
 * @company Dingxuan
 */
public class RamLedgerTest {
    RamLedgerFactory factory;
    ReadWriteBase ledger;

    @Before
    public void before() throws LedgerException {
        factory = new RamLedgerFactory();
        this.factory.setMaxSize(3);
        ledger = factory.getOrCreate("123");
    }

    @Test
    public void getOrCreateTest() throws LedgerException {
        System.out.println(ledger == null);
        ledger = factory.getOrCreate("123");
    }

    @Test
    public void iterator() throws Exception {
        byte[] bytes = new SM3().hash(Common.BlockHeader.getDefaultInstance().toByteArray());
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]);
        }
    }

    @Test
    public void append() throws Exception{
        for (int i = 0; i < 10; i++) {
            Common.Block block = Common.Block.newBuilder()
                    .setHeader(Common.BlockHeader.newBuilder()
                            .setNumber(i)
                            .build())
                    .build();
            ledger.append(block);
        }
        System.out.println(ledger.height());
    }
}
