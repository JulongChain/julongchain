/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.consenter.common.multigroup;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.util.BlockHelper;
import org.bcia.javachain.consenter.util.CommonUtils;
import org.bcia.javachain.consenter.util.Utils;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/3/16
 * @company Dingxuan
 */
public class BlockWriter {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BlockWriter.class);
    private IBlockWriterSupport support;

    private Registrar registrar;

    private long lastConfigBlockNum;

    private long lastConfigSeq;

    private Common.Block lastBlock;

    public BlockWriter(IBlockWriterSupport support, Registrar registrar, Common.Block lastBlock) {
        this.support = support;
        this.registrar = registrar;
        this.lastBlock = lastBlock;
    }


    public Common.Block createNextBlock(Common.Envelope[] messages) {

        byte[] previousBlockHash = BlockHelper.hash(lastBlock.getHeader().toByteArray());
        Common.BlockData.Builder data = Common.BlockData.newBuilder();

        for (int i = 0; i < messages.length; i++) {
            data.addData(ByteString.copyFrom(messages[i].toByteArray()));
        }

        Common.Block block = BlockHelper.createBlock(lastBlock.getHeader().getNumber() + 1, previousBlockHash);
        Common.BlockHeader.Builder header = Common.BlockHeader.newBuilder()
                .setDataHash(ByteString.copyFrom(BlockHelper.hash(data.build().toByteArray())));

        block.toBuilder()
                .setData(data)
                .setHeader(header);
        return block;
    }

    public void writeConfigBlock(Common.Block block, byte[] encodedMetadataValue) {
        Common.Envelope ctx = CommonUtils.extractEnvelop(block, 0);
        Common.Payload payload = Utils.unmarshalPayload(ctx.getPayload().toByteArray());
        if (payload.getHeader() == null) {
            log.error("Told to write a config block, but configtx payload header is missing");
        }
        Common.GroupHeader groupHeader = Utils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());
        switch (groupHeader.getType()) {
            case Common.HeaderType.CONSENTER_TRANSACTION_VALUE:
                Common.Envelope groupConfig = Utils.unmarshalEnvelope(payload.getData().toByteArray());
                //TODO 注册新的 bw.registrar.newChain(newChannelConfig)
                break;
            case Common.HeaderType.CONFIG_VALUE:
                Configtx.ConfigEnvelope configEnvelope = Utils.unmarshalConfigEnvelope(payload.getData().toByteArray());
                support.validate(configEnvelope);
                IGroupConfigBundle iGroupConfigBundle = support.createBundle(groupHeader.getGroupId(), configEnvelope.getConfig());
                support.update(iGroupConfigBundle);
                break;
            default:
                log.error(String.format("Told to write a config block with unknown header type: %v", groupHeader.getType()));
        }

        writeBlock(block, encodedMetadataValue);
    }

    public void writeBlock(Common.Block block, byte[] encodedMetadataValue) {
        //TODO 开启线程锁
        lastBlock = block;
        commitBlock(encodedMetadataValue);
    }

    private void commitBlock(byte[] encodedMetadataValue) {
        if (encodedMetadataValue != null) {

            try {
                Common.Metadata metadata = Common.Metadata.parseFrom(encodedMetadataValue);
                lastBlock.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.CONSENTER_VALUE, ByteString.copyFrom(Utils.marshalOrPanic(metadata)));
                addBlockSignature(lastBlock);
                addLastConfigSignature(lastBlock);
                try {
                    support.append(lastBlock);
                } catch (LedgerException e) {
                    e.printStackTrace();
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }

    }


    public void addBlockSignature(Common.Block block) {

        try {

            Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(Utils.marshalOrPanic(CommonUtils.newSignatureHeaderOrPanic(support)));

            Common.MetadataSignature blockSignature=Common.MetadataSignature.newBuilder().setSignatureHeader(signatureHeader.toByteString()).build();
            byte[] blockSignatureValue = new byte[0];
            //TODO toByteArray转换为Bytes
            blockSignature.toBuilder().setSignature(ByteString.copyFrom(
                    CommonUtils.signOrPanic(support, Utils.concatenateBytes(blockSignatureValue,blockSignature.getSignatureHeader().toByteArray(),
                            block.getHeader().toByteArray()))));

            Common.MetadataSignature metadataSignature=Common.MetadataSignature.parseFrom(blockSignature.toByteArray());
            Common.Metadata metadata=Common.Metadata.newBuilder()
                    .addSignatures(metadataSignature)
                    .setValue(ByteString.copyFrom(blockSignatureValue)).build();
            block.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.SIGNATURES_VALUE, ByteString.copyFrom(Utils.marshalOrPanic(metadata)));

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    public void   addLastConfigSignature(Common.Block block){
        long configSeq= support.sequence();
        if(configSeq>lastConfigSeq){
            log.debug(String.format("[channel: %s] Detected lastConfigSeq transitioning from %d to %d, setting lastConfigBlockNum from %d to %d",
                    support.groupId(),lastConfigSeq, configSeq, lastConfigBlockNum, block.getHeader().getNumber()));
            lastConfigBlockNum=block.getHeader().getNumber();
            lastConfigSeq=configSeq;
        }
        try {
            Common.LastConfig lastConfig=Common.LastConfig.newBuilder().setIndex(lastConfigBlockNum).build();
            byte[] lastConfigValue= Utils.marshalOrPanic(lastConfig);

            Common.MetadataSignature lastConfigSignature=Common.MetadataSignature.parseFrom(Utils.marshalOrPanic(CommonUtils.newSignatureHeaderOrPanic(support)));
            Common.MetadataSignature metadataSignature=Common.MetadataSignature.parseFrom(lastConfigSignature.toByteArray());
            Common.Metadata metadata=Common.Metadata.newBuilder()
                    .addSignatures(metadataSignature)
                    .setValue(ByteString.copyFrom(lastConfigValue)).build();
            block.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.LAST_CONFIG_VALUE,ByteString.copyFrom(Utils.marshalOrPanic(metadata)));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }



}
