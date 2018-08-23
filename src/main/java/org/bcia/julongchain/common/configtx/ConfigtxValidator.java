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
package org.bcia.julongchain.common.configtx;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.configtx.util.ConfigMapUtils;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.protos.ConfigUpdateEnvelopeVO;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 交易验证器
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class ConfigtxValidator implements IConfigtxValidator {
    private static JulongChainLog log = JulongChainLogFactory.getLog(ConfigtxValidator.class);

    private static final int MAX_LENGTH = 249;
    private static final String REGEX_GROUP_ID = "[a-zA-Z][a-zA-Z0-9.-]*";
    private static final String REGEX_CONFIG_ID = "[a-zA-Z0-9.-]+";
    private static final String[] ILLEGAL_PATHS = {".", ".."};

    /**
     * 当前的群组Id
     */
    private String groupId;
    /**
     * 当前的序列号
     */
    private long sequence;
    /**
     * 当前的配置
     */
    private Configtx.Config config;
    /**
     * 方便比较的配置集合
     */
    private Map<String, ConfigComparable> configComparableMap;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 策略管理器
     */
    private IPolicyManager policyManager;

    /**
     * 构造函数
     *
     * @param groupId       群组Id
     * @param config        配置对象
     * @param namespace     群组还是资源
     * @param policyManager 策略管理器
     * @throws ValidateException
     */
    public ConfigtxValidator(String groupId, Configtx.Config config, String namespace, IPolicyManager policyManager)
            throws ValidateException {
        ValidateUtils.isNotNull(config, "Config can not be null");
        ValidateUtils.isNotNull(config.getGroupTree(), "Config.tree can not be null");

        validateGroupId(groupId);

        this.groupId = groupId;
        this.config = config;
        this.namespace = namespace;
        this.policyManager = policyManager;
        this.sequence = config.getSequence();
        this.configComparableMap = ConfigMapUtils.mapConfig(config.getGroupTree(), namespace);
    }

    /**
     * 校验群组id
     *
     * @param groupId
     * @throws ValidateException
     */
    private void validateGroupId(String groupId) throws ValidateException {
        if (StringUtils.isBlank(groupId)) {
            throw new ValidateException("GroupId can not be empty");
        }

        if (groupId.length() > MAX_LENGTH) {
            throw new ValidateException("GroupId can not be longer than max length");
        }

        if (!Pattern.matches(REGEX_GROUP_ID, groupId)) {
            throw new ValidateException("Wrong groupId");
        }
    }

    private void validateConfigId(String path) throws ValidateException {
        if (StringUtils.isBlank(path)) {
            throw new ValidateException("Path can not be empty");
        }

        if (path.length() > MAX_LENGTH) {
            throw new ValidateException("Path can not be longer than max length");
        }

        if (ArrayUtils.contains(ILLEGAL_PATHS, path)) {
            throw new ValidateException("Path is not allowed: " + path);
        }

        if (!Pattern.matches(REGEX_CONFIG_ID, path)) {
            throw new ValidateException("Wrong path");
        }
    }

    @Override
    public void validate(Configtx.ConfigEnvelope configEnv) throws ValidateException, InvalidProtocolBufferException {
        ValidateUtils.isNotNull(configEnv, "ConfigEnv can not be null");
        ValidateUtils.isNotNull(configEnv.getConfig(), "ConfigEnv.getConfig can not be null");

        if (configEnv.getConfig().getSequence() != sequence + 1) {
            throw new ValidateException("ConfigEnv.getConfig.getSequence should be current sequence + 1");
        }

        Configtx.ConfigUpdateEnvelope configUpdateEnvelope =
                EnvelopeHelper.getConfigUpdateEnvelopeFrom(configEnv.getLastUpdate());
        Map<String, ConfigComparable> proposedFullConfig = authorizeUpdate(configUpdateEnvelope);

        Configtx.ConfigTree configTree = ConfigMapUtils.configMapToConfig(proposedFullConfig, namespace);

        if (!Arrays.equals(configTree.toByteArray(), configEnv.getConfig().getGroupTree().toByteArray())) {
            throw new ValidateException("ConfigEnv.getConfig.getGroupTree don't match");
        }
    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws
            InvalidProtocolBufferException, ValidateException {
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = EnvelopeHelper.getConfigUpdateEnvelopeFrom(configtx);
        Map<String, ConfigComparable> proposedFullConfig = authorizeUpdate(configUpdateEnvelope);

        Configtx.ConfigTree configTree = ConfigMapUtils.configMapToConfig(proposedFullConfig, namespace);

        Configtx.ConfigEnvelope.Builder builder = Configtx.ConfigEnvelope.newBuilder();
        builder.setLastUpdate(configtx);
        builder.getConfigBuilder().setGroupTree(configTree);
        builder.getConfigBuilder().setSequence(sequence + 1);

        return builder.build();
    }

    private Map<String, ConfigComparable> authorizeUpdate(Configtx.ConfigUpdateEnvelope configUpdateEnvelope) throws
            InvalidProtocolBufferException, ValidateException {
        ConfigUpdateEnvelopeVO configUpdateEnvelopeVO = new ConfigUpdateEnvelopeVO();
        configUpdateEnvelopeVO.parseFrom(configUpdateEnvelope);

        Configtx.ConfigUpdate configUpdate = configUpdateEnvelopeVO.getConfigUpdate();
        if (groupId != null && !groupId.equals(configUpdate.getGroupId())) {
            String errorMsg = "GroupId is '" + groupId + "', but configUpdate's group is: " + configUpdate.getGroupId();
            log.error(errorMsg);
            throw new ValidateException(errorMsg);
        }

        Map<String, ConfigComparable> readSet = ConfigMapUtils.mapConfig(configUpdate.getReadSet(), namespace);
        verifyReadSet(readSet);

        Map<String, ConfigComparable> writeSet = ConfigMapUtils.mapConfig(configUpdate.getWriteSet(), namespace);

        Map<String, ConfigComparable> deltaSet = computeDeltaSet(readSet, writeSet);

        List<SignedData> signedDataList = SignedData.asSingedData(configUpdateEnvelope);
        verifyDeltaSet(deltaSet, signedDataList);

        Map<String, ConfigComparable> proposedFullConfig = computeUpdateResult(deltaSet);
        verifyFullProposedConfig(writeSet, proposedFullConfig);

        return proposedFullConfig;
    }

    private Map<String, ConfigComparable> computeUpdateResult(Map<String, ConfigComparable> deltaSet) {
        Map<String, ConfigComparable> newConfigMap = new HashMap<>();
        newConfigMap.putAll(configComparableMap);
        newConfigMap.putAll(deltaSet);
        return newConfigMap;
    }

    private void verifyDeltaSet(Map<String, ConfigComparable> deltaSet, List<SignedData> signedDataList)
            throws ValidateException {
        ValidateUtils.isNotNull(deltaSet, "deltaSet can not be null");

        if (deltaSet.size() > 0) {
            Iterator<Map.Entry<String, ConfigComparable>> iterator = deltaSet.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ConfigComparable> entry = iterator.next();
                String key = entry.getKey();
                ConfigComparable value = entry.getValue();

                validateModPolicy(value.getModPolicy());

                ConfigComparable existingConfig = this.configComparableMap.get(key);
                if (existingConfig == null) {
                    if (value.getVersion() != 0L) {
                        log.error("ConfigComparableMap has not this value: " + key);
                        throw new ValidateException("ConfigComparableMap has not this value: " + key);
                    }

                    continue;
                }

                if (value.getVersion() != existingConfig.getVersion() + 1) {
                    throw new ValidateException("Value.getHeight is " + value.getVersion() + ", but current is "
                            + existingConfig.getVersion());
                }

                IPolicy policy = policyForItem(existingConfig);
                ValidateUtils.isNotNull(policy, "PolicyForItem can not be null: " + key);

//                try {
//                    policy.evaluate(signedDataList);
//                } catch (PolicyException e) {
//                    log.error(e.getMessage(), e);
//                    throw new ValidateException(e);
//                }
            }
        }
    }

    private IPolicy policyForItem(ConfigComparable itemConfig) {
        String modPolicy = itemConfig.getModPolicy();

        if (StringUtils.isNotBlank(modPolicy) && !modPolicy.startsWith(CommConstant.PATH_SEPARATOR)
                && ArrayUtils.isNotEmpty(itemConfig.getPath())) {
            String[] paths = itemConfig.getPath();
            String[] newPaths = new String[itemConfig.getPath().length - 1];
            System.arraycopy(paths, 1, newPaths, 0, paths.length - 1);

            IPolicyManager subPolicyManager = this.policyManager.getSubPolicyManager(newPaths);
            if (subPolicyManager == null) {
                log.warn("Could not find subPolicyManager: " + newPaths);
                return null;
            }

            if (itemConfig.getT() instanceof Configtx.ConfigTree) {
                subPolicyManager = this.policyManager.getSubPolicyManager(new String[]{itemConfig.getKey()});
                if (subPolicyManager == null) {
                    log.warn("Could not find subPolicyManager: " + itemConfig.getKey());
                    return null;
                }
            }
        }

        return this.policyManager.getPolicy(itemConfig.getModPolicy());
    }


    private void verifyFullProposedConfig(Map<String, ConfigComparable> writeSet,
                                          Map<String, ConfigComparable> fullProposedConfig) throws ValidateException {
        for (String key : writeSet.keySet()) {
            if (!fullProposedConfig.containsKey(key)) {
                throw new ValidateException("Is not in fullProposedConfig: " + key);
            }
        }
    }

    private void validateModPolicy(String modPolicy) throws ValidateException {
        ValidateUtils.isNotBlank(modPolicy, "ModPolicy can not be empty");

        int startIndex = 0;
        if (modPolicy.startsWith(CommConstant.PATH_SEPARATOR)) {
            startIndex = 1;
        }

        String[] paths = modPolicy.split(CommConstant.PATH_SEPARATOR);
        for (int i = startIndex; i < paths.length; i++) {
            validateConfigId(paths[i]);
        }
    }


    private Map<String, ConfigComparable> computeDeltaSet(Map<String, ConfigComparable> readSet, Map<String,
            ConfigComparable> writeSet) {
        Map<String, ConfigComparable> deltaSet = new HashMap<>();
        if (writeSet != null && readSet != null) {
            Iterator<Map.Entry<String, ConfigComparable>> iterator = writeSet.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ConfigComparable> entry = iterator.next();
                String key = entry.getKey();

                ConfigComparable configComparable = readSet.get(key);
                if (configComparable != null && configComparable.getVersion() == entry.getValue().getVersion()) {
                    continue;
                }

                deltaSet.put(key, entry.getValue());
            }
        }

        return deltaSet;
    }

    private void verifyReadSet(Map<String, ConfigComparable> readSet) throws ValidateException {
        Iterator<Map.Entry<String, ConfigComparable>> iterator = readSet.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConfigComparable> entry = iterator.next();
            String key = entry.getKey();

            if (!this.configComparableMap.containsKey(key)) {
                throw new ValidateException("Key is not exists: " + key);
            }

            ConfigComparable configComparable = this.configComparableMap.get(key);
            if (configComparable.getVersion() != entry.getValue().getVersion()) {
                throw new ValidateException("Version is not same: " + key);
            }
        }
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public long getSequence() {
        return sequence;
    }

    @Override
    public Configtx.Config getConfig() {
        return config;
    }
}
