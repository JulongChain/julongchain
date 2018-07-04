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
package org.bcia.julongchain.consenter.util;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/1
 * @company Dingxuan
 */
public class YamlLoader {
    private static JavaChainLog log = JavaChainLogFactory.getLog(YamlLoader.class);

    public static Map readYamlFile(String file){
        HashMap map=new HashMap();
        try {
            Yaml yaml = new Yaml();
            map =(HashMap)yaml.load(YamlLoader.class.getClassLoader().getResourceAsStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


}
