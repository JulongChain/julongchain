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
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.msp.entity.IdentityIdentifier;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.msp.MspConfigPackage;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public interface IMsp extends IIdentityDeserializer{
    /**
     *  根据配置信息设置MSP实例
     * @param config
     */

    IMsp setup(MspConfigPackage.MSPConfig config);

    /**
     * 返回此msp的版本
     * @return
     */
    public int getVersion();

    /**
     * const常量,之后需要确认
     */

    /**
     * 返回提供者类型
     * @return
     */
     int  getType();

    /**
     * 返回提供者的标识符
     * @return
     */
     String getIdentifier();

    /**
     * 返回与提供的标识符对应的签名标识
     * @param identityIdentifier
     * @return
     */
     ISigningIdentity getSigningIdentity(IdentityIdentifier identityIdentifier);

    /**
     * 返回默认的签名标识
     * @return
     */
     ISigningIdentity getDefaultSigningIdentity();

    /**
     * 返回此MSP的TLS根证书
     * @return
     */
     byte[][] getTLSRootCerts();

    /**
     * 返回此MSP的TLS中间根证书
     * @return
     */
     byte[][] getTLSIntermediateCerts();

    /**
     * 验证检查提供的身份是否有效
     * @param id
     */
     void validate(IIdentity id)throws MspException;

    /**
     * SatisfiesPrincipal检查该标识是否与MSPPrincipal中提供的描述相匹配。
     * 检查可能涉及逐字节比较（如果委托人是序列化身份）或可能需要MSP验证
     * @param id
     * @param principal
     */
     void satisfiesPrincipal(IIdentity id, MspPrincipal.MSPPrincipal principal) throws IOException, MspException;
}
