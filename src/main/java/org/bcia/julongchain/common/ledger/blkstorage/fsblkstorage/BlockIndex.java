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

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.util.TxValidationFlags;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作block索引类
 * 用于完成区块索引以及索引数据库写入、查询
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockIndex implements Index {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockIndex.class);

    private Map<String, Boolean> indexItemsMap;
    private IDBProvider db;
    private String ledgerId;

	private static final String BLOCK_NUM_IDX_KEY_PREFIX      		= "n";
    private static final String BLOCK_HASH_IDX_KEY_PREFIX			= "h";
    private static final String TX_ID_IDX_KEY_PREFIX 				= "t";
    private static final String BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX	= "a";
    private static final String BLOCK_TX_ID_IDX_KEY_PREFIX          = "b";
    private static final String TX_VALIDATION_RESULT_IDX_KEY_PREFIX = "v";
    private static final String INDEX_CHECK_POINT_KEY_STR			= "indexCheckpointKey";

    public BlockIndex(IndexConfig indexConfig, IDBProvider db, String id) {
        String[] indexItems = indexConfig.getAttrsToIndex();
        logger.debug(String.format("newBlockIndex() - indexItems length: [%d]", indexItems.length));
        Map<String, Boolean> indexItemMap = new HashMap<>();
        for(String indexItem : indexItems){
            indexItemMap.put(indexItem, true);
        }
        this.indexItemsMap = indexItemMap;
        this.db = db;
        this.ledgerId = id;
    }

    /**
     * 获取最新索引
     */
    @Override
    public long getLastBlockIndexed() throws LedgerException {
        byte[] blockNumBytes;
        blockNumBytes = db.get(constructIndexCheckpointKey());
        if (blockNumBytes == null){
        	logger.info("Got null result");
        	return -1;
        }
        return Util.bytesToLong(blockNumBytes, 0, blockNumBytes.length);
    }

    /**
     * 设置索引
     */
    @Override
    public void indexBlock(BlockIndexInfo blockIndexInfo) throws LedgerException {
        if(indexItemsMap.size() == 0){
           logger.debug("No indexing block, as nothing to index");
           return;
        }
        logger.debug(String.format("Indexing block [%s]", blockIndexInfo));
        FileLocPointer flp = blockIndexInfo.getFlp();
        List<TxIndexInfo> txOffsets = blockIndexInfo.getTxOffsets();
        TxValidationFlags txsfltr = new TxValidationFlags(blockIndexInfo.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER_VALUE).size());
        UpdateBatch batch = new UpdateBatch();
        byte[] flpBytes = flp.marshal();

        //index1 blockHash数据 - getBlockByHash()
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH))){
            batch.put(constructBlockHashKey(blockIndexInfo.getBlockHash()), flpBytes);
        }

        //index2 blockNum数据 - getBlockByNum()
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM))){
            batch.put(constructBlockNumKey(blockIndexInfo.getBlockNum()), flpBytes);
        }

        //index3 用来通过txid获取tx - getTxById()
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_ID))){
            for(TxIndexInfo txOffset : txOffsets){
                FileLocPointer txFlp = new FileLocPointer(flp.getFileSuffixNum(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for txID: [%s] to index", txFlp, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructTxIDKey(txOffset.getTxID()), txFlpBytes);
            }
        }

        //index4 查询历史数据
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM))){
            for(int i = 0; i < txOffsets.size(); i++){
                TxIndexInfo txOffset = txOffsets.get(i);
                FileLocPointer txFlp = new FileLocPointer(flp.getFileSuffixNum(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for tx num: [%d] ID: [%s] to blockNumTranNum index", txFlp, i, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructBlockNumTranNumKey(blockIndexInfo.getBlockNum(), (long) i), txFlpBytes);
            }
        }

        //index5 通过txid获取区块 getBlockByTxId()
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID))){
            for(TxIndexInfo txOffset : txOffsets){
                batch.put(constructBlockTxIDKey(txOffset.getTxID()), flpBytes);
            }
        }

        //index6 根据txid获取交易有效标志
        if(Boolean.TRUE.equals(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE))){
            for (int i = 0; i < txOffsets.size(); i++) {
                TxIndexInfo txOffset = txOffsets.get(i);
                batch.put(constructTxValidationCodeIDKey(txOffset.getTxID()), String.valueOf(txsfltr.flag(i).getNumber()).getBytes());
            }
        }

        batch.put(constructIndexCheckpointKey(), Util.longToBytes(blockIndexInfo.getBlockNum(), BlockFileManager.PEEK_BYTES_LEN));
        db.writeBatch(batch, true);
    }

    /**
     * 根据blockhash获取区块位置
     */
    @Override
    public FileLocPointer getBlockLocByHash(byte[] blockHash) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH) == null){
        	logger.error("Function getBlockLocByHash is not indexed");
        	return null;
        }
        byte[] b = db.get(constructBlockHashKey(blockHash));
        if(b == null){
        	logger.info("Get block loc by hash got null result");
        	return null;
        }
        FileLocPointer blkLoc = new FileLocPointer();
        blkLoc.unmarshal(b);
        return blkLoc;
    }

    /**
     * 根据blockID获取区块位置信息
     */
    @Override
    public FileLocPointer getBlockLocByBlockNum(long blockID) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM) == null){
	        logger.error("Function getBlockLocByBlockNum is not indexed");
	        return null;
        }
        byte[] b = db.get(constructBlockNumKey(blockID));
        if(b == null){
	        logger.info("Get block loc by block num got null result");
        	return null;
        }
        FileLocPointer blkLoc = new FileLocPointer();
        blkLoc.unmarshal(b);
        return blkLoc;
    }

    /**
     * 根据交易ID获取交易信息
     */
    @Override
    public FileLocPointer getTxLoc(String txID) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_ID) == null){
	        logger.error("Function getTxLoc is not indexed");
	        return null;
        }
        byte[] b = db.get(constructTxIDKey(txID));
        if(b == null){
	        logger.info("Get tx loc got null result");
        	return null;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    /**
     * 根据交易ID获取区块信息
     */
    @Override
    public FileLocPointer getBlockLocByTxID(String txID) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID) == null){
	        logger.error("Function getBlockLocByTxID is not indexed");
	        return null;
        }
        byte[] b = db.get(constructBlockTxIDKey(txID));
        if(b == null){
	        logger.info("Get block loc by tx id got null result");
        	return null;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    /**
     * 根据blockID, 交易ID获取交易信息
     */
    @Override
    public FileLocPointer getTXLocByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM) == null){
	        logger.error("Function getTXLocByBlockNumTranNum is not indexed");
	        return null;
        }
        byte[] b = db.get(constructBlockNumTranNumKey(blockNum, tranNum));
        if(b == null){
	        logger.info("Get tx loc by block num tran num got null result");
        	return null;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    /**
     * 获取交易验证结果
     */
    @Override
    public TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE) == null){
	        logger.error("Function getTxValidationCodeByTxID is not indexed");
	        return null;
        }
        byte[] raw = db.get(constructTxValidationCodeIDKey(txID));
        if(raw == null){
	        logger.info("Get tx validationg code by tx id got null result");
        	return null;
        } else if (raw.length != 1){
        	logger.error("Got wrong txValidationCode which length is 1");
        	return null;
        }
        return TransactionPackage.TxValidationCode.forNumber(Integer.valueOf(new String(raw)));
    }

    private byte[] constructBlockNumKey(long blockNum) {
        byte[] blkNumBytes = Util.longToBytes(blockNum, BlockFileManager.PEEK_BYTES_LEN);
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, BLOCK_NUM_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, blkNumBytes);
        return result;
    }

    private byte[] constructBlockHashKey(byte[] blockHash) {
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, BLOCK_HASH_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, blockHash);
        return result;
    }

    private byte[] constructTxIDKey(String txID) {
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, TX_ID_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, txID.getBytes());
        return result;
    }

    private byte[] constructBlockTxIDKey(String txID) {
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, BLOCK_TX_ID_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, txID.getBytes());
        return result;
    }

    private byte[] constructTxValidationCodeIDKey(String txID) {
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, TX_VALIDATION_RESULT_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, txID.getBytes());
        return result;
    }

    private byte[] constructBlockNumTranNumKey(long blockNum, long txNum) {
        byte[] blkNumBytes = Util.longToBytes(blockNum, BlockFileManager.PEEK_BYTES_LEN);
        byte[] txNumBytes = Util.longToBytes(txNum, BlockFileManager.PEEK_BYTES_LEN);
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX.getBytes());
        result = ArrayUtils.addAll(result, blkNumBytes);
        result = ArrayUtils.addAll(result, txNumBytes);
        return result;
    }

    private byte[] constructIndexCheckpointKey(){
        byte[] result = new byte[0];
        result = ArrayUtils.addAll(result, INDEX_CHECK_POINT_KEY_STR.getBytes());
        return result;
    }

    public Map<String, Boolean> getIndexItemsMap() {
        return indexItemsMap;
    }

    public void setIndexItemsMap(Map<String, Boolean> indexItemsMap) {
        this.indexItemsMap = indexItemsMap;
    }

    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
        this.db = db;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }
}
