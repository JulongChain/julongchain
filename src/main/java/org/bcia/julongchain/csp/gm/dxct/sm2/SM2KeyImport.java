/**
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
package org.bcia.julongchain.csp.gm.dxct.sm2;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bcia.julongchain.csp.intfs.IKey;

import java.security.NoSuchAlgorithmException;

/**
 * @author zhangmingyang
 * @Date: 2018/5/4
 * @company Dingxuan
 */
public class SM2KeyImport implements IKey {
    private byte[] privateKey;
    private byte[] publicKey;
    private SM3 sm3;

    public SM2KeyImport(Object privateKey, Object publicKey) {
        if (privateKey == null) {
            this.privateKey = null;
        } else {
            this.privateKey = (byte[]) privateKey;
        }
        if (publicKey == null) {
            this.publicKey = null;
        } else {
            this.publicKey = (byte[]) publicKey;
        }
        sm3 = new SM3();
    }

    @Override
    public byte[] toBytes() {
        return privateKey;
    }

    @Override
    public byte[] ski() throws CspException{
        try {
            return sm3.hash(privateKey);
        } catch (NoSuchAlgorithmException e) {
            throw new CspException(e.getMessage());
        }
    }

    @Override
    public boolean isSymmetric() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return true;
    }

    @Override
    public IKey getPublicKey() {
        return new Sm2PublicKeyImport(publicKey);
    }
}
