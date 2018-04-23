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
import org.apache.activemq.store.memory.MemoryTransactionStore;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBHandle;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;
import sun.security.x509.OIDMap;

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
public class BlockfileMgr {

    private static final String BLOCKFILE_PREFIX = "blockfile";
    private static final byte[] BLK_MGR_INFO_KEY = "blkMgrInfo".getBytes();
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockfileMgr.class);
    public static final int LAST_BLOCK_BYTES = 0;
    public static final int CURRENT_OFFSET = 1;
    public static final int NUM_BLOCKS = 2;

    private String rootDir;
    private Conf conf;
    private LevelDbProvider db;
    private Index index;
    private CheckpointInfo cpInfo;
    private BlockfileWriter currentFileWriter;
    private Ledger.BlockchainInfo bcInfo;
    private Lock lock;

    public static BlockfileMgr newBlockfileMgr(String id,
                                               Conf conf,
                                               IndexConfig indexConfig,
                                               LevelDbProvider indexStore) throws LedgerException {
        BlockfileMgr mgr = new BlockfileMgr();
        logger.debug(String.format("newBlockfileMgr() initializing file-based block storage for ledger: %s", id));
        //根据配置文件、id生成rootDir
        String rootDir = conf.getLedgerBlockDir(id);
        mgr.setRootDir(rootDir);
        mgr.setConf(conf);
        mgr.setDb(indexStore);
        mgr.setLock(new ReentrantLock());
        mgr.setCpInfo(mgr.loadCurrentInfo());

        IoUtil.createDirIfMissing(mgr.getRootDir());

        if(mgr.getCpInfo() == null){
            logger.info("Getting block information from block storage");
            mgr.setCpInfo(BlockfileHelper.constructCheckpointInfoFromBlockFiles(mgr.getRootDir()));
            logger.debug(String.format("Info constructed by scanning the blocks dir = %s", mgr.getCpInfo().toString()));
        } else {
            logger.debug("Synching block information from block storage (if needed)");
            mgr.syncCpInfoFromFS(mgr.getRootDir(), mgr.getCpInfo());
        }
        mgr.saveCurrentInfo(mgr.getCpInfo(), true);

        mgr.setCurrentFileWriter(BlockfileRw.newBlockfileWriter(deriveBlockfilePath(mgr.getRootDir(), mgr.getCpInfo().getLastestFileChunkSuffixNum())));
        mgr.getCurrentFileWriter().truncateFile(mgr.getCpInfo().getLatestFileChunksize());

        mgr.setIndex(BlockIndex.newBlockIndex(indexConfig, indexStore));
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

    public synchronized void syncCpInfoFromFS(String rootDir, CheckpointInfo cpInfo) throws LedgerException {
        logger.debug(String.format("Starting checkpoint [%s]", cpInfo));

        String filePath = deriveBlockfilePath(rootDir, cpInfo.getLastestFileChunkSuffixNum());
        long size = IoUtil.fileExists(filePath);
        logger.debug(String.format("Status of file [%s]: exists=[%s], size=[%d]", filePath, size < 0, size));
        if(size < 0 || size == cpInfo.getLastBlockNumber()){
            return;
        }

        long endOffsetLastBlock = 0;
        int numBlocks = 0;

        scanForLastCompleteBlock(rootDir, cpInfo.getLastestFileChunkSuffixNum(), (long) cpInfo.getLatestFileChunksize());
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

    public static String deriveBlockfilePath(String rootDir, Integer suffixNum) {
        return String.format("%s/%s%06d", rootDir, BLOCKFILE_PREFIX, suffixNum);
    }

    public void close() {

    }

    public synchronized void moveToNextFile() throws LedgerException {
        CheckpointInfo cpInfo = new CheckpointInfo();
        cpInfo.setLastestFileChunkSuffixNum(this.cpInfo.getLastestFileChunkSuffixNum() + 1);
        cpInfo.setLatestFileChunksize(0);
        cpInfo.setLastBlockNumber(this.cpInfo.getLastBlockNumber());

        BlockfileWriter nextFileWriter = BlockfileRw.newBlockfileWriter(deriveBlockfilePath(this.rootDir, cpInfo.getLastestFileChunkSuffixNum()));

        saveCurrentInfo(cpInfo, true);
        this.currentFileWriter = nextFileWriter;
        updateCheckpoint(cpInfo);
    }

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
        long maxBlockFileSize = 0;
        //总长度大于配置的文件长度,重新开启新文件
        if(currentOffset + totalBytesToAppend > maxBlockFileSize){
            moveToNextFile();
            currentOffset = 0;
        }
        //添加区块长度
        currentFileWriter.append(blockBytesLenEncoded, false);
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
        updateCheckpoint(newCPInfo);
        updateBlockchainInfo(blockHash.toByteArray(), block);
    }

    public synchronized void syncIndex() throws LedgerException{
        long lastBlockIndexed = 0;
        boolean indexEmpty = false;
        try {
            lastBlockIndexed = index.getLastBlockIndexed();
        } catch (LedgerException e) {
            if(!"NoBlockIndexed".equals(e.getMessage())){
                logger.error("Got error when syncIndex");
                throw e;
            }
            indexEmpty = true;
        }
        //初始化index
        int startFileNum = 0;
        long startOffset = 0;
        boolean skipFirstBlock = false;
        //获取之前block的checkInfo
        int endFileNum = cpInfo.getLastestFileChunkSuffixNum();
        long startingBlockNum = 0;

        if(!indexEmpty){
            if(lastBlockIndexed == cpInfo.getLastBlockNumber()){
                logger.debug("Both the block files and indices are in sync");
                return;
            }
            logger.debug(String.format("Last block indexed [%d], last block present in block files [%d]"
                    , lastBlockIndexed, cpInfo.getLastBlockNumber()));
            FileLocPointer flp;
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

        BlockStream stream = new BlockStream();
        stream.newBlockStream(rootDir, startFileNum, startOffset, endFileNum);
        byte[] blockBytes = null;
        BlockPlacementInfo blockPlacementInfo = null;
        if(skipFirstBlock){
            blockBytes = stream.nextBlockBytes();
            if(blockBytes == null){
                throw new LedgerException(String.format("Block btyes for block num = [%d] should not be null here." +
                        " The indexes for the block are already present", lastBlockIndexed));
            }
        }

        BlockIdxInfo blockIdxInfo = new BlockIdxInfo();
        while(true){
            AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> entry = stream.nextBlockBytesAndPlacementInfo();
            blockBytes = entry.getKey();
            blockPlacementInfo = entry.getValue();
            if(blockBytes == null){
                break;
            }
            SerializedBlockInfo info = BlockSerialization.extractSerializedBlockInfo(blockBytes);

            int numBytesToShift = (int) (blockPlacementInfo.getBlockBytesOffset() - blockPlacementInfo.getBlockStartOffset());
            for(TxIndexInfo offset : info.getTxOffsets()){
                offset.getLoc().setOffset(offset.getLoc().getOffset() + numBytesToShift);
            }

            //更新blockIndexInfo
            blockIdxInfo.setBlockHash(info.getBlockHeader().getDataHash().toByteArray());
            blockIdxInfo.setBlockNum(info.getBlockHeader().getNumber());
            FileLocPointer flp = new FileLocPointer();
            LocPointer lp = new LocPointer();
            lp.setOffset(blockPlacementInfo.getBlockStartOffset().intValue());
            flp.setFileSuffixNum(blockPlacementInfo.getFileNum());
            flp.setLocPointer(lp);
            blockIdxInfo.setTxOffsets(info.getTxOffsets());
            blockIdxInfo.setMetadata(info.getMetadata());

            logger.debug(String.format("syncIndex() indexing block [%d]", blockIdxInfo.getBlockNum()));
            index.indexBlock(blockIdxInfo);
            if(blockIdxInfo.getBlockNum() % 10000 == 0){
                logger.info(String.format("Indexed block number [%d]", blockIdxInfo.getBlockNum()));
            }
        }
        logger.info(String.format("Finished building index. Last block indexed [%d]", blockIdxInfo.getBlockNum()));
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
        Ledger.BlockchainInfo neBCInfo = Ledger.BlockchainInfo.newBuilder()
                .setHeight(currentBCInfo.getHeight() + 1)
                .setCurrentBlockHash(ByteString.copyFrom(latestBlockHash))
                .setPreviousBlockHash(latestBlock.getHeader().getPreviousHash())
                .build();
        bcInfo = neBCInfo;
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

    public Common.Block retrieveBlockByNumber(Long blockNum) throws LedgerException {
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

    public BlocksItr retrieveBlocks(Long startNum) {
        return BlocksItr.newBlockItr(this, startNum);
    }

    public Common.Envelope retrieveTransactionByID(String txID) throws LedgerException {
        logger.debug(String.format("retrieveTransactionByID() - txID = [%d]", txID));
        FileLocPointer loc = null;
        try {
            loc = index.getTxLoc(txID);
        } catch (LedgerException e) {
            return null;
        }
        return fetchTransactionEnvelope(loc);
    }

    public Common.Envelope retrieveTransactionByBlockNumTranNum(Long blockNum, Long tranNum) throws LedgerException{
        logger.debug(String.format("retrieveTransactionByBlockNumTranNum() - blockNum = [%d], tranNum = [%d]"
                , blockNum, tranNum));
        FileLocPointer loc = null;
        loc = index.getTXLocByBlockNumTranNum(blockNum, tranNum);
        return fetchTransactionEnvelope(loc);
    }

    public Common.Block fetchBlock(FileLocPointer lp ) throws LedgerException{
        byte[] blockBytes = fetchBlockBytes(lp);
        Common.Block block = null;
        block = BlockSerialization.deserializeBlock(blockBytes);
        return block;
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
        BlockfileStream stream = new BlockfileStream();
        try {
            stream.newBlockFileStream(rootDir, lp.getFileSuffixNum(), (long) (lp.getLocPointer().getOffset()));
            return stream.nextBlockBytes();
        } finally {
            stream.close();
        }
    }

    public byte[] fetchRawBytes(FileLocPointer lp) throws LedgerException {
        BlockfileReader reader = null;
        String filePath = deriveBlockfilePath(rootDir, lp.getFileSuffixNum());
        reader = BlockfileRw.newBlockfileReader(filePath);
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

    public void saveCurrentInfo(CheckpointInfo checkpointInfo, Boolean sync) throws LedgerException {
        byte[] b = checkpointInfo.marshal();
        //TODO debug: value is null
        db.put(BLK_MGR_INFO_KEY, b, sync);
    }

    /** scanForLastCompleteBlock scan a given block file and detects the last offset in the file
     * after which there may lie a block partially written (towards the end of the file in a crash scenario).
     */
    public static List<Object> scanForLastCompleteBlock(String rootDir, Integer fileNum, Long startingOffset) throws LedgerException{
        BlockfileStream stream = null;
        byte[] blockBytes = null;
        byte[] lastBlockBytes = null;
        try {
            int numBlock = 0;
            stream = new BlockfileStream();
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

    public LevelDbProvider getDb() {
        return db;
    }

    public void setDb(LevelDbProvider db) {
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

    public BlockfileWriter getCurrentFileWriter() {
        return currentFileWriter;
    }

    public void setCurrentFileWriter(BlockfileWriter currentFileWriter) {
        this.currentFileWriter = currentFileWriter;
    }

    public Ledger.BlockchainInfo getBcInfo() {
        return bcInfo;
    }

    public void setBcInfo(Ledger.BlockchainInfo bcInfo) {
        this.bcInfo = bcInfo;
    }
}
