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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.iq80.leveldb.impl.LookupKey;

/**
 * 提供leveldb实现的VersionDB辅助
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class VersionLevelDBProvider implements IVersionedDBProvider {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(VersionLevelDBProvider.class);

    private LevelDbProvider db;

    public static IVersionedDBProvider newVersionedDBProvider() throws LedgerException{
        String dbPath = LedgerConfig.getStateLevelDBPath();
        VersionLevelDBProvider vdbProvider =  new VersionLevelDBProvider();
        vdbProvider.setDb(LevelDbProvider.newProvider(dbPath));
        logger.debug("Create vdb using path " + vdbProvider.getDb().getDbPath());
        return vdbProvider;
    }

    @Override
    public IVersionedDB getDBHandle(String id) throws LedgerException {
        return new VersionLevelDB(db, db.getDb().getDbName());
    }

    @Override
    public void close() {
        try {
            db.close();
        } catch (LedgerException e) {
            throw new RuntimeException("Got error when close level db");
        }
    }

    public LevelDbProvider getDb() {
        return db;
    }

    public void setDb(LevelDbProvider db) {
        this.db = db;
    }
}
