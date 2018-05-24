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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.IDBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IVersionedDBProvider;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.sceventmgmt.KVLedgerLSSCStateListener;

/**
 * 提供leveldb实现的VersionDB辅助
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class VersionedLevelDBProvider implements IVersionedDBProvider {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(VersionedLevelDBProvider.class);

    private IDBProvider db;

    public static IVersionedDBProvider newVersionedDBProvider() throws LedgerException{
        String dbPath = KvLedger.getConfig().getVersionedDBPath();
        VersionedLevelDBProvider vdbProvider =  new VersionedLevelDBProvider();
        vdbProvider.setDb(LevelDBProvider.newProvider(dbPath));
        logger.debug("Create vdb using path " + vdbProvider.getDb().getDbPath());
        return vdbProvider;
    }

    @Override
    public IVersionedDB getDBHandle(String id) throws LedgerException {
        VersionedLevelDB vdb = new VersionedLevelDB();
        vdb.setDb(db);
        vdb.setDbName(db.getDb().getDbName());
        return vdb;
    }

    @Override
    public void close() {
        try {
            db.close();
        } catch (LedgerException e) {
            throw new RuntimeException("Got error when close level db");
        }
    }

    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
        this.db = db;
    }
}
