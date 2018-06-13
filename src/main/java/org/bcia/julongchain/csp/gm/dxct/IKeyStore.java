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
package org.bcia.julongchain.csp.gm.dxct;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.IKey;

/**
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public interface IKeyStore {
    // ReadOnly returns true if this KeyStore is read only, false otherwise.
    // If ReadOnly is true then StoreKey will fail.
    boolean readOnly();
    // GetKey returns a key object whose SKI is the one passed.
    IKey getKey(byte[] ski) throws JavaChainException;
    // StoreKey stores the key k in this KeyStore.
    // If this KeyStore is read only then the method will fail.
    void storeKey(IKey ikey);
}
