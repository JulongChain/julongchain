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
package org.bcia.julongchain.core.node.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.*;
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IApplicationConfig;
import org.bcia.julongchain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.julongchain.common.resourceconfig.ResourcesConfigBundle;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.node.ConfigtxProcessor;
import org.bcia.julongchain.core.node.GroupSupport;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.entity.Group;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.Map;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class ConfigTxUtils {
    private static final String RESOURCE_CONFIG_SEED_DATA = "resource_config_seed_data";

    public static Configtx.Config validateAndApplyResourceConfig(String groupId, Common.Envelope txEnvelope)
            throws NodeException, ValidateException, InvalidTxException, InvalidProtocolBufferException, PolicyException {
        Map<String, Group> groupMap = Node.getInstance().getGroupMap();
        ValidateUtils.isNotNull(groupMap, "groupMap can not be null");

        Group group = groupMap.get(groupId);
        ValidateUtils.isNotNull(group, "group can not be null");

        GroupSupport groupSupport = group.getGroupSupport();
        ValidateUtils.isNotNull(groupSupport, "groupSupport can not be null");

        IResourcesConfigBundle resourcesConfigBundle = groupSupport.getResourcesConfigBundle();
        Configtx.Config fullResourceConfig = null;
        try {
            fullResourceConfig = computeFullConfig(resourcesConfigBundle, txEnvelope);
        } catch (Exception e) {
            throw new InvalidTxException(e);
        }

        IResourcesConfigBundle bundle = new ResourcesConfigBundle(groupId, fullResourceConfig,
                groupSupport.getGroupConfigBundle(), null);
        groupSupport.setResourcesConfigBundle(bundle);

        return fullResourceConfig;
    }

    public static Configtx.Config computeFullConfig(IResourcesConfigBundle resourcesConfigBundle,
                                                    Common.Envelope txEnvelope)
            throws InvalidProtocolBufferException, ValidateException {
        Configtx.ConfigEnvelope configEnvelope =
                resourcesConfigBundle.getConfigtxValidator().proposeConfigUpdate(txEnvelope);
        return configEnvelope.getConfig();
    }

    public static boolean isResourceConfigCapabilityOn(String groupId, Configtx.Config groupConfig) throws
            ValidateException, PolicyException, InvalidProtocolBufferException {
        IGroupConfigBundle groupConfigBundle = new GroupConfigBundle(groupId, groupConfig);

        IApplicationConfig applicationConfig = groupConfigBundle.getGroupConfig().getApplicationConfig();

        return applicationConfig != null && applicationConfig.getCapabilities() != null && applicationConfig
                .getCapabilities().isResourcesTree();
    }

    public static Configtx.Config getConfigFromSeedTx(Configtx.ConfigEnvelope configEnvelope) throws
            InvalidProtocolBufferException, ValidateException {
        if (configEnvelope == null || configEnvelope.getLastUpdate() == null) {
            return null;
        }

        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = EnvelopeHelper.getConfigUpdateEnvelopeFrom
                (configEnvelope.getLastUpdate());
        ValidateUtils.isNotNull(configUpdateEnvelope.getConfigUpdate(), "configUpdate can not be null");
        Configtx.ConfigUpdate configUpdate = Configtx.ConfigUpdate.parseFrom(configUpdateEnvelope.getConfigUpdate());

        Map<String, ByteString> isolatedDataMap = configUpdate.getIsolatedDataMap();
        if (isolatedDataMap == null || !isolatedDataMap.containsKey(RESOURCE_CONFIG_SEED_DATA)) {
            return null;
        }

        return Configtx.Config.parseFrom(isolatedDataMap.get(RESOURCE_CONFIG_SEED_DATA));
    }

    /**
     * 获取群组配置
     *
     * @param nodeLedger
     * @return
     * @throws LedgerException
     * @throws InvalidProtocolBufferException
     */
    public static Configtx.Config retrievePersistedGroupConfig(INodeLedger nodeLedger) throws LedgerException,
            InvalidProtocolBufferException {
        IQueryExecutor queryExecutor = nodeLedger.newQueryExecutor();

        return ConfigtxProcessor.retrievePersistedConfig(queryExecutor, ConfigtxProcessor.GROUP_CONFIG_KEY);
    }

    /**
     * 获取群组配置
     *
     * @param nodeLedger
     * @return
     * @throws LedgerException
     * @throws InvalidProtocolBufferException
     */
    public static Configtx.Config retrievePersistedResourceConfig(INodeLedger nodeLedger) throws LedgerException,
            InvalidProtocolBufferException {
        IQueryExecutor queryExecutor = nodeLedger.newQueryExecutor();

        return ConfigtxProcessor.retrievePersistedConfig(queryExecutor, ConfigtxProcessor.RESOURCES_CONFIG_KEY);
    }


}
