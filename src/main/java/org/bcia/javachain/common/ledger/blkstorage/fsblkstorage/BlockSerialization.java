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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.protos.common.Common;

import java.nio.Buffer;
import java.nio.ByteBuffer;
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
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockSerialization.class);

    private BlockHeader blockHeader;
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

    /**
     * 序列化block
     */
    public static AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]> serializeBlock(Block block) {
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        //序列化Data
        info.setTxOffsets(addDataBytes(block));
        AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]> entry =
                new AbstractMap.SimpleEntry<>(info, block.toByteArray());
        return entry;
    }

    /**
     * 解码为block
     */
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
        info.setTxOffsets(addDataBytes(block));
        return info;
    }

    /**
     * 序列化头部
     */
    private static byte[] addHeaderBytes(BlockHeader blockHeader){
        //数量
        byte[] result = Util.longToBytes(blockHeader.getNumber(), 8);
        //Data hash 长度
        result = ArrayUtils.addAll(result, Util.longToBytes(blockHeader.getDataHash().toByteArray().length, 8));
        //hash
        result = ArrayUtils.addAll(result, blockHeader.getDataHash().toByteArray());
        //pre data hash 长度
        result = ArrayUtils.addAll(result, Util.longToBytes(blockHeader.getPreviousHash().toByteArray().length, 8));
        //pre hash
        result = ArrayUtils.addAll(result, blockHeader.getPreviousHash().toByteArray());
        return result;
    }

    /**
     * 序列化Data并将data信息封装到TxIndexInfo中
     */
    private static List<TxIndexInfo> addDataBytes(Block block){
        //获取头部序列化长度
        int headerLen = block.getHeader().getSerializedSize();
        List<TxIndexInfo> list = new ArrayList<>();
        BlockData data = block.getData();
        for(ByteString txEnvelopeBytes : data.getDataList()){
            //记录当前位置
            int offset = headerLen;
            GroupHeader gh = null;
            //解析并获取TxID
            try {
                Envelope txEnvelope = Envelope.parseFrom(txEnvelopeBytes);
                Payload txPayload = Payload.parseFrom(txEnvelope.getPayload());
                gh = GroupHeader.parseFrom(txPayload.getHeader().getGroupHeader());
            } catch (InvalidProtocolBufferException e) {
                logger.error("Got error when resolve object from byteString");
                return null;
            }
            //构造locpointer对象
            LocPointer locPointer = new LocPointer(offset, txEnvelopeBytes.toByteArray().length);
            //构造txIndexInfo对象
            TxIndexInfo indexInfo = new TxIndexInfo();
            indexInfo.setTxID(gh.getTxId());
            indexInfo.setLoc(locPointer);
            list.add(indexInfo);
        }

        return list;
    }

    /**
     * 序列化metadata
     */
    private static void addMetadataBytes(BlockMetadata metadata, byte[] preBytes){
        if(metadata == null){
            return;
        }
        long metadataLen = metadata.toByteArray().length;
        //metadata长度
        byte[] result = ArrayUtils.addAll(preBytes, Util.longToBytes(metadataLen, 8));
        //metadata数量
        result = ArrayUtils.addAll(result, Util.longToBytes(metadata.getMetadataCount(), 8));
        for(ByteString b : metadata.getMetadataList()){
            //将metadata添加到结果中
            result = ArrayUtils.addAll(result, b.toByteArray());
        }
    }
}
