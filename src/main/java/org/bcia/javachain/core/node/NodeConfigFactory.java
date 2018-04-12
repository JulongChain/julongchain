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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    public static NodeConfig loadNodeConfig() throws IOException {
        Yaml yaml = new Yaml();
        URL url = NodeConfigFactory.class.getClassLoader().getResource(NodeConfig.NODECONFIG_FILE_PATH);

        InputStream is = null;
        try {
            is = new FileInputStream(url.getFile());
            NodeConfig nodeConfig = yaml.loadAs(is, NodeConfig.class);
            return nodeConfig;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
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
