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
package org.bcia.javachain.msp;

import org.bcia.javachain.msp.entity.OUIdentifier;
import org.bcia.javachain.protos.common.MspPrincipal;

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
     String getMSPIdentifier();

     void validate();

     OUIdentifier[] getOrganizationalUnits();

     void verify(byte[] msg, byte[] sig);

     byte[] serialize();

     void satisfiesPrincipal(MspPrincipal principal);
}
