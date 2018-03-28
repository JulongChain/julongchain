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
package org.bcia.javachain.csp.gm.sm4;

import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bouncycastle.util.encoders.Hex;


import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 类描述
 *
 * @author
 * @date 18-3-27
 * @company Dingxuan
 */
public class SM4Key {

    public static final int SM4KeyLength = 128;

    public static void main(String[] args) throws IOException {

        System.out.println(getKey());

        System.out.println((getKeyByRaw("testkey")));
    }

    /**
     * 随机生成密钥
     */
    public static String getKey() throws IOException {

        SecureRandom random = new SecureRandom();

        BigInteger r = new BigInteger(128, random);
        String key = Hex.toHexString(SM3.hash((r.toByteArray()))).substring(0, SM4KeyLength / 4);

        return key;

    }


    /**
     * 使用指定的字符串生成秘钥
     */
    public static String getKeyByRaw(String SM2KeyByRaw) throws IOException {

        BigInteger r = new BigInteger(128, new SecureRandom(SM2KeyByRaw.getBytes()));
        String key = Hex.toHexString(SM3.hash((r.toByteArray()))).substring(0, SM4KeyLength / 4);

        return key;

    }

}