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
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.common.Policies;

import java.util.List;
import java.util.Map;

/**
 * 内置元策略
 *
 * @author zhouhui
 * @date 2018/4/17
 * @company Dingxuan
 */
public class ImplicitMetaPolicy implements IPolicy {
    private int threshold;
    private IPolicy[] subPolicies;
    private Map<String, IPolicyManager> managers;
    private String subPolicyName;

    public ImplicitMetaPolicy(Policies.ImplicitMetaPolicy policy, Map<String, IPolicyManager> managers) {
        this.managers = managers;
        this.subPolicyName = policy.getSubPolicy();

        subPolicies = new IPolicy[managers.size()];

        int i = 0;
        for (IPolicyManager policyManager : managers.values()) {
            subPolicies[i++] = policyManager.getPolicy(policy.getSubPolicy());
        }

        if (Policies.ImplicitMetaPolicy.Rule.ANY.equals(policy.getRule())) {
            threshold = 1;
        } else if (Policies.ImplicitMetaPolicy.Rule.ALL.equals(policy.getRule())) {
            threshold = subPolicies.length;
        } else if (Policies.ImplicitMetaPolicy.Rule.ALL.equals(policy.getRule())) {
            threshold = subPolicies.length / 2 + 1;
        }

        if (subPolicies.length == 0) {
            threshold = 0;
        }
    }


    @Override
    public void evaluate(List<SignedData> signatureSet) throws PolicyException {
        int remaining = threshold;

        for (IPolicy policy : subPolicies) {
            policy.evaluate(signatureSet);
            remaining--;

            if (remaining == 0) {
                return;
            }
        }

        if (remaining > 0) {
            throw new PolicyException("evaluate failed: " + (threshold - remaining) + " policies satisfied, but " +
                    "needed " + threshold);
        }

    }
}
