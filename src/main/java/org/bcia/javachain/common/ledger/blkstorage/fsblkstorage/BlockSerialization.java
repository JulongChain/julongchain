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
    public static AbstractMap.SimpleEntry<SerializedBlockInfo, byte[]> serializeBlock(Block block, long blockPosition) {
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        //序列化Data
        info.setTxOffsets(addDataBytes(block, blockPosition));
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

    public static SerializedBlockInfo extractSerializedBlockInfo(byte[] serializedBlockBytes, long blockPosition) throws LedgerException {
        Block block = deserializeBlock(serializedBlockBytes);
        SerializedBlockInfo info = new SerializedBlockInfo();
        info.setBlockHeader(block.getHeader());
        info.setMetadata(block.getMetadata());
        info.setTxOffsets(addDataBytes(block, blockPosition));
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
     * 在block中解析出Envelope,并确定起始位置
     * 获取所有Envelope的长度和起始位置
     */
    private static List<TxIndexInfo> addDataBytes(Block block, long blockPosition){
        //获取头部序列化长度
        int headerSerializedLen = block.getHeader().getSerializedSize();
        //序列化长度给出序列化之后的长度，还应加上标头部识位长度2, 尾部标识位长度2
        //序列化长度为0时，没有尾部标识位
        int headerLen = headerSerializedLen + (headerSerializedLen == 0 ? 2 : 4);
        //头部结束后为Data起始位置
        int envPosition = headerLen + (int) blockPosition;
        //迭代Data
        for(ByteString txEnvelopeBytes : block.getData().getDataList()){
            //记录当前位置
            int offset = envPosition;
            Envelope txEnvelope;
            Payload txPayload;
            GroupHeader gh = null;
            int txEvnelopeLength = 0;
            //解析并获取TxID
            try {
                txEnvelope = Envelope.parseFrom(txEnvelopeBytes);
                txPayload = Payload.parseFrom(txEnvelope.getPayload());
                gh = GroupHeader.parseFrom(txPayload.getHeader().getGroupHeader());
                txEvnelopeLength = txEnvelope.toByteArray().length + 2;
            } catch (InvalidProtocolBufferException e) {
                logger.error("Got error when resolve object from byteString");
                return null;
            }
            //构造locpointer对象
            //offset        Envelope起始位置
            //bytesLength   Envelope长度, 应包含头部长度2
            LocPointer locPointer = new LocPointer(offset, txEvnelopeLength);
            //构造txIndexInfo对象
            TxIndexInfo indexInfo = new TxIndexInfo();
            indexInfo.setTxID(gh.getTxId());
            indexInfo.setLoc(locPointer);
            txOffsets.add(indexInfo);

            //Envelope起始位置相应移动
            envPosition += txEvnelopeLength;
        }

        return txOffsets;
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
