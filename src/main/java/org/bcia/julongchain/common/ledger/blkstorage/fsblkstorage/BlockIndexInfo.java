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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;

import java.util.List;

/**
 * 用于封装block索引信息
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockIndexInfo {

    private long blockNum;
    private byte[] blockHash;
    private FileLocPointer flp;
    private List<TxIndexInfo> txOffsets;
    private Common.BlockMetadata metadata;

    public BlockIndexInfo(){}

    public BlockIndexInfo(long blockNum, byte[] blockHash, FileLocPointer flp, List<TxIndexInfo> txIndexInfos, Common.BlockMetadata metadata){
        this.blockNum = blockNum;
        this.blockHash = blockHash;
        this.flp = flp;
        this.txOffsets = txIndexInfos;
        this.metadata = metadata;
    }

    private static final JavaChainLog loogger = JavaChainLogFactory.getLog(BlockIndexInfo.class);
    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    public FileLocPointer getFlp() {
        return flp;
    }

    public void setFlp(FileLocPointer flp) {
        this.flp = flp;
    }

    public Common.BlockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Common.BlockMetadata metadata) {
        this.metadata = metadata;
    }

    public List<TxIndexInfo> getTxOffsets() {
        return txOffsets;
    }

    public void setTxOffsets(List<TxIndexInfo> txOffsets) {
        this.txOffsets = txOffsets;
    }

    @Override
    public String toString() {
        return String.format("blockNum=%d, blockHash length=%d, txOffsets length=%d", blockNum, blockHash == null ? 0 : blockHash.length, txOffsets.size());
    }
}
