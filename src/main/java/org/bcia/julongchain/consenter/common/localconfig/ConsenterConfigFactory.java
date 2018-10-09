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
package org.bcia.julongchain.consenter.common.localconfig;

import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * consenterconfig配置加载
 *
 * @author zhangmingyang
 * @Date: 2018/05/24
 * @company Dingxuan
 */
public class ConsenterConfigFactory {
    private static JulongChainLog log = JulongChainLogFactory.getLog(ConsenterConfigFactory.class);
    private static ConsenterConfig consenterConfig;

    public static ConsenterConfig getConsenterConfig() {
        if (consenterConfig == null) {
            synchronized (ConsenterConfig.class) {
                if (consenterConfig == null) {
                    consenterConfig = loadConsenterConfig();
                }
            }
        }
        return consenterConfig;
    }

    public static ConsenterConfig loadConsenterConfig() {
        Yaml yaml = new Yaml();
        InputStream is = null;
        ConsenterConfig consenterConfig=null;
        try {
            is = new FileInputStream(CommConstant.CONFIG_DIR_PREFIX + ConsenterConfig.CONSENTER_CONFIG_PATH);
            consenterConfig = yaml.loadAs(is, ConsenterConfig.class);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return consenterConfig;
    }
}
