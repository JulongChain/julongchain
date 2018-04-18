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
package org.bcia.javachain.core.ledger.ledgerstorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.BlockStoreProvider;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Conf;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreProvider;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.PvtNsCollFilter;
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class Store implements BlockStore {
    private org.bcia.javachain.core.ledger.pvtdatastorage.Store pvtdataStore = null;
    private BlockStore blkStorage = null;

    public static Provider newProvider() throws LedgerException{
        Provider provider = new Provider();
        //文件系统provider初始化条件
        String[] attrsToIndex = {
                BlockStore.INDEXABLE_ATTR_BLOCK_HASH,
                BlockStore.INDEXABLE_ATTR_BLOCK_NUM,
                BlockStore.INDEXABLE_ATTR_TX_ID,
                BlockStore.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM,
                BlockStore.INDEXABLE_ATTR_BLOCK_TX_ID,
                BlockStore.INDEXABLE_ATTR_TX_VALIDATION_CODE
        };
        //文件系统provider
        IndexConfig indexConfig = new IndexConfig();
        indexConfig.setAttrsToIndex(attrsToIndex);

        Conf conf = Config.newConf("/home/bcia/fsblocksotrage", 10000);

        BlockStoreProvider blockStoreProvider = FsBlockStoreProvider.newProvider(conf, indexConfig);

        provider.setBlkStoreProvider(blockStoreProvider);

        org.bcia.javachain.core.ledger.pvtdatastorage.Provider pvtDataStoreProvider =
                org.bcia.javachain.core.ledger.pvtdatastorage.Provider.newProvider();


        provider.setPvtDataStoreProvider(pvtDataStoreProvider);
        return provider;
    }

    public synchronized void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException{
        List<TxPvtData> pvtDatas = new ArrayList<>();
        for(Map.Entry<Long, TxPvtData> entry : blockAndPvtData.getBlockPvtData().entrySet()){
            pvtDatas.add(entry.getValue());
        }

        pvtdataStore.prepare(blockAndPvtData.getBlock().getHeader().getNumber(), pvtDatas);
        try {
            addBlock(blockAndPvtData.getBlock());
        } catch (Throwable e) {
            pvtdataStore.rollback();
            throw new LedgerException(e);
        }
        pvtdataStore.commit();
    }

    public synchronized BlockAndPvtData getPvtDataAndBlockByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        Common.Block block = retrieveBlockByNumber(blockNum);
        List<TxPvtData> pvtData = getPvtDataByNumWithoutLock(blockNum, filter);
        BlockAndPvtData bapd = new BlockAndPvtData();
        bapd.setBlock(block);
        bapd.setBlockPvtData(constructPvtdataMap(pvtData));
        return bapd;
    }

    public synchronized List<TxPvtData> getPvtDataByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException{
        return getPvtDataByNumWithoutLock(blockNum, filter);
    }

    private List<TxPvtData> getPvtDataByNumWithoutLock(long blockNum, PvtNsCollFilter filter) throws LedgerException {
        List<TxPvtData> pvtData = pvtdataStore.getPvtDataByBlockNum(blockNum, filter);
        return pvtData;
    }

    private Map<Long, TxPvtData> constructPvtdataMap(List<TxPvtData> pvtData){
        if(pvtData == null){
            return null;
        }
        Map<Long, TxPvtData> m = new HashMap<>();
        for(TxPvtData txPvtData : pvtData){
            m.put(txPvtData.getSeqInBlock(), txPvtData);
        }
        return m;
    }

    @Override
    public void addBlock(Common.Block block) throws LedgerException {

    }

    @Override
    public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator retrieveBlocks(long startBlockNumber) {
        return null;
    }

    @Override
    public ResultsIterator retrieveBlocks(Long startNum) throws LedgerException {
        return null;
    }

    @Override
    public Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException {
        return null;
    }

    @Override
    public Common.Block retrieveBlockByNumber(Long blockNum) throws LedgerException {
        return null;
    }

    @Override
    public Common.Envelope retrieveTxByID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public Common.Envelope retrieveTxByBlockNumTranNum(Long blockNum, Long tranNum) throws LedgerException {
        return null;
    }

    @Override
    public Common.Block retrieveBlockByTxID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) throws LedgerException {
        return null;
    }

    @Override
    public void shutdown() {

    }

    public void init() throws LedgerException{
        if(!initPvtdataStoreFromExistingBlockchain()){
            syncPvtdataStoreWithBlockStore();
        }
    }

    private boolean initPvtdataStoreFromExistingBlockchain() throws LedgerException{
        Ledger.BlockchainInfo bcInfo = blkStorage.getBlockchainInfo();
        boolean pvtdataStoreEmpty = pvtdataStore.isEmpty();
        if(pvtdataStoreEmpty && bcInfo.getHeight() > 0){
            pvtdataStore.initLastCommitedBlock(bcInfo.getHeight() - 1);
            return true;
        }
        return false;
    }

    private void syncPvtdataStoreWithBlockStore() throws LedgerException{
        boolean pendingPvtbatch = pvtdataStore.hasPendingBatch();
        if(!pendingPvtbatch){
            return;
        }
        Ledger.BlockchainInfo bcInfo = getBlockchainInfo();
        long pvtdataStoreHt = pvtdataStore.lastCommitedBlockHeight();
        if(bcInfo.getHeight() == pvtdataStoreHt){
            pvtdataStore.rollback();
            return;
        } else if(bcInfo.getHeight() == pvtdataStoreHt + 1){
            pvtdataStore.commit();
            return;
        }
        throw new LedgerException(String.format("This is not expected. blockStoreHeight = %d, pvtdataStoreHeight = %s"
                , bcInfo.getHeight(), pvtdataStoreHt));
    }

    public BlockStore getBlkStorage() {
        return blkStorage;
    }

    public void setBlkStorage(BlockStore blkStorage) {
        this.blkStorage = blkStorage;
    }

    public org.bcia.javachain.core.ledger.pvtdatastorage.Store getPvtdataStore() {
        return pvtdataStore;
    }

    public void setPvtdataStore(org.bcia.javachain.core.ledger.pvtdatastorage.Store pvtdataStore) {
        this.pvtdataStore = pvtdataStore;
    }
}
