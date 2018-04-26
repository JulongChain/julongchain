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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;

/**
 *  区块迭代器
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlocksItr implements ResultsIterator {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlocksItr.class);

    private BlockFileManager mgr;
    private long maxBlockNumAvailable;
    private long blockNumToRetrieve;
    private BlockStream stream;
    private Boolean closeMarker;

    public static BlocksItr newBlockItr(BlockFileManager mgr, long startBlockNum) {
        BlocksItr itr = new BlocksItr();
        itr.setMgr(mgr);
        itr.setMaxBlockNumAvailable(mgr.getCpInfo().getLastBlockNumber());
        itr.setBlockNumToRetrieve(startBlockNum);
        itr.setStream(null);
        itr.setCloseMarker(false);
        return itr;
    }

    /**
     * TODO 读取区块时, 区块长度不足将等待区块的添加
     */
    public synchronized long waitForBlock(long blockNum) {
//        while(mgr.getCpInfo().getLastBlockNumber() < blockNum && !shouldClose()){
//            logger.debug(String.format("Going to wait for newer blocks.maxAvailaBlockNumber=[%d], waitForBlockNum=[%d]"
//                    , mgr.getCpInfo().getLastBlockNumber(), blockNum));
//        }
        return 0;
    }

    /**
     * 初始化区块文件流
     */
    public void initStream() throws LedgerException{
        FileLocPointer lp = mgr.getIndex().getBlockLocByBlockNum(blockNumToRetrieve);
        stream.newBlockStream(mgr.getRootDir(), lp.getFileSuffixNum(), lp.getLocPointer().getOffset(), -1);
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
            initStream();
        }
        byte[] nextBlockBytes = stream.nextBlockBytes();
        blockNumToRetrieve++;
        return (QueryResult) BlockSerialization.deserializeBlock(nextBlockBytes).toByteString();
    }

    @Override
    public synchronized void close() throws LedgerException{
        closeMarker = true;
        if(stream != null){
            stream.close();
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
