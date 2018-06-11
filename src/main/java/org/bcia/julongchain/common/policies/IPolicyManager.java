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

import org.bcia.julongchain.common.policies.policy.IPolicy;

import java.util.Map;

/**
 * 策略管理者接口
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface IPolicyManager {
    IPolicy getPolicy(String id);

    /**
     * the sub-policy getPolicyManager for a given path and whether it exists
     *
     * @param paths
     * @return
     */
    IPolicyManager getSubPolicyManager(String[] paths);

    Map<String, IPolicy> getPolicies();
}
