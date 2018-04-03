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
package org.bcia.javachain.csp.gm.sm2;

import org.bcia.javachain.csp.intfs.IKey;
import org.bouncycastle.math.ec.ECPoint;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class SM2Key implements IKey {
    SM2 sm2=new SM2();
    private  SM2KeyPair sm2KeyPair;

    @Override
    public byte[] toBytes() {
        sm2KeyPair = sm2.generateKeyPair();
        return  sm2KeyPair.getPrivateKey().toByteArray();
    }

    @Override
    public byte[] ski() {
        return new byte[0];
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
