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
package org.bcia.julongchain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/3/18
 * @company Dingxuan
 */
public class BlockUtils {
    public static Common.Block getBlockFromBlockBytes(byte[] blockBytes) throws InvalidProtocolBufferException {
        return Common.Block.parseFrom(blockBytes);
    }

    public static String getGroupIDFromBlock(Common.Block block) throws JavaChainException {
        Common.Envelope envelope = null;
        Common.Payload payload = null;
        Common.GroupHeader gh = null;
        try {
            if (block == null || block.getData() == null || block.getData().getDataCount() == 0) {
                return null;
            }
            envelope = Common.Envelope.parseFrom(block.getData().getData(0));
            if (envelope == null || envelope.getPayload() == null) {
                return null;
            }
            payload = Common.Payload.parseFrom(envelope.getPayload());
            if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
                return null;
            }
            gh = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
            if (gh == null || gh.getGroupId() == null) {
                return null;
            }
        } catch (InvalidProtocolBufferException e) {
            throw new JavaChainException(e);
        }
        return gh.getGroupId();
    }

    /**
     * 从区块里面解析出指定的信封对象
     *
     * @param block
     * @param index
     * @return
     * @throws ValidateException
     * @throws InvalidProtocolBufferException
     */
    public static Common.Envelope extractEnvelope(Common.Block block, int index) throws ValidateException,
            InvalidProtocolBufferException {
        //非空校验
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getData(), "block.data can not be null");

        if (block.getData().getDataList() == null || block.getData().getDataList().isEmpty()) {
            throw new ValidateException("block.data.dataList can not be null");
        }

        //索引值校验
        if (index < 0 || index > block.getData().getDataCount() - 1) {
            throw new ValidateException("Wrong Envelope index: " + index);
        }

        ByteString byteString = block.getData().getData(index);
        return Common.Envelope.parseFrom(byteString);
    }

    /**
     * 获取最新配置所在的索引
     *
     * @param block
     * @return
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    public static long getLastConfigIndexFromBlock(Common.Block block) throws InvalidProtocolBufferException,
            ValidateException {
        Common.Metadata lastConfigMetadata = getMetadataFromBlock(block, Common.BlockMetadataIndex
                .LAST_CONFIG_VALUE);

        //非空校验
        ValidateUtils.isNotNull(lastConfigMetadata, "lastConfigMetadata can not be null");

        Common.LastConfig lastConfig = Common.LastConfig.parseFrom(lastConfigMetadata.getValue());
        ValidateUtils.isNotNull(lastConfig, "lastConfig can not be null");

        return lastConfig.getIndex();
    }

    /**
     * 从区块中获取元数据
     *
     * @param block
     * @param blockMetadataIndex
     * @return
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    public static Common.Metadata getMetadataFromBlock(Common.Block block, int blockMetadataIndex) throws
            InvalidProtocolBufferException, ValidateException {
        //非空校验
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getMetadata(), "block.metadata can not be null");
        if (block.getMetadata().getMetadataList() == null || block.getMetadata().getMetadataList().isEmpty()) {
            throw new ValidateException("block.metadata.metadataList can not be null");
        }

        //索引值校验
        if (blockMetadataIndex < 0 || blockMetadataIndex > block.getMetadata().getMetadataCount() - 1) {
            throw new ValidateException("Wrong blockMetadataIndex: " + blockMetadataIndex);
        }

        ByteString byteString = block.getMetadata().getMetadata(blockMetadataIndex);
        return Common.Metadata.parseFrom(byteString);
    }

    /**
     * 创建一个空的Block对象，供测试用
     *
     * @return
     */
    public static Common.Block newEmptyBlock() {
        Common.BlockMetadata.Builder blockMetadataBuilder = Common.BlockMetadata.newBuilder();

        blockMetadataBuilder.addMetadata(Common.Metadata.getDefaultInstance().toByteString());
        blockMetadataBuilder.addMetadata(Common.Metadata.getDefaultInstance().toByteString());
        blockMetadataBuilder.addMetadata(Common.Metadata.getDefaultInstance().toByteString());
        blockMetadataBuilder.addMetadata(Common.Metadata.getDefaultInstance().toByteString());

        Common.BlockMetadata blockMetadata = blockMetadataBuilder.build();

        Common.Block.Builder blockBuilder = Common.Block.newBuilder();
        blockBuilder.setHeader(Common.BlockHeader.getDefaultInstance());
        blockBuilder.setData(Common.BlockData.getDefaultInstance());
        blockBuilder.setMetadata(blockMetadata);
        blockBuilder.setMetadata(blockMetadata);

        return blockBuilder.build();
    }

    /**
     * 填充元数据
     *
     * @param blockBuilder
     */
    public static void initBlockMetadata(Common.Block.Builder blockBuilder) {
        Common.BlockMetadata.Builder blockMetadataBuilder = null;

        if (blockBuilder.getMetadata() == null) {
            blockMetadataBuilder = Common.BlockMetadata.newBuilder();
            blockBuilder.setMetadata(blockMetadataBuilder);
        } else {
            blockMetadataBuilder = blockBuilder.getMetadataBuilder();
        }

        int metaCount = blockBuilder.getMetadata().getMetadataCount();
        int metaMaxCount = Common.BlockMetadataIndex.CONSENTER_VALUE + 1;
        if (metaCount < metaMaxCount) {
            for (int i = metaCount; i < metaMaxCount; i++) {
                blockMetadataBuilder.addMetadata(Common.BlockMetadata.getDefaultInstance().toByteString());
            }
        }
    }
}
