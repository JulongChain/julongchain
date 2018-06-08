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
 * IConfig encapsulates config (group or resource) tree
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
public interface IConfig {
    /**
     * getCurrentConfig returns the current config
     *
     * @return
     */
    Configtx.Config getCurrentConfig();

    /**
     * updateProposeConfig attempts to validate a new configtx against the current config state
     */
    Configtx.ConfigEnvelope updateProposeConfig(Common.Envelope configtx) throws InvalidProtocolBufferException,
            ValidateException;
}
