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
package org.bcia.julongchain.csp.gm.sdt.SM4;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SM4CBCResult;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM4 algorithm
 *
 * @author tengxiumin
 * @date 2018/05/14
 * @company SDT
 */
public class SM4 {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog( SM4.class );
    private static final SMJniApi smJniApi = new SMJniApi();

    private static final int TYPE_ENCRYPT = 0;
    private static final int TYPE_DECRYPT = 1;

    /**
     * sm4密钥生成
     *
     * @return 密钥
     */
    public static byte[] generateKey() throws JavaChainException{
        byte[] result = null;
        try {
            result = smJniApi.randomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new JavaChainException("SM4KeyGen error: generate random failed.");
        }
        return result;
    }

    /**
     * ECB模式对数据进行加密
     *
     * @param plainData 明文数据
     * @param sm4Key 密钥
     * @return 密文数据
     */
    public byte[] encryptECB(byte[] plainData, byte[] sm4Key) throws JavaChainException{
        if(null == plainData || 0 == plainData.length) {
            logger.error("Invalid plainData. It must not be nil or empty.");
            throw new JavaChainException("Invalid plainData. It must not be nil or empty.");
        }
        if(null == sm4Key) {
            logger.error("Invalid key. It must not be nil.");
            throw new JavaChainException("Invalid key. It must not be nil.");
        }
        //填充数据
        byte[] paddingData = padding(plainData);
        return proccessDataECB(paddingData, sm4Key, TYPE_ENCRYPT);
    }

    /**
     * ECB模式对密文数据进行解密
     *
     * @param cipherData 密文数据
     * @param sm4Key 密钥
     * @return 明文数据
     */
    public byte[] decryptECB(byte[] cipherData, byte[] sm4Key) throws JavaChainException {
        if(null == cipherData || 0 == cipherData.length) {
            logger.error("Invalid cipherData. It must not be nil or empty.");
            throw new JavaChainException("Invalid cipherData. It must not be nil or empty.");
        }
        if(null == sm4Key) {
            logger.error("Invalid key. It must not be nil.");
            throw new JavaChainException("Invalid key. It must not be nil.");
        }
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] plainData = proccessDataECB(cipherData, sm4Key, TYPE_DECRYPT);
        //去填充
        byte[] result = unpadding(plainData);
        return result;
    }

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
                throw new JavaChainException("Encrypt data with SM4 ECB mode failed.");
            }

            if(null != tmpDestData) {
                System.arraycopy(tmpDestData, 0, result, result.length-leftLength, dataLength);
                leftLength = leftLength-dataLength;
            } else {
                logger.error("Encrypt data with SM4 ECB mode failed.");
                throw new JavaChainException("Encrypt data with SM4 ECB mode failed.");
            }
        }
        return result;
    }

    /**
     * CBC模式对数据进行加密
     *
     * @param plainData 明文数据
     * @param sm4Key 密钥
     * @param iv 初始化向量
     * @return 密文数据
     */
    public byte[] encryptCBC(byte[] plainData, byte[] sm4Key, byte[] iv) throws JavaChainException{
        if(null == plainData || 0 == plainData.length) {
            logger.error("Invalid plainData. It must not be nil or empty.");
            throw new JavaChainException("Invalid plainData. It must not be nil or empty.");
        }
        if(null == sm4Key || 0 == sm4Key.length) {
            logger.error("Invalid key. It must not be nil or empty.");
            throw new JavaChainException("Invalid key. It must not be nil or empty.");
        }
        if(null == iv || 0 == iv.length) {
            logger.error("Invalid iv. It must not be nil or empty.");
            throw new JavaChainException("Invalid iv. It must not be nil or empty.");
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            logger.error("Invalid iv length.");
            throw new JavaChainException("Invalid iv length.");
        }
        //填充数据
        byte[] paddingData = padding(plainData);
        return proccessDataCBC(paddingData, sm4Key, iv, TYPE_ENCRYPT);
    }

    /**
     * CBC模式对密文数据进行解密
     *
     * @param cipherData 密文数据
     * @param sm4Key 密钥
     * @param iv 初始化向量
     * @return 明文数据
     */
    public byte[] decryptCBC(byte[] cipherData, byte[] sm4Key, byte[] iv) throws JavaChainException{
        if(null == cipherData || 0 == cipherData.length) {
            logger.error("Invalid cipherData. It must not be nil or empty.");
            throw new JavaChainException("Invalid cipherData. It must not be nil or empty.");
        }
        if(null == sm4Key || 0 == sm4Key.length) {
            logger.error("Invalid key. It must not be nil or empty.");
            throw new JavaChainException("Invalid key. It must not be nil or empty.");
        }
        if(null == iv || 0 == iv.length) {
            logger.error("Invalid iv. It must not be nil or empty.");
            throw new JavaChainException("Invalid iv. It must not be nil or empty.");
        }
        if(Constants.SM4_IV_LEN != iv.length) {
            logger.error("Invalid iv length.");
            throw new JavaChainException("Invalid iv length.");
        }
        //分包处理(每包长度为Constants.SM4_PACKAGE_LEN)
        byte[] plainData = proccessDataCBC(cipherData, sm4Key, iv, TYPE_DECRYPT);
        //去填充
        byte[] result = unpadding(plainData);
        return result;
    }

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
                throw new JavaChainException("Encrypt data with SM4 CBC mode failed.");
            }

            if(null != tmpResult) {
                System.arraycopy(tmpResult.getIv(), 0, tmpIv, 0, Constants.SM4_IV_LEN);
                System.arraycopy(tmpResult.getData(), 0, result, result.length-leftLength, dataLength);
                leftLength = leftLength-dataLength;
            } else {
                logger.error("Encrypt data with SM4 CBC mode failed.");
                throw new JavaChainException("Encrypt SM4 data with SM4 CBC mode failed.");
            }
        }
        return result;
    }

    /**
     * 数据填充
     *
     * @param input
     * @return
     */
    private byte[] padding(byte[] input) throws JavaChainException{
        if (null == input) {
            throw new JavaChainException("Invalid input. It must not be nil.");
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
     *
     * @param input
     * @return
     */
    private byte[] unpadding(byte[] input) throws JavaChainException{
        if (null == input || 1 > input.length) {
            throw new JavaChainException("Invalid input. It must not be nil.");
        }
        int padLen = input[input.length - 1];
        if(padLen > input.length) {
            throw new JavaChainException("Invalid input, input length error.");
        }
        byte[] buff = new byte[input.length - padLen];
        System.arraycopy(input, 0, buff, 0, input.length - padLen);
        return buff;
    }
}
