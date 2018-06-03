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
package org.bcia.javachain.consenter.common.msgprocessor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ConsenterException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.GroupConfigBundle;
import org.bcia.javachain.common.groupconfig.GroupConfigConstant;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.groupconfig.config.IConsenterConfig;
import org.bcia.javachain.common.groupconfig.config.IConsortiumConfig;
import org.bcia.javachain.common.groupconfig.value.ConsortiumValue;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.util.CommonUtils;
import org.bcia.javachain.consenter.util.ConfigTxUtil;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Configuration;
import java.net.ConnectException;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/26
 * @company Dingxuan
 */
public class DefaultTemplator implements IGroupConfigTemplator {
    private static JavaChainLog log = JavaChainLogFactory.getLog(DefaultTemplator.class);
    private IGroupConfigBundle groupConfigBundle;

    public DefaultTemplator(IGroupConfigBundle groupConfigBundle) {
        this.groupConfigBundle = groupConfigBundle;
    }

    @Override
    public IGroupConfigBundle newGroupConfig(Common.Envelope envelope) {
        Common.Payload configUpdatePayload = CommonUtils.unmarshalPayload(envelope.getPayload().toByteArray());
        Configtx.ConfigUpdateEnvelope configUpdateEnv = ConfigTxUtil.unmarshalConfigUpdateEnvelope(configUpdatePayload.getData().toByteArray());
        if (configUpdatePayload.getHeader() == null) {
            log.error("Failed initial channel config creation because config update header was missing");
            try {
                throw new ConsenterException("Failed initial channel config creation because config update header was missing");
            } catch (ConsenterException e) {
                e.printStackTrace();
            }
        }
        Common.GroupHeader groupHeader = CommonUtils.unmarshalGroupHeader(configUpdatePayload.getHeader().getGroupHeader().toByteArray());
        Configtx.ConfigUpdate configUpdate = ConfigTxUtil.unmarshalConfigUpdate(configUpdateEnv.getConfigUpdate().toByteArray());
        if (configUpdate.getGroupId() != groupHeader.getGroupId()) {
            log.error(String.format("Failing initial group config creation: mismatched group IDs: '%s' != '%s'", configUpdate.getGroupId(), groupHeader.getGroupId()));
        }
        if (configUpdate.getWriteSet() == null) {
            log.error("Config update has an empty writeset");
        }
        if (configUpdate.getWriteSet().getChildsMap() == null || configUpdate.getWriteSet().getChildsMap().get(GroupConfigConstant.APPLICATION) == null) {
            log.error("Config update has missing application group");
        }
        long uv = configUpdate.getWriteSet().getChildsMap().get(GroupConfigConstant.APPLICATION).getVersion();
        if (uv != 1) {
            log.error(String.format("Config update for channel creation does not set application group version to 1, was %d", uv));
        }
        Configtx.ConfigValue consortiumConfigValue = configUpdate.getWriteSet().getValuesMap().get(GroupConfigConstant.CONSORTIUM);
        if (consortiumConfigValue == null) {
            log.error("Consortium config value missing");
        }
        Configuration.Consortium consortium = null;
        try {
            consortium = Configuration.Consortium.parseFrom(consortiumConfigValue.getValue().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        Configtx.ConfigTree applicationGroup = Configtx.ConfigTree.getDefaultInstance();
        Map<String, IConsortiumConfig> consortiumsConfig = groupConfigBundle.getGroupConfig().getConsortiumsConfig().getConsortiumConfigMap();
        if (consortiumsConfig == null) {
            log.error("The ordering system channel does not appear to support creating channels");
        }
        IConsortiumConfig consortiumConf = consortiumsConfig.get(consortium.getName());
        if (consortiumConf == null) {
            log.error(String.format("Unknown consortium name: %s", consortium.getName()));
        }
        Configtx.ConfigPolicy.Builder configPolicy = Configtx.ConfigPolicy.newBuilder().setPolicy(consortiumConf.getGroupCreationPolicy());
        applicationGroup.toBuilder().putPolicies(GroupConfigConstant.GROUP_CREATION_POLICY, configPolicy.build());
        applicationGroup.toBuilder().setModPolicy(GroupConfigConstant.GROUP_CREATION_POLICY);

        //获取当前的系统通道配置
        Configtx.ConfigTree systemGroupTree = groupConfigBundle.getConfigtxValidator().getConfig().getGroupTree();
        if (systemGroupTree.getChildsMap().get(GroupConfigConstant.CONSORTIUM).getChildsMap().get(consortium.getName()).getChildsMap().size() > 0 &&
                configUpdate.getWriteSet().getChildsMap().get(GroupConfigConstant.APPLICATION).getChildsMap().size() == 0) {
            try {
                throw new ConnectException("Proposed configuration has no application group members, but consortium contains members");
            } catch (ConnectException e) {
                e.printStackTrace();
            }
        }
        if (systemGroupTree.getChildsMap().get(GroupConfigConstant.CONSORTIUM).getChildsMap().get(consortium.getName()).getChildsMap().size() > 0) {
            Map<String, Configtx.ConfigTree> configTreeMap = configUpdate.getWriteSet().getChildsMap().get(GroupConfigConstant.APPLICATION).getChildsMap();
            for (String key : configTreeMap.keySet()) {
                Configtx.ConfigTree consortiumGroup = systemGroupTree.getChildsMap().get(GroupConfigConstant.APPLICATION).getChildsMap().get(consortium.getName()).getChildsMap().get(key);
                applicationGroup.toBuilder().putChilds(key, consortiumGroup);
            }
        }
        Configtx.ConfigTree groupTree = Configtx.ConfigTree.getDefaultInstance();
        Map<String, Configtx.ConfigValue> configValue = systemGroupTree.getValuesMap();
        for (String key : configValue.keySet()) {
            groupTree.toBuilder().putValues(key, configValue.get(key));
            if (key == GroupConfigConstant.CONSORTIUM) {
                continue;
            }
        }
        Map<String, Configtx.ConfigPolicy> configPolicyMap = systemGroupTree.getPoliciesMap();
        for (String key : configPolicyMap.keySet()) {
            groupTree.toBuilder().putPolicies(key, configPolicyMap.get(key));
        }

        groupTree.toBuilder().putChilds(GroupConfigConstant.CONSENTER, systemGroupTree.getChildsMap().get(GroupConfigConstant.CONSENTER));
        groupTree.toBuilder().putChilds(GroupConfigConstant.APPLICATION, applicationGroup);

        Configtx.ConfigValue confValue = Configtx.ConfigValue.newBuilder()
                .setModPolicy(GroupConfigConstant.POLICY_ADMINS)
                .setValue(ByteString.copyFrom(CommonUtils.marshlOrPanic(new ConsortiumValue(consortium.getName()).getValue()))).build();

        groupTree.toBuilder().putValues(GroupConfigConstant.CONSORTIUM, confValue);

        IConsenterConfig consenterConfig = groupConfigBundle.getGroupConfig().getConsenterConfig();
        if (consenterConfig != null && consenterConfig.getCapabilities().isPredictableGroupTemplate()) {
            groupTree.toBuilder().setModPolicy(systemGroupTree.getModPolicy());
            zeroVersions(groupTree);
        }
        Configtx.Config config = Configtx.Config.newBuilder().setGroupTree(groupTree).build();
        GroupConfigBundle bundle = null;
        try {
            bundle = new GroupConfigBundle(groupHeader.getGroupId(), config);
        } catch (ValidateException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    public void zeroVersions(Configtx.ConfigTree configTree) {
        configTree.toBuilder().setVersion(0);
        Map<String, Configtx.ConfigValue> configValueMap = configTree.getValuesMap();
        for (String key : configValueMap.keySet()) {
            configValueMap.get(key).toBuilder().setVersion(0);
        }
        Map<String, Configtx.ConfigPolicy> configPolicyMap = configTree.getPoliciesMap();
        for (String key : configPolicyMap.keySet()) {
            configPolicyMap.get(key).toBuilder().setVersion(0);
        }
        Map<String, Configtx.ConfigTree> configTreeMap = configTree.getChildsMap();
        for (String key: configTreeMap.keySet()) {
            zeroVersions(configTree);
        }
    }

    public IGroupConfigBundle getGroupConfigBundle() {
        return groupConfigBundle;
    }

    public void setGroupConfigBundle(IGroupConfigBundle groupConfigBundle) {
        this.groupConfigBundle = groupConfigBundle;
    }
}
