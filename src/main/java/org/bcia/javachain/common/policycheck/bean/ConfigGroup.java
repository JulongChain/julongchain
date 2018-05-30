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

import java.util.Map;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 04/05/18
 * @company Aisino
 */
public class ConfigGroup {
    private Long version;
    private Map groups;
    private Map values;
    private Map policies;
    private String modPolicy;

    public ConfigGroup(Long version, Map groups, Map values, Map policies, String modPolicy) {
        this.version = version;
        this.groups = groups;
        this.values = values;
        this.policies = policies;
        this.modPolicy = modPolicy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Map getGroups() {
        return groups;
    }

    public void setGroups(Map groups) {
        this.groups = groups;
    }

    public Map getValues() {
        return values;
    }

    public void setValues(Map values) {
        this.values = values;
    }

    public Map getPolicies() {
        return policies;
    }

    public void setPolicies(Map policies) {
        this.policies = policies;
    }

    public String getModPolicy() {
        return modPolicy;
    }

    public void setModPolicy(String modPolicy) {
        this.modPolicy = modPolicy;
    }
}
