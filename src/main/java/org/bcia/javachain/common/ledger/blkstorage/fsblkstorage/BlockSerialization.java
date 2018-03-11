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

import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.protos.common.Common.*;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/7
 * @company Dingxuan
 */
public class BlockSerialization {

    Map<SerializedBlockInfo, byte[]> serializeBlock(Block block) throws LedgerException {
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        Map<SerializedBlockInfo, byte[]> map = new HashMap<>(1);
        map.put(info, block.toByteArray());
        return map;
    }

    Block deserializeBlock(byte[] serializedBlockBytes) throws LedgerException {
        try {
            Block block = Block.parseFrom(serializedBlockBytes);
            return block;
        }catch (InvalidProtocolBufferException e){
            throw new LedgerException(e.getMessage(),e);
        }
    }

    SerializedBlockInfo extractSerializedBlockInfo(byte[] serializedBlockBytes) throws LedgerException {
        Block block = deserializeBlock(serializedBlockBytes);
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        return info;
    }

    void addHeaderBytes(BlockHeader blockHeader, SerializedBlockInfo blockInfo) throws LedgerException {
        return;
    }

    TxIndexInfo[] addDataBytes(BlockData blockData, SerializedBlockInfo blockInfo) throws LedgerException {
        return null;
    }

    void addMetadataBytes(BlockMetadata blockMetadata, SerializedBlockInfo blockInfo) throws LedgerException {
        return;
    }

    BlockHeader extractHeader(SerializedBlockInfo blockInfo) throws LedgerException {
        return null;
    }

    Map<BlockData, TxIndexInfo[]> extractData(SerializedBlockInfo blockInfo) throws LedgerException {
        return null;
    }

    BlockMetadata extractMetadata(SerializedBlockInfo blockInfo) throws LedgerException {
        return null;
    }

    String extractTxID(byte[] txEnvelopBytes) throws LedgerException {
        return null;
    }

}
