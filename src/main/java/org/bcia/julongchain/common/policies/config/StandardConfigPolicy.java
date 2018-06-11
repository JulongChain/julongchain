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
package org.bcia.julongchain.common.policies.config;

import org.bcia.julongchain.protos.common.Policies;

/**
 * 标准配置策略
 *
 * @author zhouhui
 * @date 2018/3/8
 * @company Dingxuan
 */
public class StandardConfigPolicy implements IConfigPolicy {
    protected String key;
    protected Policies.Policy value;

    public StandardConfigPolicy(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Policies.Policy getValue() {
        return value;
    }

    /**
     * 构造内置元策略
     *
     * @param subPolicyName
     * @param rule
     * @return
     */
    protected Policies.Policy buildImplicitMetaPolicy(String subPolicyName, Policies.ImplicitMetaPolicy.Rule rule) {
        //构造ImplicitMetaPolicy对象
        Policies.ImplicitMetaPolicy.Builder implicitMetaPolicyBuilder = Policies.ImplicitMetaPolicy.newBuilder();
        implicitMetaPolicyBuilder.setSubPolicy(subPolicyName);
        implicitMetaPolicyBuilder.setRule(rule);
        Policies.ImplicitMetaPolicy implicitMetaPolicy = implicitMetaPolicyBuilder.build();

        //构造Policy对象
        Policies.Policy.Builder policyBuilder = Policies.Policy.newBuilder();
        policyBuilder.setType(Policies.Policy.PolicyType.IMPLICIT_META_VALUE);
        policyBuilder.setValue(implicitMetaPolicy.toByteString());
        return policyBuilder.build();
    }
}
