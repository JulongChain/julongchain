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

    private JavaChainLog logger = new JavaChainLog();

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

    public byte[] randomGen(int length) throws Exception {
        if(0 > length) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nRandomGen(length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm2MakeKey(byte[] sk) throws Exception {
        if(null == sk) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM2MakeKey(sk, sk.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm2KDF(byte[] key, int length) throws Exception {
        if(null == key || 0 > length) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            int keyLen = key.length;
            outData = nSM2KDF(key, keyLen, length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm2Sign(byte[] hash, byte[] random,
                          byte[] priKey) throws Exception {
        if(null == hash || null == random || null == priKey) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM2Sign(hash, hash.length,
                            random, random.length,
                            priKey, priKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public int sm2Verify(byte[] hash, byte[] pubKey,
                            byte[] sign) throws Exception {
        if(null == hash || null == pubKey ||  null == sign) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        int result = 1;
        try {
            result = nSM2Verify(hash, hash.length, pubKey, pubKey.length, sign, sign.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        if(0 != result) {
            throw new JavaChainException(getErrorMsg(result));
        }
        return result;
    }

    public byte[] sm2Encrypt(byte[] plainData, byte[] random,
                          byte[] pubKey) throws Exception {
        if(null == plainData || null == random || null == pubKey) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM2Encrypt(plainData, plainData.length, random, random.length, pubKey, pubKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm2Decrypt(byte[] cipherData, byte[] priKey) throws Exception {
        if(null == cipherData || null == priKey) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM2Decrypt(cipherData, cipherData.length, priKey, priKey.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm3Hash(byte[] message) throws Exception {
        if(null == message) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM3Hash(message, message.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm4ECBEncrypt(byte[] key, byte[] plainData) throws JavaChainException {
        if(null == key || null == plainData) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM4ECBEncrypt(key, key.length, plainData, plainData.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] sm4ECBDecrypt(byte[] key, byte[] cipherData) throws JavaChainException {
        if(null == key || null == cipherData) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM4ECBDecrypt(key, key.length, cipherData, cipherData.length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public SM4CBCResult sm4CBCEncrypt(byte[] key, byte[] iv, byte[] plainData) throws JavaChainException {
        if(null == key || null == iv || null == plainData) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        try {
            byte[] outData  = nSM4CBCEncrypt(key, key.length, iv, iv.length, plainData, plainData.length);
            if(null != outData) {
                byte[] newIV = new byte[Constants.SM4_IV_LEN];
                byte[] cipherData = new byte[outData.length];
                System.arraycopy(outData, outData.length-Constants.SM4_IV_LEN, newIV, 0, Constants.SM4_IV_LEN);
                System.arraycopy(outData, 0, cipherData, 0, outData.length);
                return new SM4CBCResult(newIV, cipherData);
            }
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return null;
    }

    public SM4CBCResult sm4CBCDecrypt(byte[] key, byte[] iv, byte[] cipherData) throws JavaChainException {
        if(null == key || null == iv || null == cipherData) {
            throw new JavaChainException(getErrorMsg(SMJNIAPI_ERR_PARAM));
        }
        byte[] outData = null;
        try {
            outData = nSM4CBCDecrypt(key, key.length, iv, iv.length, cipherData, cipherData.length);
            if(null != outData) {
                byte[] newIV = new byte[Constants.SM4_IV_LEN];
                byte[] plainData = new byte[outData.length];
                System.arraycopy(outData, outData.length-Constants.SM4_IV_LEN, newIV, 0, Constants.SM4_IV_LEN);
                System.arraycopy(outData, 0, plainData, 0, outData.length);
                return new SM4CBCResult(newIV, plainData);
            }
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return null;
    }

    private String getErrorMsg(int errorCode) {
        String description;
        switch(errorCode) {
            case SMJNIAPI_ERR_PARAM:
            {
                description = "input parameter error";
                break;
            }
            case SMJNIAPI_ERR_MAKEKEY:
            {
                description = "generate SM2 public key error";
                break;
            }
            case SMJNIAPI_ERR_SM2SIGN:
            {
                description = "SM2 private key encrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM2VERIFY:
            {
                description = "verify signature failed";
                break;
            }
            case SMJNIAPI_ERR_SM2ENC:
            {
                description = "SM2 public key encrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM2DEC:
            {
                description = "SM2 private key decrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM4ECBENC:
            {
                description = "SM4 ECB mode encrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM4ECBDEC:
            {
                description = "SM4 ECB mode decrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM4CBCENC:
            {
                description = "SM4 CBC mode encrypt error";
                break;
            }
            case SMJNIAPI_ERR_SM4CBCDEC:
            {
                description = "SM4 CBC mode decrypt error";
                break;
            }
            default:
            {
                description = "unknown error code";
                break;
            }
        }
        String message = String.format("Error code: %d, Error description: %s",
                                        errorCode, description);
        return message;
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
