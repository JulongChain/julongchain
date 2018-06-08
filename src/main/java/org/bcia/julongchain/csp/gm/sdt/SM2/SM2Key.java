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
package org.bcia.julongchain.csp.gm.sdt.SM2;

import org.bcia.julongchain.csp.gm.sdt.SM3.SM3;
import org.bcia.julongchain.csp.intfs.IKey;

/**
 * GM SM2 密钥
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */

public class SM2Key implements IKey {

    private SM2KeyPair sm2KeyPair;
    private SM3 sm3;

    public SM2Key() {
    }

    public SM2Key(SM2KeyPair sm2KeyPair) {
        this.sm2KeyPair = sm2KeyPair;
        this.sm3 = new SM3();
    }

    @Override
    public byte[] toBytes() {
        return sm2KeyPair.getPrivateKey();
    }

    @Override
    public byte[] ski() {

        try {
            return sm3.hash(sm2KeyPair.getPrivateKey());
        } catch (Exception e) {
            return null;
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
        return new SM2PublicKey(sm2KeyPair.getPublicKey());
    }
}
