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
package org.bcia.julongchain.csp.pkcs11.sw;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;
import org.bcia.julongchain.csp.pkcs11.aes.AesDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesEncrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaKeyOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaSignOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.*;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11HashOpts;
import sun.security.provider.SecureRandom;


/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class CspImpl implements ICsp {
    private IPKCS11SwFactoryOpts PKCS11SwFactoryOpts;

    CspImpl(IPKCS11SwFactoryOpts PKCS11FactoryOpts) {
        this.PKCS11SwFactoryOpts=PKCS11FactoryOpts;
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {


        if (opts == null) {
            return null;
        }

        IKey key;
        GenerateKeyImpl gen = new GenerateKeyImpl();
        if (opts instanceof RsaOpts.RSA1024KeyGenOpts)
        {
            key = gen.genRsaKey(1024);
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof RsaOpts.RSA2048KeyGenOpts)
        {
            key = gen.genRsaKey(2048);
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof RsaOpts.RSA3072KeyGenOpts)
        {
            key = gen.genRsaKey(3072);
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof RsaOpts.RSA4096KeyGenOpts)
        {
            key = gen.genRsaKey(4096);
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof EcdsaOpts.ECDSA256KeyGenOpts)
        {
            /*AlgorithmParameters.EC SupportedCurves = [secp112r1,1.3.132.0.6]
             * |[secp112r2,1.3.132.0.7]|[secp128r1,1.3.132.0.28]|[secp128r2,1.3.132.0.29]
             * |[secp160k1,1.3.132.0.9]|[secp160r1,1.3.132.0.8]|[secp160r2,1.3.132.0.30]
             * |[secp192k1,1.3.132.0.31]|[secp192r1,NIST P-192,X9.62 prime192v1,1.2.840.10045.3.1.1]
             * |[secp224k1,1.3.132.0.32]|[secp224r1,NIST P-224,1.3.132.0.33]|[secp256k1,1.3.132.0.10]
             * |[secp256r1,NIST P-256,X9.62 prime256v1,1.2.840.10045.3.1.7]
             * |[secp384r1,NIST P-384,1.3.132.0.34]|[secp521r1,NIST P-521,1.3.132.0.35]
             * |[X9.62 prime192v2,1.2.840.10045.3.1.2]|[X9.62 prime192v3,1.2.840.10045.3.1.3]
             * |[X9.62 prime239v1,1.2.840.10045.3.1.4]|[X9.62 prime239v2,1.2.840.10045.3.1.5]
             * |[X9.62 prime239v3,1.2.840.10045.3.1.6]|[sect113r1,1.3.132.0.4]|[sect113r2,1.3.132.0.5]
             * |[sect131r1,1.3.132.0.22]|[sect131r2,1.3.132.0.23]|[sect163k1,NIST K-163,1.3.132.0.1]
             * |[sect163r1,1.3.132.0.2]|[sect163r2,NIST B-163,1.3.132.0.15]|[sect193r1,1.3.132.0.24]
             * |[sect193r2,1.3.132.0.25]|[sect233k1,NIST K-233,1.3.132.0.26]
             * |[sect233r1,NIST B-233,1.3.132.0.27]|[sect239k1,1.3.132.0.3]
             * |[sect283k1,NIST K-283,1.3.132.0.16]|[sect283r1,NIST B-283,1.3.132.0.17]
             * |[sect409k1,NIST K-409,1.3.132.0.36]|[sect409r1,NIST B-409,1.3.132.0.37]
             * |[sect571k1,NIST K-571,1.3.132.0.38]|[sect571r1,NIST B-571,1.3.132.0.39]
             * |[X9.62 c2tnb191v1,1.2.840.10045.3.0.5]|[X9.62 c2tnb191v2,1.2.840.10045.3.0.6]
             * |[X9.62 c2tnb191v3,1.2.840.10045.3.0.7]|[X9.62 c2tnb239v1,1.2.840.10045.3.0.11]
             * |[X9.62 c2tnb239v2,1.2.840.10045.3.0.12]|[X9.62 c2tnb239v3,1.2.840.10045.3.0.13]
             * |[X9.62 c2tnb359v1,1.2.840.10045.3.0.18]|[X9.62 c2tnb431r1,1.2.840.10045.3.0.20]
             * |[brainpoolP160r1,1.3.36.3.3.2.8.1.1.1]|[brainpoolP192r1,1.3.36.3.3.2.8.1.1.3]
             * |[brainpoolP224r1,1.3.36.3.3.2.8.1.1.5]|[brainpoolP256r1,1.3.36.3.3.2.8.1.1.7]
             * |[brainpoolP320r1,1.3.36.3.3.2.8.1.1.9]|[brainpoolP384r1,1.3.36.3.3.2.8.1.1.11]
             * |[brainpoolP512r1,1.3.36.3.3.2.8.1.1.13]
             */
            key = gen.genEcdsaKey("secp256k1");//比特币选用
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof EcdsaOpts.ECDSA384KeyGenOpts)
        {
            key = gen.genEcdsaKey("secp384r1");
            if(!opts.isEphemeral())
            {
                gen.savePrivateKeyAsPEM(key.toBytes(), PKCS11SwFactoryOpts.getPath());
                gen.savePublicKeyAsPEM(key.getPublicKey().toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        if (opts instanceof AesOpts.AES128KeyGenOpts) {
            key = gen.genAESKey(128);
            if(!opts.isEphemeral())
            {
                gen.writefile(key.toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }
        if (opts instanceof AesOpts.AES192KeyGenOpts) {
            key = gen.genAESKey(192);
            if(!opts.isEphemeral())
            {
                gen.writefile(key.toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }
        if (opts instanceof AesOpts.AES256KeyGenOpts) {
            key = gen.genAESKey(256);
            if(!opts.isEphemeral())
            {
                gen.writefile(key.toBytes(), PKCS11SwFactoryOpts.getPath());
            }
            return key;
        }

        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        return null;
    }


    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
/*
		File dir = new File(PKCS11SwFactoryOpts.getPath());
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    continue;
                } else if (fileName.endsWith("pem")) { // 判断文件名是否以.avi结尾
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    String strAlg;
                    GenerateKeyImpl gen = new GenerateKeyImpl();
                    if(strFileName.contentEquals("rsa"))
                    {
                    	strAlg = PKCS11CSPConstant.RSA;
                    }else if(strFileName.contentEquals("ecdsa")) {
                    	strAlg = PKCS11CSPConstant.ECDSA;
                    }

                    try {
                    	PrivateKey prikey = gen.LoadPrivateKeyAsPEM(PKCS11SwFactoryOpts.getPath(), strAlg);
                    	PublicKey pubkey = gen.LoadPublicKeyAsPEM(PKCS11SwFactoryOpts.getPath(), strAlg);
                    }catch(IOException|NoSuchAlgorithmException|InvalidKeySpecException e) {
                    	throw new JavaChainException("[JC_PKCS_SOFT]: LoadKey Error!");
                    }
                } else if (fileName.endsWith("key")) { // 判断文件名是否以.avi结尾
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    filelist.add(files[i]);
                } else {
                    continue;
                }
            }

        }
 */
        return null;
    }


    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {

        HashImpl hashimpl = new HashImpl();
        if(opts instanceof PKCS11HashOpts.MD2Opts)
        {
            return hashimpl.getAlgDigest(opts.getAlgorithm(), msg);
        }
        if(opts instanceof PKCS11HashOpts.MD5Opts)
        {
            return hashimpl.getAlgDigest(opts.getAlgorithm(), msg);
        }
        if(opts instanceof PKCS11HashOpts.SHA1Opts)
        {
            return hashimpl.getAlgDigest(opts.getAlgorithm(), msg);
        }
        if(opts instanceof PKCS11HashOpts.SHA256Opts)
        {
            return hashimpl.getAlgDigest(opts.getAlgorithm(), msg);
        }
        if(opts instanceof PKCS11HashOpts.SHA384Opts)
        {
            return hashimpl.getAlgDigest(opts.getAlgorithm(), msg);
        }
        if(opts instanceof PKCS11HashOpts.SHA3_256Opts)
        {
            return hashimpl.getSHA3AlgDigest(256, msg);
        }
        if(opts instanceof PKCS11HashOpts.SHA3_384Opts)
        {
            return hashimpl.getSHA3AlgDigest(384, msg);
        }

        return null;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {

        HashImpl hashimpl = new HashImpl();
        return hashimpl.getHash(opts.getAlgorithm());

    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {

        if(((key instanceof RsaKeyOpts.RsaPriKey) && (opts instanceof RsaSignOpts))
                ||((key instanceof EcdsaKeyOpts.EcdsaPriKey) && (opts instanceof EcdsaSignOpts))) {

            SignImpl signdata = new SignImpl();
            byte[] signatura = signdata.signData(key, digest, opts.getAlgorithm());
            return signatura;
        }else {
            throw new JavaChainException("[JC_PKCS_SOFT]:Parameter error or mismatch");
        }
    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {

        if(((key instanceof RsaKeyOpts.RsaPriKey) && (opts instanceof RsaSignOpts))
                ||((key instanceof EcdsaKeyOpts.EcdsaPriKey) && (opts instanceof EcdsaSignOpts))) {
            VerifyImpl verifydata = new VerifyImpl();
            boolean rv = verifydata.verifyData(key, digest, signature, opts.getAlgorithm());
            return rv;
        }else {
            throw new JavaChainException("[JC_PKCS_SOFT]:Parameter error or mismatch");
        }
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {

        EncryptImpl encryptdata = new EncryptImpl();
        if (opts instanceof RsaEncrypterOpts) {
            byte[] encodedata = encryptdata.encryptData(key, plaintext, ((RsaEncrypterOpts) opts).getMode(),
                    ((RsaEncrypterOpts) opts).getPadding(), ((RsaEncrypterOpts) opts).getFlagpub());
            return encodedata;
        }else if(opts instanceof AesEncrypterOpts) {
            byte[] encodedata = encryptdata.encryptData(key, plaintext, ((AesEncrypterOpts) opts).getMode(),
                    ((AesEncrypterOpts) opts).getPadding());
            return encodedata;
        }else {
            throw new JavaChainException("[JC_PKCS_SOFT]:Parameter error or mismatch");
        }
    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        DecryptImpl decryptdata = new DecryptImpl();
        if (opts instanceof RsaDecrypterOpts) {
            byte[] data = decryptdata.decryptData(key, ciphertext, ((RsaDecrypterOpts) opts).getMode(),
                    ((RsaDecrypterOpts) opts).getPadding(), ((RsaDecrypterOpts) opts).getFlagpub());
            return data;
        }else if(opts instanceof AesDecrypterOpts) {
            byte[] data = decryptdata.decryptData(key, ciphertext, ((AesDecrypterOpts) opts).getMode(),
                    ((AesDecrypterOpts) opts).getMode());
            return data;
        }else {
            throw new JavaChainException("[JC_PKCS_SOFT]:Parameter error or mismatch");
        }
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        byte[] none=new SecureRandom().engineGenerateSeed(len);
        return none;
    }
}
