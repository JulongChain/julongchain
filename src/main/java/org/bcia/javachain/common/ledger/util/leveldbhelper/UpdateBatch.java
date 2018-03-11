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
package org.bcia.javachain.common.ledger.util.leveldbhelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class UpdateBatch {

    private Map<String,byte[]> kvs = new HashMap<>();

    /** Put adds a KV
     *
     * @param key
     * @param value
     */
    public void put(byte[] key, byte[] value) {

    }

    /** Delete deletes a Key and associated value
     *
     * @param key
     */
    public void delete(byte[] key) {

    }

    public Map<String, byte[]> getKvs() {
        return kvs;
    }

    public void setKvs(Map<String, byte[]> kvs) {
        this.kvs = kvs;
    }
}
