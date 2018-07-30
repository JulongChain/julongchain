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
package org.bcia.julongchain.csp.gm.sdt.sm4;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SM4CBCResult;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM4 算法
 *
 * @author tengxiumin
 * @date 2018/05/14
 * @company SDT
 */
public class SM4 {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(SM4.class);
    private static SMJniApi smJniApi = new SMJniApi();

    private static final int TYPE_ENCRYPT = 0;
    private static final int TYPE_DECRYPT = 1;

    /**
     * 生成sm4密钥
     * @return sm4密钥
     * @throws JavaChainException
     */
    public byte[] generateKey() throws JavaChainException{
        byte[] result = null;
        try {
            result = smJniApi.randomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new JavaChainException(e.getMessage());
        }
        return result;
    }

    /**
     * SM4 ECB模式加密
     * @param plainText 明文数据
     * @param key 密钥
     * @return 密文数据
     * @throws JavaChainException
     */
    public byte[] encryptECB(byte[] plainText, byte[] key) throws JavaChainException {
        if(null == plainText) {
            logger.error("Invalid plainText. It must not be null.");
            throw new JavaChainException("Invalid plainText. It must not be null.");
        }
        if(0 == plainText.length) {
            logger.error("Invalid plainText. Cannot be empty.");
            throw new JavaChainException("Invalid plainText. Cannot be empty.");
        }
        if(null == key) {
            logger.error("Invalid key. It must not be null.");
            throw new JavaChainException("Invalid key. It must not be null.");
        }
        //填充数据
        byte[] paddingData = padding(plainText);
        return proccessDataECB(paddingData, key, TYPE_ENCRYPT);
    }

    /**
     * SM4 ECB模式解密
     * @param cipherText 密文数据
     * @param key 密钥
     * @return 明文数据
     * @throws JavaChainException
     */
    public byte[] decryptECB(byte[] cipherText, byte[] key) throws JavaChainException {
        if(null == cipherText) {
            logger.error("Invalid cipherText. It must not be null.");
            throw new JavaChainException("Invalid cipherText. It must not be null.");
        }
        if(Constants.SM4_BLOCK_LEN > cipherText.length) {
            String errorMsg = "Invalid cipherText. It's length cannot be smaller than " +
                    Integer.toString(Constants.SM4_BLOCK_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        if(null == key) {
            logger.error("Invalid key. It must not be null.");
            throw new JavaChainException("Invalid key. It must not be null.");
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] plainText = proccessDataECB(cipherText, key, TYPE_DECRYPT);
        //去填充
        byte[] result = unpadding(plainText);
        return result;
    }

    /**
     * ECB模式加解密数据处理接口
     * @param data 数据
     * @param key 密钥
     * @param type 类型（加密/解密）
     * @return
     * @throws JavaChainException
     */
    private byte[] proccessDataECB(byte[] data, byte[] key, int type) throws JavaChainException {
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] result = new byte[data.length];
        int leftLength = data.length;
        while(leftLength > 0) {
            int dataLength = leftLength;
            if(dataLength > Constants.SM4_PACKAGE_LEN) {
                dataLength = Constants.SM4_PACKAGE_LEN;
            }
            byte[] tmpSrcData = new byte[dataLength];
            System.arraycopy(data, data.length-leftLength, tmpSrcData, 0, dataLength);
            byte[] tmpDestData = null;
            try {
                switch (type) {
                    case TYPE_ENCRYPT:
                    {
                        tmpDestData = smJniApi.sm4ECBEncrypt(key, tmpSrcData);
                        break;
                    }
                    case TYPE_DECRYPT:
                    {
                        tmpDestData = smJniApi.sm4ECBDecrypt(key, tmpSrcData);
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new JavaChainException(e.getMessage());
            }

            if(null != tmpDestData) {
                System.arraycopy(tmpDestData, 0, result, result.length-leftLength, dataLength);
                leftLength = leftLength-dataLength;
            } else {
                String errorMsg = "Failed ";
                if(type == TYPE_ENCRYPT) {
                    errorMsg = errorMsg + "encrypting data with SM4 ECB mode";
                } else {
                    errorMsg = errorMsg + "decrypting data with SM4 ECB mode";
                }
                logger.error(errorMsg);
                throw new JavaChainException(errorMsg);
            }
        }
        return result;
    }

    /**
     * SM4 CBC模式加密
     * @param plainText 明文数据
     * @param key 密钥
     * @param iv 初始向量
     * @return 密文数据
     * @throws JavaChainException
     */
    public byte[] encryptCBC(byte[] plainText, byte[] key, byte[] iv) throws JavaChainException {
        if(null == plainText) {
            logger.error("Invalid plainText. It must not be null.");
            throw new JavaChainException("Invalid plainText. It must not be null.");
        }
        if(0 == plainText.length) {
            logger.error("Invalid plainText. Cannot be empty.");
            throw new JavaChainException("Invalid plainText. Cannot be empty.");
        }
        if(null == key) {
            logger.error("Invalid key. It must not be null.");
            throw new JavaChainException("Invalid key. It must not be null.");
        }
        if(null == iv) {
            logger.error("Invalid iv. It must not be null.");
            throw new JavaChainException("Invalid iv. It must not be null.");
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            String errorMsg = "Invalid iv. It's length must be " +
                    Integer.toString(Constants.SM4_IV_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        //填充数据
        byte[] paddingData = padding(plainText);
        return proccessDataCBC(paddingData, key, iv, TYPE_ENCRYPT);
    }

    /**
     * SM4 CBC模式解密
     * @param cipherText 密文数据
     * @param key 密钥
     * @param iv 初始向量
     * @return 明文数据
     * @throws JavaChainException
     */
    public byte[] decryptCBC(byte[] cipherText, byte[] key, byte[] iv) throws JavaChainException{
        if(null == cipherText) {
            logger.error("Invalid cipherText. It must not be null.");
            throw new JavaChainException("Invalid cipherText. It must not be null.");
        }
        if(Constants.SM4_BLOCK_LEN > cipherText.length) {
            String errorMsg = "Invalid cipherText. It's length cannot be smaller than " +
                                Integer.toString(Constants.SM4_BLOCK_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        if(null == key) {
            logger.error("Invalid key. It must not be null.");
            throw new JavaChainException("Invalid key. It must not be null.");
        }
        if(Constants.SM4_KEY_LEN != key.length) {
            String errorMsg = "Invalid key. It's length must be " +
                    Integer.toString(Constants.SM4_KEY_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        if(null == iv) {
            logger.error("Invalid iv. It must not be null.");
            throw new JavaChainException("Invalid iv. It must not be null.");
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            String errorMsg = "Invalid iv. It's length must be " +
                    Integer.toString(Constants.SM4_IV_LEN) + ".";
            logger.error(errorMsg);
            throw new JavaChainException(errorMsg);
        }
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] plainText = proccessDataCBC(cipherText, key, iv, TYPE_DECRYPT);
        //去填充
        byte[] result = unpadding(plainText);
        return result;
    }

    /**
     * CBC模式加解密数据处理接口
     * @param data 数据
     * @param key 密钥
     * @param iv 初始向量
     * @param type 类型（加密/解密）
     * @return
     * @throws JavaChainException
     */
    private byte[] proccessDataCBC(byte[] data, byte[] key, byte[] iv, int type) throws JavaChainException{
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] result = new byte[data.length];
        int leftLength = data.length;
        byte[] tmpIv = new byte[Constants.SM4_IV_LEN];
        System.arraycopy(iv, 0, tmpIv, 0, Constants.SM4_IV_LEN);
        while(leftLength > 0) {
            int dataLength = leftLength;
            if(dataLength > Constants.SM4_PACKAGE_LEN) {
                dataLength = Constants.SM4_PACKAGE_LEN;
            }
            byte[] tmpSrcData = new byte[dataLength];
            System.arraycopy(data, data.length-leftLength, tmpSrcData, 0, dataLength);
            SM4CBCResult tmpResult = null;
            try {
                switch (type) {
                    case TYPE_ENCRYPT:
                    {
                        tmpResult = smJniApi.sm4CBCEncrypt(key, tmpIv, tmpSrcData);
                        break;
                    }
                    case TYPE_DECRYPT:
                    {
                        tmpResult = smJniApi.sm4CBCDecrypt(key, tmpIv, tmpSrcData);
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new JavaChainException(e.getMessage());
            }

            if(null != tmpResult) {
                System.arraycopy(tmpResult.getIv(), 0, tmpIv, 0, Constants.SM4_IV_LEN);
                System.arraycopy(tmpResult.getData(), 0, result, result.length-leftLength, dataLength);
                leftLength = leftLength-dataLength;
            } else {
                String errorMsg = "Failed ";
                if(type == TYPE_ENCRYPT) {
                    errorMsg = errorMsg + "encrypting data with SM4 CBC mode";
                } else {
                    errorMsg = errorMsg + "decrypting data with SM4 CBC mode";
                }
                logger.error(errorMsg);
                throw new JavaChainException(errorMsg);
            }
        }
        return result;
    }

    /**
     * 数据填充（填充后数据长度为Constants.SM4_BLOCK_LEN的整数倍）
     * @param input 待填充数据
     * @return 填充后的数据
     * @throws JavaChainException
     */
    private byte[] padding(byte[] input) throws JavaChainException{
        if (null == input) {
            throw new JavaChainException("Invalid input. It must not be null.");
        }
        int padLen = Constants.SM4_BLOCK_LEN - input.length % Constants.SM4_BLOCK_LEN;
        byte[] buff = new byte[input.length + padLen];
        System.arraycopy(input, 0, buff, 0, input.length);
        for(int i = 0; i < padLen; i++) {
            buff[input.length + i] = (byte) padLen;
        }
        return buff;
    }

    /**
     * 数据去填充
     * @param input 填充后的数据
     * @return 原数据
     * @throws JavaChainException
     */
    private byte[] unpadding(byte[] input) throws JavaChainException{
        if (null == input) {
            throw new JavaChainException("Invalid input. It must not be null.");
        }
        if (1 > input.length) {
            throw new JavaChainException("Invalid input. It's length must be bigger than 1.");
        }
        int padLen = input[input.length - 1];
        if(padLen > input.length) {
            throw new JavaChainException("Invalid input. It's padding length is bigger than the total data length.");
        }
        byte[] buff = new byte[input.length - padLen];
        System.arraycopy(input, 0, buff, 0, input.length - padLen);
        return buff;
    }
}
