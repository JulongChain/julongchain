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
package org.bcia.javachain.core.node.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.node.ConfigtxProcessor;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

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

    public static boolean isResourceConfigCapabilityOn(String groupId, Configtx.Config groupConfig) {
        //TODO

        return true;
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
}
