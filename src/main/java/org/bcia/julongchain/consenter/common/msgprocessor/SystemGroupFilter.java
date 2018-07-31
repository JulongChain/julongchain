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
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IConsenterConfig;
import org.bcia.julongchain.common.groupconfig.config.IGroupConfig;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/5/26
 * @company Dingxuan
 */
public class SystemGroupFilter implements IRule {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SystemGroupFilter.class);
    private IChainCreator chainCreator;
    private IGroupConfig groupConfig;

    public SystemGroupFilter(IChainCreator chainCreator, IGroupConfig groupConfig) {
        this.chainCreator = chainCreator;
        this.groupConfig = groupConfig;
    }

    @Override
    public void apply(Common.Envelope message) throws ConsenterException, InvalidProtocolBufferException {
        Common.Payload msgData = null;
        try {
            msgData = Common.Payload.parseFrom(message.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new ConsenterException("bad payload");
        }
        if (msgData.getHeader() == null) {
            throw new ConsenterException("missing payload header");
        }
        Common.GroupHeader chdr = CommonUtils.unmarshalGroupHeader(msgData.getHeader().toByteArray());
        if (chdr.getType() != Common.HeaderType.CONSENTER_TRANSACTION_VALUE) {
            return;
        }
        IConsenterConfig consenterConfig = groupConfig.getConsenterConfig();
        if (consenterConfig == null) {
            log.error("System channel does not have consenter config");
        }
        long maxChannels = consenterConfig.getMaxChannelsCount();
        if (maxChannels > 0) {
            if ((chainCreator.groupCount()) > maxChannels) {
                throw new ConsenterException(String.format("group creation would exceed maximimum number of groups: %d", maxChannels));
            }
        }
        Common.Envelope configTx = null;
        try {
            configTx = Common.Envelope.parseFrom(msgData.getData().toByteArray());
        } catch (InvalidProtocolBufferException e) {

            e.printStackTrace();
        }
        authorizeAndInspect(configTx);
    }

    public void authorizeAndInspect(Common.Envelope configTx) throws ConsenterException, InvalidProtocolBufferException {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(configTx.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new ConsenterException("payload data error unmarshaling to envelope");
        }
        if (payload.getHeader() == null) {
            throw new ConsenterException("wrapped configtx envelope missing header");
        }
        Common.GroupHeader chdr = CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());
        if (chdr.getType() != Common.HeaderType.CONFIG_VALUE) {
            throw new ConsenterException("wrapped configtx envelope not a config transaction");
        }
        Configtx.ConfigEnvelope configEnvelope = null;
        try {
            configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            throw new ConsenterException("error unmarshalling wrapped configtx config envelope from payload");
        }
        if (configEnvelope.getLastUpdate() == null) {
            throw new ConsenterException("error constructing new group config from update");
        }

        IGroupConfigBundle res = chainCreator.newGroupConfig(configEnvelope.getLastUpdate());
        Configtx.ConfigEnvelope newGroupConfigEnv = null;
        try {
            newGroupConfigEnv = res.getConfigtxValidator().proposeConfigUpdate(configEnvelope.getLastUpdate());
        } catch (InvalidProtocolBufferException e) {
            throw new ConsenterException(e);
        } catch (ValidateException e) {
            throw new ConsenterException(e);
        }
        if (!configEnvelope.equals(newGroupConfigEnv)) {
            throw new ConsenterException("config proposed by the channel creation request did not match the config received with the channel creation request");
        }
        IGroupConfigBundle bundle = chainCreator.createBundle(res.getConfigtxValidator().getGroupId(),
                newGroupConfigEnv.getConfig());
        try {
            new GroupConfigBundle(res.getConfigtxValidator().getGroupId(), newGroupConfigEnv.getConfig()).validateNew(bundle);
        } catch (ValidateException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
        IConsenterConfig consenterConfig = bundle.getGroupConfig().getConsenterConfig();
        if (consenterConfig == null) {
            throw new ConsenterException("config is missing consenter group");
        }
        if(!consenterConfig.getCapabilities().isSupported()) {
            throw new ConsenterException("config update is not compatible");
        }
        if(!bundle.getGroupConfig().getCapabilities().isSupported()){
            throw new ConsenterException("config update is not compatible");
        }
    }

    public IChainCreator getChainCreator() {
        return chainCreator;
    }

    public void setChainCreator(IChainCreator chainCreator) {
        this.chainCreator = chainCreator;
    }

    public IGroupConfig getGroupConfig() {
        return groupConfig;
    }

    public void setGroupConfig(IGroupConfig groupConfig) {
        this.groupConfig = groupConfig;
    }
}
