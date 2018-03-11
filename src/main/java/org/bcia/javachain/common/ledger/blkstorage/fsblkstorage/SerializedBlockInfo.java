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
 * @date 2018/3/7
 * @company Dingxuan
 */
public class SerializedBlockInfo {

    private Common.BlockHeader blockHeader;
    private TxIndexInfo txOffsets;
    private Common.BlockMetadata metadata;

    public Common.BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(Common.BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public TxIndexInfo getTxOffsets() {
        return txOffsets;
    }

    public void setTxOffsets(TxIndexInfo txOffsets) {
        this.txOffsets = txOffsets;
    }

    public Common.BlockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Common.BlockMetadata metadata) {
        this.metadata = metadata;
    }
}
