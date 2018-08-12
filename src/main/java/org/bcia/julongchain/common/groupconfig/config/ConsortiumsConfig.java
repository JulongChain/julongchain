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
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 多联盟配置对象
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class ConsortiumsConfig implements IConsortiumsConfig {
    private Map<String, IConsortiumConfig> consortiumConfigMap;

    public ConsortiumsConfig(Configtx.ConfigTree consortiumsTree, MSPConfigHandler mspConfigHandler) throws
            InvalidProtocolBufferException, ValidateException {
        consortiumConfigMap = new HashMap<String, IConsortiumConfig>();
        if (consortiumsTree != null && consortiumsTree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = consortiumsTree.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String consortiumName = entry.getKey();
                Configtx.ConfigTree consortiumTree = entry.getValue();

                IConsortiumConfig consortiumConfig = new ConsortiumConfig(consortiumTree, mspConfigHandler);
                consortiumConfigMap.put(consortiumName, consortiumConfig);
            }
        }
    }

    @Override
    public Map<String, IConsortiumConfig> getConsortiumConfigMap() {
        return consortiumConfigMap;
    }
}
