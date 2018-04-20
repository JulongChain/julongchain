/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.javachain.csp.pkcs11;

import org.bcia.javachain.common.exception.JavaChainException;

import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IHash;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.*;
import org.bcia.javachain.csp.pkcs11.ecdsa.EcdsaImpl;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11KeyData;
import sun.security.provider.SecureRandom;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11Csp implements ICsp {

    private IPKCS11FactoryOpts PKCS11FactoryOpts;

    PKCS11Csp(IPKCS11FactoryOpts PKCS11FactoryOpts) {
        this.PKCS11FactoryOpts=PKCS11FactoryOpts;
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if (opts == null) {
            return null;
        }

        switch (opts.getAlgorithm())
        {
            case "ECDSA256":
                return new EcdsaImpl.generateECKey(256, opts.isEphemeral(), PKCS11FactoryOpts).getIKey();
            case "ECDSA384":
                return new EcdsaImpl.generateECKey(384, opts.isEphemeral(), PKCS11FactoryOpts).getIKey();
            case "ECDSA":
                break;
            case "RSA1024":
                break;
            case "RSA2048":
                break;
            case "RSA3072":
                break;
            case "RSA4096":
                break;
            case "RSA":
                break;

        }
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {

        if(raw == null || opts == null)
        {
            return null;
        }
        byte[] ski;
        EcdsaImpl.ImportECKey importkey = new EcdsaImpl.ImportECKey();
        PKCS11KeyData keyraw = (PKCS11KeyData) raw;
        byte[] pubder;
        byte[] prider;
        String Type = opts.getClass().getSimpleName();
        switch(Type) {
            case "ECDSAPrivateKeyImportOpts":
                pubder = (byte[])keyraw.getRawPub();
                prider = (byte[])keyraw.getRawPri();
                //der2 =
                if(pubder.length<=0 && prider.length<=0)
                {
                    return null;
                }

                ski = importkey.importECKey(prider,pubder,opts.isEphemeral(),PKCS11FactoryOpts,true,false);

                return importkey.getKey(ski,prider,pubder);
            case "ECDSAPublicKeyImportOpts":
                pubder = (byte[])keyraw.getRawPub();
                prider = (byte[])keyraw.getRawPri();
                if(pubder.length<=0)
                {
                    return null;
                }
                if(PKCS11FactoryOpts.getNoImport()) {
                    ski = importkey.importECKey(null,pubder,opts.isEphemeral(),PKCS11FactoryOpts,true,true);
                }else {
                    if (!PKCS11FactoryOpts.getSoftVerify()) {
                        //add log
                        // Warn about potential future problems
                    }
                    ski = importkey.importECKey(null,pubder,opts.isEphemeral(),PKCS11FactoryOpts,true,false);
                }
                return importkey.getKey(ski,prider,pubder);
            case "X509PublicKeyImportOpts":

                break;
        }
        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        return  null;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        return false;
    }


    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        byte[] none=new SecureRandom().engineGenerateSeed(len);
        return none;
    }
}
