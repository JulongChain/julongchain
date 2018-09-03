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
package org.bcia.julongchain.msp.mgmt;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.msp.Identities;

import java.util.Map;

/**
 * 反序列化管理
 *
 * @author zhangmingyang
 * @date 2018/07/20
 * @company Dingxuan
 */
public class DeserializersManager {
    public DeserializersManager() {
    }

    /**
     * 身份的反序列化
     *
     * @param raw
     * @return
     * @throws MspException
     */
    public Identities.SerializedIdentity deserialize(byte[] raw) throws MspException {
        Identities.SerializedIdentity sId = null;
        try {
            sId = Identities.SerializedIdentity.parseFrom(raw);
        } catch (InvalidProtocolBufferException e) {
            throw new MspException(e.getMessage());
        }
        return sId;
    }

    /**
     * 获取本地msp的身份标识
     *
     * @return
     */
    public static String getLoalMspIdentifier() {
        String id = GlobalMspManagement.getLocalMsp().getIdentifier();
        return id;
    }

    /**
     * 获取本地的msp
     *
     * @return
     */
    public static IMsp getLocalDeserializer() {
        return GlobalMspManagement.getLocalMsp();
    }

    /**
     * 获取反序列化map
     *
     * @return
     */
    public static Map<String, IMspManager> getGroupDeserializers() {
        return MspMgmtMgr.getDeserializers();
    }

}
