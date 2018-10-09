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
package org.bcia.julongchain.common.groupconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.common.groupconfig.capability.ConsenterProvider;
import org.bcia.julongchain.common.groupconfig.capability.IConsenterCapabilities;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Configuration;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 共识节点配置对象
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class ConsenterConfig implements IConsenterConfig {
    private Configuration.ConsensusType consensusTypeProto;
    private Configuration.BatchSize batchSize;
    private Configuration.BatchTimeout batchTimeoutProto;
    private Configuration.KafkaBrokers kafkaBrokersProto;
    private Configuration.GroupRestrictions groupRestrictions;
    private org.bcia.julongchain.protos.common.Configuration.Capabilities capabilitiesProto;

    private Map<String, IOrganizationConfig> organizationConfigMap;
    private String consensusType;
    private long batchTimeout;
    private List<String> kafkaBrokers;
    private long maxChannelsCount;
    private IConsenterCapabilities capabilities;

    public ConsenterConfig(Configtx.ConfigTree consenterTree, MSPConfigHandler mspConfigHandler) throws
            InvalidProtocolBufferException, ValidateException {
        if (consenterTree != null && consenterTree.getValuesMap() != null) {
            Configtx.ConfigValue consensusTypeValue = consenterTree.getValuesMap().get(GroupConfigConstant
                    .CONSENSUS_TYPE);
            if (consensusTypeValue != null) {
                consensusTypeProto = Configuration.ConsensusType.parseFrom(consensusTypeValue.getValue());
                consensusType = consensusTypeProto.getType();
            }

            Configtx.ConfigValue batchSizeValue = consenterTree.getValuesMap().get(GroupConfigConstant.BATCH_SIZE);
            if (batchSizeValue != null) {
                batchSize = Configuration.BatchSize.parseFrom(batchSizeValue.getValue());
            }

            Configtx.ConfigValue batchTimeoutValue = consenterTree.getValuesMap().get(GroupConfigConstant
                    .BATCH_TIMEOUT);
            if (batchTimeoutValue != null) {
                batchTimeoutProto = Configuration.BatchTimeout.parseFrom(batchTimeoutValue.getValue());
            }

            Configtx.ConfigValue kafkaBrokersValue = consenterTree.getValuesMap().get(GroupConfigConstant
                    .KAFKA_BROKERS);
            if (kafkaBrokersValue != null) {
                kafkaBrokersProto = Configuration.KafkaBrokers.parseFrom(kafkaBrokersValue.getValue());
            }

            Configtx.ConfigValue groupRestrictionsValue = consenterTree.getValuesMap().get(GroupConfigConstant
                    .GROUP_RESTRICTIONS);
            if (groupRestrictionsValue != null) {
                groupRestrictions = Configuration.GroupRestrictions.parseFrom(groupRestrictionsValue.getValue());
                maxChannelsCount = groupRestrictions.getMaxCount();
            }

            Configtx.ConfigValue capabilitiesValue = consenterTree.getValuesMap().get(GroupConfigConstant.CAPABILITIES);
            if (capabilitiesValue != null) {
                capabilitiesProto = org.bcia.julongchain.protos.common.Configuration.Capabilities.parseFrom
                        (capabilitiesValue.getValue());
                capabilities = new ConsenterProvider(capabilitiesProto.getCapabilitiesMap());
            }

            validateBatchSize();
            batchTimeout = validateBatchTimeout();
            kafkaBrokers = validateKafkaBrokers();
        }

        organizationConfigMap = new HashMap<String, IOrganizationConfig>();
        if (consenterTree != null && consenterTree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = consenterTree.getChildsMap().entrySet()
                    .iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String orgName = entry.getKey();
                Configtx.ConfigTree orgTree = entry.getValue();

                IOrganizationConfig organizationConfig = new OrganizationConfig(orgName, mspConfigHandler, orgTree);
                organizationConfigMap.put(orgName, organizationConfig);
            }
        }
    }

    private void validateBatchSize() throws ValidateException {
        //TODO：是否允许为空
        if (batchSize == null) {
            return;
        }

        if (batchSize.getAbsoluteMaxBytes() == 0) {
            throw new ValidateException("AbsoluteMaxBytes is 0");
        }

        if (batchSize.getMaxMessageCount() == 0) {
            throw new ValidateException("MaxMessageCount is 0");
        }

        if (batchSize.getPreferredMaxBytes() == 0) {
            throw new ValidateException("PreferredMaxBytes is 0");
        }

        if (batchSize.getPreferredMaxBytes() > batchSize.getAbsoluteMaxBytes()) {
            throw new ValidateException("PreferredMaxBytes greater than absoluteMaxBytes?");
        }
    }

    private long validateBatchTimeout() throws ValidateException {
        //TODO：是否允许为空
        if (batchTimeoutProto == null) {
            return 0;
        }
        try {
            return Long.parseLong(batchTimeoutProto.getTimeout());
        } catch (Exception ex) {
            throw new ValidateException("Wrong batchTimeout");
        }
    }

    private List<String> validateKafkaBrokers() throws ValidateException {
        List<String> brokerList = new ArrayList<String>();
        if (kafkaBrokersProto != null && kafkaBrokersProto.getBrokersList() != null) {
            for (String broker : kafkaBrokersProto.getBrokersList()) {
                validateKafkaBroker(broker);
                brokerList.add(broker);
            }
        }

        return brokerList;
    }

    private void validateKafkaBroker(String broker) throws ValidateException {
        ValidateUtils.isNotBlank(broker, "broker can not be empty");

        String[] hostAndPort = broker.split(":");
        if (hostAndPort.length != 2) {
            throw new ValidateException("Wrong hostAndPort");
        }

        String host = hostAndPort[0];

        try {
            int port = Integer.parseInt(hostAndPort[1]);
        } catch (Exception ex) {
            throw new ValidateException("Wrong port");
        }

        if (!Pattern.matches("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9])$", host)) {
            throw new ValidateException("Wrong host");
        }
    }

    @Override
    public String getConsensusType() {
        return consensusType;
    }

    @Override
    public Configuration.BatchSize getBatchSize() {
        return batchSize;
    }

    @Override
    public long getBatchTimeout() {
        return batchTimeout;
    }

    @Override
    public List<String> getKafkaBrokers() {
        return kafkaBrokers;
    }

    @Override
    public long getMaxChannelsCount() {
        return maxChannelsCount;
    }

    @Override
    public IConsenterCapabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public Map<String, IOrganizationConfig> getOrganizationConfigMap() {
        return organizationConfigMap;
    }
}