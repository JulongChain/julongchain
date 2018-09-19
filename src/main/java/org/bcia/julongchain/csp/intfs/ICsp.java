package org.bcia.julongchain.csp.intfs;

/**
 * Copyright Dingxuan. All Rights Reserved.
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

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.intfs.opts.*;

/**
 * @author zhanglin, zhangmingyang
 * @purpose Define the interface, ICsp, and list its elements
 * @date 2018-01-25
 * @company Dingxuan
 */

public interface ICsp {

    /**
     * 密钥生成,根据密钥生成选项生成密钥
     *
     * @param opts
     * @return
     * @throws CspException
     */
    IKey keyGen(IKeyGenOpts opts) throws CspException;

    /**
     * keyDeriv根据类型为IKeyDerivOpts的opts从k派生密钥
     *
     * @param k
     * @param opts
     * @return
     * @throws CspException
     */
    IKey keyDeriv(IKey k, IKeyDerivOpts opts) throws CspException;

    /**
     * 根据IKeyImportOpts将raw导入密钥
     *
     * @param raw
     * @param opts
     * @return
     * @throws CspException
     */
    IKey keyImport(Object raw, IKeyImportOpts opts) throws CspException;

    /**
     * 根据ski值获取一个密钥
     *
     * @param ski
     * @return
     * @throws CspException
     */
    IKey getKey(byte[] ski) throws CspException;

    /**
     * 根据hash选项,对消息进行hash
     *
     * @param msg
     * @param opts
     * @return
     * @throws CspException
     */
    byte[] hash(byte[] msg, IHashOpts opts) throws CspException;

    /**
     * 根据IHashOpts选项，返回Hsah实例
     *
     * @param opts
     * @return
     * @throws CspException
     */
    IHash getHash(IHashOpts opts) throws CspException;

    /**
     * 根据ISignerOpts,使用k对摘要进行数字签名
     *
     * @param k
     * @param digest
     * @param opts
     * @return
     * @throws CspException
     */
    byte[] sign(IKey k, byte[] digest, ISignerOpts opts) throws CspException;

    /**
     * 根据ISignerOpts,使用k对签名值进行验证
     *
     * @param k
     * @param signature
     * @param digest
     * @param opts
     * @return
     * @throws CspException
     */
    boolean verify(IKey k, byte[] signature, byte[] digest, ISignerOpts opts) throws CspException;

    /**
     * 根据IEncrypterOpts,使用密钥k从将明文计算出密文,
     * 选项应包括对称加密算法和适当的分组密码模式
     *
     * @param k
     * @param plaintext
     * @param opts
     * @return
     * @throws CspException
     */
    byte[] encrypt(IKey k, byte[] plaintext, IEncrypterOpts opts) throws CspException;

    /**
     * 根据IDecrypterOpts的opts,解密器使用密钥k从密文输出明文,
     * opts应采用指定的算法和模式作为参数
     *
     * @param k
     * @param ciphertext
     * @param opts
     * @return
     * @throws CspException
     */
    byte[] decrypt(IKey k, byte[] ciphertext, IDecrypterOpts opts) throws CspException;

    /**
     * 根据IRngOpts,rng提供指定长度内的随机数
     *
     * @param len
     * @param opts
     * @return
     * @throws CspException
     */
    byte[] rng(int len, IRngOpts opts) throws CspException;
}
