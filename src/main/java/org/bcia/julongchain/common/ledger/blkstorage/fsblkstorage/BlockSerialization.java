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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.util.Util;

import java.util.*;

import static org.bcia.julongchain.protos.common.Common.*;

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
    public List<TxIndexInfo> txOffsets = new ArrayList<>();
    private BlockMetadata metadata;

    /**
     * 序列化block
     */
    public static SerializedBlockInfo serializeBlock(Block block, long blockPosition) {
        return new SerializedBlockInfo(block.getHeader(),
                //序列化Data
                addDataBytes(block, blockPosition),
                block.getMetadata());
    }

    /**
     * 解码为block
     */
    public static Block deserializeBlock(byte[] serializedBlockBytes) throws LedgerException {
        try {
            return Block.parseFrom(serializedBlockBytes);
        }catch (InvalidProtocolBufferException e){
            throw new LedgerException(e.getMessage(),e);
        }
    }

    public static SerializedBlockInfo extractSerializedBlockInfo(byte[] serializedBlockBytes, long blockPosition) throws LedgerException {
        Block block = deserializeBlock(serializedBlockBytes);
        return new SerializedBlockInfo(block.getHeader(), addDataBytes(block, blockPosition), block.getMetadata());
    }

    /**
     * 序列化头部
     */
    private static byte[] addHeaderBytes(BlockHeader blockHeader){
        //数量
        byte[] result = Util.longToBytes(blockHeader.getNumber(), BlockFileManager.PEEK_BYTES_LEN);
        //Data hash 长度
        result = ArrayUtils.addAll(result, Util.longToBytes(blockHeader.getDataHash().toByteArray().length, BlockFileManager.PEEK_BYTES_LEN));
        //hash
        result = ArrayUtils.addAll(result, blockHeader.getDataHash().toByteArray());
        //pre data hash 长度
        result = ArrayUtils.addAll(result, Util.longToBytes(blockHeader.getPreviousHash().toByteArray().length, BlockFileManager.PEEK_BYTES_LEN));
        //pre hash
        result = ArrayUtils.addAll(result, blockHeader.getPreviousHash().toByteArray());
        return result;
    }

    /**
     * 确定并封装区块中每个Envelope的位置
     */
    public static List<TxIndexInfo> addDataBytes(Block block, long blockPosition){
        //block为blockHeader + blockData + blockMetadata结构
        //envelope在blockData中, 先跳过blockHeader
        List<TxIndexInfo> txOffsets = new ArrayList<>();
        //获取头部序列化长度
        long headerSerializedLen = block.getHeader().getSerializedSize();
        //获取Data序列化长度
        long dataSerializedLen = block.getData().getSerializedSize();
        //序列化后首部为1位标识位 + headerLen
        //序列化后尾部为1位表示位 + dataLen
        //headerLen、dataLen为每7位2进制位长度+1
							//区块头部长度			//区块头部标志位长度							//区块尾部标志位长度
        long headerLen = 	headerSerializedLen + 	1 + computeLength(headerSerializedLen) + 	1 + computeLength(dataSerializedLen);
        //头部结束后为Data起始位置
		//当前位置为blockData的开始位置
        long offset = headerLen + blockPosition;
		//进入BlockData
		//blockData每一项为envelope
		//blockData包含blockData标志位1位 + Envelope长度
        for(ByteString txEnvelopeBytes : block.getData().getDataList()){
            Envelope txEnvelope;
            Payload txPayload;
            GroupHeader gh = null;
            long txEvnelopeLength;
            //解析并获取TxID
            try {
                txEnvelope = Envelope.parseFrom(txEnvelopeBytes);
                txPayload = Payload.parseFrom(txEnvelope.getPayload());
                gh = GroupHeader.parseFrom(txPayload.getHeader().getGroupHeader());
                txEvnelopeLength = txEnvelope.getSerializedSize();
            } catch (InvalidProtocolBufferException e) {
                logger.error("Got error when resolve object from byteString");
                return null;
            }
            //跳过blockData标志位
            offset += (1 + computeLength(txEvnelopeLength));
            //构造locpointer对象, 保存Envelope位置信息
            //offset        Envelope起始位置
            //bytesLength   Envelope长度, 应包含头部长度
            LocPointer locPointer = new LocPointer(offset, txEvnelopeLength);
            //构造txIndexInfo对象
            txOffsets.add(new TxIndexInfo(gh.getTxId(), locPointer));
            //Envelope起始位置相应移动
            offset += txEvnelopeLength;
        }

        return txOffsets;
    }

    /**
     * 用于计算protobuf对象序列化后长度位长度
     * protobuf对象序列化后，byte[]长度增加128倍，长度位长度增加1
     * 既    0~127          1
     *      128~16383       2
     *      16384~2097152   3
     *      ...
     */
    private static int computeLength(long i){
        int result = 0;
        while ((i >>= 7) > 0){
            result++;
        }
        return ++result;
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
        byte[] result = ArrayUtils.addAll(preBytes, Util.longToBytes(metadataLen, BlockFileManager.PEEK_BYTES_LEN));
        //metadata数量
        result = ArrayUtils.addAll(result, Util.longToBytes(metadata.getMetadataCount(), BlockFileManager.PEEK_BYTES_LEN));
        for(ByteString b : metadata.getMetadataList()){
            //将metadata添加到结果中
            result = ArrayUtils.addAll(result, b.toByteArray());
        }
    }

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

    public List<TxIndexInfo> getTxOffsets() {
        return txOffsets;
    }

    public void setTxOffsets(List<TxIndexInfo> txOffsets) {
        this.txOffsets = txOffsets;
    }

}
