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
package org.bcia.julongchain.consenter.common.msgprocessor;


import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IConsenterConfig;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.PolicyConstant;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.consensus.IProcessor;
import org.bcia.julongchain.consenter.entity.ConfigMsg;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public class SystemGroup  implements IProcessor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SystemGroup.class);
    private StandardGroup standardGroup;
    private IGroupConfigTemplator groupConfigTemplator;
    private RuleSet filters;

    public SystemGroup() {
    }

    public SystemGroup(StandardGroup standardGroup, IGroupConfigTemplator groupConfigTemplator, RuleSet filters) {
        this.standardGroup = standardGroup;
        this.groupConfigTemplator = groupConfigTemplator;
        this.filters = filters;
    }

    public static SystemGroup newSystemGroup(IStandardGroupSupport standardGroupSupport,IGroupConfigTemplator groupConfigTemplator,RuleSet filters){
        StandardGroup standardGroup= new StandardGroup(standardGroupSupport,filters);
        return new SystemGroup(standardGroup,groupConfigTemplator,filters);
    }

    public static  RuleSet createSystemChannelFilters(IChainCreator chainCreator,IGroupConfigBundle filterSupport) {
        IConsenterConfig consenterConfig=filterSupport.getGroupConfig().getConsenterConfig();
       if(consenterConfig==null){
           try {
               throw new ConsenterException("Cannot create system channel filters without orderer config");
           } catch (ConsenterException e) {
               e.printStackTrace();
           }
       }
        RuleSet ruleSet=new RuleSet(new IRule[]{new EmptyRejectRule(),new ExpirationRejectRule(filterSupport),new SizeFilter(consenterConfig),
               new SigFilter(PolicyConstant.GROUP_APP_WRITERS,filterSupport.getPolicyManager()),
               new SystemGroupFilter(chainCreator,filterSupport.getGroupConfig())
       });

        return ruleSet;
    }

    @Override
    public boolean classfiyMsg(Common.GroupHeader chdr) {
        return false;
    }

    @Override
    public long processNormalMsg(Common.Envelope env) throws InvalidProtocolBufferException {
        String groupId = CommonUtils.groupId(env);
        if (groupId != standardGroup.getSupport().getGroupId()) {
            return 0;
        }
        return standardGroup.processNormalMsg(env);
    }

    @Override
    public ConfigMsg processConfigUpdateMsg(Common.Envelope envConfigUpdate) throws ConsenterException, InvalidProtocolBufferException, ValidateException {
        String groupId = CommonUtils.groupId(envConfigUpdate);
        log.debug(String.format("Processing config update tx with system channel message processor for channel ID %s", groupId));
        String  standardGroupName= standardGroup.getSupport().getGroupId();
        if (groupId.equals(standardGroup.getSupport().getGroupId()) ) {
            return standardGroup.processConfigUpdateMsg(envConfigUpdate);
        }
        log.debug(String.format("Processing group create tx for group %s on system group %s", groupId,
                standardGroup.getSupport().getGroupId()));
        IGroupConfigBundle bundle = groupConfigTemplator.newGroupConfig(envConfigUpdate);

        Configtx.ConfigEnvelope newGroupConfigEnv = bundle.getConfigtxValidator().proposeConfigUpdate(envConfigUpdate);

        Common.Envelope newChannelEnvConfig = TxUtils.createSignedEnvelope(Common.HeaderType.CONFIG_VALUE, groupId, standardGroup.getSupport().getSigner(), newGroupConfigEnv, Constant.MSGVERSION, Constant.EPOCH);

        Common.Envelope wrappedOrdererTransaction =
                TxUtils.createSignedEnvelope(Common.HeaderType.CONSENTER_TRANSACTION_VALUE,
                        standardGroup.getSupport().getGroupId(), standardGroup.getSupport().getSigner(), newChannelEnvConfig,
                        Constant.MSGVERSION, Constant.EPOCH);
//       new RuleSet(standardGroup.getFilters().getRules()).apply(wrappedOrdererTransaction);

        return new ConfigMsg(wrappedOrdererTransaction, standardGroup.getSupport().getSequence());
    }

    @Override
    public ConfigMsg processConfigMsg(Common.Envelope env) throws ConsenterException, InvalidProtocolBufferException, ValidateException, PolicyException {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(env.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        if (payload.getHeader() == null) {
            try {
                throw new ConsenterException("Abort processing config msg because no head was set");
            } catch (ConsenterException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        if (payload.getHeader().getGroupHeader() == null) {
            try {
                throw new ConsenterException("Abort processing config msg because no group header was set");
            } catch (ConsenterException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        Common.GroupHeader groupHeader = CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());

        switch (groupHeader.getType()) {

            case Common.HeaderType.CONFIG_VALUE:
                Configtx.ConfigEnvelope configEnvelope = null;
                try {
                    configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData().toByteArray());
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
                return standardGroup.processConfigUpdateMsg(configEnvelope.getLastUpdate());
            case Common.HeaderType.CONSENTER_TRANSACTION_VALUE:
                Configtx.ConfigEnvelope configEnv = null;
                Common.Envelope envelope = CommonUtils.unmarshalEnvelope(payload.getData().toByteArray());
                try {
                    configEnv = Configtx.ConfigEnvelope.parseFrom(payload.getData());
                    CommonUtils.unmarshalEnvelopeOfType(envelope, Common.HeaderType.CONFIG,configEnv);
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
                return processConfigUpdateMsg(configEnv.getLastUpdate());
            default:
                log.error(String.format("Panic processing config msg due to unexpected envelope type"));
                return null;
        }
    }


    public static ChainSupport newDefaultTemplator(ChainSupport support) {
        return support;
    }

//    @Override
//    public IGroupConfigBundle newGroupConfig(Common.Envelope envelope) {
//        return null;
//    }

    public StandardGroup getStandardGroup() {
        return standardGroup;
    }

    public void setStandardGroup(StandardGroup standardGroup) {
        this.standardGroup = standardGroup;
    }

    public IGroupConfigTemplator getGroupConfigTemplator() {
        return groupConfigTemplator;
    }

    public void setGroupConfigTemplator(IGroupConfigTemplator groupConfigTemplator) {
        this.groupConfigTemplator = groupConfigTemplator;
    }

    public RuleSet getFilters() {
        return filters;
    }

    public void setFilters(RuleSet filters) {
        this.filters = filters;
    }
}
