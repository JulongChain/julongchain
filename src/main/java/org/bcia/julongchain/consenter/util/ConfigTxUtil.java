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
package org.bcia.julongchain.consenter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * @author zhangmingyang
 * @Date: 2018/5/10
 * @company Dingxuan
 */
public class ConfigTxUtil {
    public static Configtx.Config unmarshalConfig(byte[] data) {
        Configtx.Config config = null;
        try {
            config = Configtx.Config.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static Configtx.Config unmarshalConfigOrPanic(byte[] data) {
        Configtx.Config result = unmarshalConfig(data);
        return result;
    }

    public static Configtx.ConfigUpdate unmarshalConfigUpdate(byte[] data) {
        Configtx.ConfigUpdate configUpdate = null;
        try {
            configUpdate = Configtx.ConfigUpdate.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return configUpdate;
    }

    public static Configtx.ConfigUpdate unmarshalConfigUpdateOrPanic(byte[] data) {
        Configtx.ConfigUpdate result = unmarshalConfigUpdate(data);
        return result;
    }

    public static Configtx.ConfigUpdateEnvelope unmarshalConfigUpdateEnvelope(byte[] data) {
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = null;
        try {
            configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return configUpdateEnvelope;
    }

    public static Configtx.ConfigUpdateEnvelope unmarshalConfigUpdateEnvelopeOrPanic(byte[] data) {
        Configtx.ConfigUpdateEnvelope result = unmarshalConfigUpdateEnvelope(data);
        return result;
    }


    public static Configtx.ConfigEnvelope unmarshalConfigEnvelope(byte[] data) {
        Configtx.ConfigEnvelope configEnvelope = null;
        try {
            configEnvelope = Configtx.ConfigEnvelope.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return configEnvelope;
    }

    public static Configtx.ConfigEnvelope unmarshalConfigEnvelopeOrPanic(byte[] data) {
        Configtx.ConfigEnvelope result = unmarshalConfigEnvelope(data);
        return result;
    }

}
