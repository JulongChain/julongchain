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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author zhangmingyang
 * @Date: 2018/05/24
 * @company Dingxuan
 */
public class ConsenterConfigFactory {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConsenterConfigFactory.class);

    public static ConsenterConfig loadConsenterConfig() {
        Yaml yaml = new Yaml();
        InputStream is = ConsenterConfigFactory.class.getClassLoader().getResourceAsStream(ConsenterConfig.CONSENTER_CONFIG_PATH);
        ConsenterConfig consenterConfig = yaml.loadAs(is, ConsenterConfig.class);
        return consenterConfig;
    }
    public static void main(String[] args) {
        ConsenterConfig consenterConfig = loadConsenterConfig();
        System.out.println(consenterConfig.getKafka().getComumer().get("maxReads"));
    }
}
