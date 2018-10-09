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

import java.io.IOException;
import java.util.Date;

/**
 * 身份接口
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public interface IIdentity {
    /**
     *到期时间
     * @return
     * @throws MspException
     */
    Date expireAt() throws MspException;

    /**
     * 获取身份
     * @return
     */
    IdentityIdentifier getIdentifier();

    /**
     *获取msp身份
     * @return
     */
    String getMSPIdentifier();

    /**
     * 验证
     * @throws MspException
     */
    void validate() throws MspException;

    /**
     * 获取组织单元
     * @return
     * @throws MspException
     */
    OUIdentifier[] getOrganizationalUnits() throws MspException;

    /**
     * 消息验签
     * @param msg
     * @param sig
     * @throws VerifyException
     */
    void verify(byte[] msg, byte[] sig) throws VerifyException;

    /**
     * 身份序列化
     * @return
     */
    byte[] serialize();

    /**
     * mspprincipal
     * @param principal
     * @throws MspException
     * @throws IOException
     */
    void satisfiesPrincipal(MspPrincipal.MSPPrincipal principal) throws MspException, IOException;
}
