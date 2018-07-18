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
package org.bcia.julongchain.msp.signer;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;
import org.bouncycastle.crypto.CryptoException;

/**
 * @author zhangmingyang
 * @Date: 2018/4/18
 * @company Dingxuan
 */
public class NodeSigner implements ISigner {
    private ICsp csp;
    private IKey sk;
    private IKey pk;

    public NodeSigner(ICsp csp, IKey sk) {
        this.csp = csp;
        this.sk = sk;
    }
    @Override
    public Object publicKey() {
        return this.pk;
    }

    @Override
    public byte[] sign(IKey key, byte[] msgContent, ISignerOpts opts){
        try {
            return this.csp.sign(this.sk,msgContent,opts);
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ICsp getCsp() {
        return csp;
    }

    public IKey getSk() {
        return sk;
    }

    public Object getPk() {
        return pk;
    }
}
