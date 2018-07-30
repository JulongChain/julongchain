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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SymmetryImpl {

    GMT0016CspLog csplog = new GMT0016CspLog();

    public byte[] SymmetryEncrypt(byte[] plaintext, long lKeyHandle, BlockCipherParam blockCipherParam, IGMT0016FactoryOpts opts) throws JavaChainException {
        try {

            opts.getSKFFactory().SKF_EncryptInit(lKeyHandle, blockCipherParam);

            byte[] signature = opts.getSKFFactory().SKF_Encrypt(lKeyHandle, plaintext, plaintext.length);
            return signature;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, SymmetryImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, SymmetryImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }

    }

    public byte[] SymmetryDecrypt(byte[] ciphertext, long lKeyHandle, BlockCipherParam blockCipherParam, IGMT0016FactoryOpts opts) throws JavaChainException {
        try {

            opts.getSKFFactory().SKF_DecryptInit(lKeyHandle, blockCipherParam);

            byte[] signature = opts.getSKFFactory().SKF_Decrypt(lKeyHandle, ciphertext, ciphertext.length);
            return signature;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, SymmetryImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, SymmetryImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }
    }
}
