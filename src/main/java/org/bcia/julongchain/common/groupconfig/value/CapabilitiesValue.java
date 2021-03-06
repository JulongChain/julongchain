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
package org.bcia.julongchain.common.groupconfig.value;

import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.protos.common.Configuration;

import java.util.Map;

/**
 * 能力集配置项
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
public class CapabilitiesValue extends StandardConfigValue {
    public CapabilitiesValue(Map<String, Boolean> capabilities) {
        this.key = GroupConfigConstant.CAPABILITIES;

        Configuration.Capabilities.Builder capabilitiesBuilder = Configuration.Capabilities.newBuilder();
        if (capabilities != null && capabilities.size() > 0) {
            for (String str : capabilities.keySet()) {
                if (capabilities.get(str)) {
                    //如果为true才需要加入列表
                    Configuration.Capability newCapability = Configuration.Capability.newBuilder().build();
                    capabilitiesBuilder.putCapabilities(str, newCapability);
                }
            }
        }

        this.value = capabilitiesBuilder.build();
    }
}
