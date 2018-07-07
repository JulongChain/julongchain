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
package org.bcia.julongchain.consenter.common.multigroup;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.consenter.util.BlockHelper;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.consenter.util.Utils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/3/16
 * @company Dingxuan
 */
public class BlockWriter {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BlockWriter.class);
    // private IBlockWriterSupport support;
    private ChainSupport support;

    private Registrar registrar;

    private long lastConfigBlockNum;

    private long lastConfigSeq;

    private Common.Block lastBlock;

    public BlockWriter(ChainSupport support, Registrar registrar, Common.Block lastBlock) {
        this.support = support;
        this.registrar = registrar;
        this.lastBlock = lastBlock;
        this.lastConfigSeq = support.getSequence();
    }


    public Common.Block createNextBlock(Common.Envelope[] messages) {

        byte[] previousBlockHash = BlockHelper.hash(lastBlock.getHeader().toByteArray());
        Common.BlockData.Builder data = Common.BlockData.newBuilder();

        for (int i = 0; i < messages.length; i++) {
            data.addData(ByteString.copyFrom(messages[i].toByteArray()));
        }

        Common.Block block = BlockHelper.createBlock(lastBlock.getHeader().getNumber() + 1, previousBlockHash);
        Common.BlockHeader.Builder header = Common.BlockHeader.newBuilder(block.getHeader())
                .setDataHash(ByteString.copyFrom(BlockHelper.hash(data.build().toByteArray())));

        Common.Block.Builder updateBlockBuilder=Common.Block.newBuilder(block);
        updateBlockBuilder.setData(data).setHeader(header);
     //   lastBlock=updateBlockBuilder.build();
        return updateBlockBuilder.build();
    }

    public void writeConfigBlock(Common.Block block, byte[] encodedMetadataValue) throws InvalidProtocolBufferException, LedgerException, ValidateException, PolicyException {
        Common.Envelope ctx = CommonUtils.extractEnvelop(block, 0);
        Common.Payload payload = Utils.unmarshalPayload(ctx.getPayload().toByteArray());
        if (payload.getHeader() == null) {
            log.error("Told to write a config block, but configtx payload header is missing");
        }
        Common.GroupHeader groupHeader = Utils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());
        switch (groupHeader.getType()) {
            case Common.HeaderType.CONSENTER_TRANSACTION_VALUE:
                Common.Envelope groupConfig = Utils.unmarshalEnvelope(payload.getData().toByteArray());
                registrar.newChain(groupConfig);
                break;
            case Common.HeaderType.CONFIG_VALUE:
                Configtx.ConfigEnvelope configEnvelope = Utils.unmarshalConfigEnvelope(payload.getData().toByteArray());
                support.validate(configEnvelope);

                IGroupConfigBundle iGroupConfigBundle = support.createBundle(groupHeader.getGroupId(), configEnvelope.getConfig());
                //  support.getLedgerResources().getMutableResources().update();
                support.update(iGroupConfigBundle);
                break;
            default:
                log.error(String.format("Told to write a config block with unknown header type: %s", groupHeader.getType()));
        }

        writeBlock(block, encodedMetadataValue);
    }

    public synchronized void writeBlock(Common.Block block, byte[] encodedMetadataValue) {
        lastBlock = block;
        commitBlock(encodedMetadataValue);
    }

    private void commitBlock(byte[] encodedMetadataValue) {
        if (encodedMetadataValue != null) {
            Common.Metadata metadata = null;
            try {
                metadata = Common.Metadata.parseFrom(encodedMetadataValue);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            Common.Block.Builder block = Common.Block.newBuilder(lastBlock);
            block.getMetadataBuilder().setMetadata(Common.BlockMetadataIndex.CONSENTER_VALUE, ByteString.copyFrom(Utils.marshalOrPanic(metadata)));
            lastBlock = block.build();
        }
        addBlockSignature(lastBlock);
        addLastConfigSignature(lastBlock);
        try {
            support.getLedgerResources().getReadWriteBase().append(lastBlock);
        } catch (LedgerException e) {
            e.printStackTrace();
        }
    }


    public void addBlockSignature(Common.Block block) {

        try {
            Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(Utils.marshalOrPanic(CommonUtils.newSignatureHeaderOrPanic(support.getLocalSigner())));
            //构建MetadataSignatureBuilder 对象
            Common.MetadataSignature.Builder blockSignatureBuilder = Common.MetadataSignature
                    .newBuilder()
                    .setSignatureHeader(signatureHeader.toByteString());
            byte[] blockSignatureValue = new byte[0];
            blockSignatureBuilder.setSignature(ByteString.copyFrom(
                    CommonUtils.signOrPanic(support.getLocalSigner(), Utils.concatenateBytes(blockSignatureValue, blockSignatureBuilder.getSignatureHeader().toByteArray(),
                            block.getHeader().toByteArray()))));

            Common.MetadataSignature metadataSignature = Common.MetadataSignature.parseFrom(blockSignatureBuilder.build().toByteArray());
            Common.Metadata metadata = Common.Metadata.newBuilder()
                    .addSignatures(metadataSignature)
                    .setValue(ByteString.copyFrom(blockSignatureValue)).build();
            Common.Block.Builder updateBlockBuilder = Common.Block.newBuilder(block);
            updateBlockBuilder.getMetadataBuilder().setMetadata(Common.BlockMetadataIndex.SIGNATURES_VALUE,ByteString.copyFrom(metadata.toByteArray()));
            Common.Block updateBlock = updateBlockBuilder.build();
            //ProtoUtils.printMessageJson(block1);
            lastBlock = updateBlock;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    public void addLastConfigSignature(Common.Block block) {
        long configSeq = support.getSequence();
        if (configSeq > lastConfigSeq) {
            log.debug(String.format("[channel: %s] Detected lastConfigSeq transitioning from %d to %d, setting lastConfigBlockNum from %d to %d",
                    support.getGroupId(), lastConfigSeq, configSeq, lastConfigBlockNum, block.getHeader().getNumber()));
            lastConfigBlockNum = block.getHeader().getNumber();
            lastConfigSeq = configSeq;
        }
        try {
            Common.LastConfig lastConfig = Common.LastConfig.newBuilder().setIndex(lastConfigBlockNum).build();
            byte[] lastConfigValue = Utils.marshalOrPanic(lastConfig);

            Common.MetadataSignature lastConfigSignature = Common.MetadataSignature.parseFrom(Utils.marshalOrPanic(CommonUtils.newSignatureHeaderOrPanic(support.getLocalSigner())));
            Common.MetadataSignature metadataSignature = Common.MetadataSignature.parseFrom(lastConfigSignature.toByteArray());
            Common.Metadata metadata = Common.Metadata.newBuilder()
                    .addSignatures(metadataSignature)
                    .setValue(ByteString.copyFrom(lastConfigValue)).build();
            Common.Block.Builder updateblock = Common.Block.newBuilder(block);
            updateblock.getMetadataBuilder().setMetadata(Common.BlockMetadataIndex.LAST_CONFIG_VALUE,ByteString.copyFrom(metadata.toByteArray()));
            lastBlock = updateblock.build();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    public static JavaChainLog getLog() {
        return log;
    }

    public static void setLog(JavaChainLog log) {
        BlockWriter.log = log;
    }

    public ChainSupport getSupport() {
        return support;
    }

    public void setSupport(ChainSupport support) {
        this.support = support;
    }

    public Registrar getRegistrar() {
        return registrar;
    }

    public void setRegistrar(Registrar registrar) {
        this.registrar = registrar;
    }

    public long getLastConfigBlockNum() {
        return lastConfigBlockNum;
    }

    public void setLastConfigBlockNum(long lastConfigBlockNum) {
        this.lastConfigBlockNum = lastConfigBlockNum;
    }

    public long getLastConfigSeq() {
        return lastConfigSeq;
    }

    public void setLastConfigSeq(long lastConfigSeq) {
        this.lastConfigSeq = lastConfigSeq;
    }

    public Common.Block getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(Common.Block lastBlock) {
        this.lastBlock = lastBlock;
    }
}
