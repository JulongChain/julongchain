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

package org.bcia.javachain.common.policycheck.bean;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 04/05/18
 * @company Aisino
 */
public class ConfigPolicy {
    private Long version;
    private Policy policy;
    private String modPolicy;

    public ConfigPolicy(Long version, Policy policy, String modPolicy) {
        this.version = version;
        this.policy = policy;
        this.modPolicy = modPolicy;
    }


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getModPolicy() {
        return modPolicy;
    }

    public void setModPolicy(String modPolicy) {
        this.modPolicy = modPolicy;
    }
}
