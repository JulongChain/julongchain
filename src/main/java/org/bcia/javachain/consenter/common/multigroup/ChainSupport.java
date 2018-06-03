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

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.GroupConfigBundle;
import org.bcia.javachain.common.ledger.blockledger.Util;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.consenter.common.blockcutter.BlockCutter;
import org.bcia.javachain.consenter.common.msgprocessor.IStandardGroupSupport;
import org.bcia.javachain.consenter.common.msgprocessor.StandardGroup;
import org.bcia.javachain.consenter.consensus.*;
import org.bcia.javachain.consenter.util.BlockUtils;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public class ChainSupport implements IStandardGroupSupport {
    LedgerResources ledgerResources;
    IProcessor processor;
    BlockWriter blockWriter;
    IChain chain;
    IReceiver cutter;
    ILocalSigner localSigner;


    public ChainSupport() {
    }

    public ChainSupport(Registrar registrar, LedgerResources ledgerResources, Map<String, IConsensue> consenters, ILocalSigner signer) {
        Common.Block lastBlock = null;
        Common.Metadata metadata = null;
        try {
            lastBlock = Util.getBlock(ledgerResources.reader, ledgerResources.reader.height() - 1);
            metadata = BlockUtils.getMetadataFromBlock(lastBlock, Common.BlockMetadataIndex.CONSENTER_VALUE);

        } catch (LedgerException e) {
            e.printStackTrace();
        }
        this.ledgerResources = ledgerResources;
        this.localSigner = signer;
        this.cutter = new BlockCutter(ledgerResources.mutableResources.getGroupConfig().getConsenterConfig());
        this.processor = new StandardGroup((IStandardGroupSupport) this, new StandardGroup().createStandardGroupFilters(this.ledgerResources.getMutableResources()));
        this.blockWriter = new BlockWriter(blockWriter.getSupport(), registrar, lastBlock);
        String consensusType = ledgerResources.mutableResources.getGroupConfig().getConsenterConfig().getConsensusType();
        IConsensue consenter = consenters.get(consensusType);
        chain = consenter.handleChain((IConsenterSupport) this, metadata);
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

    @Override
    public void validate(Configtx.ConfigEnvelope configEnv) {
        ledgerResources.mutableResources.getConfigtxValidator().validate(configEnv);
    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws InvalidProtocolBufferException, ValidateException {
        Configtx.ConfigEnvelope env = ledgerResources.mutableResources.getConfigtxValidator().proposeConfigUpdate(configtx);
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
        return ledgerResources.mutableResources.getConfigtxValidator().getGroupId();
    }

    @Override
    public long getSequence() {
        return ledgerResources.mutableResources.getConfigtxValidator().getSequence();
    }

    @Override
    public Configtx.Config getConfig() {
        return ledgerResources.mutableResources.getConfigtxValidator().getConfig();
    }

    @Override
    public ILocalSigner signer() {
        return localSigner;
    }
}
