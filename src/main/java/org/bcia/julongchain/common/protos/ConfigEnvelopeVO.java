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
package org.bcia.julongchain.common.protos;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/27
 * @company Dingxuan
 */
public class ConfigEnvelopeVO implements IProtoVO<Configtx.ConfigEnvelope> {
    private Configtx.Config config;
    private Common.Envelope lastUpdate;

    @Override
    public void parseFrom(Configtx.ConfigEnvelope configEnvelope) throws InvalidProtocolBufferException,
            ValidateException {
        ValidateUtils.isNotNull(configEnvelope, "configEnvelope can not be null");

        this.config = configEnvelope.getConfig();
        this.lastUpdate = configEnvelope.getLastUpdate();
    }

    @Override
    public Configtx.ConfigEnvelope toProto() {
        Configtx.ConfigEnvelope.Builder builder = Configtx.ConfigEnvelope.newBuilder();
        builder.setConfig(config);
        builder.setLastUpdate(lastUpdate);
        return builder.build();
    }
}
