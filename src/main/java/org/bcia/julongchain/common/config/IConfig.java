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
package org.bcia.julongchain.common.config;


import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * IConfig 封装了config树
 *
 * @author sunianle,zhangmingyang
 * @date 3/13/18
 * @company Dingxuan
 */
public interface IConfig {
    /**
     * 获取当前配置
     * @return
     */
    Configtx.Config getCurrentConfig();

    /**
     * 更新propose配置,尝试针对当前配置状态验证新的configtx
     * @param configtx
     * @return
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    Configtx.ConfigEnvelope updateProposeConfig(Common.Envelope configtx) throws InvalidProtocolBufferException,
            ValidateException;
}
