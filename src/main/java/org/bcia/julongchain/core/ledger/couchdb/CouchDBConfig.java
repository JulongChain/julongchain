/**
 * Copyright ShanghaiGeer. All Rights Reserved.
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
package org.bcia.julongchain.core.ledger.couchdb;

import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.node.NodeConfigFactory;

import java.util.Map;

/**
 * read couchdb config
 *
 * @author zhaiyihua
 * @date 2018/05/20
 * @company ShanghaiGeer
 */
public class CouchDBConfig {

    public static Map getCouchDBDefinition(){
        NodeConfig config = NodeConfigFactory.getNodeConfig();
        NodeConfig.Ledger ledger = config.getLedger();
        NodeConfig.State state = ledger.getState();
        Map<String, String> couchDBConfig = state.getCouchDBConfig();
        return couchDBConfig;
    }
}
