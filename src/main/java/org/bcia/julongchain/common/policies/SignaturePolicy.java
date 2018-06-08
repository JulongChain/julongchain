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
package org.bcia.julongchain.common.policies;

import org.bcia.julongchain.common.policycheck.common.IsSignaturePolicyType;
import org.bcia.julongchain.protos.common.Policies;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/3/8
 * @company Dingxuan
 *//**
     *
     */
public class SignaturePolicy extends StandardConfigPolicy {
    public SignaturePolicy(String key, Policies.SignaturePolicyEnvelope envelope) {
        super(key);

        this.value = buildSignaturePolicy(key, envelope);
    }

    private Policies.Policy buildSignaturePolicy(String policyName, Policies.SignaturePolicyEnvelope envelope) {
        //构造Policy对象
        Policies.Policy.Builder policyBuilder = Policies.Policy.newBuilder();
        policyBuilder.setType(Policies.Policy.PolicyType.SIGNATURE_VALUE);
        policyBuilder.setValue(envelope.toByteString());
        return policyBuilder.build();
    }


    public IsSignaturePolicyType isSignaturePolicy_type;


}
