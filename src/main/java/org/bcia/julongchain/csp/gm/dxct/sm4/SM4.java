package org.bcia.julongchain.csp.gm.dxct.sm4; /**
 * Copyright DingXuan. All Rights Reserved.
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

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.SecureRandom;

/**
 * @author zhangmingyang
 * @Date: 2018/4/26
 * @company Dingxuan
 */
public class SM4 {
    /**
     * 加密类型
     */
    public static final int SM4_ENCRYPT = 1;
    /**
     * 解密类型
     */
    public static final int SM4_DECRYPT = 0;


    /**
     * sm4密钥生成
     *
     * @return
     */
    public  static byte[] generateKey() {
        KeyGenerationParameters param = new KeyGenerationParameters(new SecureRandom(), 128);
        CipherKeyGenerator cipherKeyGenerator = new CipherKeyGenerator();
        cipherKeyGenerator.init(param);
        byte[] sm4key = cipherKeyGenerator.generateKey();
        return sm4key;
    }

    /**
     * 数据填充、去填充
     *
     * @param input
     * @param mode
     * @return
     */
    private static byte[] padding(byte[] input, int mode) {
        if (input == null) {
            return null;
        }

        byte[] ret = (byte[]) null;
        if (mode == SM4_ENCRYPT) {
            int p = 16 - input.length % 16;
            ret = new byte[input.length + p];
            System.arraycopy(input, 0, ret, 0, input.length);
            for (int i = 0; i < p; i++) {
                ret[input.length + i] = (byte) p;
            }
        } else {
            int p = input[input.length - 1];
            ret = new byte[input.length - p];
            System.arraycopy(input, 0, ret, 0, input.length - p);
        }
        return ret;
    }


    /**
     * ECB模式对数据进行加密
     *
     * @param plainText
     * @return
     */

    public byte[] encryptECB(byte[] plainText,byte[] sm4key) {
        byte[] paddingData = padding(plainText, SM4_ENCRYPT);
        byte[] output = processData(paddingData,sm4key,SM4_ENCRYPT);
        return output;
    }

    /**
     * 对加密的数据进行解密
     *
     * @param encryptData
     * @return
     */
    public byte[] decryptECB(byte[] encryptData,byte[] sm4Key) {
        byte[] output = processData(encryptData,sm4Key,SM4_DECRYPT);
        byte[] decrypt = padding(output, SM4_DECRYPT);
        return decrypt;

    }

    /**
     * 处理消息原文或加密消息
     *
     * @param data
     * @return
     */
    private static byte[] processData(byte[] data,byte[] sm4Key,int mode) {
        int length = data.length;
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        SM4Engine engine = new SM4Engine();
        if(mode==SM4_ENCRYPT){
            engine.init(true, new KeyParameter(sm4Key));
        }else{
            engine.init(false, new KeyParameter(sm4Key));
        }
        for (; length > 0; length -= 16) {


            byte[] buf = new byte[16];
            System.arraycopy(data, 0, buf, 0, buf.length);
            try {
                bins.read(buf);
                engine.processBlock(buf, 0, buf, 0);
                bous.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] output = bous.toByteArray();
        return output;
    }
}
