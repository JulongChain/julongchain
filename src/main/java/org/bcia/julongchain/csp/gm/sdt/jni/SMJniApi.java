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
package org.bcia.julongchain.csp.gm.sdt.jni;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;

/**
 * JNI interface definition
 *
 * @author tengxiumin
 * @date 2018/04/24
 * @company SDT
 */
public class SMJniApi {

    //错误码定义
    private static final int SMJNIAPI_ERR_PARAM = 0x1001;
    private static final int SMJNIAPI_ERR_MAKEKEY = 0x1002;
    private static final int SMJNIAPI_ERR_SM2SIGN = 0x1003;
    private static final int SMJNIAPI_ERR_SM2VERIFY = 0x1004;
    private static final int SMJNIAPI_ERR_SM2ENC = 0x1005;
    private static final int SMJNIAPI_ERR_SM2DEC = 0x1006;
    private static final int SMJNIAPI_ERR_SM4ECBENC = 0x1007;
    private static final int SMJNIAPI_ERR_SM4ECBDEC = 0x1008;
    private static final int SMJNIAPI_ERR_SM4CBCENC = 0x1009;
    private static final int SMJNIAPI_ERR_SM4CBCDEC = 0x100A;

    static {
        try {
            System.loadLibrary("sdtsmjni");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成随机数
     * @param length 随机数长度
     * @return 随机数
     * @throws JavaChainException
     */
    public byte[] randomGen(int length) throws JavaChainException {
        if(0 >= length) {
            throw new JavaChainException("Invalid length. It must be bigger than 0.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.MAX_RANDOM_LENGTH < length) {
            String errorMsg = "Invalid length. It must be smaller than " + Integer.toString(Constants.MAX_RANDOM_LENGTH+1) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nRandomGen(length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * 根据SM2私钥生成公钥
     * @param privateKey SM2私钥
     * @return SM2 公钥
     * @throws JavaChainException
     */
    public byte[] sm2MakeKey(byte[] privateKey) throws JavaChainException {
        if(null == privateKey) {
            throw new JavaChainException("Invalid privateKey. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_PRIVATEKEY_LEN != privateKey.length) {
            String errorMsg = "Invalid privateKey. It's length must be " +
                                Integer.toString(Constants.SM2_PRIVATEKEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM2MakeKey(privateKey, privateKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                    new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM2密钥派生
     * @param key 原密钥
     * @param length 派生密钥长度
     * @return 派生密钥数据
     * @throws JavaChainException
     */
    public byte[] sm2KDF(byte[] key, int length) throws JavaChainException {
        if(null == key) {
            throw new JavaChainException("Invalid key. It must not be null.",
                                        new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == key.length) {
            throw new JavaChainException("Invalid key. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.MAX_KDF_SOURCE_KEY_LENGTH < key.length) {
            String errorMsg = "Invalid key. It's length must be smaller than " +
                    Integer.toString(Constants.MAX_KDF_SOURCE_KEY_LENGTH+1) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 >= length) {
            throw new JavaChainException("Invalid length. It must be bigger than 0.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.MAX_KDF_LENGTH < length) {
            String errorMsg = "Invalid length. It must be smaller than " +
                                Integer.toString(Constants.MAX_KDF_LENGTH+1) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            int keyLen = key.length;
            outData = nSM2KDF(key, keyLen, length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM2签名
     * @param digest 消息摘要
     * @param random 随机数
     * @param privateKey 私钥
     * @return 签名值
     * @throws JavaChainException
     */
    public byte[] sm2Sign(byte[] digest, byte[] random,
                          byte[] privateKey) throws JavaChainException {
        if(null == digest) {
            throw new JavaChainException("Invalid digest. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM3_DIGEST_LEN != digest.length) {
            String errorMsg = "Invalid digest. It's length must be " +
                    Integer.toString(Constants.SM3_DIGEST_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == random) {
            throw new JavaChainException("Invalid random. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_SIGN_RANDOM_LEN != random.length) {
            String errorMsg = "Invalid random. It's length must be " +
                    Integer.toString(Constants.SM2_SIGN_RANDOM_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == privateKey) {
            throw new JavaChainException("Invalid privateKey. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_PRIVATEKEY_LEN != privateKey.length) {
            String errorMsg = "Invalid privateKey. It's length must be " +
                    Integer.toString(Constants.SM2_PRIVATEKEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM2Sign(digest, digest.length,
                            random, random.length,
                            privateKey, privateKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM2验签
     * @param publicKey 公钥
     * @param signature 签名值
     * @return 验签结果
     * @throws JavaChainException
     */
    public int sm2Verify(byte[] digest, byte[] publicKey,
                            byte[] signature) throws JavaChainException {
        if(null == digest) {
            throw new JavaChainException("Invalid digest. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM3_DIGEST_LEN != digest.length) {
            String errorMsg = "Invalid digest. It's length must be " +
                    Integer.toString(Constants.SM3_DIGEST_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == publicKey) {
            throw new JavaChainException("Invalid publicKey. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_PUBLICKEY_LEN != publicKey.length) {
            String errorMsg = "Invalid publicKey. It's length must be " +
                    Integer.toString(Constants.SM2_PUBLICKEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == signature) {
            throw new JavaChainException("Invalid signature. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_SIGNATURE_LEN != signature.length) {
            String errorMsg = "Invalid signature. It's length must be " +
                    Integer.toString(Constants.SM2_SIGNATURE_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        int result = 1;
        try {
            result = nSM2Verify(digest, digest.length, publicKey, publicKey.length, signature, signature.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        if(0 != result) {
            throw new JavaChainException("Failed verifying signature.");
        }
        return result;
    }

    /**
     * SM2非对称加密
     * @param plainText 明文数据
     * @param random 随机数
     * @param publicKey 公钥
     * @return 密文数据
     * @throws JavaChainException
     */
    public byte[] sm2Encrypt(byte[] plainText, byte[] random,
                          byte[] publicKey) throws JavaChainException {
        if(null == plainText) {
            throw new JavaChainException("Invalid plainText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == plainText.length) {
            throw new JavaChainException("Invalid plainText. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == random) {
            throw new JavaChainException("Invalid random. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_ENC_RANDOM_LEN != random.length) {
            String errorMsg = "Invalid random. It's length must be " +
                    Integer.toString(Constants.SM2_ENC_RANDOM_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == publicKey) {
            throw new JavaChainException("Invalid publicKey. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_PUBLICKEY_LEN != publicKey.length) {
            String errorMsg = "Invalid publicKey. It's length must be " +
                    Integer.toString(Constants.SM2_PUBLICKEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM2Encrypt(plainText, plainText.length, random, random.length, publicKey, publicKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM2非对称解密
     * @param cipherText 密文数据
     * @param privateKey 私钥
     * @return 明文数据
     * @throws JavaChainException
     */
    public byte[] sm2Decrypt(byte[] cipherText, byte[] privateKey) throws JavaChainException {
        if(null == cipherText) {
            throw new JavaChainException("Invalid cipherText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == cipherText.length) {
            throw new JavaChainException("Invalid cipherText. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == privateKey) {
            throw new JavaChainException("Invalid privateKey. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM2_PRIVATEKEY_LEN != privateKey.length) {
            String errorMsg = "Invalid privateKey. It's length must be " +
                    Integer.toString(Constants.SM2_PRIVATEKEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM2Decrypt(cipherText, cipherText.length, privateKey, privateKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * 计算消息摘要
     * @param message 消息数据
     * @return 摘要数据
     * @throws JavaChainException
     */
    public byte[] sm3Hash(byte[] message) throws JavaChainException {
        if(null == message) {
            throw new JavaChainException("Invalid message. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == message.length) {
            throw new JavaChainException("Invalid message. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM3Hash(message, message.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM4 ECB 模式加密
     * @param key 密钥
     * @param plainText 明文数据
     * @return 密文数据
     * @throws JavaChainException
     */
    public byte[] sm4ECBEncrypt(byte[] key, byte[] plainText) throws JavaChainException {
        if(null == key) {
            throw new JavaChainException("Invalid key. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == plainText) {
            throw new JavaChainException("Invalid plainText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == plainText.length) {
            throw new JavaChainException("Invalid plainText. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM4ECBEncrypt(key, key.length, plainText, plainText.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM4 ECB 模式解密
     * @param key 密钥
     * @param cipherText 密文数据
     * @return 明文数据
     * @throws JavaChainException
     */
    public byte[] sm4ECBDecrypt(byte[] key, byte[] cipherText) throws JavaChainException {
        if(null == key) {
            throw new JavaChainException("Invalid key. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == cipherText) {
            throw new JavaChainException("Invalid cipherText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_BLOCK_LEN > cipherText.length) {
            String errorMsg = "Invalid cipherText. It's length must not be smaller than " +
                    Integer.toString(Constants.SM4_BLOCK_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM4ECBDecrypt(key, key.length, cipherText, cipherText.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return outData;
    }

    /**
     * SM4 CBC 模式加密
     * @param key 密钥
     * @param iv  初始向量
     * @param plainText 明文数据
     * @return 密文数据
     * @throws JavaChainException
     */
    public SM4CBCResult sm4CBCEncrypt(byte[] key, byte[] iv, byte[] plainText) throws JavaChainException {
        if(null == key) {
            throw new JavaChainException("Invalid key. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == iv) {
            throw new JavaChainException("Invalid iv. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            String errorMsg = "Invalid iv. It's length must be " +
                    Integer.toString(Constants.SM4_IV_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == plainText) {
            throw new JavaChainException("Invalid plainText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(0 == plainText.length) {
            throw new JavaChainException("Invalid plainText. Cannot be empty.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        try {
            byte[] outData  = nSM4CBCEncrypt(key, key.length, iv, iv.length, plainText, plainText.length);
            if(null != outData) {
                byte[] newIV = new byte[Constants.SM4_IV_LEN];
                byte[] cipherData = new byte[outData.length];
                System.arraycopy(outData, outData.length-Constants.SM4_IV_LEN, newIV, 0, Constants.SM4_IV_LEN);
                System.arraycopy(outData, 0, cipherData, 0, outData.length);
                return new SM4CBCResult(newIV, cipherData);
            }
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return null;
    }

    /**
     * SM4 CBC 模式解密
     * @param key 密钥
     * @param iv  初始向量
     * @param cipherText 密文数据
     * @return 明文数据
     * @throws JavaChainException
     */
    public SM4CBCResult sm4CBCDecrypt(byte[] key, byte[] iv, byte[] cipherText) throws JavaChainException {
        if(null == key) {
            throw new JavaChainException("Invalid key. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == iv) {
            throw new JavaChainException("Invalid iv. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            String errorMsg = "Invalid iv. It's length must be " +
                    Integer.toString(Constants.SM4_IV_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(null == cipherText) {
            throw new JavaChainException("Invalid cipherText. It must not be null.",
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        if(Constants.SM4_BLOCK_LEN > cipherText.length) {
            String errorMsg = "Invalid cipherText. It's length must not be smaller than " +
                    Integer.toString(Constants.SM4_BLOCK_LEN) + ".";
            throw new JavaChainException(errorMsg,
                    new Exception("Error code: " + Integer.toString(SMJNIAPI_ERR_PARAM)));
        }
        byte[] outData = null;
        try {
            outData = nSM4CBCDecrypt(key, key.length, iv, iv.length, cipherText, cipherText.length);
            if(null != outData) {
                byte[] newIV = new byte[Constants.SM4_IV_LEN];
                byte[] plainData = new byte[outData.length];
                System.arraycopy(cipherText, cipherText.length-Constants.SM4_IV_LEN, newIV, 0, Constants.SM4_IV_LEN);
                System.arraycopy(outData, 0, plainData, 0, outData.length);
                return new SM4CBCResult(newIV, plainData);
            }
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())),
                                        new Exception("Error code: " + e.getMessage()));
        }
        return null;
    }

    /**
     * 根据错误码获取错误信息
     * @param errorCode 错误码
     * @return 错误信息
     */
    private String getErrorMsg(int errorCode) {
        String description;
        switch(errorCode) {
            case SMJNIAPI_ERR_PARAM:
            {
                description = "Invalid input parameters.";
                break;
            }
            case SMJNIAPI_ERR_MAKEKEY:
            {
                description = "Failed generating sm2 public key.";
                break;
            }
            case SMJNIAPI_ERR_SM2SIGN:
            {
                description = "Failed signing data.";
                break;
            }
            case SMJNIAPI_ERR_SM2VERIFY:
            {
                description = "Failed verifying signature.";
                break;
            }
            case SMJNIAPI_ERR_SM2ENC:
            {
                description = "Failed encrypting data with SM2 public key.";
                break;
            }
            case SMJNIAPI_ERR_SM2DEC:
            {
                description = "Failed decrypting data with SM2 private key.";
                break;
            }
            case SMJNIAPI_ERR_SM4ECBENC:
            {
                description = "Failed encrypting data with SM4 ECB mode.";
                break;
            }
            case SMJNIAPI_ERR_SM4ECBDEC:
            {
                description = "Failed decrypting data with SM4 ECB mode.";
                break;
            }
            case SMJNIAPI_ERR_SM4CBCENC:
            {
                description = "Failed encrypting data with SM4 CBC mode.";
                break;
            }
            case SMJNIAPI_ERR_SM4CBCDEC:
            {
                description = "Failed decrypting data with SM4 CBC mode.";
                break;
            }
            default:
            {
                description = "Unknown error.";
                break;
            }
        }
        return description;
    }

    private native byte[] nRandomGen(int length);
    private native byte[] nSM2MakeKey(byte[] key, int keyLen);
    private native byte[] nSM2KDF(byte[] key, int keyLen, int length);
    private native byte[] nSM2Sign(byte[] hash, int hashLen,
                                  byte[] random, int randomLen,
                                  byte[] priKey, int priKeyLen);
    private native int nSM2Verify(byte[] hash, int hashLen,
                                    byte[] pubKey, int pubKeyLen,
                                    byte[] sign, int signLen);
    private native byte[] nSM2Encrypt(byte[] plainData, int plainDataLen,
                                   byte[] random, int randomLen,
                                   byte[] pubKey, int pubKeyLen);
    private native byte[] nSM2Decrypt(byte[] cipherData, int cipherDataLen,
                                  byte[] priKey, int priKeyLen);
    private native byte[] nSM3Hash(byte[] message, int messageLen);
    private native byte[] nSM4ECBEncrypt(byte[] key, int keyLen,
                                      byte[] plainData, int plainDataLen);
    private native byte[] nSM4ECBDecrypt(byte[] key, int keyLen,
                                          byte[] cipherData, int cipherDataLen);
    private native byte[] nSM4CBCEncrypt(byte[] key, int keyLen,
                                       byte[] iv, int ivLen,
                                       byte[] plainData, int plainDataLen);
    private native byte[] nSM4CBCDecrypt(byte[] key, int keyLen,
                                      byte[] iv, int ivLen,
                                      byte[] cipherData, int cipherDataLen);
}
