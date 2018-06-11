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
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class PolicyRouter implements IPolicyManager {
    private static final String GROUP = "Group";
    private static final String RESOURCES = "Resources";

    private IPolicyManager groupPolicyManager;
    private IPolicyManager resourcesPolicyManager;

    public PolicyRouter(IPolicyManager groupPolicyManager, IPolicyManager resourcesPolicyManager) {
        this.groupPolicyManager = groupPolicyManager;
        this.resourcesPolicyManager = resourcesPolicyManager;
    }

    @Override
    public IPolicy getPolicy(String id) {
        if (id.startsWith(GROUP)) {
            return groupPolicyManager.getPolicy(id);
        } else if (id.startsWith(RESOURCES)) {
            return resourcesPolicyManager.getPolicy(id);
        }

        return null;
    }

    @Override
    public IPolicyManager getSubPolicyManager(String[] path) {
        if (path == null || path.length <= 0) {
            return this;
        }

        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);

        if (GROUP.equals(path[0])) {
            return groupPolicyManager.getSubPolicyManager(newPath);
        } else if (RESOURCES.equals(path[0])) {
            return resourcesPolicyManager.getSubPolicyManager(newPath);
        }

        return null;
    }

    @Override
    public Map<String, IPolicy> getPolicies() {
        return null;
    }
}
