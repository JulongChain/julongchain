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
package org.bcia.julongchain.csp.gm.sdt.sm2;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM2算法
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */

public class SM2 {

    private static JulongChainLog logger = JulongChainLogFactory.getLog(SM2.class);
    private static SMJniApi smJniApi = new SMJniApi();

    public SM2() { }

    /**
     * 生成SM2公私钥对
     * @return SM2KeyPair SM2密钥对
     * @throws CspException
     */
    public SM2KeyPair generateKeyPair() throws CspException {

        byte[] privateKey = new byte[Constants.SM2_PRIVATEKEY_LEN];
        byte[] publicKey = new byte[Constants.SM2_PUBLICKEY_LEN];
        try {
            privateKey = smJniApi.randomGen(Constants.SM2_PRIVATEKEY_LEN);
            publicKey = smJniApi.sm2MakeKey(privateKey);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CspException(e.getMessage());
        }

        if (null == privateKey || Constants.SM2_PRIVATEKEY_LEN != privateKey.length
            || null == publicKey || Constants.SM2_PUBLICKEY_LEN != publicKey.length) {
            throw new CspException("Failed generating SM2 key pair.");
        }
        return new SM2KeyPair(publicKey, privateKey);
    }

    /**
     * SM2密钥派生
     * @param key 原密钥
     * @param length 派生密钥长度
     * @return 派生密钥数据
     * @throws CspException
     */
    public byte[] keyDerive(byte[] key, int length) throws CspException {
        //TODO: 待实现
        return null;
    }

    /**
     * SM2签名
     * @param digest 消息摘要
     * @param privateKey 私钥
     * @return 签名值
     * @throws CspException
     */
    public byte[] sign(byte[] digest, byte[] privateKey) throws CspException {
        byte[] result = null;
        try {
            byte[] random = smJniApi.randomGen(Constants.SM2_SIGN_RANDOM_LEN);
            result = smJniApi.sm2Sign(digest, random, privateKey);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CspException(e.getMessage());
        }
        return result;
    }

    /**
     * SM2验签
     * @param digest 消息摘要
     * @param publicKey 公钥
     * @param signature 签名值
     * @return 验签结果
     * @throws CspException
     */
    public int verify(byte[] digest, byte[] publicKey, byte[] signature) throws CspException {
        int result = 1;
        try {
            result = smJniApi.sm2Verify(digest, publicKey, signature);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CspException(e.getMessage());
        }
        return result;
    }
}