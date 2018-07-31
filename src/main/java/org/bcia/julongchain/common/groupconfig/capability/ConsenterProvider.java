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
package org.bcia.julongchain.common.groupconfig.capability;

import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.protos.common.Configuration;

import java.util.Map;

/**
 * 共识提供者
 *
 * @author zhouhui
 * @date 2018/5/10
 * @company Dingxuan
 */
public class ConsenterProvider implements IConsenterCapabilities {
    private static final boolean DEFAULT_PREDICTABLE_GROUP_TEMPLATE = true;
    private static final boolean DEFAULT_RESUBMISSION = true;
    private static final boolean DEFAULT_EXPIRATION = true;

    private Map<String, Configuration.Capability> capabilityMap;

    private boolean supported;
    private boolean predictableGroupTemplate;
    private boolean resubmission;
    private boolean expiration;

    public ConsenterProvider(Map<String, Configuration.Capability> capabilityMap) {
        this.capabilityMap = capabilityMap;

        this.supported = true;
        //当前版本是否支持，如果支持才看能力设置是否打开，需要双重判断，以保障兼容性
        this.predictableGroupTemplate = DEFAULT_PREDICTABLE_GROUP_TEMPLATE && capabilityMap.containsKey
                (GroupConfigConstant.CONSENTER_PREDICTABLE_GROUP_TEMPLATE);
        this.resubmission = DEFAULT_RESUBMISSION && capabilityMap.containsKey(GroupConfigConstant
                .CONSENTER_RESUBMISSION);
        this.expiration = DEFAULT_EXPIRATION && capabilityMap.containsKey(GroupConfigConstant
                .CONSENTER_EXPIRATION);
    }

    @Override
    public boolean isSupported() {
        return supported;
    }

    @Override
    public boolean isPredictableGroupTemplate() {
        return predictableGroupTemplate;
    }

    @Override
    public boolean isResubmission() {
        return resubmission;
    }

    @Override
    public boolean isExpiration() {
        return expiration;
    }
}
