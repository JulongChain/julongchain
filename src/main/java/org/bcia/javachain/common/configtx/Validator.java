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
package org.bcia.javachain.common.configtx;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.configtx.util.ConfigMapUtils;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.policies.IPolicyManager;
import org.bcia.javachain.common.protos.ConfigUpdateEnvelopeVO;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class Validator implements IValidator {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Validator.class);

    private static final int MAX_LENGTH = 249;
    private static final String REGEX_GROUP_ID = "[a-zA-Z][a-zA-Z0-9.-]*";
    private static final String REGEX_CONFIG_ID = "[a-zA-Z0-9.-]+";
    private static final String[] ILLEGAL_PATHS = {".", ".."};

    private String groupId;
    private long sequence;
    private Configtx.Config config;
    private Map<String, ConfigComparable> configComparableMap;
    private String namespace;
    private IPolicyManager policyManager;

    public Validator(String groupId, Configtx.Config config, String namespace, IPolicyManager policyManager) throws
            ValidateException {
        this.groupId = groupId;
        this.config = config;
        this.namespace = namespace;
        this.policyManager = policyManager;

        ValidateUtils.isNotNull(config, "config can not be null");
        ValidateUtils.isNotNull(config.getGroupTree(), "config.tree can not be null");

        validateGroupId(groupId);

        //TODO：待补充
        this.configComparableMap = ConfigMapUtils.mapConfig(config.getGroupTree(), namespace);
    }

    private void validateGroupId(String groupId) throws ValidateException {
        if (StringUtils.isBlank(groupId)) {
            throw new ValidateException("groupId can not be null");
        }

        if (groupId.length() > MAX_LENGTH) {
            throw new ValidateException("groupId cannot be longer than max length");
        }

        if (!Pattern.matches(REGEX_GROUP_ID, groupId)) {
            throw new ValidateException("Wrong groupId");
        }
    }

    private void validateConfigId(String path) throws ValidateException {
        if (StringUtils.isBlank(path)) {
            throw new ValidateException("path can not be null");
        }

        if (path.length() > MAX_LENGTH) {
            throw new ValidateException("path cannot be longer than max length");
        }

        if (ArrayUtils.contains(ILLEGAL_PATHS, path)) {
            throw new ValidateException("path is not allowed-----$" + path);
        }

        if (!Pattern.matches(REGEX_CONFIG_ID, path)) {
            throw new ValidateException("Wrong path");
        }
    }

    @Override
    public void validate(Configtx.ConfigEnvelope configEnv) {

    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws
            InvalidProtocolBufferException, ValidateException {
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = EnvelopeHelper.getConfigUpdateEnvelopeFrom(configtx);
        authorizeUpdate(configUpdateEnvelope);

//        if(configUpdateEnvelope.get)

        return null;
    }

    private void authorizeUpdate(Configtx.ConfigUpdateEnvelope configUpdateEnvelope) throws
            InvalidProtocolBufferException, ValidateException {
        ConfigUpdateEnvelopeVO configUpdateEnvelopeVO = new ConfigUpdateEnvelopeVO();
        configUpdateEnvelopeVO.parseFrom(configUpdateEnvelope);

        Configtx.ConfigUpdate configUpdate = configUpdateEnvelopeVO.getConfigUpdate();
        if (groupId != null && !groupId.equals(configUpdate.getGroupId())) {
            String errorMsg = "groupId is " + groupId + ", but configUpdate's group is " + configUpdate.getGroupId();
            log.error(errorMsg);
            throw new ValidateException(errorMsg);
        }

        Map<String, ConfigComparable> readSet = ConfigMapUtils.mapConfig(configUpdate.getReadSet(), namespace);
        verifyReadSet(readSet);

        Map<String, ConfigComparable> writeSet = ConfigMapUtils.mapConfig(configUpdate.getWriteSet(), namespace);
        verifyReadSet(writeSet);

        Map<String, ConfigComparable> deltaSet = computeDeltaSet(readSet, writeSet);

        List<SignedData> signedDataList = SignedData.asSingedData(configUpdateEnvelope);
        verifyDeltaSet(deltaSet, signedDataList);


//        writeSet, err :=mapConfig(configUpdate.WriteSet, vi.namespace)
//        if err != nil {
//            return nil,errors.Wrapf(err, "error mapping WriteSet")
//        }
//
//        deltaSet:=computeDeltaSet(readSet, writeSet)
//        signedData, err :=configUpdateEnv.AsSignedData()
//        if err != nil {
//            return nil,err
//        }
//
//        if err = vi.verifyDeltaSet(deltaSet, signedData);
//        err != nil {
//            return nil,errors.Wrapf(err, "error validating DeltaSet")
//        }


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
                        throw new ValidateException("configComparableMap has not this value-----$" + key);
                    }
                }

                if (value.getVersion() != existingConfig.getVersion() + 1) {
                    throw new ValidateException("value.getVersion is " + value.getVersion() + ", but current is "
                            + existingConfig.getVersion());
                }

                IPolicy policy = policyForItem(existingConfig);
                ValidateUtils.isNotNull(policy, "policyForItem can not be null-----$" + key);

                try {
                    policy.evaluate(signedDataList);
                } catch (PolicyException e) {
                    log.error(e.getMessage(), e);
                    throw new ValidateException(e);
                }
            }
        }
    }

    private IPolicy policyForItem(ConfigComparable itemConfig) {
//        this.policyManager =

        return null;

    }

    private void validateModPolicy(String modPolicy) throws ValidateException {
        ValidateUtils.isNotBlank(modPolicy, "modPolicy can not be empty");

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
                throw new ValidateException("key is not exists: " + key);
            }

            ConfigComparable configComparable = this.configComparableMap.get(key);
            if (configComparable.getVersion() != entry.getValue().getVersion()) {
                throw new ValidateException("version is not same: " + key);
            }
        }
    }

    @Override
    public String groupId() {
        return groupId;
    }

    @Override
    public Configtx.Config configProto() {
        return null;
    }

    @Override
    public long sequence() {
        return sequence;
    }
}
