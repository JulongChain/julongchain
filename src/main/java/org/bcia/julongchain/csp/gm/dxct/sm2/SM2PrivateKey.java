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

import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bcia.julongchain.csp.intfs.IKey;

/**
 * @author zhangmingyang
 * @Date: 2018/5/4
 * @company Dingxuan
 */
public class SM2PrivateKey implements IKey {
    private byte[] privateKey;
    private SM3 sm3;
    public SM2PrivateKey(Object privateKey) {
        this.privateKey= (byte[]) privateKey;
        sm3=new SM3();
    }

    @Override
    public byte[] toBytes() {
        return privateKey;
    }

    @Override
    public byte[] ski() {
        return sm3.hash(privateKey);
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
