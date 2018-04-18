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
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBHandle;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
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
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockIndex implements Index {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockIndex.class);

    private Map<String, Boolean> indexItemsMap = new HashMap<>();
    private LevelDbProvider db;

    public static final String BLOCK_NUM_IDX_KEY_PREFIX           = "n";
    public static final String BLOCK_HASH_IDX_KEY_PREFIX          = "h";
    public static final String TX_ID_IDX_KEY_PREFIX               = "t";
    public static final String BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX    = "a";
    public static final String BLOCK_TX_ID_IDX_KEY_PREFIX          = "b";
    public static final String TX_VALIDATION_RESULT_IDX_KEY_PREFIX = "v";
    public static final String INDEX_CHECK_POINT_KEY_STR          = "indexCheckpointKey";
    private static final byte[] INDEX_CHECKPOINT_KEY  = INDEX_CHECK_POINT_KEY_STR.getBytes();

    public static BlockIndex newBlockIndex(IndexConfig indexConfig, LevelDbProvider db) {
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

    public Long getLastBlockIndexed() throws LedgerException {
        byte[] blockNumBytes = null;
        blockNumBytes = db.get(INDEX_CHECKPOINT_KEY);
        if (blockNumBytes == null){
            throw new LedgerException("NoBlockIndexed");
        }
        return Util.bytesToLong(blockNumBytes, 0, blockNumBytes.length);
    }

    public void indexBlock(BlockIdxInfo blockIdxInfo) throws LedgerException {
        if(indexItemsMap.size() == 0){
           logger.debug("No indexing block, as nothing to index");
           return;
        }
        logger.debug(String.format("Indexing block [%s]", blockIdxInfo));
        FileLocPointer flp = blockIdxInfo.getFlp();
        List<TxIndexInfo> txOffsets = blockIdxInfo.getTxOffsets();
        TxValidationFlags txsfltr = new TxValidationFlags(blockIdxInfo.getMetadata().getMetadataList());
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        byte[] flpBayte = flp.marshal();

        //index1
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH) == null){
            batch.put(constructBlockHashKey(blockIdxInfo.getBlockHash()), flpBayte);
        }

        //index2
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM) == null){
            batch.put(constructBlockNumKey(blockIdxInfo.getBlockNum()), flpBayte);
        }

        //index3 用来通过txid获取tx
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_ID) == null){
            for(TxIndexInfo txOffset : txOffsets){
                FileLocPointer txFlp = FileLocPointer.newFileLocationPointer(flp.getFileSuffixNum(), flp.getLocPointer().getOffset(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for txID: [%s] to index", txFlp, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructBlockTxIDKey(txOffset.getTxID()), txFlpBytes);
            }
        }

        //index4 查询历史数据
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRANS_NUM) == null){
            for(int i = 0; i < txOffsets.size(); i++){
                TxIndexInfo txOffset = txOffsets.get(i);
                FileLocPointer txFlp = FileLocPointer.newFileLocationPointer(flp.getFileSuffixNum(), flp.getLocPointer().getOffset(), txOffset.getLoc());
                logger.debug(String.format("Adding txLoc [%s] for tx num: [%d] ID: [%s] to blockNumTranNum index", txFlp, i, txOffset.getTxID()));
                byte[] txFlpBytes = txFlp.marshal();
                batch.put(constructBlockNumTranNumKey(blockIdxInfo.getBlockNum(), (long) i), txFlpBytes);
            }
        }

        //index5 通过txid获取区块
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID) == null){
            for(TxIndexInfo txOffset : txOffsets){
                batch.put(constructBlockTxIDKey(txOffset.getTxID()), flpBayte);
            }
        }

        //index6 根据txid获取交易有效性
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE) == null){
            for (int i = 0; i < txOffsets.size(); i++) {
                TxIndexInfo txOffset = txOffsets.get(i);
                batch.put(constructTxValidationCodeIDKey(txOffset.getTxID()), String.valueOf(txsfltr.flag(i).getNumber()).getBytes());
            }
        }
    }

    public FileLocPointer getBlockLocByHash(byte[] blockHash) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_HASH) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockHashKey(blockHash));
        if(b == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        }
        FileLocPointer blkLoc = new FileLocPointer();
        blkLoc.unmarshal(b);
        return blkLoc;
    }

    public FileLocPointer getBlockLocByBlockNum(Long blockNum) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockNumKey(blockNum));
        if(b == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        }
        FileLocPointer blkLoc = new FileLocPointer();
        blkLoc.unmarshal(b);
        return blkLoc;
    }

    public FileLocPointer getTxLoc(String txID) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_ID) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructTxIDKey(txID));
        if(b == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    public FileLocPointer getBlockLocByTxID(String txID) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockTxIDKey(txID));
        if(b == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    public FileLocPointer getTXLocByBlockNumTranNum(Long blockNum, Long tranNum) throws LedgerException{
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRANS_NUM) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] b = db.get(constructBlockNumTranNumKey(blockNum, tranNum));
        if(b == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        }
        FileLocPointer txFLP = new FileLocPointer();
        txFLP.unmarshal(b);
        return txFLP;
    }

    public TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException {
        if(indexItemsMap.get(BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE) == null){
            throw BlockStorage.ERR_ARRT_NOT_INDEXED;
        }
        byte[] raw = db.get(constructTxValidationCodeIDKey(txID));
        if(raw == null){
            throw BlockStorage.ERR_NOT_FOUND_IN_INDEX;
        } else if (raw.length != 1){
            throw new LedgerException("Invalid value in indexItems");
        }
        return TransactionPackage.TxValidationCode.forNumber((int) raw[0]);
    }

    byte[] constructBlockNumKey(Long blockNum) {
        byte[] blkNumBytes = Util.longToBytes(blockNum, 8);
        return ArrayUtils.addAll(BLOCK_HASH_IDX_KEY_PREFIX.getBytes(), blkNumBytes);
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

    byte[] constructBlockNumTranNumKey(Long blockNum, Long txNum) {
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

    public LevelDbProvider getDb() {
        return db;
    }

    public void setDb(LevelDbProvider db) {
        this.db = db;
    }
}
