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

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.ledger.blockledger.Util;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.consenter.common.blockcutter.BlockCutter;
import org.bcia.julongchain.consenter.common.msgprocessor.IStandardGroupSupport;
import org.bcia.julongchain.consenter.common.msgprocessor.StandardGroup;
import org.bcia.julongchain.consenter.consensus.*;
import org.bcia.julongchain.consenter.util.BlockUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public class ChainSupport implements IStandardGroupSupport, IConsenterSupport {
    private LedgerResources ledgerResources;
    private IProcessor processor;
    private BlockWriter blockWriter;
    private IChain chain;
    private IReceiver cutter;
    private ILocalSigner localSigner;
    private IGroupConfigBundle updateBundle;

    public ChainSupport() {
    }

    public ChainSupport(Registrar registrar, LedgerResources ledgerResources, Map<String, IConsensusPlugin> consenters, ILocalSigner signer) {
        Common.Block lastBlock = null;
        Common.Metadata metadata = null;
        try {
            lastBlock = Util.getBlock(ledgerResources.getReadWriteBase(), ledgerResources.getReadWriteBase().height() - 1);
            metadata = BlockUtils.getMetadataFromBlock(lastBlock, Common.BlockMetadataIndex.CONSENTER_VALUE);

        } catch (LedgerException e) {
            e.printStackTrace();
        }
        this.ledgerResources = ledgerResources;
        this.localSigner = signer;
        this.cutter = new BlockCutter(ledgerResources.getMutableResources().getGroupConfig().getConsenterConfig());
        this.processor = new StandardGroup(this, new StandardGroup().createStandardGroupFilters(this.ledgerResources.getMutableResources()));
        this.blockWriter = new BlockWriter(this, registrar, lastBlock);
        // this.blockWriter = new BlockWriter(blockWriter.getSupport(), registrar, lastBlock);
        String consensusType = ledgerResources.getMutableResources().getGroupConfig().getConsenterConfig().getConsensusType();
        IConsensusPlugin consenter = consenters.get(consensusType);
        chain = consenter.handleChain(this, metadata);
    }

    public GroupConfigBundle createBundle(String groupId, Configtx.Config config) throws ValidateException, PolicyException, InvalidProtocolBufferException {
        return Registrar.newBundle(groupId, config);
    }


    public void update(IGroupConfigBundle groupConfigBundle) {
        this.updateBundle = groupConfigBundle;
    }

    public void start() {
        chain.start();
    }

    public LedgerResources getLedgerResources() {
        return ledgerResources;
    }

    public IProcessor getProcessor() {
        return processor;
    }

    public BlockWriter getBlockWriter() {
        return blockWriter;
    }

    public IChain getChain() {
        return chain;
    }

    public IReceiver getCutter() {
        return cutter;
    }

    public ILocalSigner getLocalSigner() {
        return localSigner;
    }

    public IGroupConfigBundle getUpdateBundle() {
        return updateBundle;
    }

    public void setLedgerResources(LedgerResources ledgerResources) {
        this.ledgerResources = ledgerResources;
    }

    public void setProcessor(IProcessor processor) {
        this.processor = processor;
    }

    public void setBlockWriter(BlockWriter blockWriter) {
        this.blockWriter = blockWriter;
    }

    public void setChain(IChain chain) {
        this.chain = chain;
    }

    public void setCutter(IReceiver cutter) {
        this.cutter = cutter;
    }

    public void setLocalSigner(ILocalSigner localSigner) {
        this.localSigner = localSigner;
    }

    public void setUpdateBundle(IGroupConfigBundle updateBundle) {
        this.updateBundle = updateBundle;
    }

    @Override
    public void validate(Configtx.ConfigEnvelope configEnv) throws InvalidProtocolBufferException, ValidateException {
        ledgerResources.getMutableResources().getConfigtxValidator().validate(configEnv);
    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws InvalidProtocolBufferException, ValidateException {
        Configtx.ConfigEnvelope env = ledgerResources.getMutableResources().getConfigtxValidator().proposeConfigUpdate(configtx);
        GroupConfigBundle bundle = null;
        try {
            bundle = new GroupConfigBundle(this.getGroupId(), env.getConfig());
        } catch (PolicyException e) {
            e.printStackTrace();
        }
        bundle.validateNew(bundle);
        return env;
    }

    @Override
    public String getGroupId() {
        return ledgerResources.getMutableResources().getConfigtxValidator().getGroupId();
    }

    @Override
    public long getSequence() {
        return ledgerResources.getMutableResources().getConfigtxValidator().getSequence();
    }

    @Override
    public Configtx.Config getConfig() {
        return ledgerResources.getMutableResources().getConfigtxValidator().getConfig();
    }

    @Override
    public ILocalSigner getSigner() {
        return localSigner;
    }

    @Override
    public Common.Block createNextBlock(Common.Envelope[] messages) {
        return blockWriter.createNextBlock(messages);
    }

    @Override
    public void writeBlock(Common.Block block, byte[] encodedMetadataValue) {
        blockWriter.writeBlock(block, encodedMetadataValue);
    }

    @Override
    public void writeConfigBlock(Common.Block block, byte[] encodedMetadataValue) throws InvalidProtocolBufferException, LedgerException, ValidateException, PolicyException {
        blockWriter.writeConfigBlock(block, encodedMetadataValue);
    }

}
