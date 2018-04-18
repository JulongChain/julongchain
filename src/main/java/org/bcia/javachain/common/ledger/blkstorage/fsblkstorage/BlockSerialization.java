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

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.protos.common.Common;

import java.util.*;

import static org.bcia.javachain.protos.common.Common.*;

/**
 * 序列化区块
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlockSerialization {
    private Common.BlockHeader blockHeader;
    public static List<TxIndexInfo> txOffsets = new ArrayList<>();
    private BlockMetadata metadata;

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public BlockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(BlockMetadata metadata) {
        this.metadata = metadata;
    }

    public static AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]> serializeBlock(Block block) {
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]> entry =
                new AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]>(info, block.toByteArray());
        return entry;
    }

    public static Block deserializeBlock(byte[] serializedBlockBytes) throws LedgerException {
        try {
            Block block = Block.parseFrom(serializedBlockBytes);
            return block;
        }catch (InvalidProtocolBufferException e){
            throw new LedgerException(e.getMessage(),e);
        }
    }

    public static SerializedBlockInfo extractSerializedBlockInfo(byte[] serializedBlockBytes) throws LedgerException {
        Block block = deserializeBlock(serializedBlockBytes);
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        return info;
    }
}
