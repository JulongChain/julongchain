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
package org.bcia.javachain.common.ledger.util;

import java.util.*;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class Util {

    /**
     * 获取map key的排序
     */
    public static <T> List<String> getSortedKeys(Map<String, T> m){
        List<String> list = new ArrayList<>();
        for(String key : m.keySet()){
            list.add(key);
        }
        Collections.sort(list);
        return list;
    }

    /**
     *
     */
    public static <T> List<T> getValuesBySortedKeys (Map<String, T> m){
        List<String> list = getSortedKeys(m);
        List<T> l = new ArrayList<>();
        for(String s : list){
            l.add(m.get(s));
        }
        return l;
    }

    /** EncodeOrderPreservingVarUint64 returns a byte-representation for a uint64 number such that
     * all zero-bits starting bytes are trimmed in order to reduce the length of the array
     * For preserving the order in a default bytes-comparison, first byte contains the number of remaining bytes.
     * The presence of first byte also allows to use the returned bytes as part of other larger byte array such as a
     * composite-key representation in db
     */
    public static byte[] encodeOrderPreservingVarUint64(Long number) {
        return null;
    }

    /** DecodeOrderPreservingVarUint64 decodes the number from the bytes obtained from method 'EncodeOrderPreservingVarUint64'.
     * Also, returns the number of bytes that are consumed in the process
     */
    public static Long decodeOrderPreservingVarUint64(byte[] bytes) {
        return null;
    }

}
