/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.config;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 类描述
 *
 * @author zhangmingyang
 * @date 2018/06/29
 * @company Dingxuan
 */
public class ConfigFactory {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConfigFactory.class);

    public static Config loadConfig() throws FileNotFoundException {
        Yaml yaml = new Yaml();

        InputStream is = null;
        try {
            is = new FileInputStream(CommConstant.CONFIG_DIR_PREFIX + Config.Config_FILE_PATH);
            Config config = yaml.loadAs(is, Config.class);
            return config;
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

    public static void main(String[] args) throws FileNotFoundException {
        Config config=ConfigFactory.loadConfig();
        System.out.println(config.getOrganizationalUnitIdentifiers().get("certificate"));
    }
}
