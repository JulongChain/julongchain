/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.julongchain.common.policycheck.cauthdsl;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.proto.ConfigGroup;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.common.Policies;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class PolicyUtil {
    public void templatePolicy(String key, Policies.SignaturePolicyEnvelope  sigPolicyEnv) throws InvalidProtocolBufferException, ValidateException {
        Configtx.Config c = Configtx.Config.newBuilder().build();
        ConfigGroup configGroup = new ConfigGroup();


    }

}

