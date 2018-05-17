/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt.SM2;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.common.Constants;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM2 algorithm
 *
 * @author tengxiumin
 * @date 5/16/18
 * @company SDT
 */

public class SM2 {

    private static JavaChainLog logger = JavaChainLogFactory.getLog( SM2.class);
    private static final SMJniApi smJniApi = new SMJniApi();
    public static final String YamlFile = "gmcsp.yaml";

    public SM2() { }

    /**
     *  生成SM2公私钥对
     *
     * @return SM2KeyPair
     */
    public static SM2KeyPair generateKeyPair() {

        byte[] privateKey = new byte[Constants.SM2_SK_LEN];
        byte[] publicKey = new byte[Constants.SM2_PK_LEN];
        try {
            privateKey = smJniApi.RandomGen(Constants.SM2_SK_LEN);
            publicKey = smJniApi.SM2MakeKey(privateKey);
        } catch (Exception e) {
            logger.error("SM2KeyPair error: generate key pair failed.");
            e.printStackTrace();
        }

        if (null == privateKey || Constants.SM2_SK_LEN != privateKey.length
            || null == publicKey || Constants.SM2_PK_LEN != publicKey.length) {
            return null;
        }
        return new SM2KeyPair(publicKey, privateKey);
    }

    /**
     * sm2密钥对derive
     *
     * @return
     */
    public static byte[] SM2KeyDerive(byte[] key, int length) {
        //TODO: 待实现
        return null;
    }

    /**
     * 对数据进行签名
     *
     * @param
     * @param
     * @return
     * @throws
     */
    public static byte[] sign(byte[] hash, byte[] priKey) {
        if(null == hash || 0 == hash.length) {
            logger.error("Invalid hash. It must not be nil or empty.");
            return null;
        }
        if(null == priKey || 0 == priKey.length) {
            logger.error("Invalid priKey. It must not be nil or empty.");
            return null;
        }
        byte[] result = new byte[0];
        try {
            byte[] random = smJniApi.RandomGen(Constants.SM2_SIGN_RANDOM_LEN);
            result = smJniApi.SM2Sign(hash, random, priKey);
        } catch (Exception e) {
            logger.error( "SM2 signature error: sign data failed" );
        }
        return result;
    }


    /**
     * 验证签名值
     *
     * @param
     * @param
     * @param
     * @return
     */
    public static int verify(byte[] hash, byte[] pubKey, byte[] sign) {
        if(null == hash || 0 == hash.length) {
            logger.error("Invalid hash. It must not be nil or empty.");
            return 1;
        }
        if(null == pubKey || 0 == pubKey.length) {
            logger.error("Invalid pubKey. It must not be nil or empty.");
            return 1;
        }
        if(null == sign || 0 == sign.length) {
            logger.error("Invalid sign. It must not be nil or empty.");
            return 1;
        }
        int result = 0;
        try {
            result = smJniApi.SM2Verify(hash, pubKey, sign);
        } catch (Exception e) {
            logger.error( "SM2 verify signature error: SM2Verify failed" );
        }
        return result;
    }
}
