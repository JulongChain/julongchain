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
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.IBlockStore;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.iq80.leveldb.DBIterator;

import java.util.Map;

/**
 * 查询HistoryDB result
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class HistoryScanner implements IResultsIterator {
    private static final JavaChainLog  logger = JavaChainLogFactory.getLog(HistoryScanner.class);

    /**
     * HistoryDB key头部 包含namespace, key
     */
    private byte[] compositePartialKey = null;
    private String nameSpace = null;
    private String key = null;
    private DBIterator dbIter = null;
    private IBlockStore blockStore = null;
    private long blockNum;
    private long tranNum;

    public static HistoryScanner newHistoryScanner(byte[] compositePartialKey,
                                                   String nameSpace,
                                                   String key,
                                                   DBIterator dbIter,
                                                   IBlockStore blockStore){
        HistoryScanner scanner = new HistoryScanner();
        scanner.compositePartialKey = compositePartialKey;
        scanner.nameSpace = nameSpace;
        scanner.key = key;
        scanner.dbIter = dbIter;
        scanner.blockStore = blockStore;
        return scanner;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(!dbIter.hasNext()){
            return null;
        }
        Map.Entry<byte[], byte[]> entry = dbIter.next();
        byte[] historyKey = entry.getKey();
        blockNum = HistoryDBHelper.splitCompositeHistoryKeyForBlockNum(historyKey, compositePartialKey.length);
        tranNum = HistoryDBHelper.splitCompositeHistoryKeyForTranNum(historyKey, compositePartialKey.length);
        logger.debug(String.format("Found history record for namespace: %s, key: %s. BlockNum: %d, TranNum: %d", nameSpace, key, blockNum, tranNum));

        return new QueryResult(this);
    }

    @Override
    public void close() throws LedgerException {

    }

    public byte[] getCompositePartialKey() {
        return compositePartialKey;
    }

    public void setCompositePartialKey(byte[] compositePartialKey) {
        this.compositePartialKey = compositePartialKey;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DBIterator getDbIter() {
        return dbIter;
    }

    public void setDbIter(DBIterator dbIter) {
        this.dbIter = dbIter;
    }

    public IBlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public long getTranNum() {
        return tranNum;
    }

    public void setTranNum(long tranNum) {
        this.tranNum = tranNum;
    }
}
