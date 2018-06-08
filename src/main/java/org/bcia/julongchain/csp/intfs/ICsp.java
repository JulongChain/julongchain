package org.bcia.julongchain.csp.intfs;

/**
 * Copyright BCIA. All Rights Reserved.
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

import org.bcia.julongchain.csp.intfs.opts.*;
import org.bcia.julongchain.common.exception.JavaChainException;

/**
 * @author zhanglin
 * @purpose Define the interface, ICsp, and list its elements
 * @date 2018-01-25
 * @company Dingxuan
 */

// ICsp is the blockchain cryptographic service provider that offers implementations of
// cryptographic algorithms, such as NIST, GM(P.R.China's standards) and so on.
public interface ICsp {

    // The keyGen generates a key according to the opts whose type is the IKeyGenOpts.
    IKey keyGen(IKeyGenOpts opts) throws JavaChainException;

    // The keyDeriv derives a key from the k according to the opts whose type is the IKeyDerivOpts.
    IKey keyDeriv(IKey k, IKeyDerivOpts opts) throws JavaChainException;

    // The keyImport imports a key from the raw according to the opts whose type is the IKeyImportOpts.
    IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException;

    // The getKey returns a key according to the ski, i.e. Subject Key Identifier.
    IKey getKey(byte[] ski) throws JavaChainException;

    // The hash generates a hash value of the msg according to the opts whose type is the IHashOpts.
    byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException;

    // The getHash returns an instance of Hash according to opts whose type is the IHashOpts.
    IHash getHash(IHashOpts opts) throws JavaChainException;

    // The sign generates a digital signature for the digest using the key k, according to the opts
    // whose type is the ISignerOpts.
    // The opts specifies a signature algorithm used, but also a hash function to produce a digest
    // for a large-sized message.
    byte[] sign(IKey k, byte[] digest, ISignerOpts opts) throws JavaChainException;

    // The verify checks the validation of the signature against the key k and the digest, according
    // to the opts whose type is the ISignerOpts. Note that it is ISignerOpts definitely.
    boolean verify(IKey k, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException;

    // The encrypt computes a ciphertext from the plaintext using key k, according to the opts
    // whose type is IEncrypterOpts.
    // The opts should consist of a symmetric encryption algorithm and an appropriate block-cipher mode.
    byte[] encrypt(IKey k, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException;

    // The decrypt outputs a plaintext from the ciphertext using the key k, according to the opts
    // whose type is IDecrypterOpts.
    // The opts should take a specified algorithm and mode as the argument.
    byte[] decrypt(IKey k, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException;

    // The rng provides a random number within a specified length, according to the opts whose type is IRngOpts.
    byte[] rng(int len, IRngOpts opts) throws JavaChainException;
}
