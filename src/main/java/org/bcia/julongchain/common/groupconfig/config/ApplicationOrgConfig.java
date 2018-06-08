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
import org.bcia.julongchain.protos.node.Configuration;

import java.util.List;

/**
 * 应用组织配置对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class ApplicationOrgConfig extends OrganizationConfig implements IApplicationOrgConfig {
    private Configuration.AnchorNodes anchorNodes;

    public ApplicationOrgConfig(String name, MSPConfigHandler mspConfigHandler, Configtx.ConfigTree orgTree) throws
            InvalidProtocolBufferException, ValidateException {
        super(name, mspConfigHandler, orgTree);

        if (orgTree != null && orgTree.getValuesMap() != null) {
            Configtx.ConfigValue configValue = orgTree.getValuesMap().get(GroupConfigConstant.ANCHOR_NODES);
            if (configValue != null) {
                anchorNodes = Configuration.AnchorNodes.parseFrom(configValue.getValue());
            }
        }
    }

    @Override
    public List<Configuration.AnchorNode> getAnchorNodes() {
        return anchorNodes.getAnchorNodesList();
    }
}
