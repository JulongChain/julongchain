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

import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;
import org.bouncycastle.crypto.CryptoException;

/**
 * 签名接口,定义获取公钥和签名接口
 *
 * @author zhangmingyang
 * @Date: 2018/4/18
 * @company Dingxuan
 */
public interface ISigner {

    /**
     * 获取公钥
     * @return
     */
    Object getPublicKey();

    /**
     * 数据签名
     * @param key
     * @param msgContent
     * @param opts
     * @return
     */
    byte[] sign(IKey key, byte[] msgContent, ISignerOpts opts) throws MspException;

}