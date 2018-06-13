package org.bcia.julongchain.csp.gm.dxct;

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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.*;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bcia.julongchain.csp.gm.dxct.sm4.*;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

/**
 * @author zhanglin
 * @purpose Define the class, GmCsp
 * @date 2018-01-25
 * @company Dingxuan
 */

// GmCsp provides the Guomi's software implements of the ICsp interface.
public class GmCsp implements ICsp {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GmCsp.class);
    // List algorithms to be used.
    private SM2 sm2;
    private SM3 sm3;
    private SM4 sm4;

    public GmCsp() {
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
    }

    private IGmFactoryOpts gmOpts;


    public GmCsp(IGmFactoryOpts gmOpts) {
        this.gmOpts = gmOpts;
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
    }


    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if (opts == null) {
            log.error("Invalid Opts parameter. It must not be null.");
            throw new JavaChainException("Invalid Opts parameter. It must not be null.");
        }
        if (opts instanceof SM2KeyGenOpts) {

            SM2Key sm2Key = new SM2Key(sm2.generateKeyPair());

            if (!opts.isEphemeral()) {
                //TODO  实现密钥存储接口
                // CryptoUtil.publicKeyFileGen(Hex.toHexString(sm2Key.getPublicKey().ski()), sm2Key.getPublicKey().toBytes());
                CryptoUtil.privateKeyFileGen(Hex.toHexString(sm2Key.ski()), sm2Key.toBytes());
            }
            return sm2Key;
        }
        if (opts instanceof SM4KeyGenOpts) {
            return new SM4Key();
        }
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        if (raw == null) {
            log.error("Invalid raw. It must not be nil.");
            throw new JavaChainException("Invalid raw. It must not be nil.");
        }
        if (opts == null) {
            log.error("Invalid opts. It must not be nil.");
            throw new JavaChainException("Invalid opts. It must not be nil.");
        }
        if (opts instanceof SM2KeyImportOpts) {
            IKey sm2PrivateKey = new SM2PrivateKey(raw);
            if (!opts.isEphemeral()) {
                CryptoUtil.privateKeyFileGen(Hex.toHexString(sm2PrivateKey.ski()), sm2PrivateKey.toBytes());
            }
            return sm2PrivateKey;
        }

        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        if (msg.length == 0) {
            log.error("Invalid msg. Cannot be empty.");
            throw new JavaChainException("Invalid msg. Cannot be empty.");
        }

        byte[] results = sm3.hash(msg);
        return results;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        if (key == null) {
            log.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (digest.length == 0) {
            log.error("Invalid digest. Cannot be empty.");
            throw new JavaChainException("Invalid digest. Cannot be empty.");
        }
        if (opts instanceof SM2SignerOpts) {
            log.info("privateKey:" + Hex.toHexString(key.toBytes()));
            return sm2.sign(key.toBytes(), digest);
        }
        return null;
    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        boolean verify = false;
        if (key == null) {
            log.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (signature.length == 0) {
            log.error("Invalid signature. It must not be nil.");
            throw new JavaChainException("Invalid signature. It must not be nil.");
        }
        if (opts instanceof SM2SignerOpts) {
            verify = sm2.verify(key.getPublicKey().toBytes(), signature, digest);
        }
        return verify;
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        if (key == null) {
            log.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (opts instanceof SM4EncrypterOpts) {
            return sm4.encryptECB(plaintext, key.toBytes());
        }
        return null;
    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        if (key == null) {
            log.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (ciphertext.length == 0) {
            log.error("Invalid ciphertext. Cannot be empty.");
            throw new JavaChainException("Invalid ciphertext. Cannot be empty.");
        }
        if (opts instanceof SM4DecrypterOpts) {
            return sm4.decryptECB(ciphertext, key.toBytes());
        }
        return null;
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        if (len <= 0) {
            log.error("The random length is less than Zero! ");
            throw new JavaChainException("The random length is less than Zero! ");
        }
        byte[] none = new SecureRandom().generateSeed(len);
        return none;
    }

}
