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
package org.bcia.javachain.consenter.util;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/1
 * @company Dingxuan
 */
public class LoadYaml {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LoadYaml.class);
    public  void loadyaml() {
        try {
            Yaml yaml = new Yaml();
            URL url = LoadYaml.class.getClassLoader().getResource("orderer.yaml");
            if (url != null) {
                //获取test.yaml文件中的配置数据，然后转换为obj，
                Object obj =yaml.load(new FileInputStream(url.getFile()));
                System.out.println(obj);
                //也可以将值转换为Map
                log.info("Loading the yaml file...");
                HashMap map =(HashMap)yaml.load(new FileInputStream(url.getFile()));
               // System.out.println(map.get("General"));
                //System.out.println(((HashMap) map.get("General")).get("LedgerType"));
                //System.out.println(((HashMap)((HashMap) map.get("General")).get("TLS")).get("PrivateKey"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
