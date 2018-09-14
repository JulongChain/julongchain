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

/**
 * SM4导入选项
 *
 * @author zhangmingyang
 * @date 2018/09/06
 * @company Dingxuan
 */
public class SM4KeyImport implements IKey{
    private byte[] sm4Key;
    private SM3 sm3;
    public SM4KeyImport(Object key) {
        sm4Key= (byte[]) key;
    }

    @Override
    public byte[] toBytes() throws CspException {
        return sm4Key;
    }

    @Override
    public byte[] ski() throws CspException {
        try {
            return sm3.hash(sm4Key);
        } catch (CspException e) {
            throw new CspException(e);
        }
    }

    @Override
    public boolean isSymmetric() throws CspException {
        return true;
    }

    @Override
    public boolean isPrivate() throws CspException {
        return true;
    }

    @Override
    public IKey getPublicKey() throws CspException {
        return null;
    }
}
