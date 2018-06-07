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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.IDBProvider;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.common.util.BytesHexStrTranslate;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.*;

/**
 * 管理block file
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockFileManager {

    private static final String BLOCKFILE_PREFIX = "blockfile";
    private static final byte[] BLK_MGR_INFO_KEY = "blkMgrInfo".getBytes();
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockFileManager.class);

    public static final int LAST_BLOCK_BYTES = 0;
    public static final int CURRENT_OFFSET = 1;
    public static final int NUM_BLOCKS = 2;
    public static final Object lock = new Object();

    private String rootDir;
    private Config config;
    private IDBProvider db;
    private Index index;
    private CheckpointInfo cpInfo;
    private BlockFileWriter currentFileWriter;
    private Ledger.BlockchainInfo bcInfo;
    private String ledgerId;

    /**
     * 默认构造方法
     */
    public BlockFileManager(){}

    /**
     * 创建新的blockfilemanager对象
     */
    public BlockFileManager(String id,
                            Config config,
                            IndexConfig indexConfig,
                            IDBProvider indexStore) throws LedgerException{

        logger.debug(String.format("newBlockfileMgr() initializing file-based block storage for ledger: %s", id));
        //根据配置文件、id生成rootDir
        this.ledgerId = id;
        this.config = config;
        this.rootDir = config.getLedgerBlockDir(id);
        this.db = indexStore;
        this.cpInfo = loadCurrentInfo();
        //创建rootdir
        IoUtil.createDirIfMissing(getRootDir());
        //设置检查点信息
        if(this.cpInfo == null){
            logger.info("Getting block information from block storage");
            this.cpInfo = BlockFileHelper.constructCheckpointInfoFromBlockFiles(this.rootDir);
            logger.debug(String.format("Info constructed by scanning the blocks dir = %s", this.cpInfo.toString()));
        } else {
            logger.debug("Synching block information from block storage (if needed)");
            syncCpInfoFromFS(this.rootDir, this.cpInfo);
        }
        //保存检查点信息到leveldb中
        //blkMgrInfoKey-checkpointInfo
        saveCurrentInfo(this.cpInfo, true);
        //写入文件 writer类
        this.currentFileWriter = BlockFileRw.newBlockfileWriter(deriveBlockfilePath(this.rootDir, this.cpInfo.getLastestFileChunkSuffixNum()));
        //修剪文件为检查点保存的文件大小
        this.currentFileWriter.truncateFile(this.cpInfo.getLatestFileChunksize());
        //设置blockindex对象
        this.index = new BlockIndex(indexConfig, indexStore, id);
        //设置blockchainINfo对象
        this.bcInfo = Ledger.BlockchainInfo.newBuilder()
                .setHeight(0)
                .setCurrentBlockHash(ByteString.EMPTY)
                .setPreviousBlockHash(ByteString.EMPTY)
                .build();

        if (!this.cpInfo.getChainEmpty()) {
            try {
                syncIndex();
            } catch (LedgerException e) {
                logger.error(e.getMessage(), e);
                throw new LedgerException("Got error when syncIdex");
            }
            Common.BlockHeader lastBlockHeader = retrieveBlockHeaderByNumber(this.cpInfo.getLastBlockNumber());
            ByteString lastBlockHash = lastBlockHeader.getDataHash();
            ByteString previousBlockHash = lastBlockHeader.getPreviousHash();
            this.bcInfo = Ledger.BlockchainInfo.newBuilder()
                    .setHeight(this.cpInfo.getLastBlockNumber() + 1)
                    .setCurrentBlockHash(lastBlockHash)
                    .setPreviousBlockHash(previousBlockHash)
                    .build();
        }
    }

    /**
     * 更新检查点信息
     */
    private void syncCpInfoFromFS(String rootDir, CheckpointInfo cpInfo) throws LedgerException {
        logger.debug(String.format("Starting checkpoint [%s]", cpInfo));
        //组装区块文件名
        String filePath = deriveBlockfilePath(rootDir, cpInfo.getLastestFileChunkSuffixNum());
        //获取区块文件大小, 判断其存在性
        long size = IoUtil.fileExists(filePath);
        logger.debug(String.format("Status of file [%s]: exists=[%s], size=[%d]", filePath, size < 0, size));
        if(size < 0 || size == cpInfo.getLatestFileChunksize()){
            return;
        }
        //获取最新提交的区块
        List<Object> lastCompleteBlockInfo = scanForLastCompleteBlock(rootDir, cpInfo.getLastestFileChunkSuffixNum(), (long) cpInfo.getLatestFileChunksize());
        long endOffsetLastBlock = (long) lastCompleteBlockInfo.get(CURRENT_OFFSET);
        int numBlocks = (int) lastCompleteBlockInfo.get(NUM_BLOCKS);
        cpInfo.setLastestFileChunkSuffixNum((int) endOffsetLastBlock);
        if(numBlocks == 0){
            return;
        }
        if(cpInfo.getChainEmpty()){
            cpInfo.setLastBlockNumber((long) (numBlocks - 1));
        } else {
            cpInfo.setLastBlockNumber(cpInfo.getLastBlockNumber() + (long) numBlocks);
        }
        cpInfo.setChainEmpty(false);
        logger.debug(String.format("Checkpoint after updates by scanning the last file segment: %s", cpInfo.toString()));
    }

    /**
     * 组装区块文件名
     * rootDir/blockfile_000000
     */
    public static String deriveBlockfilePath(String rootDir, Integer suffixNum) {
        return String.format("%s/%s_%06d", rootDir, BLOCKFILE_PREFIX, suffixNum);
    }

    public void close() {

    }

    /**
     * 写下一文件
     */
    private void moveToNextFile() throws LedgerException {
        CheckpointInfo cpInfo = new CheckpointInfo(this.cpInfo.getLastestFileChunkSuffixNum() + 1,
                0,
                false,
                this.cpInfo.getLastBlockNumber());
        BlockFileWriter nextFileWriter = BlockFileRw.newBlockfileWriter(deriveBlockfilePath(this.rootDir, cpInfo.getLastestFileChunkSuffixNum()));
        saveCurrentInfo(cpInfo, true);
        this.currentFileWriter = nextFileWriter;
        updateCheckpoint(cpInfo);
    }

    /**
     * 添加区块
     * blockBytesLenEncoded     8
     * blockbytes               blockBytesLen
     */
    public void addBlock(Common.Block block) throws LedgerException {
        if(block.getHeader().getNumber() != bcInfo.getHeight()){
            throw new LedgerException(String.format("Block number should have been %d but was %d", getBlockchainInfo().getHeight(), block.getHeader().getNumber()));
        }
        //本次提交区块位置应为之前提交区块位置后8位
        Map.Entry<SerializedBlockInfo, byte[]> entry = BlockSerialization.serializeBlock(block, cpInfo.getLatestFileChunksize() + 8);
        SerializedBlockInfo info = entry.getKey();
        byte[] blockBytes = entry.getValue();

        ByteString blockHash = block.getHeader().getDataHash();
        List<TxIndexInfo> txOffsets = info.getTxOffsets();
        int currentOffset = cpInfo.getLatestFileChunksize();
        //区块长度(尾部)
        long blockBytesLen = blockBytes.length;
        //区块长度byte
        byte[] blockBytesLenEncoded = Util.longToBytes(blockBytesLen, 8);
        //总共添加的长度
        long totalBytesToAppend = blockBytesLen + blockBytesLenEncoded.length;
        //配置的最大文件长度
        long maxBlockFileSize = config.getMaxBlockFileSize();
        //总长度大于配置的文件长度,重新开启新文件
        if(currentOffset + totalBytesToAppend > maxBlockFileSize){
            moveToNextFile();
            currentOffset = 0;
        }
        try {
            //添加区块长度
            currentFileWriter.append(blockBytesLenEncoded, false);
        } catch (LedgerException e) {
            currentFileWriter.truncateFile(cpInfo.getLatestFileChunksize());
            logger.error("Got error when appending block to file " + e.getMessage());
        }
        //添加区块
        currentFileWriter.append(blockBytes, true);
        //设置新的区块信息
        CheckpointInfo currentCPInfo = cpInfo;
        CheckpointInfo newCPInfo = new CheckpointInfo(currentCPInfo.getLastestFileChunkSuffixNum(),
                currentCPInfo.getLatestFileChunksize() + (int) totalBytesToAppend,
                false,
                block.getHeader().getNumber());
        //保存
        saveCurrentInfo(newCPInfo, false);
        FileLocPointer blockFLP = new FileLocPointer(newCPInfo.getLastestFileChunkSuffixNum(), currentOffset, 0);

        BlockIndexInfo idxInfo = new BlockIndexInfo(block.getHeader().getNumber(),
                blockHash.toByteArray(),
                blockFLP,
                txOffsets,
                block.getMetadata());
        index.indexBlock(idxInfo);
        updateCheckpoint(newCPInfo);
        updateBlockchainInfo(blockHash.toByteArray(), block);
    }

    /**
     * 同步索引
     */
    private void syncIndex() throws LedgerException{
        long lastBlockIndexed = 0;
        boolean indexEmpty = false;
        //获取最新索引
        try {
            lastBlockIndexed = index.getLastBlockIndexed();
        } catch (LedgerException e) {
            if(!"[Ledger]NoBlockIndexed".equals(e.getMessage())){
                logger.error("Got error when syncIndex");
                throw e;
            }
            indexEmpty = true;
        }
        //初始化index
        int startFileNum = 0;
        long startOffset = 0;
        boolean skipFirstBlock = false;
        //获取最新block文件编号
        int endFileNum = cpInfo.getLastestFileChunkSuffixNum();
        long startingBlockNum = 0;
        //索引不为空
        if(!indexEmpty){
            //索引和区块序号同时为0, 完成同步
            if(lastBlockIndexed == cpInfo.getLastBlockNumber()){
                logger.debug("Both the block files and indixes are in sync");
                return;
            }
            logger.debug(String.format("Last block indexed [%d], last block present in block files [%d]"
                    , lastBlockIndexed, cpInfo.getLastBlockNumber()));
            FileLocPointer flp;
            //获取区块位置
            try {
                flp = index.getBlockLocByBlockNum(lastBlockIndexed);
            } catch (LedgerException e) {
                throw e;
            }
            startFileNum = flp.getFileSuffixNum();
            startOffset = flp.getLocPointer().getOffset();
            skipFirstBlock = true;
            startingBlockNum = lastBlockIndexed + 1;
        } else {
            logger.debug(String.format("No block indexed, last block present in block files=[%s]"
                    , cpInfo.getLastBlockNumber()));
        }

        logger.info(String.format("Start building index form block [%d] to last block [%d]"
                , startingBlockNum, cpInfo.getLastBlockNumber()));
        //初始化block流
        BlockStream stream = new BlockStream(rootDir, startFileNum, startOffset, endFileNum);
        byte[] blockBytes = null;
        BlockPlacementInfo blockPlacementInfo = null;
        //读取区块文件
        if(skipFirstBlock){
            blockBytes = stream.nextBlockBytes();
            if(blockBytes == null){
                throw new LedgerException(String.format("Block btyes for block num = [%d] should not be null here." +
                        " The indexes for the block are already present", lastBlockIndexed));
            }
        }
        BlockIndexInfo blockIndexInfo = new BlockIndexInfo();
        while(true){
            AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> entry = stream.nextBlockBytesAndPlacementInfo();
            blockBytes = entry.getKey();
            blockPlacementInfo = entry.getValue();
            if(blockBytes == null){
                break;
            }
            //解码block
            SerializedBlockInfo info = BlockSerialization.extractSerializedBlockInfo(blockBytes, cpInfo.getLatestFileChunksize() + 8);

            int numBytesToShift = (int) (blockPlacementInfo.getBlockBytesOffset() - blockPlacementInfo.getBlockStartOffset());
            for(TxIndexInfo offset : info.getTxOffsets()){
                offset.getLoc().setOffset(offset.getLoc().getOffset() + numBytesToShift);
            }

            //更新blockIndexInfo
            blockIndexInfo.setBlockHash(info.getBlockHeader().getDataHash().toByteArray());
            blockIndexInfo.setBlockNum(info.getBlockHeader().getNumber());
            //封装文件信息
            FileLocPointer flp = new FileLocPointer(blockPlacementInfo.getFileNum(),
                    (int) blockPlacementInfo.getBlockStartOffset(),
                    0);
            blockIndexInfo.setTxOffsets(info.getTxOffsets());
            blockIndexInfo.setMetadata(info.getMetadata());

            logger.debug(String.format("syncIndex() indexing block [%d]", blockIndexInfo.getBlockNum()));
            index.indexBlock(blockIndexInfo);
            if(blockIndexInfo.getBlockNum() % 10000 == 0){
                logger.info(String.format("Indexed block number [%d]", blockIndexInfo.getBlockNum()));
            }
        }
        logger.info(String.format("Finished building index. Last block indexed [%d]", blockIndexInfo.getBlockNum()));
    }

    /**
     * 更新检查点信息
     */
    private void updateCheckpoint(CheckpointInfo cpInfo) {
        synchronized (lock){
            this.cpInfo = cpInfo;
            logger.debug(String.format("Brodcasting about update checkpointInfo: %s", cpInfo));
            //通知所有等待区块的线程
            lock.notifyAll();
        }
    }

    /**
     * 更新区块链信息
     */
    private void updateBlockchainInfo(byte[] latestBlockHash, Common.Block latestBlock) {
        Ledger.BlockchainInfo currentBCInfo = getBlockchainInfo(); bcInfo = Ledger.BlockchainInfo.newBuilder()
                .setHeight(currentBCInfo.getHeight() + 1)
                .setCurrentBlockHash(ByteString.copyFrom(latestBlockHash))
                .setPreviousBlockHash(latestBlock.getHeader().getPreviousHash())
                .build();
    }

    /**
     * 根据区块hash查找区块
     */
    public Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException {
        logger.debug(String.format("retrieveBlockByHash() - blockHash = [%s]", BytesHexStrTranslate.bytesToHexFun1(blockHash)));
        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByHash(blockHash);
        } catch (LedgerException e) {
            throw e;
        }
        return fetchBlock(loc);
    }

    /**
     * 根据区块号查找区块
     */
    public Common.Block retrieveBlockByNumber(long blockNum) throws LedgerException {
        logger.debug(String.format("retrieveBlockByHash() - blockHash = [%d]", blockNum));
        if(blockNum == Long.MAX_VALUE){
            blockNum = getBlockchainInfo().getHeight() - 1;
        }

        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByBlockNum(blockNum);
        } catch (LedgerException e) {
            throw e;
        }
        return fetchBlock(loc);
    }

    /**
     * 根据交易ID查找区块
     */
    public Common.Block retrieveBlockByTxID(String txID) throws LedgerException {
        logger.debug(String.format("retrieveBlockByTxID() - txID = [%s]", txID));

        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByTxID(txID);
        } catch (LedgerException e) {
            throw e;
        }
        return fetchBlock(loc);
    }

    /**
     * 根据交易ID查找交易校验码
     */
    public TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) throws LedgerException{
        logger.debug(String.format("retrieveTxValidationCodeByTxID() - txID = [%s]", txID));
        try {
            return index.getTxValidationCodeByTxID(txID);
        } catch (LedgerException e) {
            throw e;
        }
    }

    /**
     * 根据区块号查找区块头
     * @param blockNum
     * @return
     * @throws LedgerException
     */
    public Common.BlockHeader retrieveBlockHeaderByNumber(long blockNum) throws  LedgerException {
        logger.debug(String.format("retrieveBlockHeaderByNumber - blockNum = [%d]", blockNum));
        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByBlockNum(blockNum);
        } catch (LedgerException e) {
            throw e;
        }
        byte[] blockBytes = fetchBlockBytes(loc);
        SerializedBlockInfo info = null;
        try {
            info = BlockSerialization.extractSerializedBlockInfo(blockBytes, loc.getLocPointer().getOffset() + 8);
        } catch (LedgerException e) {
            throw e;
        }
        return info.getBlockHeader();
    }

    /**
     * 获取区块迭代器
     */
    public BlocksItr retrieveBlocks(long startNum) {
        return new BlocksItr(this, startNum);
    }

    /**
     * 根据交易ID查找交易
     */
    public Common.Envelope retrieveTransactionByID(String txID) throws LedgerException {
        logger.debug(String.format("retrieveTransactionByID() - txID = [%s]", txID));
        FileLocPointer loc = null;
        try {
            loc = index.getTxLoc(txID);
        } catch (LedgerException e) {
            throw e;
        }
        return fetchTransactionEnvelope(loc);
    }

    /**
     * 根据交易区块号以及交易序号查找交易
     */
    public Common.Envelope retrieveTransactionByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException{
        logger.debug(String.format("retrieveTransactionByBlockNumTranNum() - blockNum = [%d], tranNum = [%d]"
                , blockNum, tranNum));
        FileLocPointer loc = null;
        try {
            loc = index.getTXLocByBlockNumTranNum(blockNum, tranNum);
        } catch (LedgerException e) {
            throw e;
        }
        return fetchTransactionEnvelope(loc);
    }

    /**
     * 获取区块
     */
    private Common.Block fetchBlock(FileLocPointer lp) throws LedgerException{
        byte[] blockBytes = fetchBlockBytes(lp);
        if (blockBytes == null){
            throw new LedgerException(String.format("Fail to fetch block by [%s]", lp));
        }
        if(blockBytes[0] != (byte) 10){
            throw new LedgerException(String.format("Fetch error block by [%s]", lp));
        }
        return BlockSerialization.deserializeBlock(blockBytes);
   }

    /**
     * 获取交易Envelope
     */
    private Common.Envelope fetchTransactionEnvelope(FileLocPointer lp) throws LedgerException{
        logger.debug(String.format("Entering fetchTransactionEnvelope() %s", lp));
        byte[] txEnvelopeBytes = fetchRawBytes(lp);
        if (txEnvelopeBytes == null){
            throw new LedgerException(String.format("Fail to fetch envelope by [%s]", lp));
        }
        if(txEnvelopeBytes[0] != (byte) 10){
            throw new LedgerException(String.format("Fetch error envelope by [%s]", lp));
        }
        Common.Envelope envelope = null;
        try {
            envelope = Common.Envelope.parseFrom(txEnvelopeBytes);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting envelope from block bytes");
            throw new LedgerException(e);
        }
        return envelope;
    }

    /**
     * 获取区块
     */
    private byte[] fetchBlockBytes(FileLocPointer lp) throws LedgerException {
        BlockFileStream stream = null;
        try {
            stream = new BlockFileStream(rootDir, lp.getFileSuffixNum(), (long) (lp.getLocPointer().getOffset()));
            byte[] bytes = stream.nextBlockBytes();
            return bytes;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 获取获取对应位置字节
     */
    private byte[] fetchRawBytes(FileLocPointer lp) throws LedgerException {
        BlockFileReader reader = null;
        String filePath = deriveBlockfilePath(rootDir, lp.getFileSuffixNum());
        reader = BlockFileRw.newBlockfileReader(filePath);
        return reader.read(lp.getLocPointer().getOffset(), lp.getLocPointer().getBytesLength());
    }

    /**
     * 加载当前信息
     */
    private CheckpointInfo loadCurrentInfo() throws LedgerException{
        byte[] b = null;
        b = db.get(compositeBlockManagerInfoKey(ledgerId));
        if(b == null){
            logger.debug("Fail to got BLK_MGR_INFO_KEY with " + ledgerId);
            return null;
        }
        CheckpointInfo checkpointInfo = new CheckpointInfo();
        checkpointInfo.unmarshal(b);
        return checkpointInfo;
    }

    /**
     * 将当前检查点信息保存到leveldb中
     */
    private void saveCurrentInfo(CheckpointInfo checkpointInfo, Boolean sync) throws LedgerException {
        byte[] b = checkpointInfo.marshal();
        db.put(compositeBlockManagerInfoKey(ledgerId), b, sync);
    }

    /**
     * 检索最新的完整区块
     * @Return LAST_BLOCK_BYTES: 最新区块
     *          CURRENT_OFFSET: 位置
     *          NUM_BLOCKS: 区块数量
     */
    public static List<Object> scanForLastCompleteBlock(String rootDir, Integer fileNum, long startingOffset) throws LedgerException{
        BlockFileStream stream = null;
        byte[] blockBytes = null;
        byte[] lastBlockBytes = null;
        try {
            int numBlock = 0;
            stream = new BlockFileStream(rootDir, fileNum, startingOffset);
            while (true) {
                blockBytes = stream.nextBlockBytes();
                if(blockBytes == null){
                    break;
                }
                lastBlockBytes = blockBytes;
                numBlock++;
            }
            List<Object> list = new ArrayList<>();
            list.add(LAST_BLOCK_BYTES, lastBlockBytes);
            list.add(CURRENT_OFFSET, stream.getCurrentOffset());
            list.add(NUM_BLOCKS, numBlock);
            return list;
        } finally {
            if(stream != null){
                stream.close();
            }
        }
    }

    private byte[] compositeBlockManagerInfoKey(String ledgerid){
        return ArrayUtils.addAll(BLK_MGR_INFO_KEY, ledgerid.getBytes());
    }

    public static String getBlockfilePrefix() {
        return BLOCKFILE_PREFIX;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public IDBProvider getDb() {
        return db;
    }

    public void setDb(IDBProvider db) {
        this.db = db;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public CheckpointInfo getCpInfo() {
        return cpInfo;
    }

    public void setCpInfo(CheckpointInfo cpInfo) {
        this.cpInfo = cpInfo;
    }

    public BlockFileWriter getCurrentFileWriter() {
        return currentFileWriter;
    }

    public void setCurrentFileWriter(BlockFileWriter currentFileWriter) {
        this.currentFileWriter = currentFileWriter;
    }

    public Ledger.BlockchainInfo getBcInfo() {
        return bcInfo;
    }

    public void setBcInfo(Ledger.BlockchainInfo bcInfo) {
        this.bcInfo = bcInfo;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Ledger.BlockchainInfo getBlockchainInfo() {
        return bcInfo;
    }

}
