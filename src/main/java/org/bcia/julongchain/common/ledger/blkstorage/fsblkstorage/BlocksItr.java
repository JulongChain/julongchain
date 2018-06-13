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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;

/**
 *  区块迭代器
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlocksItr implements IResultsIterator {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlocksItr.class);
    private static final Object lock = BlockFileManager.lock;

    private BlockFileManager mgr;
    private long maxBlockNumAvailable;
    private long blockNumToRetrieve;
    private BlockStream stream;
    private Boolean closeMarker;

    public BlocksItr() {
    }

    public BlocksItr(BlockFileManager mgr, long startBlockNum) {
        this.mgr = mgr;
        this.maxBlockNumAvailable = mgr.getCpInfo().getLastBlockNumber();
        this.blockNumToRetrieve = startBlockNum;
        this.closeMarker = false;
    }

    /**
     * 读取区块时, 区块长度不足将等待区块的添加
     */
    public long waitForBlock(long blockNum) throws LedgerException {
        synchronized (lock){
            while(mgr.getCpInfo().getLastBlockNumber() < blockNum && !shouldClose()){
                logger.debug(String.format("Going to wait for newer blocks.maxAvailaBlockNumber=[%d], waitForBlockNum=[%d]", mgr.getCpInfo().getLastBlockNumber(), blockNum));
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    throw new LedgerException(e);
                }
                logger.debug("Coming out of wait. MaxAvailaBlockNumber=[{}]", mgr.getCpInfo().getLastBlockNumber());
            }
            return mgr.getCpInfo().getLastBlockNumber();
        }
    }

    /**
     * 初始化区块文件流
     */
    public void initStream() throws LedgerException{
        FileLocPointer lp = mgr.getIndex().getBlockLocByBlockNum(blockNumToRetrieve);
        stream = new BlockStream(mgr.getRootDir(), lp.getFileSuffixNum(), lp.getLocPointer().getOffset(), -1);
    }

    /**
     * 区块文件迭代器是否可以关闭
     */
    public synchronized boolean shouldClose() {
        return closeMarker;
    }

    /**
     * 迭代区块文件
     * @return block转成的byteString
     */
    @Override
    public synchronized QueryResult next() throws LedgerException {
        //当区块长度不足时等待
        if(maxBlockNumAvailable < blockNumToRetrieve){
            maxBlockNumAvailable = waitForBlock(blockNumToRetrieve);
        }
        if(closeMarker){
            return null;
        }
        if(stream == null){
            logger.debug("Initializing block stream for iterator, maxBlockNumAvaliable = " + maxBlockNumAvailable);
            this.stream = new BlockStream();
            initStream();
        }
        byte[] nextBlockBytes = stream.nextBlockBytes();
        blockNumToRetrieve++;
        return new QueryResult(BlockSerialization.deserializeBlock(nextBlockBytes));
    }

    @Override
    public synchronized void close() throws LedgerException{
        closeMarker = true;
        synchronized (lock){
            lock.notifyAll();
            if(stream != null){
                        stream.close();
                    }
        }
    }

    public BlockFileManager getMgr() {
        return mgr;
    }

    public void setMgr(BlockFileManager mgr) {
        this.mgr = mgr;
    }

    public long getMaxBlockNumAvailable() {
        return maxBlockNumAvailable;
    }

    public void setMaxBlockNumAvailable(long maxBlockNumAvailable) {
        this.maxBlockNumAvailable = maxBlockNumAvailable;
    }

    public long getBlockNumToRetrieve() {
        return blockNumToRetrieve;
    }

    public void setBlockNumToRetrieve(long blockNumToRetrieve) {
        this.blockNumToRetrieve = blockNumToRetrieve;
    }

    public BlockStream getStream() {
        return stream;
    }

    public void setStream(BlockStream stream) {
        this.stream = stream;
    }

    public Boolean getCloseMarker() {
        return closeMarker;
    }

    public void setCloseMarker(Boolean closeMarker) {
        this.closeMarker = closeMarker;
    }
}
