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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.Reader;
import org.bcia.javachain.common.ledger.blockledger.Util;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.policies.IManager;
import org.bcia.javachain.consenter.common.deliver.ISupport;
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
public class ChainSupport implements IStandardGroupSupport, ISupport {
    LedgerResources ledgerResources;
    IProcessor processor;
    BlockWriter blockWriter;
    IChain iChain;
    IReceiver cutter;
    ILocalSigner localSigner;

    public ChainSupport(LedgerResources ledgerResources, IProcessor processor, BlockWriter blockWriter, IChain iChain, IReceiver cutter, ILocalSigner localSigner) {
        this.ledgerResources = ledgerResources;
        this.processor = processor;
        this.blockWriter = blockWriter;
        this.iChain = iChain;
        this.cutter = cutter;
        this.localSigner = localSigner;
    }


    public ChainSupport newChainSupport(Registrar registrar, LedgerResources ledgerResources, Map<String, IConsensue> consenters, ILocalSigner signer) {
        Common.Block lastBlock=null;
        Common.Metadata metadata=null;
        try {
            lastBlock = Util.getBlock(ledgerResources.reader, ledgerResources.reader.height() - 1);
            metadata=BlockUtils.getMetadataFromBlock(lastBlock,Common.BlockMetadataIndex.CONSENTER_VALUE);
        } catch (LedgerException e) {
            e.printStackTrace();
        }
        this.ledgerResources=ledgerResources;

        return ;
    }


    @Override
    public long sequence() {
        return ledgerResources.mutableResources.configtxValidator().sequence();
    }

    @Override
    public IManager poclicyManager() {
        return null;
    }

    @Override
    public Reader reader() {
        return null;
    }

    @Override
    public String chainId() {
        return ledgerResources.mutableResources.configtxValidator().groupId();
    }

    @Override
    public ILocalSigner signer() {
        return localSigner;
    }

    @Override
    public Common.Envelope proposeConfigUpdate(Common.Envelope configtx) {
        Configtx.ConfigEnvelope env = ledgerResources.mutableResources.configtxValidator().proposeConfigUpdate(configtx);

        return null;
    }
}
