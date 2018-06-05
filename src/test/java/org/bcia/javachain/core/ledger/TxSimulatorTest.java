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
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxSimulator;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.junit.Assert;
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
    final String ns = "myGroup";
    final String coll = "coll";

    @Before
    public void before() throws LedgerException  {
        LedgerManager.initialize(null);
        ledger = LedgerManager.openLedger(ns);
        simulator = ledger.newTxSimulator("5");
    }

    @Test
    public void testSetState() throws Exception {
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.setState(ns, "key", "test set state".getBytes());
        simulator.setState(ns, "key1", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertEquals(kvRWSet.getWrites(1).getKey(), "key1");
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set state");
        Assert.assertEquals(kvRWSet.getWrites(1).getValue().toStringUtf8(), "test set state");
        simulator.setState(ns + "1", "key", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(1).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set state");
    }

    @Test
    public void testDeleteState() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        simulator.setState(ns, "key", "test set state".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set state");
        simulator.deleteState(ns, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
    }

    @Test
    public void testSetStateMulitipleKeys() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        simulator.setStateMultipleKeys(ns, new HashMap<String, byte[]>(){{
            put("key1", "test set state1".getBytes());
            put("key2", "test set state2".getBytes());
            put("key3", "test set state3".getBytes());
            put("key4", "test set state4".getBytes());
            put("key5", "test set state5".getBytes());
            put("key6", "test set state6".getBytes());
        }});
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPublicReadWriteSet().getNsRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        int i = 0;
        for(KvRwset.KVWrite write : kvRWSet.getWritesList()){
            int j = ++i;
            Assert.assertEquals(write.getKey(), "key" + j);
            Assert.assertFalse(write.getIsDelete());
            Assert.assertEquals(write.getValue().toStringUtf8(), "test set state" + j);
        }
    }

    @Test
    public void testsetPrivateData() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set private data");
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(1).getKey(), "key1");
        Assert.assertFalse(kvRWSet.getWrites(1).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(1).getValue().toStringUtf8(), "test set private data1");
        simulator.setPrivateData(ns, coll + "1", "key1", "test set private data1".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(1).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key1");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set private data1");
        simulator.setPrivateData(ns + 1, coll + "1", "key1", "test set private data1".getBytes());
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(1).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key1");
        Assert.assertFalse(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "test set private data1");
    }

    @Test
    public void testSetPirvateDataMultipleKeys() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
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
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(2).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        int i = 0;
        for(KvRwset.KVWrite write : kvRWSet.getWritesList()){
            int j = ++i;
            Assert.assertEquals(write.getKey(), "key" + j);
            Assert.assertFalse(write.getIsDelete());
            Assert.assertEquals(write.getValue().toStringUtf8(), "test set private data mulitiple keys" + j);
        }
    }

    @Test
    public void testDeletePrivateData() throws Exception{
        ByteString rwset = null;
        KvRwset.KVRWSet kvRWSet = null;
        Rwset.CollectionPvtReadWriteSet CollRWSet = null;
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        simulator.setPrivateData(ns, coll, "key", "test set private data".getBytes());
        simulator.setPrivateData(ns, coll, "key1", "test set private data1".getBytes());
        simulator.deletePrivateData(ns, coll, "key");
        txSimulationResults = simulator.getTxSimulationResults();
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(0).getKey(), "key");
        Assert.assertTrue(kvRWSet.getWrites(0).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(0).getValue().toStringUtf8(), "");
        rwset = txSimulationResults.getPrivateReadWriteSet().getNsPvtRwset(0).getCollectionPvtRwset(0).getRwset();
        kvRWSet = KvRwset.KVRWSet.parseFrom(rwset);
        Assert.assertEquals(kvRWSet.getWrites(1).getKey(), "key1");
        Assert.assertFalse(kvRWSet.getWrites(1).getIsDelete());
        Assert.assertEquals(kvRWSet.getWrites(1).getValue().toStringUtf8(), "test set private data1");
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
    }
}


