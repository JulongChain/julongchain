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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IPrunePolicy;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.*;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.history.historydb.IHistoryDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.IDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.ITxManager;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr.LockBasedTxManager;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.ledgerstorage.Store;
import org.bcia.julongchain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.julongchain.core.ledger.sceventmgmt.ScEventManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.*;

/**
 * kv账本
 *
 * @author sunzongyu1
 * @date 2018/04/13
 * @company Dingxuan
 */
public class KvLedger implements INodeLedger {

	private static JulongChainLog log = JulongChainLogFactory.getLog(KvLedger.class);

	private String ledgerID;
	private IBlockStore blockStore;
	private ITxManager txtmgmt;
	private IHistoryDB historyDB;

	/**
	 * 创建新KvLedger
	 */
	public KvLedger(String ledgerID,
	                IBlockStore blockStore,
	                IDB versionedDB,
	                IHistoryDB historyDB,
	                Map<String, IStateListener> stateListeners) throws LedgerException {
		log.debug("Creating KVLedger ledgerID = " + ledgerID);

		ITxManager txmgmt = new LockBasedTxManager(ledgerID, versionedDB, stateListeners);

		this.ledgerID = ledgerID;
		this.blockStore = blockStore;
		this.txtmgmt = txmgmt;
		this.historyDB = historyDB;

		ISmartContractLifecycleEventListener scEventListener = versionedDB.getSmartcontractEventListener();
		log.debug("Register state db for smartcontract lifecycle event " + (scEventListener != null));

		if(scEventListener != null){
			ScEventManager.getMgr().register(ledgerID, scEventListener);
		}

		recoverDBs();
	}

	/**
	 * 恢复账本
	 */
	private void recoverDBs() throws LedgerException {
		log.debug("Evtering revocerDBs()");
		Ledger.BlockchainInfo info = blockStore.getBlockchainInfo();
		if(info.getHeight() == 0){
			log.info("Block storage is empty");
			return;
		}
		long lastAvailableBlockNum = info.getHeight() - 1;
		List<IRecoverable> recoverables = new ArrayList<>();
		List<Recoverer> recoverers = new ArrayList<>();
		recoverables.add(txtmgmt);
		recoverables.add(historyDB);
		//循环添加需要恢复的db
		for(IRecoverable recoverable : recoverables){
			long firstBlockNum = recoverable.shouldRecover();
			if(firstBlockNum  == -1 || firstBlockNum - 1 != lastAvailableBlockNum){
				Recoverer recoverer = new Recoverer(firstBlockNum, recoverable);
				recoverers.add(recoverer);
			}
		}
		if(recoverers.size() == 0){
			return;
		} else if(recoverers.size() == 1){
			recommitLostBlocks(recoverers.get(0).getFirstBlockNum(), lastAvailableBlockNum, recoverers.get(0).getRecoverable());
		} else {
			//小号放前面 升序
			Collections.sort(recoverers, new Comparator<Recoverer>() {
				@Override
				public int compare(Recoverer o1, Recoverer o2) {
					return o1.getFirstBlockNum() > o2.getFirstBlockNum() ? 1 : -1;
				}
			});
			//小号向大号看齐
			recommitLostBlocks(recoverers.get(0).getFirstBlockNum(), recoverers.get(1).getFirstBlockNum() - 1,
					recoverers.get(0).getRecoverable());
			//大号向正确看齐
			recommitLostBlocks(recoverers.get(1).getFirstBlockNum(), lastAvailableBlockNum,
					recoverers.get(0).getRecoverable(), recoverers.get(1).getRecoverable());
		}
	}

	/**
	 * 重新提交区块
	 */
	private void recommitLostBlocks(long firstBlockNum, long lastBlockNum, IRecoverable... recoverables) throws LedgerException{
		BlockAndPvtData blockAndPvtData;
		for (long blockNumber = firstBlockNum; blockNumber <= lastBlockNum; blockNumber++) {
			blockAndPvtData = getPvtDataAndBlockByNum(blockNumber, null);
			for(IRecoverable recoverable : recoverables){
				recoverable.commitLostBlock(blockAndPvtData);
			}
		}
	}

	/**
	 * 根据交易ID获取交易
	 */
	@Override
	public synchronized TransactionPackage.ProcessedTransaction getTransactionByID(String txID) throws LedgerException {
		Common.Envelope tranEvn = null;
		TransactionPackage.TxValidationCode txVResult = null;
		tranEvn = blockStore.retrieveTxByID(txID);
		txVResult = blockStore.retrieveTxValidationCodeByTxID(txID);
		if(tranEvn == null || txVResult == null){
			log.info(String.format("Transaction not found, using id = [%s]", txID));
			return null;
		}
		return TransactionPackage.ProcessedTransaction.newBuilder()
				.setTransactionEnvelope(tranEvn)
				.setValidationCode(txVResult.getNumber())
				.build();
	}

	/**
	 * 获取当前区块链状态
	 */
	@Override
	public synchronized Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
		Ledger.BlockchainInfo bcInfo = blockStore.getBlockchainInfo();
		if (bcInfo == null) {
			log.info("Blockchain info not found");
		}
		return bcInfo;
	}

	/**
	 * 根据区块号获取区块
	 */
	@Override
	public synchronized Common.Block getBlockByNumber(long blockNumber) throws LedgerException {
		Common.Block block = blockStore.retrieveBlockByNumber(blockNumber);
		if (block == null) {
			log.info(String.format("block not found, using block num = [%d]", blockNumber));
		}
		return block;
	}

	/**
	 * 获取区块迭代器
	 */
	@Override
	public IResultsIterator getBlocksIterator(long startBlockNumber) throws LedgerException{
		IResultsIterator itr = blockStore.retrieveBlocks(startBlockNumber);
		if (itr == null) {
			log.info(String.format("Blocks iterator not found, using start block num = [%d]", startBlockNumber));
		}
		return itr;
	}

	/**
	 * 根据区块Hash(headerHash)获取区块
	 */
	@Override
	public synchronized Common.Block getBlockByHash(byte[] blockHash) throws LedgerException {
		Common.Block block = blockStore.retrieveBlockByHash(blockHash);
		if (block == null) {
			log.info("Block not found");
		}
		return block;
	}

	/**
	 * 根据交易ID获取区块
	 */
	@Override
	public synchronized Common.Block getBlockByTxID(String txID) throws LedgerException {
		Common.Block block = blockStore.retrieveBlockByTxID(txID);
		if (block == null) {
			log.info(String.format("Block not found, using txid = [%s]", txID));
		}
		return block;
	}

	/**
	 * 通过交易Id获取交易可行性代码
	 */
	@Override
	public synchronized TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException {
		TransactionPackage.TxValidationCode txValidationCode = blockStore.retrieveTxValidationCodeByTxID(txID);
		if (txValidationCode == null) {
			log.info(String.format("Tx validation code not found, using txid = [%s]", txID));
		}
		return txValidationCode;
	}

	/**
	 * 获取交易模拟器
	 */
	@Override
	public ITxSimulator newTxSimulator(String txId) throws LedgerException {
		return txtmgmt.newTxSimulator(txId);
	}

	/**
	 * TODO
	 * 修剪策略
	 */
	@Override
	public void prune(IPrunePolicy prunePolicy) throws LedgerException {
		throw new LedgerException("Not yet implement");
	}

	/**
	 * 新建交易查询器
	 */
	@Override
	public IQueryExecutor newQueryExecutor() throws LedgerException{
		return txtmgmt.newQueryExecutor(UUID.randomUUID().toString());
	}

	/**
	 * 新建历史查询器
	 */
	@Override
	public IHistoryQueryExecutor newHistoryQueryExecutor() throws LedgerException {
		return historyDB.newHistoryQueryExecutor(blockStore);
	}

	/**
	 * 根据区块号获取pvtdata和区块
	 */
	@Override
	public synchronized BlockAndPvtData getPvtDataAndBlockByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
		return ((Store) blockStore).getPvtDataAndBlockByNum(blockNum, filter);
	}

	/**
	 * 根据区块号获取pvtdata
	 */
	@Override
	public synchronized List<TxPvtData> getPvtDataByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException {
		return  ((Store) blockStore).getPvtDataByNum(blockNum, filter);
	}

	/**
	 * 修剪pvtdata策略
	 */
	@Override
	public void purgePrivateData(long maxBlockNumToRetain) throws LedgerException {
		throw new LedgerException("Not yet implement");
	}

	@Override
	public long privateDataMinBlockNum() throws LedgerException {
		throw new LedgerException("Not yet implement");
	}

	/**
	 * 提交区块
	 */
	@Override
	public void commit(Common.Block block) throws LedgerException {
		commitWithPvtData(new BlockAndPvtData(block, null, null));
	}

	/**
	 * 关闭账本
	 */
	@Override
	public void close() {
		blockStore.shutdown();
		try {
			txtmgmt.shutdown();
		} catch (LedgerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 提交区块和pvtdata
	 */
	@Override
	public synchronized void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException {
		long blockNo = blockAndPvtData.getBlock().getHeader().getNumber();
		log.debug(String.format("Group %s: Validating state for block %d", ledgerID, blockNo));
		//执行校验工作, 并准备更新包
		txtmgmt.validateAndPrepare(blockAndPvtData, true);
		log.debug(String.format("Group %s: Committing block %d to storage", ledgerID, blockNo));
		//提交区块私有信息
		blockStore.commitWithPvtData(blockAndPvtData);
		log.info(String.format("Group %s: Committed block %d to storage", ledgerID, blockNo));
		log.debug(String.format("Group %s: Committing block %d transaction to state db", ledgerID, blockNo));
		//提交stateDB数据
		txtmgmt.commit();
		//在HistoryDB允许的情况下提交历史信息
		if(LedgerConfig.isHistoryDBEnabled()){
			log.debug(String.format("Group %s: Committing block %d transaction to history db", ledgerID, blockNo));
			historyDB.commit(blockAndPvtData.getBlock());
		}
	}

	@Override
	public String getLedgerID() {
		return ledgerID;
	}

	public void setLedgerID(String ledgerID) {
		this.ledgerID = ledgerID;
	}

	public IBlockStore getBlockStore() {
		return blockStore;
	}

	public void setBlockStore(IBlockStore blockStore) {
		this.blockStore = blockStore;
	}

	public ITxManager getTxtmgmt() {
		return txtmgmt;
	}

	public void setTxtmgmt(ITxManager txtmgmt) {
		this.txtmgmt = txtmgmt;
	}

	public IHistoryDB getHistoryDB() {
		return historyDB;
	}

	public void setHistoryDB(IHistoryDB historyDB) {
		this.historyDB = historyDB;
	}
}
