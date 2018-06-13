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
package org.bcia.julongchain.common.groupconfig;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.configtx.ConfigtxValidator;
import org.bcia.julongchain.common.configtx.IConfigtxValidator;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.config.*;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.policies.IPolicyProvider;
import org.bcia.julongchain.common.policies.PolicyManager;
import org.bcia.julongchain.common.policycheck.policies.PolicyProvider;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.consenter.common.multigroup.IMutableResources;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.common.Policies;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 群组配置集
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class GroupConfigBundle implements IGroupConfigBundle,IMutableResources {
    /**
     * 策略管理器
     */
    private IPolicyManager policyManager;
    /**
     * Msp管理器
     */
    private IMspManager mspManager;
    /**
     * 群组配置
     */
    private IGroupConfig groupConfig;
    /**
     * 校验者
     */
    private IConfigtxValidator configtxValidator;

    private Configtx.Config config;

    public static GroupConfigBundle parseFrom(Common.Envelope envelope) throws ValidateException,
            InvalidProtocolBufferException, PolicyException {
        ValidateUtils.isNotNull(envelope, "envelope can not be null");
        ValidateUtils.isNotNull(envelope.getPayload(), "envelope.Payload can not be null");

        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());

        ValidateUtils.isNotNull(payload.getHeader(), "envelope.Payload.header can not be null");
        ValidateUtils.isNotNull(payload.getHeader().getGroupHeader(), "envelope.groupHeader can not be null");
        ValidateUtils.isNotNull(payload.getData(), "envelope.Payload.data can not be null");

        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());

        if (groupHeader.getType() != Common.HeaderType.CONFIG_VALUE) {
            throw new ValidateException("Wrong groupHeader type");
        }

        Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
        return new GroupConfigBundle(groupHeader.getGroupId(), configEnvelope.getConfig());
    }

    public GroupConfigBundle(String groupId, Configtx.Config config) throws ValidateException,
            InvalidProtocolBufferException, PolicyException {
        preValidate(config);

        this.config = config;
        this.groupConfig = new GroupConfig(config.getGroupTree());

        HashMap<Integer, IPolicyProvider> policyProviderMap = new HashMap<>();
        policyProviderMap.put(Policies.Policy.PolicyType.SIGNATURE_VALUE, new PolicyProvider(groupConfig
                .getMspManager()));

        this.policyManager = new PolicyManager(GroupConfigConstant.GROUP, policyProviderMap, config.getGroupTree());

        this.configtxValidator = new ConfigtxValidator(groupId, config, GroupConfigConstant.GROUP, policyManager);
    }

    /**
     * 预验证配置对象
     *
     * @param config
     * @throws ValidateException
     */
    private void preValidate(Configtx.Config config) throws ValidateException {
        ValidateUtils.isNotNull(config, "config can not be null");
        ValidateUtils.isNotNull(config.getGroupTree(), "GroupTree can not be null");
        ValidateUtils.isNotNull(config.getGroupTree().getChildsMap(), "GroupTree's child can not be null");

        Configtx.ConfigTree groupTree = config.getGroupTree();
        if (groupTree.getChildsMap().containsKey(GroupConfigConstant.CONSENTER)) {
            Configtx.ConfigTree consenterTree = groupTree.getChildsMap().get(GroupConfigConstant.CONSENTER);
            if (consenterTree.getChildsMap() != null && !consenterTree.getValuesMap().containsKey(GroupConfigConstant
                    .CAPABILITIES)) {
                if (groupTree.getValuesMap().containsKey(GroupConfigConstant.CAPABILITIES)) {
                    //如果群组级别配置树具备某项能力，但共识级别配置树却不具备，则抛出异常
                    throw new ValidateException("group have capabilities, but consenter hasn't");
                }

                Configtx.ConfigTree appTree = groupTree.getChildsMap().get(GroupConfigConstant.APPLICATION);
                if (appTree != null && appTree.getValuesMap() != null && appTree.getValuesMap().containsKey
                        (GroupConfigConstant.CAPABILITIES)) {
                    //如果应用级别配置树具备某项能力，但共识级别配置树却不具备，则抛出异常
                    throw new ValidateException("app have capabilities, but consenter hasn't");
                }
            }
        }
    }

    public void validateNew(IGroupConfigBundle otherBundle) throws ValidateException {
        ValidateUtils.isNotNull(otherBundle, "GroupConfigBundle can not be null");
        ValidateUtils.isNotNull(otherBundle.getGroupConfig(), "GroupConfig can not be null");

        IConsenterConfig consenterConfig = groupConfig.getConsenterConfig();
        if (consenterConfig != null) {
            IConsenterConfig otherConsenterConfig = otherBundle.getGroupConfig().getConsenterConfig();
            ValidateUtils.isNotNull(otherConsenterConfig, "ConsenterConfig can not be null");

            if (!Objects.equals(consenterConfig.getConsensusType(), otherConsenterConfig.getConsensusType())) {
                throw new ValidateException("Different consensus type");
            }

            itemSameInOtherWhenExists(consenterConfig.getOrganizationConfigMap(), otherConsenterConfig
                    .getOrganizationConfigMap());
        }

        IApplicationConfig applicationConfig = groupConfig.getApplicationConfig();
        if (applicationConfig != null) {
            IApplicationConfig otherApplicationConfig = otherBundle.getGroupConfig().getApplicationConfig();
            ValidateUtils.isNotNull(otherApplicationConfig, "ApplicationConfig can not be null");

            itemSameInOtherWhenExists(applicationConfig.getApplicationOrgConfigs(), otherApplicationConfig
                    .getApplicationOrgConfigs());
        }

        IConsortiumsConfig consortiumsConfig = groupConfig.getConsortiumsConfig();
        if (consortiumsConfig != null) {
            IConsortiumsConfig otherConsortiumsConfig = otherBundle.getGroupConfig().getConsortiumsConfig();
            ValidateUtils.isNotNull(otherConsortiumsConfig, "otherConsortiumsConfig can not be null");

            if (consortiumsConfig.getConsortiumConfigMap() != null && consortiumsConfig.getConsortiumConfigMap().size
                    () > 0) {
                if (otherConsortiumsConfig.getConsortiumConfigMap() != null && otherConsortiumsConfig
                        .getConsortiumConfigMap().size() > 0) {
                    Iterator<Map.Entry<String, IConsortiumConfig>> iterator = consortiumsConfig
                            .getConsortiumConfigMap().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, IConsortiumConfig> entry = iterator.next();
                        String consortiumsName = entry.getKey();
                        IConsortiumConfig consortiumConfig = entry.getValue();

                        IConsortiumConfig otherConsortiumConfig = otherConsortiumsConfig.getConsortiumConfigMap().get
                                (consortiumsName);
                        if (otherConsortiumConfig != null) {
                            itemSameInOtherWhenExists(consortiumConfig.getOrganizationConfigMap(),
                                    otherConsortiumConfig.getOrganizationConfigMap());
                        }
                    }
                }
            }
        }
    }

    /**
     * 当另一个集合存在相应元素时，则是否相同
     *
     * @param map
     * @param otherMap
     * @return
     */
    private boolean itemSameInOtherWhenExists(Map<String, ? extends IOrganizationConfig> map, Map<String, ? extends
            IOrganizationConfig> otherMap) throws ValidateException {
        if (map != null && map.size() > 0) {
            if (otherMap != null && otherMap.size() > 0) {
                Iterator<? extends Map.Entry<String, ? extends IOrganizationConfig>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ? extends IOrganizationConfig> entry = iterator.next();
                    String orgName = entry.getKey();
                    IOrganizationConfig organizationConfig = entry.getValue();

                    IOrganizationConfig otherOrganizationConfig = otherMap.get(orgName);
                    if (otherOrganizationConfig != null && !Objects.equals(organizationConfig.getMspId(),
                            otherOrganizationConfig.getMspId())) {
                        //如果要比较的Map也存在对应的组织，但MSP却不相同,则抛出异常
                        throw new ValidateException("Different org msp-----" + orgName + "," + organizationConfig
                                .getMspId() + "," + otherOrganizationConfig.getMspId());
                    }
                }
            }
        }

        return true;
    }

    @Override
    public IPolicyManager getPolicyManager() {
        return policyManager;
    }

    @Override
    public IMspManager getMspManager() {
        return mspManager;
    }

    @Override
    public IGroupConfig getGroupConfig() {
        return groupConfig;
    }

    @Override
    public IConfigtxValidator getConfigtxValidator() {
        return configtxValidator;
    }

    @Override
    public Configtx.Config getCurrentConfig() {
        return config;
    }

    @Override
    public Configtx.ConfigEnvelope updateProposeConfig(Common.Envelope configtx) throws InvalidProtocolBufferException, ValidateException {
        return configtxValidator.proposeConfigUpdate(configtx);
    }

    @Override
    public GroupConfigBundle update() {
        return null;
    }
}
