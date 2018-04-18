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
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;

/**
 * HistoryDB查询器
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class HistoryLevelQueryExecutor implements IHistoryQueryExecutor {

    private IHistoryDB historyDB = null;
    private BlockStore blockStore = null;

    public IHistoryDB getHistoryDB() {
        return historyDB;
    }

    public void setHistoryDB(IHistoryDB historyDB) {
        this.historyDB = historyDB;
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public ResultsIterator getHistoryForKey(String namespace, String key) throws LedgerException {
        //判断HistoryDB是否开启

        byte[] compositeStartKey = null;
        byte[] compositeEndKey = null;


        return null;
    }
}
