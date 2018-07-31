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

import org.bcia.julongchain.protos.common.Common;

import java.util.List;

/**
 * 序列化的区块信息
 * 包含区块头部、全部交易、metadata
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public class SerializedBlockInfo {

    private Common.BlockHeader blockHeader;
    private List<TxIndexInfo> txOffsets;
    private Common.BlockMetadata metadata;

    public SerializedBlockInfo() {
    }

    public SerializedBlockInfo(Common.BlockHeader blockHeader, List<TxIndexInfo> txOffsets, Common.BlockMetadata metadata) {
        this.blockHeader = blockHeader;
        this.txOffsets = txOffsets;
        this.metadata = metadata;
    }

    public Common.BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(Common.BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public List<TxIndexInfo> getTxOffsets() {
        return txOffsets;
    }

    public void setTxOffsets(List<TxIndexInfo> txOffsets) {
        this.txOffsets = txOffsets;
    }

    public Common.BlockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Common.BlockMetadata metadata) {
        this.metadata = metadata;
    }
}
