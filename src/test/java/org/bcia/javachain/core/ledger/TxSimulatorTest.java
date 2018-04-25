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
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.CommonStorageDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionLevelDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxManager;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxSimulator;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.protos.common.Common;
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
    LockBasedTxSimulator simulator = null;

    @Before
    public void before() throws LedgerException  {
    }

    @Test
    public void test() throws LedgerException {
//        DB db = CommonStorageDB.newCommonStorageDB(new VersionLevelDB(LevelDbProvider.newProvider("/home/bcia/test"), "test"), "test");
//        LockBasedTxManager mgr = LockBasedTxManager.newLockBasedTxMgr("test", db, null);
//        simulator = LockBasedTxSimulator.newLockBasedTxSimulator(mgr, "test");
//        String ns = "ns";
//        String key = "key";
//        byte[] value = "value".getBytes();
//        simulator.setState(ns, key, value);
        long fileSize = new File("/home/asdad.position").length();
        System.out.println(fileSize);
    }
}
