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
 * GM SM2私钥
 *
 * @author tengxiumin
 * @date 2016/05/16
 * @company SDT
 */
public class SM2PrivateKey implements IKey {

    private byte[] privateKey;
    private SM3 sm3;

    public SM2PrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
        sm3 = new SM3();
    }

    @Override
    public byte[] toBytes() {
        return privateKey;
    }

    @Override
    public byte[] ski() {
        try {
            return sm3.hash(privateKey);
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
        return null;
    }
}
