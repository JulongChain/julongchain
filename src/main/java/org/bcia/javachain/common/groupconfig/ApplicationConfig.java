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
package org.bcia.javachain.common.groupconfig;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 应用配置
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public class ApplicationConfig implements IApplicationConfig {
    private Map<String, IApplicationOrgConfig> applicationOrgConfigs;
    private Configuration.Capabilities capabilities;

    public ApplicationConfig(Map<String, IApplicationOrgConfig> applicationOrgConfigs, Configuration.Capabilities
            capabilities) {
        this.applicationOrgConfigs = applicationOrgConfigs;
        this.capabilities = capabilities;
    }

    public ApplicationConfig(Configtx.ConfigChild appChild, MSPConfigHandler mspConfigHandler) throws
            InvalidProtocolBufferException, ValidateException {
        if (appChild != null && appChild.getValuesMap() != null) {
            Configtx.ConfigValue configValue = appChild.getValuesMap().get(GroupConfigConstant.CAPABILITIES);
            if (configValue != null) {
                capabilities = Configuration.Capabilities.parseFrom(configValue.getValue());
            }
        }

        applicationOrgConfigs = new HashMap<String, IApplicationOrgConfig>();
        if (appChild != null && appChild.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigChild>> entries = appChild.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigChild> entry = entries.next();
                String orgName = entry.getKey();
                Configtx.ConfigChild orgChild = entry.getValue();

                ApplicationOrgConfig applicationOrgConfig = new ApplicationOrgConfig(orgName, mspConfigHandler, orgChild);
                applicationOrgConfigs.put(orgName, applicationOrgConfig);
            }
        }

    }

    public Map<String, IApplicationOrgConfig> getApplicationOrgConfigs() {
        return applicationOrgConfigs;
    }

    public void setApplicationOrgConfigs(Map<String, IApplicationOrgConfig> applicationOrgConfigs) {
        this.applicationOrgConfigs = applicationOrgConfigs;
    }

    public Configuration.Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Configuration.Capabilities capabilities) {
        this.capabilities = capabilities;
    }
}
