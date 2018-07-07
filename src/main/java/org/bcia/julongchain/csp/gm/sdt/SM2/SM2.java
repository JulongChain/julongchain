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
package org.bcia.julongchain.csp.gm.sdt.SM2;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM2 algorithm
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */

public class SM2 {

    private static JavaChainLog logger = JavaChainLogFactory.getLog( SM2.class);
    private static final SMJniApi smJniApi = new SMJniApi();

    public SM2() { }

    /**
     *  生成SM2公私钥对
     *
     * @return SM2KeyPair
     */
    public static SM2KeyPair generateKeyPair() throws JavaChainException{

        byte[] privateKey = new byte[Constants.SM2_SK_LEN];
        byte[] publicKey = new byte[Constants.SM2_PK_LEN];
        try {
            privateKey = smJniApi.randomGen(Constants.SM2_SK_LEN);
            publicKey = smJniApi.sm2MakeKey(privateKey);
        } catch (Exception e) {
            logger.error("SM2KeyPair error: generate key pair failed.");
            throw new JavaChainException("SM2KeyPair error: generate key pair failed.");
        }

        if (null == privateKey || Constants.SM2_SK_LEN != privateKey.length
            || null == publicKey || Constants.SM2_PK_LEN != publicKey.length) {
            throw new JavaChainException("SM2KeyPair error: generate key pair failed.");
        }
        return new SM2KeyPair(publicKey, privateKey);
    }

    /**
     * sm2密钥对derive
     *
     * @return
     */
    public static byte[] SM2KeyDerive(byte[] key, int length) throws JavaChainException{
        //TODO: 待实现
        return null;
    }

    /**
     * 对数据进行签名
     *
     * @param hash 消息哈希值
     * @param priKey 私钥数据
     * @return 签名值
     */
    public static byte[] sign(byte[] hash, byte[] priKey) throws JavaChainException{
        if(null == hash || 0 == hash.length) {
            logger.error("Invalid hash. It must not be nil or empty.");
            throw new JavaChainException("Invalid hash. It must not be nil or empty.");
        }
        if(null == priKey || 0 == priKey.length) {
            logger.error("Invalid priKey. It must not be nil or empty.");
            throw new JavaChainException("Invalid priKey. It must not be nil or empty.");
        }
        byte[] result = null;
        try {
            byte[] random = smJniApi.randomGen(Constants.SM2_SIGN_RANDOM_LEN);
            result = smJniApi.sm2Sign(hash, random, priKey);
        } catch (Exception e) {
            logger.error( "SM2 sign data failed." );
            throw new JavaChainException("SM2 sign data failed.");
        }
        return result;
    }


    /**
     * 验证签名值
     *
     * @param hash 消息哈希值
     * @param pubKey 公钥数据
     * @param sign 签名值
     * @return
     */
    public static int verify(byte[] hash, byte[] pubKey, byte[] sign) throws JavaChainException {
        if(null == hash || 0 == hash.length) {
            logger.error("Invalid hash. It must not be nil or empty.");
            throw new JavaChainException("Invalid hash. It must not be nil or empty.");
        }
        if(null == pubKey || 0 == pubKey.length) {
            logger.error("Invalid pubKey. It must not be nil or empty.");
            throw new JavaChainException("Invalid pubKey. It must not be nil or empty.");
        }
        if(null == sign || 0 == sign.length) {
            logger.error("Invalid signature data. It must not be nil or empty.");
            throw new JavaChainException("Invalid signature data. It must not be nil or empty.");
        }
        int result = 1;
        try {
            result = smJniApi.sm2Verify(hash, pubKey, sign);
        } catch (Exception e) {
            logger.error( "SM2 verify signature failed." );
            throw new JavaChainException("SM2 verify signature failed.");
        }
        return result;
    }
}
