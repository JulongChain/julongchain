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
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.ledger.blockledger.IFactory;
import org.bcia.javachain.common.ledger.blockledger.IReader;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.ledger.blockledger.Util;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.msgprocessor.IChainCreator;
import org.bcia.javachain.consenter.common.msgprocessor.IGroupConfigTemplator;
import org.bcia.javachain.consenter.consensus.IConsensue;
import org.bcia.javachain.consenter.util.BlockUtils;
import org.bcia.javachain.consenter.util.CommonUtils;
import org.bcia.javachain.consenter.util.ConfigTxUtil;
import org.bcia.javachain.protos.common.Common;

import org.bcia.javachain.protos.common.Configtx;

import java.util.*;

/**
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public class Registrar implements IChainCreator {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Registrar.class);
    Map<String, ChainSupport> chains = new HashMap<>();

    Map<String, IConsensue> consenters;

    ILocalSigner signer;

    String systemGroupID;

    ChainSupport systemGroup;

    IFactory ledgerFactory;

    IGroupConfigTemplator templator;


    //TODO 资源配置检查
    public void checkResources(IGroupConfigBundle resources) {


    }



   public void checkResourcesOrPanic(IGroupConfigBundle res){

        if(true){
            log.error(String.format("[channel %s] ",res.getValidator().groupId()));
        }
   }

    //TODO  增加回调函数
    public void newRegistrar(IFactory ledgerFactory, Map<String, IConsensue> consenters, ILocalSigner signer) throws LedgerException, ValidateException, PolicyException, InvalidProtocolBufferException {
        List<String>existingChains= ledgerFactory.groupIDs();
        for (String chainId: existingChains) {
            ReadWriteBase rl= ledgerFactory.getOrCreate(chainId);
            Common.Envelope configTx=   getConfigTx(rl);
            if(configTx==null){
                log.error("Programming error, configTx should never be nil here");
            }
            LedgerResources ledgerResources=newLedgerResources(configTx);

        }

    }

    public GroupConfigBundle newBundle(String groupId, Configtx.Config config) throws ValidateException, PolicyException, InvalidProtocolBufferException {

        return  new GroupConfigBundle(groupId,config);
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


    public Object broadcastGroupSupport(Common.Envelope msg){
       Common.GroupHeader chdr= CommonUtils.groupHeader(msg);
       //chains
        return null;
    }


    public LedgerResources newLedgerResources(Common.Envelope configTx) throws ValidateException, PolicyException, InvalidProtocolBufferException, LedgerException {
        Common.Payload payload = CommonUtils.unmarshalPayload(configTx.getPayload().toByteArray());
        if (payload.getHeader() == null) {
            log.error("Missing group header");
        }
        Common.GroupHeader chdr = CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());

        Configtx.ConfigEnvelope configEnvelope = ConfigTxUtil.unmarshalConfigEnvelope(payload.getData().toByteArray());

        GroupConfigBundle bundle= new  GroupConfigBundle(chdr.getGroupId(),configEnvelope.getConfig());

        ledgerFactory.getOrCreate(chdr.getGroupId());


        return null;
    }

    public void newChain(Common.Envelope configtx) throws ValidateException, PolicyException, InvalidProtocolBufferException, LedgerException {
        LedgerResources ledgerResources = newLedgerResources(configtx);

        List<Common.Envelope> envelopes = Arrays.asList(new Common.Envelope[]{configtx});

        try {
            ledgerResources.writer.append(Util.createNextBlock(ledgerResources.reader, envelopes));
        } catch (LedgerException e) {
            e.printStackTrace();
        }
        Map<String, ChainSupport> newChains = new HashMap<>();

        for (Iterator entries=chains.keySet().iterator();entries.hasNext();) {
            String key=  entries.next().toString();
            newChains.put(key,chains.get(key));
        }
        ChainSupport chainSupport=new ChainSupport();
        ChainSupport cs=chainSupport.newChainSupport(this,ledgerResources,consenters,signer);
        String chainId=ledgerResources.mutableResources.getValidator().groupId();
        newChains.put(chainId,cs);
        cs.start();
        chains=newChains;
    }

    @Override
    public IGroupConfigBundle newGroupConfig(Common.Envelope envConfigUpdate) {
        return templator.newGroupConfig(envConfigUpdate);
    }

    @Override
    public IGroupConfigBundle createBundle(String groupId, Configtx.Config config) {
        try {
            return new  GroupConfigBundle(groupId,config);
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
}
