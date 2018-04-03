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
package org.bcia.javachain.tools.configtxgen.entity;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * 创世配置工厂
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
@Component
public class GenesisConfigFactory {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GenesisConfigFactory.class);

    public static GenesisConfig loadGenesisConfig() throws IOException {
        Yaml yaml = new Yaml();
        URL url = GenesisConfigFactory.class.getClassLoader().getResource(GenesisConfig.CONFIGTX_FILE_PATH);

        InputStream is = null;
        try {
            is = new FileInputStream(url.getFile());
            GenesisConfig genesisConfig = yaml.loadAs(is, GenesisConfig.class);

            genesisConfig.completeInstance();
            return genesisConfig;
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

    public static void main(String[] args) throws Exception {
        GenesisConfigFactory.loadGenesisConfig();
    }

    public static Map<String, Object> loadGenesisConfigMap() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        URL url = GenesisConfig.class.getClassLoader().getResource(GenesisConfig.CONFIGTX_FILE_PATH);

        InputStream is = null;
        try {
            is = new FileInputStream(url.getFile());
            Map<String, Object> yamlMap = yaml.load(is);
            return yamlMap;
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