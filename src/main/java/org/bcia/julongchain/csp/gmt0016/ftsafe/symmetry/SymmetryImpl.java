/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;

/**
 * 对称密钥密码提供服务相关实现
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class SymmetryImpl {

    GMT0016CspLog csplog = new GMT0016CspLog();

    /**
     * 对称密钥加密
     * @param plaintext         数据原文
     * @param lKeyHandle        对称密钥句柄
     * @param blockCipherParam  BlockCipher 
     * @param opts              Gmt0016 factory
     * @return  密文数据
     * @throws CspException
     */
    public byte[] symmetryEncrypt(byte[] plaintext, long lKeyHandle, BlockCipherParam blockCipherParam, IGMT0016FactoryOpts opts) throws CspException {
        try {

            opts.getSKFFactory().SKF_EncryptInit(lKeyHandle, blockCipherParam);

            byte[] signature = opts.getSKFFactory().SKF_Encrypt(lKeyHandle, plaintext, plaintext.length);
            return signature;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, SymmetryImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, SymmetryImpl.class);
            throw new CspException(err, ex.getCause());
        }

    }

    /**
     * 对称密钥解密
     * @param ciphertext        密文数据
     * @param lKeyHandle        对称密钥句柄
     * @param blockCipherParam  BlockCipher 
     * @param opts              Gmt0016 factory
     * @return  Plaint text 数据原文
     * @throws CspException
     */
    public byte[] symmetryDecrypt(byte[] ciphertext, long lKeyHandle, BlockCipherParam blockCipherParam, IGMT0016FactoryOpts opts) throws CspException {
        try {

            opts.getSKFFactory().SKF_DecryptInit(lKeyHandle, blockCipherParam);

            byte[] signature = opts.getSKFFactory().SKF_Decrypt(lKeyHandle, ciphertext, ciphertext.length);
            return signature;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, SymmetryImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, SymmetryImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }
}
