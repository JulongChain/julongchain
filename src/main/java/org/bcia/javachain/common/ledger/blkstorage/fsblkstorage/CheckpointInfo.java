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
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.UpdateBatch;
import org.bcia.javachain.core.ledger.util.Util;

/**
 * block检查点类
 * 每在blockfile中存储一个block都在leveldb中添加检查点信息
 *
 * @author sunzongyu
 * @date 2018/4/8
 * @company Dingxuan
 */
public class CheckpointInfo {

    /**
     *block文件后缀名
     */
    private int lastestFileChunkSuffixNum;
    /**
     * 当前block的大小
     */
    private int latestFileChunksize;
    /**
     * 当前账本是否为空
     */
    private boolean isChainEmpty;
    /**
     * 当前账本最新block序号
     */
    private long lastBlockNumber;

    /**
     * 序列化检查点
     */
    byte[] marshal() {
        //4部分共32字节
        //0~7位 lastestFileChunkSuffixNum
        byte[] bytes0 = Util.longToBytes(lastestFileChunkSuffixNum, 8);
        //8~15位 latestFileChunksize
        byte[] bytes1 = Util.longToBytes(latestFileChunksize, 8);
        //16~23位 lastBlockNumber
        byte[] bytes2 = Util.longToBytes(lastBlockNumber, 8);
        //24~31位 chainEmptyMarker
        long chainEmptyMarker = 0;
        if(isChainEmpty){
            chainEmptyMarker = 1;
        }
        byte[] bytes3 = Util.longToBytes(chainEmptyMarker, 8);
        byte[] result = ArrayUtils.addAll(bytes0, bytes1);
        result = ArrayUtils.addAll(result, bytes2);
        result = ArrayUtils.addAll(result, bytes3);
        return result;
    }

    /**
     * 反序列化
     */
    void unmarshal(byte[] b) throws LedgerException {
        //4部分共32字节
        //0~7位 lastestFileChunkSuffixNum
        lastestFileChunkSuffixNum = (int) Util.bytesToLong(b, 0, 8);
        //8~15位 latestFileChunksize
        latestFileChunksize = (int) Util.bytesToLong(b, 8, 8);
        //16~23位 lastBlockNumber
        lastBlockNumber = Util.bytesToLong(b, 16, 8);
        //24~31位 chainEmptyMarker
        isChainEmpty = Util.bytesToLong(b, 24, 8) == 1;
    }

    public int getLastestFileChunkSuffixNum() {
        return lastestFileChunkSuffixNum;
    }

    public void setLastestFileChunkSuffixNum(int lastestFileChunkSuffixNum) {
        this.lastestFileChunkSuffixNum = lastestFileChunkSuffixNum;
    }

    public int getLatestFileChunksize() {
        return latestFileChunksize;
    }

    public void setLatestFileChunksize(int latestFileChunksize) {
        this.latestFileChunksize = latestFileChunksize;
    }

    public boolean getChainEmpty() {
        return isChainEmpty;
    }

    public void setChainEmpty(boolean chainEmpty) {
        isChainEmpty = chainEmpty;
    }

    public long getLastBlockNumber() {
        return lastBlockNumber;
    }

    public void setLastBlockNumber(long lastBlockNumber) {
        this.lastBlockNumber = lastBlockNumber;
    }

    @Override
    public String toString() {
        return String.format("lastestFileChunkSuffixNum=[%d], latestFileChunksize=[%d], isChainEmpty=[%s], lastBlockNumber=[%d]"
                , lastestFileChunkSuffixNum, latestFileChunksize, isChainEmpty, lastBlockNumber);
    }
}
