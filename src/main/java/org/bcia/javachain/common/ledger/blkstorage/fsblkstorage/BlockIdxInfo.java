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

import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/8
 * @company Dingxuan
 */
public class BlockIdxInfo {

    private Long blockNum;
    private byte[] blockHash;
    private FileLocPointer flp;
    private TxIndexInfo txOffset;
    private Common.BlockMetadata metadata;

    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
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

    public TxIndexInfo getTxOffset() {
        return txOffset;
    }

    public void setTxOffset(TxIndexInfo txOffset) {
        this.txOffset = txOffset;
    }

    public Common.BlockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Common.BlockMetadata metadata) {
        this.metadata = metadata;
    }
}
