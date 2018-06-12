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
package org.bcia.julongchain.core.node;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.InvalidTxException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.julongchain.common.resourceconfig.ResourcesConfigBundle;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.customtx.IProcessor;
import org.bcia.julongchain.core.node.util.ConfigTxUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class ConfigtxProcessor implements IProcessor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConfigtxProcessor.class);

    public static final String RESOURCES_CONFIG_KEY = "resourcesconfigtx.RESOURCES_CONFIG_KEY";
    public static final String GROUP_CONFIG_KEY = "resourcesconfigtx.GROUP_CONFIG_KEY";
    private static final String NODE_NAMESPACE = "";

    @Override
    public void generateSimulationResults(Common.Envelope txEnvelope, ITxSimulator simulator, boolean
            initializingLedger)
            throws LedgerException, InvalidTxException {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(txEnvelope.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }

        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }

        switch (groupHeader.getType()) {
            case Common.HeaderType.CONFIG_VALUE:
                processGroupConfigTx(groupHeader.getGroupId(), txEnvelope, simulator);
                break;
            case Common.HeaderType.NODE_RESOURCE_UPDATE_VALUE:
                processResourceConfigTx(groupHeader.getGroupId(), txEnvelope, simulator, initializingLedger);
                break;
            default:
                log.warn("UnSupport type yet");
                break;
        }
    }

    /**
     * 处理群组配置交易
     *
     * @param groupId
     * @param txEnvelope
     * @param simulator
     * @throws LedgerException
     */
    private void processGroupConfigTx(String groupId, Common.Envelope txEnvelope, ITxSimulator simulator) throws
            LedgerException {
        try {
            Configtx.ConfigEnvelope configEnvelope = EnvelopeHelper.getConfigEnvelopeFrom(txEnvelope);
            Configtx.Config groupConfig = configEnvelope.getConfig();
            ValidateUtils.isNotNull(groupConfig, "configEnvelope.getConfig can not be null");

            persistConfig(simulator, GROUP_CONFIG_KEY, groupConfig);

            boolean resourceConfigCapabilityOn = ConfigTxUtils.isResourceConfigCapabilityOn(groupId, groupConfig);
            Configtx.Config resourcesConfigSeed = ConfigTxUtils.getConfigFromSeedTx(configEnvelope);

            if (groupConfig.getSequence() == 1L && resourceConfigCapabilityOn) {
                ValidateUtils.isNotNull(resourcesConfigSeed, "resourcesConfigSeed can not be null");

                persistConfig(simulator, RESOURCES_CONFIG_KEY, resourcesConfigSeed);
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        } catch (PolicyException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }
    }


    private void processResourceConfigTx(String groupId, Common.Envelope txEnvelope, ITxSimulator simulator, boolean
            initializingLedger) throws LedgerException, InvalidTxException {
        if (initializingLedger) {
            try {
                Configtx.Config existingResourcesConfig = retrievePersistedConfig(simulator, RESOURCES_CONFIG_KEY);
                Configtx.Config existingGroupConfig = retrievePersistedConfig(simulator, GROUP_CONFIG_KEY);

                ValidateUtils.isNotNull(existingResourcesConfig, "existingResourcesConfig can not be null");
                ValidateUtils.isNotNull(existingGroupConfig, "existingGroupConfig can not be null");

                IGroupConfigBundle groupConfigBundle = new GroupConfigBundle(groupId, existingGroupConfig);
                IResourcesConfigBundle resourcesConfigBundle = new ResourcesConfigBundle(groupId,
                        existingResourcesConfig, groupConfigBundle, null);

                Configtx.Config fullResourceConfig = ConfigTxUtils.computeFullConfig(resourcesConfigBundle, txEnvelope);
                persistConfig(simulator, RESOURCES_CONFIG_KEY, fullResourceConfig);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new LedgerException(e);
            }
        } else {
            try {
                Configtx.Config fullResourceConfig = ConfigTxUtils.validateAndApplyResourceConfig(groupId, txEnvelope);
                persistConfig(simulator, RESOURCES_CONFIG_KEY, fullResourceConfig);
            } catch (InvalidTxException e) {
                log.error(e.getMessage(), e);
                throw new InvalidTxException(e);
            }catch (Exception ex){
                log.error(ex.getMessage(), ex);
                throw new LedgerException(ex);
            }
        }
    }

    public static void persistConfig(ITxSimulator simulator, String key, Configtx.Config groupConfig) throws
            LedgerException {
        simulator.setState(NODE_NAMESPACE, key, groupConfig.toByteArray());
    }

    public static Configtx.Config retrievePersistedConfig(IQueryExecutor queryExecutor, String key) throws LedgerException,
            InvalidProtocolBufferException {
        byte[] serializedConfig = queryExecutor.getState(NODE_NAMESPACE, key);

        if (serializedConfig == null) {
            return null;
        } else {
            return Configtx.Config.parseFrom(serializedConfig);
        }
    }
}
