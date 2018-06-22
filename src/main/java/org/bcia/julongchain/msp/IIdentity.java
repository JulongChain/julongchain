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

import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.msp.entity.IdentityIdentifier;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.protos.common.MspPrincipal;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public interface IIdentity {
    /**
     * ExpiresAt() time.Time 未定义
     */
    /**
     * 为定义该方法   定义实体类 IdentityIdentifier来实现  GetIdentifier() *IdentityIdentifier
     */

    /**
     * 返回此实例的msp id
     * @return
     */



    IdentityIdentifier getMSPIdentifier();

     void validate();

     OUIdentifier[] getOrganizationalUnits();

    /**
     * 使用此标识作为参考验证某个消息的签名
     * @param msg
     * @param sig
     */
     void verify(byte[] msg, byte[] sig) throws VerifyException;

    /**
     * 将身份转换为字节
     * @return
     */
     byte[] serialize();

    /**
     * SatisfiesPrincipal检查此实例是否匹配
     在MSPPrincipal中提供的描述。 检查可能
     涉及逐字节的比较（如果委托人是
     序列化身份）或可能需要MSP验证
     * @param principal
     */
     void satisfiesPrincipal(MspPrincipal.MSPPrincipal principal) throws MspException;
}
