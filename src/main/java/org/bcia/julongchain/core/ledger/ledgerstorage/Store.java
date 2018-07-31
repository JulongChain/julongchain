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
package org.bcia.julongchain.core.ledger.ledgerstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.PvtNsCollFilter;
import org.bcia.julongchain.core.ledger.TxPvtData;
import org.bcia.julongchain.core.ledger.pvtdatastorage.IPvtDataStore;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 持有文件系统以及pvtdata
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class Store implements IBlockStore {
    private IPvtDataStore pvtdataStore = null;
    private IBlockStore blkStorage = null;

    @Override
    public void addBlock(Common.Block block) throws LedgerException {
        blkStorage.addBlock(block);
    }

    @Override
    public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
        return blkStorage.getBlockchainInfo();
    }

    @Override
    public IResultsIterator retrieveBlocks(long startNum) throws LedgerException {
        return blkStorage.retrieveBlocks(startNum);
    }

    @Override
    public Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException {
        return blkStorage.retrieveBlockByHash(blockHash);
    }

    @Override
    public Common.Block retrieveBlockByNumber(long blockNum) throws LedgerException {
        return blkStorage.retrieveBlockByNumber(blockNum);
    }

    @Override
    public Common.Envelope retrieveTxByID(String txID) throws LedgerException {
        return blkStorage.retrieveTxByID(txID);
    }

    @Override
    public Common.Envelope retrieveTxByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException {
        return blkStorage.retrieveTxByBlockNumTranNum(blockNum, tranNum);
    }

    @Override
    public Common.Block retrieveBlockByTxID(String txID) throws LedgerException {
        return blkStorage.retrieveBlockByTxID(txID);
    }

    @Override
    public TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) throws LedgerException {
        return blkStorage.retrieveTxValidationCodeByTxID(txID);
    }

    @Override
    public void shutdown() {

    }

    /**
     * 提交pvt数据
     */
    @Override
    public synchronized void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException{
        List<TxPvtData> pvtDatas = new ArrayList<>();
	    for(Map.Entry<Long, TxPvtData> entry : blockAndPvtData.getBlockPvtData().entrySet()){
		    pvtDatas.add(entry.getValue());
	    }
	    //提交数据,出现异常则回滚,否则完成提交
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
        return pvtdataStore.getPvtDataByBlockNum(blockNum, filter);
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

    public void init() throws LedgerException{
        if(!initPvtdataStoreFromExistingBlockchain()){
            syncPvtdataStoreWithBlockStore();
        }
    }

    /**
     * 通过区块文件初始化pvt数据
     * 需要则进行初始化
     */
    private boolean initPvtdataStoreFromExistingBlockchain() throws LedgerException{
        Ledger.BlockchainInfo bcInfo = blkStorage.getBlockchainInfo();
        boolean pvtdataStoreEmpty = pvtdataStore.isEmpty();
        if(pvtdataStoreEmpty && bcInfo.getHeight() > 0){
            pvtdataStore.initLastCommittedBlock(bcInfo.getHeight() - 1);
            return true;
        }
        return false;
    }

    /**
     * 同步区块与pvtdata
     */
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

    public IBlockStore getBlkStorage() {
        return blkStorage;
    }

    public void setBlkStorage(IBlockStore blkStorage) {
        this.blkStorage = blkStorage;
    }

    public IPvtDataStore getPvtdataStore() {
        return pvtdataStore;
    }

    public void setPvtdataStore(IPvtDataStore pvtdataStore) {
        this.pvtdataStore = pvtdataStore;
    }
}
