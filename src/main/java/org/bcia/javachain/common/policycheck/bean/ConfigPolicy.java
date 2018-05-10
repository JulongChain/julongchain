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
    private Long Version;
    private Policy Policy;
    private String ModPolicy;

    public Long getVersion() {
        return Version;
    }

    public void setVersion(Long version) {
        Version = version;
    }

    public org.bcia.javachain.common.policycheck.bean.Policy getPolicy() {
        return Policy;
    }

    public void setPolicy(org.bcia.javachain.common.policycheck.bean.Policy policy) {
        Policy = policy;
    }

    public String getModPolicy() {
        return ModPolicy;
    }

    public void setModPolicy(String modPolicy) {
        ModPolicy = modPolicy;
    }
}
