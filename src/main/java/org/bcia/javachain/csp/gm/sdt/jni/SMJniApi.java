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
package org.bcia.javachain.csp.gm.sdt.jni;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;

/**
 * JNI interface definition
 *
 * @author tengxiumin
 * @date 4/24/18
 * @company SDT
 */
public class SMJniApi {

    private JavaChainLog logger = new JavaChainLog();

    private static final int SMJNIAPI_ERR_PARAM = 0x1001;

    static {
        try {
            System.loadLibrary("sdtsmjni");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public byte[] RandomGen(int length) throws Exception {
        byte[] outData = null;
        try {
            outData = nRandomGen(length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] SM2KeyGen() throws Exception {
        byte[] outData = null;
        try {
            outData = nSM2KeyGen();
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] KeyDerive(byte[] key, int length) throws Exception {
        byte[] outData = null;
        try {
            int keyLen = key.length;
            outData = nKeyDerive(key, keyLen, length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] SM3Hash(byte[] message) throws Exception {
        byte[] outData = null;
        try {
            int messageLen = message.length;
            outData = nSM3Hash(message, messageLen);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] EccSign(byte[] hash, int hashLen,
                          byte[] priKey, int priKeyLen) throws Exception {
        byte[] outData = null;
        try {
            outData = nEccSign(hash, hashLen, priKey, priKeyLen);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public int EccVerify(byte[] hash, int hashLen,
                            byte[] pubKey, int pubKeyLen,
                            byte[] sign, int signLen) throws Exception {
        int result = 1;
        try {
            result = nEccVerify(hash, hashLen, pubKey, pubKeyLen, sign, signLen);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return result;
    }

    public byte[] SymmEncrypt(byte[] key, int keyLen,
                            int algMode,
                            byte[] iv, int ivLen,
                            byte[] plainData, int plainDataLen) throws Exception {
        byte[] outData = null;
        try {
            outData = nSymmEncrypt(key, keyLen, algMode, iv, ivLen, plainData, plainDataLen);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    public byte[] SymmDecrypt(byte[] key, int keyLen,
                              int algMode,
                              byte[] iv, int ivLen,
                              byte[] cipherData, int cipherDataLen) throws Exception {
        byte[] outData = null;
        try {
            outData = nSymmDecrypt(key, keyLen, algMode, iv, ivLen, cipherData, cipherDataLen);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    private String getErrorMsg(int errorCode) {
        String description;
        switch(errorCode) {
            case SMJNIAPI_ERR_PARAM:
            {
                description = "input parameter error";
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
    private native byte[] nSM2KeyGen();
    private native byte[] nKeyDerive(byte[] key, int keyLen, int length);
    private native byte[] nSM3Hash(byte[] message, int messageLen);
    private native byte[] nEccSign(byte[] hash, int hashLen,
                                  byte[] priKey, int priKeyLen);
    private native int nEccVerify(byte[] hash, int hashLen,
                                    byte[] pubKey, int pubKeyLen,
                                    byte[] sign, int signLen);
    private native byte[] nSymmEncrypt(byte[] key, int keyLen,
                                      int algMode,
                                      byte[] iv, int ivLen,
                                      byte[] plainData, int plainDataLen);
    private native byte[] nSymmDecrypt(byte[] key, int keyLen,
                                      int algMode,
                                      byte[] iv, int ivLen,
                                      byte[] cipherData, int cipherDataLen);
}
