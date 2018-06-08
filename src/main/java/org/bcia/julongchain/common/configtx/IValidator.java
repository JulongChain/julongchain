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
 * Validator provides a mechanism to propose config updates, see the config update results
 * and validate the results of a config update.
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public interface IValidator {

    /**
     * Validate attempts to apply a configtx to become the new config
     *
     * @param configEnv
     */
    void validate(Configtx.ConfigEnvelope configEnv);

    /**
     * Validate attempts to validate a new configtx against the current config state
     *
     * @param configtx
     * @return
     */
    Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) throws InvalidProtocolBufferException, ValidateException;

    /**
     * retrieves the chain ID associated with this getPolicyManager
     *
     * @return
     */
    String groupId();

    /**
     * ConfigProto returns the current config as a proto
     *
     * @return
     */
    Configtx.Config configProto();

    /**
     * Sequence returns the current sequence number of the config
     *
     * @return
     */
    long sequence();

}
