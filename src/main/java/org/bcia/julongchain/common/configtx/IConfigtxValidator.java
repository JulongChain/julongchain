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
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/01
 * @company Dingxuan
 */
public interface IConfigtxValidator {
    void validate(Configtx.ConfigEnvelope configEnv) throws ValidateException, InvalidProtocolBufferException;

    Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws InvalidProtocolBufferException,
            ValidateException;

    String getGroupId();

    long getSequence();

    Configtx.Config getConfig();
}
