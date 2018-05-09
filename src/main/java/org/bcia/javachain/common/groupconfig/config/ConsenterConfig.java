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
package org.bcia.javachain.common.groupconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.GroupConfigConstant;
import org.bcia.javachain.common.groupconfig.MSPConfigHandler;
import org.bcia.javachain.common.groupconfig.capability.IConsenterCapabilities;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.consenter.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class ConsenterConfig implements IConsenterConfig {
    private Configuration.ConsensusType consensusType;
    private Configuration.BatchSize batchSize;
    private Configuration.BatchTimeout batchTimeout;
    private Configuration.KafkaBrokers kafkaBrokers;
    private Configuration.GroupRestrictions groupRestrictions;
    private org.bcia.javachain.protos.common.Configuration.Capabilities capabilities;

    private Map<String, IOrganizationConfig> organizationConfigMap;

    public ConsenterConfig(Configtx.ConfigTree consenterTree, MSPConfigHandler mspConfigHandler) throws
            InvalidProtocolBufferException, ValidateException {
        if (consenterTree != null && consenterTree.getValuesMap() != null) {
            Configtx.ConfigValue consensusTypeValue = consenterTree.getValuesMap().get(GroupConfigConstant.CONSENSUS_TYPE);
            if (consensusTypeValue != null) {
                consensusType = Configuration.ConsensusType.parseFrom(consensusTypeValue.getValue());
            }

            Configtx.ConfigValue batchSizeValue = consenterTree.getValuesMap().get(GroupConfigConstant.BATCH_SIZE);
            if (batchSizeValue != null) {
                batchSize = Configuration.BatchSize.parseFrom(batchSizeValue.getValue());
            }

            Configtx.ConfigValue batchTimeoutValue = consenterTree.getValuesMap().get(GroupConfigConstant.BATCH_TIMEOUT);
            if (batchTimeoutValue != null) {
                batchTimeout = Configuration.BatchTimeout.parseFrom(batchTimeoutValue.getValue());
            }

            Configtx.ConfigValue kafkaBrokersValue = consenterTree.getValuesMap().get(GroupConfigConstant.KAFKA_BROKERS);
            if (kafkaBrokersValue != null) {
                kafkaBrokers = Configuration.KafkaBrokers.parseFrom(kafkaBrokersValue.getValue());
            }

            Configtx.ConfigValue groupRestrictionsValue = consenterTree.getValuesMap().get(GroupConfigConstant.GROUP_RESTRICTIONS);
            if (groupRestrictionsValue != null) {
                groupRestrictions = Configuration.GroupRestrictions.parseFrom(groupRestrictionsValue.getValue());
            }

            Configtx.ConfigValue capabilitiesValue = consenterTree.getValuesMap().get(GroupConfigConstant.CAPABILITIES);
            if (capabilitiesValue != null) {
                capabilities = org.bcia.javachain.protos.common.Configuration.Capabilities.parseFrom(capabilitiesValue.getValue());
            }

            validateBatchSize();
            validateBatchTimeout();
            validateKafkaBrokers();
        }

        organizationConfigMap = new HashMap<String, IOrganizationConfig>();
        if (consenterTree != null && consenterTree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = consenterTree.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String orgName = entry.getKey();
                Configtx.ConfigTree orgTree = entry.getValue();

                IOrganizationConfig organizationConfig = new OrganizationConfig(orgName, mspConfigHandler, orgTree);
                organizationConfigMap.put(orgName, organizationConfig);
            }
        }
    }

    @Override
    public Configuration.ConsensusType getConsensusType() {
        return consensusType;
    }

    @Override
    public Configuration.BatchSize getBatchSize() {
        return batchSize;
    }

    @Override
    public Configuration.BatchTimeout getBatchTimeout() {
        return batchTimeout;
    }

    @Override
    public Configuration.KafkaBrokers getKafkaBrokers() {
        return kafkaBrokers;
    }

    @Override
    public Configuration.GroupRestrictions getGroupRestrictions() {
        return groupRestrictions;
    }

    @Override
    public IConsenterCapabilities getCapabilities() {
        return null;
    }

    @Override
    public Map<String, IOrganizationConfig> getOrganizationConfigMap() {
        return organizationConfigMap;
    }

    private void validateBatchSize() throws ValidateException {
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

    private void validateBatchTimeout() throws ValidateException {
        try {
            Long.parseLong(batchTimeout.getTimeout());
        } catch (Exception ex) {
            throw new ValidateException("Wrong batchTimeout");
        }
    }

    private void validateKafkaBrokers() throws ValidateException {
        for (String broker : kafkaBrokers.getBrokersList()) {
            validateKafkaBroker(broker);
        }
    }

    private void validateKafkaBroker(String broker) throws ValidateException {
        if (StringUtils.isBlank(broker)) {
            throw new ValidateException("broker is null");
        }

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
}
