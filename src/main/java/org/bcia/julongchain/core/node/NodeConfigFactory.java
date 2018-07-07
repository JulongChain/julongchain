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
package org.bcia.julongchain.core.node;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 节点配置工厂
 *
 * @author zhouhui
 * @date 2018/4/9
 * @company Dingxuan
 */
@Component
public class NodeConfigFactory {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeConfigFactory.class);

    private static NodeConfig nodeConfig;

    public static NodeConfig getNodeConfig() {
        if (nodeConfig == null) {
            synchronized (NodeConfig.class) {
                if (nodeConfig == null) {
                    nodeConfig = loadNodeConfig();
                }
            }
        }

        return nodeConfig;
    }

    private static NodeConfig loadNodeConfig() {
        Yaml yaml = new Yaml();

        InputStream is = null;
        try {
//            is = NodeConfigFactory.class.getClassLoader().getResourceAsStream(NodeConfig.NODECONFIG_FILE_PATH);
            is = new FileInputStream(CommConstant.CONFIG_DIR_PREFIX + NodeConfig.NODECONFIG_FILE_PATH);

            NodeConfig nodeConfig = yaml.loadAs(is, NodeConfig.class);
            return nodeConfig;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
