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
package org.bcia.javachain.core.ledger.kvledger.history.historydb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.history.HistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.iq80.leveldb.DBIterator;

/**
 * HistoryDB查询器
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class HistoryLevelDBQueryExecutor implements HistoryQueryExecutor {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(HistoryLevelDBQueryExecutor.class);

    private HistoryLevelDB historyDB;
    private BlockStore blockStore;

    @Override
    public ResultsIterator getHistoryForKey(String ns, String key) throws LedgerException{
        if(!LedgerConfig.isHistoryDBEnabled()){
            throw new LedgerException("History db is not avilable");
        }
        byte[] compositeStartKey = HistoryDBHelper.constructPartialCompositeHistoryKey(ns, key, false);

        DBIterator iterator = (DBIterator) historyDB.getProvider().getIterator(compositeStartKey);
        return HistoryScanner.newHistoryScanner(compositeStartKey, ns, key, iterator, blockStore);
    }

    public HistoryLevelDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(HistoryLevelDB historyDB) {
        this.historyDB = historyDB;
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(BlockStore blockStore) {
        this.blockStore = blockStore;
    }
}
