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
import org.bcia.julongchain.common.deliver.ISupportManager;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IConsenterConfig;
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.IReader;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.blockledger.Util;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedgerIterator;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.broadcast.IGroupSupportRegistrar;
import org.bcia.julongchain.consenter.common.msgprocessor.DefaultTemplator;
import org.bcia.julongchain.consenter.common.msgprocessor.IChainCreator;
import org.bcia.julongchain.consenter.common.msgprocessor.IGroupConfigTemplator;
import org.bcia.julongchain.consenter.common.msgprocessor.SystemGroup;
import org.bcia.julongchain.consenter.consensus.IConsensusPlugin;
import org.bcia.julongchain.consenter.util.BlockHelper;
import org.bcia.julongchain.consenter.util.BlockUtils;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.consenter.util.ConfigTxUtil;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bouncycastle.util.encoders.Hex;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static org.bcia.julongchain.common.groupconfig.LogSanityChecks.logPolicy;
import static org.bcia.julongchain.consenter.common.msgprocessor.SystemGroup.newSystemGroup;

/**
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public class Registrar implements IChainCreator, IGroupSupportRegistrar, ISupportManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Registrar.class);
    private Map<String, ChainSupport> chains = new ConcurrentHashMap<>();

    private Map<String, IConsensusPlugin> consenters;

    private ILocalSigner signer;

    private String systemGroupID;

    private ChainSupport systemGroup;

    private IFactory ledgerFactory;

    private IGroupConfigTemplator templator;

    private LedgerResources ledgerResources;

    public Registrar() {
    }

    public Registrar(Map<String, ChainSupport> chains, Map<String, IConsensusPlugin> consenters, ILocalSigner signer, IFactory ledgerFactory, ChainSupport systemGroup) {
        this.chains = chains;
        this.consenters = consenters;
        this.signer = signer;
        this.ledgerFactory = ledgerFactory;
        this.systemGroup = systemGroup;
    }

    public void checkResources(IGroupConfigBundle resources) throws ConsenterException {
        logPolicy(resources);
        IConsenterConfig consenterConfig = resources.getGroupConfig().getConsenterConfig();
        if (consenterConfig == null) {
            throw new ConsenterException("config does not contain consenter config");
        }
        if (!consenterConfig.getCapabilities().isSupported()) {
            throw new ConsenterException("config requires unsupported channel capabilities");
        }
    }

    public void checkResourcesOrPanic(IGroupConfigBundle res) throws ConsenterException {
        checkResources(res);
    }

    private Common.Envelope getConfigTx(IReader blockLedgerReader) {
        Common.Block configBlock = null;
        try {
            Common.Block lastBlock = Util.getBlock(blockLedgerReader, blockLedgerReader.height() - 1);
            long index = BlockUtils.getLastConfigIndexFromBlock(lastBlock);
            configBlock = Util.getBlock(blockLedgerReader, index);
            if (configBlock == null) {
                log.error("Config block does not exist");
            }
        } catch (LedgerException e) {
            e.printStackTrace();
        }
        return CommonUtils.extractEnvelopeOrPanic(configBlock, 0);
    }

    public Registrar newRegistrar(IFactory ledgerFactory, Map<String, IConsensusPlugin> consenters, ILocalSigner signer) throws LedgerException, ValidateException, PolicyException, InvalidProtocolBufferException {
        this.ledgerFactory = ledgerFactory;
        this.signer = signer;
        this.consenters = consenters;
        List<String> existingChains = ledgerFactory.groupIDs();
        for (String chainId : existingChains) {
            ReadWriteBase rl = ledgerFactory.getOrCreate(chainId);
            Common.Envelope configTx = getConfigTx(rl);
            if (configTx == null) {
                log.error("Programming error, configTx should never be nil here");
            }
            LedgerResources ledgerResources = newLedgerResources(configTx);
            String groupId = ledgerResources.getMutableResources().getConfigtxValidator().getGroupId();

            if (ledgerResources.getMutableResources().getGroupConfig().getConsortiumsConfig() != null) {
                ChainSupport chain = new ChainSupport(this, ledgerResources, consenters, signer);
                this.templator = new DefaultTemplator(chain.getLedgerResources().getMutableResources());
                chain.setProcessor(newSystemGroup(chain, templator, SystemGroup.createSystemChannelFilters(this, chain.getLedgerResources().getMutableResources())));
                //组装seekPosition
                Ab.SeekPosition.Builder seekPosition = Ab.SeekPosition.newBuilder();
                Ab.SeekOldest.Builder seekOldest = Ab.SeekOldest.newBuilder();
                seekPosition.setOldest(seekOldest);

                org.bcia.julongchain.common.ledger.blockledger.IIterator iter = rl.iterator(seekPosition.build());
                //TODO 使用完关闭
                //iter.close();
                FileLedgerIterator fileLedgerIterator = (FileLedgerIterator) iter;
                if (fileLedgerIterator.getBlockNum() != 0) {
                    log.error(String.format("Error iterating over system channel: '%s', expected position 0, got %d", groupId, fileLedgerIterator.getBlockNum()));
                }
                QueryResult queryResult = iter.next();
                iter.close();
                Map.Entry<QueryResult, Common.Status> genesisBlock = (Map.Entry<QueryResult, Common.Status>) queryResult.getObj();
                genesisBlock.getValue();
                Common.Block block = (Common.Block) genesisBlock.getKey().getObj();
                if (genesisBlock.getValue() != Common.Status.SUCCESS) {
                    log.error(String.format("Error reading genesis block of system channel '%s'", groupId));
                }
                log.info(String.format("Starting system channel '%s' with genesis block hash %s and consenter type %s", groupId, Hex.toHexString(BlockHelper.hash(block.getHeader().toByteArray())), chain.getLedgerResources().getMutableResources().getGroupConfig().getConsenterConfig().getConsensusType()));
                chains.put(groupId, chain);
                systemGroupID = groupId;
                systemGroup = chain;
                chain.start();
            } else {
                log.debug(String.format("Starting chain: %s", groupId));
                ChainSupport chain = new ChainSupport(this, ledgerResources, consenters, signer);
                chains.put(groupId, chain);
                chain.start();
            }
            if (systemGroupID == "") {
                log.error("No system chain found.  If bootstrapping, does your system channel contain a consortiums group definition?");
            }
        }
        return new Registrar(chains, consenters, signer, ledgerFactory, systemGroup);
    }

    public static GroupConfigBundle newBundle(String groupId, Configtx.Config config) throws ValidateException, PolicyException, InvalidProtocolBufferException {
        return new GroupConfigBundle(groupId, config);
    }


    public void update(GroupConfigBundle groupConfigBundle) throws ConsenterException {
        checkResourcesOrPanic(groupConfigBundle);
        //TODO update实现 bundlesource
        ledgerResources.getMutableResources().update();
    }


    public String getSystemGroupID() {
        return systemGroupID;
    }

    public Map<String, Object> broadcastGroupSupport(Common.Envelope msg) throws InvalidProtocolBufferException {
        Common.GroupHeader chdr = CommonUtils.groupHeader(msg);
        ChainSupport cs = chains.get(chdr.getGroupId());
        if (cs == null) {
            cs = systemGroup;
        }
        boolean isConfig = false;
        if (chdr.getType() == Common.HeaderType.CONFIG_UPDATE_VALUE) {
            isConfig = true;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("isConfig", isConfig);
        map.put("chdr", chdr);
        map.put("cs", cs);
        return map;
    }

    public ChainSupport getChain(String chainId) {
        ChainSupport cs = chains.get(chainId);
        return cs;
    }

    public LedgerResources newLedgerResources(Common.Envelope configTx) throws ValidateException, PolicyException, InvalidProtocolBufferException, LedgerException {
        Common.Payload payload = CommonUtils.unmarshalPayload(configTx.getPayload().toByteArray());
        if (payload.getHeader() == null) {
            log.error("Missing group header");
        }
        Common.GroupHeader chdr = CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());
        Configtx.ConfigEnvelope configEnvelope = ConfigTxUtil.unmarshalConfigEnvelope(payload.getData().toByteArray());
        GroupConfigBundle bundle = new GroupConfigBundle(chdr.getGroupId(), configEnvelope.getConfig());

        //TODO  checkResourcesOrPanic报错
//        try {
//            checkResourcesOrPanic(bundle);
//        } catch (ConsenterException e) {
//         log.error(e.getMessage());
//        }
        ReadWriteBase ledger = ledgerFactory.getOrCreate(chdr.getGroupId());
        //TODO 回调函数
        return new LedgerResources(bundle, ledger);


    }

    public Registrar newChain(Common.Envelope configtx) throws ValidateException, PolicyException, InvalidProtocolBufferException, LedgerException {
        LedgerResources ledgerResources = newLedgerResources(configtx);
        List<Common.Envelope> envelopes = Arrays.asList(new Common.Envelope[]{configtx});
        try {
            ledgerResources.getReadWriteBase().append(Util.createNextBlock(ledgerResources.getReadWriteBase(), envelopes));
        } catch (LedgerException e) {
            e.printStackTrace();
        }
        ChainSupport cs = new ChainSupport(this, ledgerResources, consenters, signer);
        String chainId = ledgerResources.getMutableResources().getConfigtxValidator().getGroupId();
        chains.put(chainId, cs);
        cs.start();

        return this;
    }

    @Override
    public IGroupConfigBundle newGroupConfig(Common.Envelope envConfigUpdate) throws InvalidProtocolBufferException {
        return templator.newGroupConfig(envConfigUpdate);
    }

    @Override
    public IGroupConfigBundle createBundle(String groupId, Configtx.Config config) {
        try {
            return new GroupConfigBundle(groupId, config);
        } catch (ValidateException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int groupCount() {
        return chains.size();
    }

    public Map<String, ChainSupport> getChains() {
        return chains;
    }

    public Map<String, IConsensusPlugin> getConsenters() {
        return consenters;
    }

    public ILocalSigner getSigner() {
        return signer;
    }

    public ChainSupport getSystemGroup() {
        return systemGroup;
    }

    public IFactory getLedgerFactory() {
        return ledgerFactory;
    }

    public IGroupConfigTemplator getTemplator() {
        return templator;
    }

    public LedgerResources getLedgerResources() {
        return ledgerResources;
    }
}
