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
import org.bcia.julongchain.consenter.consensus.IProcessor;
import org.bcia.julongchain.consenter.entity.ConfigMsg;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/5/18
 * @company Dingxuan
 */
public class StandardGroup implements IProcessor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(StandardGroup.class);

    private IStandardGroupSupport standardGroupSupport;

    private RuleSet filters;

    public StandardGroup() {
    }

    public StandardGroup(IStandardGroupSupport standardGroupSupport, RuleSet filters) {
        this.standardGroupSupport = standardGroupSupport;
        this.filters = filters;
    }

    public RuleSet createStandardGroupFilters(IGroupConfigBundle filterSupport) {
        IConsenterConfig consenterConfig = filterSupport.getGroupConfig().getConsenterConfig();
        if (consenterConfig == null) {
            try {
                throw new ConsenterException("Missing Consenter config");
            } catch (ConsenterException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        //TODO EmptyRejectRule 待确定是否为无参构函数
        filters = new RuleSet(new IRule[]{new EmptyRejectRule(), new ExpirationRejectRule(filterSupport),
                new SizeFilter(consenterConfig), new SigFilter(PolicyConstant.GROUP_APP_WRITERS, filterSupport.getPolicyManager())});
        return filters;
    }

    @Override
    public boolean classfiyMsg(Common.GroupHeader chdr) {
        //配置类型返回true
        if (Common.HeaderType.CONFIG_VALUE == chdr.getType()) {
            return true;
        } else if (Common.HeaderType.CONFIG_UPDATE_VALUE == chdr.getType()) {
            return true;
        } else if (Common.HeaderType.CONSENTER_TRANSACTION_VALUE == chdr.getType()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long processNormalMsg(Common.Envelope env) {
        long configSeq = standardGroupSupport.getSequence();
        return configSeq;
    }

    @Override
    public ConfigMsg processConfigUpdateMsg(Common.Envelope env) throws InvalidProtocolBufferException, ValidateException{
        long seq = standardGroupSupport.getSequence();
        //TODO 通过apply过滤
        Configtx.ConfigEnvelope configEnvelope = standardGroupSupport.proposeConfigUpdate(env);
        int headerType = 0;
        Common.Envelope config = TxUtils.createSignedEnvelope(headerType, standardGroupSupport.getGroupId(),
                standardGroupSupport.getSigner(), configEnvelope, Constant.MSGVERSION, Constant.EPOCH);
        return new ConfigMsg(config, seq);
    }

    @Override
    public ConfigMsg processConfigMsg(Common.Envelope env) throws InvalidProtocolBufferException, ValidateException, PolicyException {
        Configtx.ConfigEnvelope configEnvelope = null;
        CommonUtils.unmarshalEnvelopeOfType(env, Common.HeaderType.CONFIG, configEnvelope);
        //根据Envelope中groupHeader中的type,将data转换为ConfigEnvelop
        try {
            Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(env.getPayload().toByteArray());
            if (groupHeader.getType() == Common.HeaderType.CONFIG_VALUE) {
                Common.Payload payload = Common.Payload.parseFrom(env.getPayload().toByteArray());
                configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return processConfigUpdateMsg(configEnvelope.getLastUpdate());
    }

    public IStandardGroupSupport getSupport() {
        return standardGroupSupport;
    }

    public void setSupport(IStandardGroupSupport support) {
        this.standardGroupSupport = support;
    }

    public RuleSet getFilters() {
        return filters;
    }

    public void setFilters(RuleSet filters) {
        this.filters = filters;
    }
}
