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
package org.bcia.julongchain.common.policies.policy;

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.util.proto.SignedData;

import java.util.List;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class NormalPolicy implements IPolicy {
    private IPolicy policy;
    private String policyName;

    public NormalPolicy(IPolicy policy, String policyName) {
        this.policy = policy;
        this.policyName = policyName;
    }

    @Override
    public void evaluate(List<SignedData> signatureSet) throws PolicyException {
        policy.evaluate(signatureSet);
    }
}
