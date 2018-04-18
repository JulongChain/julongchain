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

import org.bcia.javachain.common.ledger.QueryResult;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.iq80.leveldb.table.BlockIterator;

/**
 *
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlocksItr {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlocksItr.class);

    private BlockfileMgr mgr;
    private Long maxBlockNumAvailable;
    private Long blockNumToRetrieve;
    private BlockStream stream;
    private Boolean closeMarker;

    public static BlocksItr newBlockItr(BlockfileMgr mgr, Long startBlockNum) {
        BlocksItr itr = new BlocksItr();
        itr.setMgr(mgr);
        itr.setMaxBlockNumAvailable(mgr.getCpInfo().getLastBlockNumber());
        itr.setBlockNumToRetrieve(startBlockNum);
        itr.setStream(null);
        itr.setCloseMarker(false);
        return itr;
    }

    public synchronized Long waitForBlock(Long blockNum) {
//        while(mgr.getCpInfo().getLastBlockNumber() < blockNum && !shouldClose()){
//            logger.debug(String.format("Going to wait for newer blocks.maxAvailaBlockNumber=[%d], waitForBlockNum=[%d]"
//                    , mgr.getCpInfo().getLastBlockNumber(), blockNum));
//        }
        return null;
    }

    void initStream() {
        return;
    }

    Boolean shouldClose() {
        return null;
    }

    /**
     * Next moves the cursor to next block and returns true iff the iterator is not exhausted
      */
    QueryResult Next() {
        return null;
    }

    /**
     * Close releases any resources held by the iterator
     */
    void close() {

    }

    public BlockfileMgr getMgr() {
        return mgr;
    }

    public void setMgr(BlockfileMgr mgr) {
        this.mgr = mgr;
    }

    public Long getMaxBlockNumAvailable() {
        return maxBlockNumAvailable;
    }

    public void setMaxBlockNumAvailable(Long maxBlockNumAvailable) {
        this.maxBlockNumAvailable = maxBlockNumAvailable;
    }

    public Long getBlockNumToRetrieve() {
        return blockNumToRetrieve;
    }

    public void setBlockNumToRetrieve(Long blockNumToRetrieve) {
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
