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
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxSimulator;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/24
 * @company Dingxuan
 */
public class TxSimulatorTest {
    ITxSimulator simulator = null;
    INodeLedger ledger = null;
    TxSimulationResults txSimulationResults = null;
    final String ns = "mytestgroupid2";

    @Before
    public void before() throws LedgerException  {
        LedgerManager.initialize(null);
        ledger = LedgerManager.openLedger(ns);
        simulator = ledger.newTxSimulator("5");
    }

    @Test
    public void test() throws LedgerException {
        simulator.setState(ns, "key", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }
}
