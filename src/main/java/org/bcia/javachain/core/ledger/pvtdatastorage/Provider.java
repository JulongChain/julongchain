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
package org.bcia.javachain.core.ledger.pvtdatastorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.IDBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;

/**
 * pvtdata操作类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class Provider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(Provider.class);

    private IDBProvider db;

    /**
     * 创建pvtdata
     */
    public static Provider newProvider() throws LedgerException {
        Provider provider = new Provider();
        String dbPath = LedgerConfig.getPvtDataStorePath();
        provider.db = LevelDBProvider.newProvider(dbPath);
        logger.debug("Create pvtprovider using path = " + provider.getDb().getDbPath());
        return provider;
    }

    /**
     * 根据id打开对应pvtdata
     */
    public IStore openStore(String ledgerID) throws LedgerException{
        IStore store = new StoreImpl();
        ((StoreImpl) store).setDb(db);
        ((StoreImpl) store).setLedgerID(ledgerID);
        ((StoreImpl) store).initState();
        return store;
    }

    /**
     * 关闭数据库
     */
    public void close() throws LedgerException{
        db.close();
    }

    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
        this.db = db;
    }
}
