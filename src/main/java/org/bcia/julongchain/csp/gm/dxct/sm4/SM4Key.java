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
package org.bcia.julongchain.csp.gm.dxct.sm4;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bcia.julongchain.csp.intfs.IKey;

import java.security.NoSuchAlgorithmException;

/**
 * sm4蜜钥对象
 *
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public class SM4Key implements IKey {
    private SM3 sm3;
    private byte[] sm4Key;

    public SM4Key() {
        this.sm3 = sm3;
    }

    @Override
    public byte[] toBytes() {
        this.sm4Key = SM4.generateKey();
        return sm4Key;
    }

    @Override
    public byte[] ski() throws CspException {
        try {
            return sm3.hash(sm4Key);
        } catch (NoSuchAlgorithmException e) {
            throw new CspException(e.getMessage());
        }
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public IKey getPublicKey() {
        return null;
    }
}
