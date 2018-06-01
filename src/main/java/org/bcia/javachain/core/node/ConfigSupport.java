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
package org.bcia.javachain.core.node;

import org.bcia.javachain.common.config.IConfig;
import org.bcia.javachain.common.config.IConfigManager;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.entity.Group;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/31
 * @company Dingxuan
 */
public class ConfigSupport implements IConfigManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Node.class);

    @Override
    public IConfig getGroupConfig(String groupID) {
        try {
            Group group = Node.getInstance().getGroupMap().get(groupID);
            if (group != null) {
                return group.getGroupSupport().getGroupConfigBundle();
            }
        } catch (NodeException ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public IConfig getResourceConfig(String groupID) {
        try {
            Group group = Node.getInstance().getGroupMap().get(groupID);
            if (group != null) {
                return group.getGroupSupport().getResourcesConfigBundle();
            }
        } catch (NodeException ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }
}
