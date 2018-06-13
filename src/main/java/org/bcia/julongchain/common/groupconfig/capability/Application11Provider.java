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
 * JavaChain1.1的应用提供者，兼容性考虑
 *
 * @author zhouhui
 * @date 2018/5/9
 * @company Dingxuan
 */
public class Application11Provider implements IApplicationCapabilities {
    /**
     * 是否禁止重复交易ID(当前版本不支持，所以赋值为false)
     */
    private static final boolean DEFAULT_FORBID_DUPLICATE_TXID = false;
    private static final boolean DEFAULT_RESOURCES_TREE = true;
    private static final boolean DEFAULT_PRIVATE_GROUP_DATA = true;
    private static final boolean DEFAULT_VALIDATION = true;

    private Map<String, Configuration.Capability> capabilityMap;

    private boolean supported;

    private boolean forbidDuplicateTxId;
    private boolean resourcesTree;
    private boolean privateGroupData;
    private boolean validation;

    public Application11Provider(Map<String, Configuration.Capability> capabilityMap) {
        this.capabilityMap = capabilityMap;

        this.supported = true;
        //当前版本是否支持，如果支持才看能力设置是否打开，需要双重判断，以保障兼容性
        this.forbidDuplicateTxId = DEFAULT_FORBID_DUPLICATE_TXID && capabilityMap.containsKey(GroupConfigConstant
                .APP_FORBID_DUPLICATE_TXID);
        this.resourcesTree = DEFAULT_RESOURCES_TREE && capabilityMap.containsKey(GroupConfigConstant
                .APP_RESOURCE_TREE_EXPERIMENTAL);
        this.privateGroupData = DEFAULT_PRIVATE_GROUP_DATA && capabilityMap.containsKey(GroupConfigConstant
                .APP_PRIVATE_DATA_EXPERIMENTAL);
        this.validation = DEFAULT_VALIDATION && capabilityMap.containsKey(GroupConfigConstant.APP_VALIDATION);
    }

    @Override
    public boolean isSupported() {
        return supported;
    }

    @Override
    public boolean isForbidDuplicateTxId() {
        return forbidDuplicateTxId;
    }

    @Override
    public boolean isResourcesTree() {
        return resourcesTree;
    }

    @Override
    public boolean isPrivateGroupData() {
        return privateGroupData;
    }

    @Override
    public boolean isValidation() {
        return validation;
    }
}
