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
package org.bcia.julongchain.core.ledger.pvtdatastorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;

/**
 * pvtdata操作类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class PvtDataProvider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(PvtDataProvider.class);

    private IDBProvider db;

    /**
     * 创建pvtdata
     */
    public PvtDataProvider() throws LedgerException{
		String dbPath = LedgerConfig.getPvtDataStorePath();
		this.db = new LevelDBProvider(dbPath);
		logger.debug("Create pvtprovider using path = " + this.db.getDBPath());
	}

    /**
     * 根据id打开对应pvtdata
     */
    public IPvtDataStore openStore(String ledgerID) throws LedgerException{
        return new PvtDataStoreImpl(db, ledgerID).initState();
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
