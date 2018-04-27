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
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private String rootDir;
    private Conf conf;
    private DBProvider db;
    private Index index;
    private CheckpointInfo cpInfo;
    private BlockFileWriter currentFileWriter;
    private Ledger.BlockchainInfo bcInfo;
    private Lock lock;

    /**
     * 创建新的blockfilemanager对象
     */
    public static BlockFileManager newBlockfileMgr(String id,
                                                   Conf conf,
                                                   IndexConfig indexConfig,
                                                   DBProvider indexStore) throws LedgerException {
        BlockFileManager mgr = new BlockFileManager();
        logger.debug(String.format("newBlockfileMgr() initializing file-based block storage for ledger: %s", id));
        //根据配置文件、id生成rootDir
        String rootDir = conf.getLedgerBlockDir(id);
        mgr.setRootDir(rootDir);
        mgr.setConf(conf);
        mgr.setDb(indexStore);
        mgr.setLock(new ReentrantLock());
        mgr.setCpInfo(mgr.loadCurrentInfo());
        //创建rootdir
        IoUtil.createDirIfMissing(mgr.getRootDir());
        //设置检查点信息
        if(mgr.getCpInfo() == null){
            logger.info("Getting block information from block storage");
            mgr.setCpInfo(BlockFileHelper.constructCheckpointInfoFromBlockFiles(mgr.getRootDir()));
            logger.debug(String.format("Info constructed by scanning the blocks dir = %s", mgr.getCpInfo().toString()));
        } else {
            logger.debug("Synching block information from block storage (if needed)");
            mgr.syncCpInfoFromFS(mgr.getRootDir(), mgr.getCpInfo());
        }
        //保存检查点信息到leveldb中
        //blkMgrInfoKey-checkpointInfo
        mgr.saveCurrentInfo(mgr.getCpInfo(), true);
        //写入文件 writer类
        mgr.setCurrentFileWriter(BlockFileRw.newBlockfileWriter(deriveBlockfilePath(mgr.getRootDir(), mgr.getCpInfo().getLastestFileChunkSuffixNum())));
        //修剪文件为检查点保存的文件大小
        mgr.getCurrentFileWriter().truncateFile(mgr.getCpInfo().getLatestFileChunksize());
        //设置blockindex对象
        mgr.setIndex(BlockIndex.newBlockIndex(indexConfig, indexStore));
        //设置blockchainINfo对象
        mgr.setBcInfo(Ledger.BlockchainInfo.newBuilder()
                .setHeight(0)
                .setCurrentBlockHash(ByteString.EMPTY)
                .setPreviousBlockHash(ByteString.EMPTY)
                .build());

        if(!mgr.getCpInfo().getChainEmpty()){
            try {
                mgr.syncIndex();
            } catch (LedgerException e) {
                return null;
            }
            Common.BlockHeader lastBlockHeader = mgr.retrieveBlockHeaderByNumber(mgr.getCpInfo().getLastBlockNumber());
            ByteString lastBlockHash = lastBlockHeader.getDataHash();
            ByteString previousBlockHash = lastBlockHeader.getPreviousHash();
            mgr.setBcInfo(Ledger.BlockchainInfo.newBuilder()
                    .setHeight(mgr.getCpInfo().getLastBlockNumber() + 1)
                    .setCurrentBlockHash(lastBlockHash)
                    .setPreviousBlockHash(previousBlockHash)
                    .build());
        }
        return mgr;
    }

    /**
     * 更新检查点信息
     */
    public synchronized void syncCpInfoFromFS(String rootDir, CheckpointInfo cpInfo) throws LedgerException {
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

    //组装区块文件名
    //  rootDir/blockfile000000
    public static String deriveBlockfilePath(String rootDir, Integer suffixNum) {
        return String.format("%s/%s%06d", rootDir, BLOCKFILE_PREFIX, suffixNum);
    }

    public void close() {

    }

    /**
     * 写下一文件
     */
    public synchronized void moveToNextFile() throws LedgerException {
        CheckpointInfo cpInfo = new CheckpointInfo();
        cpInfo.setLastestFileChunkSuffixNum(this.cpInfo.getLastestFileChunkSuffixNum() + 1);
        cpInfo.setLatestFileChunksize(0);
        cpInfo.setLastBlockNumber(this.cpInfo.getLastBlockNumber());

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
    public synchronized void addBlock(Common.Block block) throws LedgerException {
        if(block.getHeader().getNumber() != getBlockchainInfo().getHeight()){
            throw new LedgerException(String.format("Block number should have been %d but was %d", getBlockchainInfo().getHeight(), block.getHeader().getNumber()));
        }
        Map.Entry<SerializedBlockInfo, byte[]> entry = BlockSerialization.serializeBlock(block);
        SerializedBlockInfo info = entry.getKey();
        byte[] blockBytes = entry.getValue();

        ByteString blockHash = block.getHeader().getDataHash();
        List<TxIndexInfo> txOffsets = BlockSerialization.txOffsets;
        int currentOffset = cpInfo.getLatestFileChunksize();
        //区块长度(尾部)
        long blockBytesLen = blockBytes.length;
        //区块长度byte
        byte[] blockBytesLenEncoded = Util.longToBytes(blockBytesLen, 8);
        //总共添加的长度
        long totalBytesToAppend = blockBytesLen + blockBytesLenEncoded.length;
        //配置的最大文件长度
        long maxBlockFileSize = conf.getMaxBlockfileSize();
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
        CheckpointInfo newCPInfo = new CheckpointInfo();
        newCPInfo.setLastestFileChunkSuffixNum(currentCPInfo.getLastestFileChunkSuffixNum());
        newCPInfo.setLatestFileChunksize(currentCPInfo.getLatestFileChunksize() + (int) totalBytesToAppend);
        newCPInfo.setChainEmpty(false);
        newCPInfo.setLastBlockNumber(block.getHeader().getNumber());
        //保存
        saveCurrentInfo(newCPInfo, false);
        FileLocPointer blockFLP = new FileLocPointer();
        blockFLP.setFileSuffixNum(currentOffset);
        blockFLP.setLocPointer(new LocPointer(newCPInfo.getLastestFileChunkSuffixNum(), 0));
        BlockIndexInfo idxInfo = new BlockIndexInfo();
        idxInfo.setBlockNum(block.getHeader().getNumber());
        idxInfo.setBlockHash(blockHash.toByteArray());
        idxInfo.setFlp(blockFLP);
        idxInfo.setTxOffsets(txOffsets);
        idxInfo.setMetadata(block.getMetadata());
        index.indexBlock(idxInfo);
        updateCheckpoint(newCPInfo);
        updateBlockchainInfo(blockHash.toByteArray(), block);
    }

    /**
     * 同步索引
     */
    public synchronized void syncIndex() throws LedgerException{
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
        //没有索引
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
        BlockStream stream = new BlockStream();
        stream.newBlockStream(rootDir, startFileNum, startOffset, endFileNum);
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
            SerializedBlockInfo info = BlockSerialization.extractSerializedBlockInfo(blockBytes);

            int numBytesToShift = (int) (blockPlacementInfo.getBlockBytesOffset() - blockPlacementInfo.getBlockStartOffset());
            for(TxIndexInfo offset : info.getTxOffsets()){
                offset.getLoc().setOffset(offset.getLoc().getOffset() + numBytesToShift);
            }

            //更新blockIndexInfo
            blockIndexInfo.setBlockHash(info.getBlockHeader().getDataHash().toByteArray());
            blockIndexInfo.setBlockNum(info.getBlockHeader().getNumber());
            FileLocPointer flp = new FileLocPointer();
            LocPointer lp = new LocPointer();
            lp.setOffset((int) blockPlacementInfo.getBlockStartOffset());
            flp.setFileSuffixNum(blockPlacementInfo.getFileNum());
            flp.setLocPointer(lp);
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

    public Ledger.BlockchainInfo getBlockchainInfo() {
        return bcInfo;
    }

    public synchronized void updateCheckpoint(CheckpointInfo cpInfo) {
        this.cpInfo = cpInfo;
        logger.debug(String.format("Brodcasting about update checkpointInfo: %s", cpInfo));
        //todo broadcast
    }

    public synchronized void updateBlockchainInfo(byte[] latestBlockHash, Common.Block latestBlock) {
        Ledger.BlockchainInfo currentBCInfo = getBlockchainInfo();
        bcInfo = Ledger.BlockchainInfo.newBuilder()
                .setHeight(currentBCInfo.getHeight() + 1)
                .setCurrentBlockHash(ByteString.copyFrom(latestBlockHash))
                .setPreviousBlockHash(latestBlock.getHeader().getPreviousHash())
                .build();
    }

    public Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException {
        logger.debug(String.format("retrieveBlockByHash() - blockHash = [%s]", blockHash));
        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByHash(blockHash);
        } catch (LedgerException e) {
            return null;
        }
        return fetchBlock(loc);
    }

    public Common.Block retrieveBlockByNumber(long blockNum) throws LedgerException {
        logger.debug(String.format("retrieveBlockByHash() - blockHash = [%d]", blockNum));
        if(blockNum == Long.MAX_VALUE){
            blockNum = getBlockchainInfo().getHeight() - 1;
        }

        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByBlockNum(blockNum);
        } catch (LedgerException e) {
            return null;
        }
        return fetchBlock(loc);
    }

    public Common.Block retrieveBlockByTxID(String txID) throws LedgerException {
        logger.debug(String.format("retrieveBlockByTxID() - txID = [%s]", txID));

        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByTxID(txID);
        } catch (LedgerException e) {
            return null;
        }
        return fetchBlock(loc);
    }

    public TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) throws LedgerException{
        logger.debug(String.format("retrieveTxValidationCodeByTxID() - txID = [%s]", txID));
        return index.getTxValidationCodeByTxID(txID);
    }

    public Common.BlockHeader retrieveBlockHeaderByNumber(long blockNum) throws  LedgerException {
        logger.debug(String.format("retrieveBlockHeaderByNumber - blockNum = [%d]", blockNum));
        FileLocPointer loc = null;
        try {
            loc = index.getBlockLocByBlockNum(blockNum);
        } catch (LedgerException e) {
            return null;
        }
        byte[] blockBytes = fetchBlockBytes(loc);
        SerializedBlockInfo info = null;
        try {
            info = BlockSerialization.extractSerializedBlockInfo(blockBytes);
        } catch (LedgerException e) {
            return null;
        }
        return info.getBlockHeader();
    }

    public BlocksItr retrieveBlocks(long startNum) {
        return BlocksItr.newBlockItr(this, startNum);
    }

    public Common.Envelope retrieveTransactionByID(String txID) throws LedgerException {
        logger.debug(String.format("retrieveTransactionByID() - txID = [%s]", txID));
        FileLocPointer loc = null;
        try {
            loc = index.getTxLoc(txID);
        } catch (LedgerException e) {
            return null;
        }
        return fetchTransactionEnvelope(loc);
    }

    public Common.Envelope retrieveTransactionByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException{
        logger.debug(String.format("retrieveTransactionByBlockNumTranNum() - blockNum = [%d], tranNum = [%d]"
                , blockNum, tranNum));
        FileLocPointer loc = null;
        loc = index.getTXLocByBlockNumTranNum(blockNum, tranNum);
        return fetchTransactionEnvelope(loc);
    }

    public Common.Block fetchBlock(FileLocPointer lp ) throws LedgerException{
        byte[] blockBytes = fetchBlockBytes(lp);
        return BlockSerialization.deserializeBlock(blockBytes);
   }

    public Common.Envelope fetchTransactionEnvelope(FileLocPointer lp) throws LedgerException{
        logger.debug(String.format("Entering fetchTransactionEnvelope() %d", lp));
        byte[] txEnvelopeBytes = null;
        txEnvelopeBytes = fetchRawBytes(lp);
        long txEnvelopeBytesLen = Util.bytesToLong(txEnvelopeBytes, 0, txEnvelopeBytes.length);
        byte[] blockBytes = new byte[(int) (txEnvelopeBytes.length - txEnvelopeBytesLen)];
        System.arraycopy(txEnvelopeBytes, (int) txEnvelopeBytesLen, blockBytes, 0, (int) (txEnvelopeBytes.length - txEnvelopeBytesLen));
        Common.Envelope envelope = null;
        try {
            envelope = Common.Envelope.parseFrom(blockBytes);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting envelope from block bytes");
            throw new LedgerException(e);
        }
        return envelope;
    }

    public byte[] fetchBlockBytes(FileLocPointer lp) throws LedgerException {
        BlockFileStream stream = new BlockFileStream();
        try {
            stream.newBlockFileStream(rootDir, lp.getFileSuffixNum(), (long) (lp.getLocPointer().getOffset()));
            return stream.nextBlockBytes();
        } finally {
            stream.close();
        }
    }

    public byte[] fetchRawBytes(FileLocPointer lp) throws LedgerException {
        BlockFileReader reader = null;
        String filePath = deriveBlockfilePath(rootDir, lp.getFileSuffixNum());
        reader = BlockFileRw.newBlockfileReader(filePath);
        return reader.read(lp.getLocPointer().getOffset(), lp.getLocPointer().getBytesLength());
    }

    /** Get the current checkpoint information that is stored in the database
     *
     * @return
     */
    public CheckpointInfo loadCurrentInfo() throws LedgerException{
        byte[] b = null;
        b = db.get(BLK_MGR_INFO_KEY);
        if(b == null){
            logger.debug("Fail to got " + BLK_MGR_INFO_KEY);
            return null;
        }
        CheckpointInfo checkpointInfo = new CheckpointInfo();
        checkpointInfo.unmarshal(b);
        return checkpointInfo;
    }

    /**
     * 将当前检查点信息保存到leveldb中
     */
    public void saveCurrentInfo(CheckpointInfo checkpointInfo, Boolean sync) throws LedgerException {
        byte[] b = checkpointInfo.marshal();
        db.put(BLK_MGR_INFO_KEY, b, sync);
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
            stream = new BlockFileStream();
            stream.newBlockFileStream(rootDir, fileNum, startingOffset);
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

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
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

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    public DBProvider getDb() {
        return db;
    }

    public void setDb(DBProvider db) {
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
}
