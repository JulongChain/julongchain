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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import java.util.HashMap;
import java.util.Map;

/**
 * namespace更新包
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class NsUpdates {

    private Map<String, VersionedValue> map = new HashMap<>();

    public Map<String, VersionedValue> getMap() {
        return map;
    }

    public void setMap(Map<String, VersionedValue> map) {
        this.map = map;
    }
}
