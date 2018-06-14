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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.iq80.leveldb.DBIterator;

/**
 * HistoryDB查询器
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class HistoryLevelDBQueryExecutor implements IHistoryQueryExecutor {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(HistoryLevelDBQueryExecutor.class);

    private HistoryLevelDB historyDB;
    private IBlockStore blockStore;

    public HistoryLevelDBQueryExecutor(HistoryLevelDB historyDB, IBlockStore blockStore) {
        this.historyDB = historyDB;
        this.blockStore = blockStore;
    }

    @Override
    public IResultsIterator getHistoryForKey(String ns, String key) throws LedgerException{
        if(!LedgerConfig.isHistoryDBEnabled()){
        	String msg = "History db is not available";
			logger.debug(msg);
            throw new LedgerException(msg);
        }
        byte[] compositeStartKey = HistoryDBHelper.constructPartialCompositeHistoryKey(ns, key, false);

        DBIterator iterator = (DBIterator) historyDB.getProvider().getIterator(compositeStartKey);
        return new HistoryScanner(compositeStartKey, ns, key, iterator, blockStore);
    }

    public HistoryLevelDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(HistoryLevelDB historyDB) {
        this.historyDB = historyDB;
    }

    public IBlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }
}
