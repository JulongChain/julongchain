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
package org.bcia.julongchain.common.genesis;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.common.util.proto.ProposalUtils;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 创世区块工厂
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class GenesisBlockFactory implements IGenesisBlockFactory {
    private static final int MSP_VERSION = 1;
    private static final long CURRENT_EPOCH = 0L;
    private static final int NONCE_LENGTH = 24;

    private Configtx.ConfigTree groupTree;

    public GenesisBlockFactory(Configtx.ConfigTree groupTree) {
        this.groupTree = groupTree;
    }

    @Override
    public Common.Block getGenesisBlock(String groupId) throws JavaChainException {
        byte[] nonce = CspManager.getDefaultCsp().rng(NONCE_LENGTH, null);

        IMsp localMsp = GlobalMspManagement.getLocalMsp();
        ISigningIdentity signingIdentity = localMsp.getDefaultSigningIdentity();
        byte[] creator = signingIdentity.serialize();

        //计算交易ID
        String txId = ProposalUtils.computeProposalTxID(creator, nonce);

        //构造负载头部
        Common.Header header = EnvelopeHelper.buildHeader(Common.HeaderType.CONFIG_VALUE, MSP_VERSION, groupId, txId,
                CURRENT_EPOCH, null, creator, nonce);

        //构造ConfigEnvelope对象，作为负载数据
        Configtx.Config config = Configtx.Config.newBuilder().setGroupTree(groupTree).build();
        Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.newBuilder().setConfig(config).build();

        //构造负载对象
        Common.Payload payload = Common.Payload.newBuilder().setData(configEnvelope.toByteString()).setHeader(header)
                .build();
        //构造信封对象
        Common.Envelope envelope = Common.Envelope.newBuilder().setPayload(payload.toByteString()).build();

        //构造区块数据
        Common.BlockData.Builder blockDataBuilder = Common.BlockData.newBuilder();
        blockDataBuilder.addData(envelope.toByteString());
        Common.BlockData blockData = blockDataBuilder.build();

        //构造区块头部
        Common.BlockHeader.Builder blockHeaderBuilder = Common.BlockHeader.newBuilder();
        blockHeaderBuilder.setNumber(0);
        byte[] dataHash = CspManager.getDefaultCsp().hash(blockData.toByteArray(), null);
        blockHeaderBuilder.setDataHash(ByteString.copyFrom(dataHash));
        Common.BlockHeader blockHeader = blockHeaderBuilder.build();

        //构造区块元数据
        Common.LastConfig lastConfig = Common.LastConfig.newBuilder().setIndex(0).build();
        Common.Metadata metadata = Common.Metadata.newBuilder().setValue(lastConfig.toByteString()).build();
        Common.BlockMetadata.Builder blockMetadataBuilder = Common.BlockMetadata.newBuilder();

        blockMetadataBuilder.addMetadata(Common.Metadata.newBuilder().build().toByteString());
        blockMetadataBuilder.addMetadata(metadata.toByteString());
        blockMetadataBuilder.addMetadata(Common.Metadata.newBuilder().build().toByteString());
        blockMetadataBuilder.addMetadata(Common.Metadata.newBuilder().build().toByteString());

//        blockMetadataBuilder.setMetadata(Common.BlockMetadataIndex.SIGNATURES_VALUE, Common.Metadata.newBuilder()
//                .build().toByteString());
//        blockMetadataBuilder.setMetadata(Common.BlockMetadataIndex.LAST_CONFIG_VALUE, metadata.toByteString());
//        blockMetadataBuilder.setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER_VALUE, Common.Metadata.newBuilder()
//                .build().toByteString());
//        blockMetadataBuilder.setMetadata(Common.BlockMetadataIndex.CONSENTER_VALUE, Common.Metadata.newBuilder()
//                .build().toByteString());

        Common.BlockMetadata blockMetadata = blockMetadataBuilder.build();

        //构造最终区块
        Common.Block.Builder blockBuilder = Common.Block.newBuilder();
        blockBuilder.setHeader(blockHeader);
        blockBuilder.setData(blockData);
        blockBuilder.setMetadata(blockMetadata);

        return blockBuilder.build();
    }
}
