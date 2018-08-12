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
package org.bcia.julongchain.common.groupconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.common.Policies;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 联盟配置对象
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class ConsortiumConfig implements IConsortiumConfig {
    private Policies.Policy groupCreationPolicy;
    private Map<String, IOrganizationConfig> organizationConfigMap;

    public ConsortiumConfig(Configtx.ConfigTree consortiumTree, MSPConfigHandler mspConfigHandler) throws
            InvalidProtocolBufferException, ValidateException {
        if (consortiumTree != null && consortiumTree.getValuesMap() != null) {
            Configtx.ConfigValue configValue = consortiumTree.getValuesMap().get(GroupConfigConstant.GROUP_CREATION_POLICY);
            if (configValue != null) {
                groupCreationPolicy = Policies.Policy.parseFrom(configValue.getValue());
            }
        }

        organizationConfigMap = new HashMap<String, IOrganizationConfig>();
        if (consortiumTree != null && consortiumTree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = consortiumTree.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String orgName = entry.getKey();
                Configtx.ConfigTree orgTree = entry.getValue();

                IOrganizationConfig organizationConfig = new OrganizationConfig(orgName, mspConfigHandler, orgTree);
                organizationConfigMap.put(orgName, organizationConfig);
            }
        }
    }

    @Override
    public Policies.Policy getGroupCreationPolicy() {
        return groupCreationPolicy;
    }

    @Override
    public Map<String, IOrganizationConfig> getOrganizationConfigMap() {
        return organizationConfigMap;
    }
}
