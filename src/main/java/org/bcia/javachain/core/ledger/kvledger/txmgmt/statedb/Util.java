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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.csp.gm.sm3.SM3;

import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class Util {

    /** EncodeValue appends the value to the version, allows storage of version and value in binary form
     *
     * @param value
     * @param version
     * @return
     */
    public static byte[] encodeValue(byte[] value, Height version) {
        return null;
    }

    /** DecodeValue separates the version and value from a binary value
     *
     * @param encodedValue
     * @return
     */
    public static Map<Height, byte[]> decodeValue(byte[] encodedValue) {
        return null;
    }


    /**
     * 进行hash运算
     */
    public static byte[] getHashBytes(byte[] bytes){
        byte[] target = new SM3().hash(bytes);
        return target;
    }
}
