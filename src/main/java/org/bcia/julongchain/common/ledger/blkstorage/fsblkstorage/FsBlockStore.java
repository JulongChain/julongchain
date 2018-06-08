/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 区块文件系统
 *
 * @author sunzongyu
 * @date 2018/4/19
 * @company Dingxuan
 */
public class FsBlockStore implements IBlockStore {

    private String id;
    private BlockFileManager blockFileManager;
    private Config config;

    public static FsBlockStore newFsBlockStore(String id,
                                               Config config,
                                               IndexConfig indexConfig,
                                               IDBProvider dbHandle) throws LedgerException {
        FsBlockStore fsBlockStore = new FsBlockStore();
        BlockFileManager mgr = new BlockFileManager(id, config, indexConfig, dbHandle);
        fsBlockStore.setId(id);
        fsBlockStore.setConfig(config);
        fsBlockStore.setBlockFileManager(mgr);
        return fsBlockStore;
    }

    @Override
    public void addBlock(Common.Block block) throws LedgerException {
        blockFileManager.addBlock(block);
    }

    @Override
    public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
        return blockFileManager.getBlockchainInfo();
    }

    @Override
    public IResultsIterator retrieveBlocks(long startBlockNumber) throws LedgerException {
        return blockFileManager.retrieveBlocks(startBlockNumber);
    }

    @Override
    public Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException {
        return blockFileManager.retrieveBlockByHash(blockHash);
    }

    @Override
    public Common.Block retrieveBlockByNumber(long blockNum) throws LedgerException {
        return blockFileManager.retrieveBlockByNumber(blockNum);
    }

    @Override
    public Common.Envelope retrieveTxByID(String txID) throws LedgerException {
        return blockFileManager.retrieveTransactionByID(txID);
    }

    @Override
    public Common.Envelope retrieveTxByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException {
        return blockFileManager.retrieveTransactionByBlockNumTranNum(blockNum, tranNum);
    }

    @Override
    public Common.Block retrieveBlockByTxID(String txID) throws LedgerException {
        return blockFileManager.retrieveBlockByTxID(txID);
    }

    @Override
    public TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) throws LedgerException {
        return blockFileManager.retrieveTxValidationCodeByTxID(txID);
    }

    @Override
    public void shutdown() {
        blockFileManager.close();
    }

    @Override
    public void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BlockFileManager getBlockFileManager() {
        return blockFileManager;
    }

    public void setBlockFileManager(BlockFileManager blockFileManager) {
        this.blockFileManager = blockFileManager;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
