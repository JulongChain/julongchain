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
package org.bcia.julongchain.common.configtx;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 配置交易验证器接口
 *
 * @author zhouhui
 * @date 2018/06/01
 * @company Dingxuan
 */
public interface IConfigtxValidator {
    /**
     * 验证配置交易信封对象
     *
     * @param configEnv
     * @throws ValidateException
     * @throws InvalidProtocolBufferException
     */
    void validate(Configtx.ConfigEnvelope configEnv) throws ValidateException, InvalidProtocolBufferException;

    /**
     * 针对一个交易信封对象提议配置更新
     *
     * @param configtx
     * @return
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws InvalidProtocolBufferException,
            ValidateException;

    /**
     * 获取群组Id
     *
     * @return
     */
    String getGroupId();

    /**
     * 获取序列码
     *
     * @return
     */
    long getSequence();

    /**
     * 获取配置
     *
     * @return
     */
    Configtx.Config getConfig();
}
