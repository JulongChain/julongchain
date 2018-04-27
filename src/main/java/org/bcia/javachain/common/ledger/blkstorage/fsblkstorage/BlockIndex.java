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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.BlockStorage;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.util.TxValidationFlags;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作block索引类
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockIndex implements Index {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockIndex.class);

    private Map<String, Boolean> indexItemsMap = new HashMap<>();
    private DBProvider db;

    public static final String BLOCK_NUM_IDX_KEY_PREFIX           = "n";
    public static final String BLOCK_HASH_IDX_KEY_PREFIX          = "h";
    public static final String TX_ID_IDX_KEY_PREFIX               = "t";
    public static final String BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX    = "a";
    public static final String BLOCK_TX_ID_IDX_KEY_PREFIX          = "b";
    public static final String TX_VALIDATION_RESULT_IDX_KEY_PREFIX = "v";
    public static final String INDEX_CHECK_POINT_KEY_STR          = "indexCheckpointKey";
    private static final byte[] INDEX_CHECKPOINT_KEY  = INDEX_CHECK_POINT_KEY_STR.getBytes();

    public static BlockIndex newBlockIndex(IndexConfig indexConfig, DBProvider db) {
        String[] indexItems = indexConfig.getAttrsToIndex();
        logger.debug(String.format("newBlockIndex() - indexItems length: [%d]", indexItems.length));
        Map<String, Boolean> indexItemMap = new HashMap<>();
        for(String indexItem : indexItems){
           indexItemMap.put(indexItem, true);
        }
        BlockIndex index = new BlockIndex();
        index.setIndexItemsMap(indexItemMap);
        index.setDb(db);
        return index;
    }

    /**
     * 获取最新索引
     */
    @Override
    public long getLastBlockIndexed() throws LedgerException {
        byte[] blockNumBytes = null;
        blockNumBytes = db.get(INDEX_CHECKPOINT_KEY);
        if (blockNumBytes == null){
            throw new LedgerException("NoBlockIndexed");
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
        TxValidationFlags txsfltr = new TxValidationFlags(blockIndexInfo.getMetadata().getMetadataList().size());
        UpdateBatch batch = LevelDBProvider.newUpdateBatch();
        byte[] flpBytes = flp.marshal();

        //index1
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH)){
            batch.put(constructBlockHashKey(blockIndexInfo.getBlockHash()), flpBytes);
        }

        //index2
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM)){
            batch.put(constructBlockNumKey(blockIndexInfo.getBlockNum()), flpBytes);
        }

        //index3 用来通过txid获取tx
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_ID)){
            for(TxIndexInfo txOffset : txOffsets){
                FileLocPointer txFlp = FileLocPointer.newFileLocationPointer(flp.getFileSuffixNum(), flp.getLocPointer().getOffset(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for txID: [%s] to index", txFlp, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructBlockTxIDKey(txOffset.getTxID()), txFlpBytes);
            }
        }

        //index4 查询历史数据
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM)){
            for(int i = 0; i < txOffsets.size(); i++){
                TxIndexInfo txOffset = txOffsets.get(i);
                FileLocPointer txFlp = FileLocPointer.newFileLocationPointer(flp.getFileSuffixNum(), flp.getLocPointer().getOffset(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for tx num: [%d] ID: [%s] to blockNumTranNum index", txFlp, i, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructBlockNumTranNumKey(blockIndexInfo.getBlockNum(), (long) i), txFlpBytes);
            }
        }

        //index5 通过txid获取区块
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID)){
            for(TxIndexInfo txOffset : txOffsets){
                batch.put(constructBlockTxIDKey(txOffset.getTxID()), flpBytes);
            }
        }

        //index6 根据txid获取交易有效性
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE)){
            for (int i = 0; i < txOffsets.size(); i++) {
                TxIndexInfo txOffset = txOffsets.get(i);
                batch.put(constructTxValidationCodeIDKey(txOffset.getTxID()), String.valueOf(txsfltr.flag(i).getNumber()).getBytes());
            }
        }

        batch.put(INDEX_CHECKPOINT_KEY, Util.longToBytes(blockIndexInfo.getBlockNum(), 8));
        db.writeBatch(batch, true);
    }

    /**
     * 根据blockhash获取区块位置
     */
    @Override
    public FileLocPointer getBlockLocByHash(byte[] blockHash) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockHashKey(blockHash));
        if(b == null){
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
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockNumKey(blockID));
        if(b == null){
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
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructTxIDKey(txID));
        if(b == null){
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
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockTxIDKey(txID));
        if(b == null){
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
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockNumTranNumKey(blockNum, tranNum));
        if(b == null){
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
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] raw = db.get(constructTxValidationCodeIDKey(txID));
        if(raw == null){
            return null;
        } else if (raw.length != 1){
            throw new LedgerException("Invalid value in indexItems");
        }
        return TransactionPackage.TxValidationCode.forNumber((int) raw[0]);
    }

    byte[] constructBlockNumKey(long blockNum) {
        byte[] blkNumBytes = Util.longToBytes(blockNum, 8);
        return ArrayUtils.addAll(BLOCK_NUM_IDX_KEY_PREFIX.getBytes(), blkNumBytes);
    }

    byte[] constructBlockHashKey(byte[] blockHash) {
        return ArrayUtils.addAll(BLOCK_HASH_IDX_KEY_PREFIX.getBytes(), blockHash);
    }

    byte[] constructTxIDKey(String txID) {
        return ArrayUtils.addAll(TX_ID_IDX_KEY_PREFIX.getBytes(), txID.getBytes());
    }

    byte[] constructBlockTxIDKey(String txID) {
        return ArrayUtils.addAll(BLOCK_TX_ID_IDX_KEY_PREFIX.getBytes(), txID.getBytes());
    }

    byte[] constructTxValidationCodeIDKey(String txID) {
        return ArrayUtils.addAll(TX_VALIDATION_RESULT_IDX_KEY_PREFIX.getBytes(), txID.getBytes());
    }

    byte[] constructBlockNumTranNumKey(long blockNum, long txNum) {
        byte[] blkNumBytes = Util.longToBytes(blockNum, 8);
        byte[] txNumBytes = Util.longToBytes(txNum, 8);
        byte[] key = ArrayUtils.addAll(blkNumBytes, txNumBytes);
        return ArrayUtils.addAll(BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX.getBytes(), key);
    }

    public Map<String, Boolean> getIndexItemsMap() {
        return indexItemsMap;
    }

    public void setIndexItemsMap(Map<String, Boolean> indexItemsMap) {
        this.indexItemsMap = indexItemsMap;
    }

    public DBProvider getDb() {
        return db;
    }

    public void setDb(DBProvider db) {
        this.db = db;
    }
}
