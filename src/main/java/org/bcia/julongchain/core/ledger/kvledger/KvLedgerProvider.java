/*
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
package org.bcia.julongchain.core.ledger.kvledger;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.INodeLedgerProvider;
import org.bcia.julongchain.core.ledger.IStateListener;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.HistoryLevelDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.IHistoryDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.CommonStorageDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.IDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.IDBPorvider;
import org.bcia.julongchain.core.ledger.ledgerstorage.Provider;
import org.bcia.julongchain.core.ledger.ledgerstorage.Store;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供操作账本的方法
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public class KvLedgerProvider implements INodeLedgerProvider {
    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(KvLedgerProvider.class);
    private static final LedgerException ERR_LEDGER_ID_EXISTS = new LedgerException("LedgerID is already exists");
    private static final LedgerException ERR_NON_EXISTS_LEDGER_ID  = new LedgerException("LedgerID does not exists");
    private static final LedgerException ERR_LEDGER_NOT_OPEN = new LedgerException("Ledger is not opened yet");

    private IdStore idStore = null;
    private Provider ledgerStoreProvider = null;
    private IDBPorvider vdbProvider = null;
    private IDBProvider provider = null;
    private IHistoryDBProvider historyDBProvider = null;
    private Map<String, IStateListener> stateListeners = new HashMap<>();

	/**
	 * 新建Provider
	 */
	public KvLedgerProvider() throws LedgerException{
		logger.info("Initializing ledger provider");
		//初始化idstore(ledgerProvider)
		IdStore idStore = IdStore.openIDStore();
		//初始化文件系统(chains/chains)以及pvtdata(pvtdataStore)
		Provider ledgerStoreProvider = new Provider();
		//初始化versiondb(stateLeveldb)
		CommonStorageDBProvider vdbProvider = CommonStorageDBProvider.NewCommonStorageDBProvider();
		//初始化HistoryDB(historyLeveldb)
		IHistoryDBProvider historyDBProvider = new HistoryLevelDBProvider();

		logger.info("Ledger provider initialized");
		this.idStore = idStore;
		this.ledgerStoreProvider = ledgerStoreProvider;
		this.vdbProvider = vdbProvider;
		this.historyDBProvider = historyDBProvider;
		this.stateListeners = null;
		//修复未完成的db
		recoverUnderConstructionLedger();
	}

    /**
     * 初始化
     */
    @Override
    public void initialize(Map<String, IStateListener> stateListeners){
        this.stateListeners = stateListeners;
    }

    /**
     * 创建账本
     */
    @Override
    public INodeLedger create(Common.Block genesisBlock) throws LedgerException{
        String ledgerID = null;
        //获取账本id
        try {
            ledgerID = BlockUtils.getGroupIDFromBlock(genesisBlock);
        } catch (JavaChainException e) {
            logger.error("Got error when creating kvledger provider");
            throw new LedgerException(e);
        }
        //账本以存在,抛出异常
        if(idStore.ledgerIDExists(ledgerID)) {
            throw ERR_LEDGER_ID_EXISTS;
        }
        //开始创建账本, 并保存信息
        idStore.creatingLedgerID(ledgerID, genesisBlock);
        //设置创建标志
        idStore.setUnderConstructionFlag(ledgerID);
        //打开内部存储
        INodeLedger lgr = openInternal(ledgerID);
        BlockAndPvtData bapd = new BlockAndPvtData();
        bapd.setBlock(genesisBlock);
        //提交创世区块
        lgr.commitWithPvtData(bapd);
        //完成账本创建
        idStore.createLedgerID(ledgerID);
        return lgr;
    }

    /**
     * 打开账本
     */
    @Override
    public INodeLedger open(String ledgerID) throws LedgerException{
        logger.debug("Opening kvledger with ledgerid " + ledgerID);
        //没有创建过ledgerid,抛出异常
        if(!idStore.ledgerIDExists(ledgerID)){
            logger.error("Ledger {} does not exists", ledgerID);
            throw ERR_NON_EXISTS_LEDGER_ID;
        }
        return openInternal(ledgerID);
    }

    /**
     * 判断账本是否存k
     */
    @Override
    public Boolean exists(String ledgerID) throws LedgerException {
        return idStore.ledgerIDExists(ledgerID);
    }

    /**
     * 列出账本
     */
    @Override
    public List<String> list() throws LedgerException {
        return idStore.getAllLedgerIDs();
    }

    /**
     * 关闭账本
     */
    @Override
    public void close() throws LedgerException {
        idStore.close();
        ledgerStoreProvider.close();
        vdbProvider.close();
        historyDBProvider.close();
    }

    /**
     * 恢复在建账本(idstore中有UnderConstruction标记)
     */
    @Override
    public void recoverUnderConstructionLedger() throws LedgerException{
        logger.debug("Recovering under construction ledger");
        String ledgerID = idStore.getUnderConstructionFlag();
        //不存在有在建标记的账本
        if(ledgerID == null){
            logger.debug("No under construction ledger found.");
            return;
        }
        //存在在建标记
        logger.info(String.format("Ledger [%s] found as under construction", ledgerID));
        INodeLedger ledger = openInternal(ledgerID);
        Ledger.BlockchainInfo bcInfo = ledger.getBlockchainInfo();

        switch ((int) bcInfo.getHeight()){
            case 0:
                logger.info("Genesis block was not committed.");
                runCleanup(ledgerID);
                //重新提交创世区块
                BlockAndPvtData bapd = new BlockAndPvtData();
                bapd.setBlock(idStore.getCreatingBlock(ledgerID));
                //idstore中未发现保存的区块信息
                if(bapd.getBlock() == null){
                    break;
                }
                ledger.commitWithPvtData(bapd);
                idStore.unsetUnderConstructionFlag();
                break;
            case 1:
                logger.info("Genesis block was committed.");
                Common.Block genesisBlock = ledger.getBlockByNumber((long) 0);
                idStore.createLedgerID(ledgerID);
                break;
            default:
                throw new LedgerException(String.format(
                        "Under construction flag is set for ledger [%s] while the height of the blockchain is [%d]"
                        , ledgerID, bcInfo.getHeight()));
        }
        ledger.close();
    }

    /**
     * 打开内部仓库
     */
    private INodeLedger openInternal(String ledgerID) throws LedgerException{
        //账本的block仓库
        Store blockStore = ledgerStoreProvider.open(ledgerID);
        //账本的状态db
        IDB vdb = vdbProvider.getDBHandle(ledgerID);
        //账本的历史db
        IHistoryDB historyDB = historyDBProvider.getDBHandle(ledgerID);

        return new KvLedger(ledgerID, blockStore, vdb, historyDB, stateListeners);
    }

    private void runCleanup(String ledgerID){

    }

    public Provider getLedgerStoreProvider() {
        return ledgerStoreProvider;
    }

    public void setLedgerStoreProvider(Provider ledgerStoreProvider) {
        this.ledgerStoreProvider = ledgerStoreProvider;
    }

    public IdStore getIdStore() {
        return idStore;
    }

    public void setIdStore(IdStore idStore) {
        this.idStore = idStore;
    }

    public IDBProvider getProvider() {
        return provider;
    }

    public void setProvider(IDBProvider provider) {
        this.provider = provider;
    }

    public IHistoryDBProvider getHistoryDBProvider() {
        return historyDBProvider;
    }

    public void setHistoryDBProvider(IHistoryDBProvider historyDBProvider) {
        this.historyDBProvider = historyDBProvider;
    }

    public Map<String, IStateListener> getStateListeners() {
        return stateListeners;
    }

    public void setStateListeners(Map<String, IStateListener> stateListeners) {
        this.stateListeners = stateListeners;
    }

    public IDBPorvider getVdbProvider() {
        return vdbProvider;
    }

    public void setVdbProvider(IDBPorvider vdbProvider) {
        this.vdbProvider = vdbProvider;
    }
}
