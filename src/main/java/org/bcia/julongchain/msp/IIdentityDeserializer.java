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
package org.bcia.julongchain.msp;

import org.bcia.julongchain.protos.msp.Identities;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public interface IIdentityDeserializer {
    /**
     * 反序列化身份
     * 如果身份关联到与正在执行反序列化的msp不同的msp，则反序列化将失败。
     * @param serializedIdentity
     * @return
     */
     IIdentity deserializeIdentity(byte[] serializedIdentity);

    /**
     * IsWellFormed检查给定的身份是否可以反序列化为其提供者特定的形式
     * @param identity
     */
     void isWellFormed(Identities.SerializedIdentity identity);
}
