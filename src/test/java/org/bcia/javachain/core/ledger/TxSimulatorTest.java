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

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxSimulator;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

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
    final String coll = "coll";

    @Before
    public void before() throws LedgerException  {
        LedgerManager.initialize(null);
        ledger = LedgerManager.openLedger(ns);
        simulator = ledger.newTxSimulator("5");
    }

    @Test
    public void testSetState() throws LedgerException {
        simulator.setState(ns, "key", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }

    @Test
    public void testDeleteState() throws Exception{
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        simulator.setState(ns, "key", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }

    @Test
    public void testSetStateMulitipleKeys() throws Exception{
        simulator.setStateMultipleKeys(ns, new HashMap<String, byte[]>(){{
            put("key1", "test set state1".getBytes());
            put("key2", "test set state2".getBytes());
            put("key3", "test set state3".getBytes());
            put("key4", "test set state4".getBytes());
            put("key5", "test set state5".getBytes());
            put("key6", "test set state6".getBytes());
        }});
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }

    @Test
    public void testsetProvateData() throws Exception{
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes());
        simulator.setPrivateData(ns, coll + "1", "key1", "test set private data1".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }

    @Test
    public void testSetPirvateDataMultipleKeys() throws Exception{
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes());
        simulator.setPrivateData(ns, coll + "1", "key1", "test set private data1".getBytes());
        simulator.setPirvateDataMultipleKeys(ns, coll + "2", new HashMap<String, byte[]>(){{
            put("key1", "test set private data mulitiple keys1".getBytes());
            put("key2", "test set private data mulitiple keys2".getBytes());
            put("key3", "test set private data mulitiple keys3".getBytes());
            put("key4", "test set private data mulitiple keys4".getBytes());
            put("key5", "test set private data mulitiple keys5".getBytes());
            put("key6", "test set private data mulitiple keys6".getBytes());

        }});
        txSimulationResults = simulator.getTxSimulationResults();
        System.out.println(txSimulationResults);
    }

    @Test
    public void testDeletePrivateData() throws Exception{
        byte[] rwset = null;
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset().toByteArray();
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes());
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
    }
}


