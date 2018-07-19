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
package org.bcia.julongchain.csp.gmt0016.ftsafe;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECImpl;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016KeyData;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAImpl;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAImportOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.*;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMDigest;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMHashOpts;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IDecrypterOpts;
import org.bcia.julongchain.csp.intfs.opts.IEncrypterOpts;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyDerivOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.intfs.opts.IRngOpts;
import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;
import sun.security.provider.SecureRandom;

import java.util.UUID;

/**
 * Class description
 *
 * @author
 * @date 7/3/18
 * @company FEITIAN
 */
public class GMT0016Csp implements IGMT0016Csp {

    private IGMT0016FactoryOpts gmt0016FactoryOpts;
    GMT0016CspLog csplog = new GMT0016CspLog();

    GMT0016Csp(IGMT0016FactoryOpts gmt0016FactoryOpts)
    {
        this.gmt0016FactoryOpts = gmt0016FactoryOpts;
    }

    @Override
    public void finalized() throws JavaChainException {

        gmt0016FactoryOpts.getSKFFactory().SKF_DisconnectDev(gmt0016FactoryOpts.getDevHandle());
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if (opts == null) {
            csplog.setLogMsg("[JC_SKF]:keyGenParam Err!", 2, GMT0016Csp.class);
            throw new JavaChainException("[JC_SKF]:keyGenParam Err!");
        }


        IKey key;
        String containerName = UUID.randomUUID().toString();
        if(opts instanceof RSAOpts.RSA1024KeyGenOpts)
        {
            RSAImpl rsa = new RSAImpl();
            key = rsa.generateRSAKey(containerName, 1024, gmt0016FactoryOpts);
            return key;
        }

        if(opts instanceof RSAOpts.RSA2048KeyGenOpts)
        {
            RSAImpl rsa = new RSAImpl();
            key = rsa.generateRSAKey(containerName, 2048, gmt0016FactoryOpts);
            return key;
        }

        if(opts instanceof ECCOpts.ECCKeyGenOpts) {
            ECImpl ecc = new ECImpl();
            key = ecc.generateECKey(containerName, gmt0016FactoryOpts);
            return key;
        }

        if(opts instanceof ISymmKeyGenOpts)
        {
            byte[] random = gmt0016FactoryOpts.getSKFFactory().SKF_GenRandom(
                    gmt0016FactoryOpts.getDevHandle(), ((ISymmKeyGenOpts) opts).getBitLen());
            long lSessionHandle = gmt0016FactoryOpts.getSKFFactory().SKF_SetSymmKey(
                    gmt0016FactoryOpts.getDevHandle(), random, ((ISymmKeyGenOpts) opts).getAlgID());
            SymmCspKey symmkey = new SymmCspKey(random, opts.isEphemeral(), lSessionHandle);
            return symmkey;
        }

        csplog.setLogMsg("[JC_SKF]:No Support KeyGen Opts!", 1, GMT0016Csp.class);
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {

        throw new JavaChainException("[JC_SKF]:No Support keyDeriv Impl!");

    }


    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {

        if(raw == null || opts == null)
        {
            csplog.setLogMsg("[JC_SKF]:keyImportParam Err!", 2, GMT0016Csp.class);
            throw new JavaChainException("[JC_SKF]:Param Err!");
        }
        GMT0016KeyData keyraw = (GMT0016KeyData)raw;
        if (opts instanceof ECCOpts.ECCKeyImportOpts)
        {
            ECImpl ecc = new ECImpl();
            IKey key = ecc.importECKey(((ECCOpts.ECCKeyImportOpts) opts).getAlgID(), keyraw.getRawPub(), keyraw.getRawPri(),
                    keyraw.getContainer(), gmt0016FactoryOpts);
            return key;
        }

        if (opts instanceof RSAImportOpts)
        {
            RSAImpl rsa = new RSAImpl();
            IKey key = rsa.importRSAKey(((RSAImportOpts) opts).getAlgID(), keyraw.getRawPub(), keyraw.getRawPri(),
                    keyraw.getContainer(), gmt0016FactoryOpts);
            return key;
        }

        csplog.setLogMsg("[JC_SKF]:No Support KeyImport Opts!", 1, GMT0016Csp.class);
        return null;
    }


    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {

        String sContainerName = "";
        boolean bSignFlag = true;
        byte[] cipher;
        byte[] hash;
        int type = 0;
        //analyze ski
        int skiIndex = 0;
        while (skiIndex < ski.length) {
            int tag = ski[skiIndex];
            skiIndex++;
            switch (tag) {
                case GMT0016CspConstant.TAG_CONTAINER:
                    int nameLen = ski[skiIndex];
                    skiIndex++;
                    byte[] name = new byte[nameLen];
                    System.arraycopy(ski, skiIndex, name, 0, nameLen);
                    skiIndex += nameLen;
                    sContainerName = new String(name);
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_SIGN_FLAG:
                    int flagLen = ski[skiIndex];
                    skiIndex++;
                    bSignFlag = (ski[skiIndex] == (byte)1); //Sign Flag
                    skiIndex += flagLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_CIPHER_DATA:
                    int dataLen = ski[skiIndex];
                    skiIndex++;
                    cipher = new byte[dataLen];
                    System.arraycopy(ski, skiIndex, cipher, 0, dataLen);
                    skiIndex += dataLen;
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_HASH:
                    int hashLen = ski[skiIndex];
                    skiIndex++;
                    hash = new byte[hashLen];
                    System.arraycopy(ski, skiIndex, hash, 0, hashLen);
                    skiIndex += hashLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_TYPE:
                    int typelen = ski[skiIndex];
                    type = ski[++skiIndex];
                    skiIndex += typelen;
                    break;
                default:
                    break;
            }
        }

        if (type == 1)
        {
            RSAImpl rsa = new RSAImpl();
            IKey key = rsa.getRSAKey(sContainerName, bSignFlag, gmt0016FactoryOpts);
            return key;
        }else if(type == 2){
            ECImpl ecc = new ECImpl();
            IKey key = ecc.getECKey(sContainerName, bSignFlag, gmt0016FactoryOpts);
            return key;
        }

        return null;
    }


    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {

        if(opts instanceof GMHashOpts.SM3SignPreOpts)
        {
            GMHashOpts.SM3SignPreOpts sm3opts = (GMHashOpts.SM3SignPreOpts)opts;
            byte[] ski = sm3opts.getSki();
            String sContainerName = "";
            boolean bSignFlag = true;
            int skiIndex = 0;
            int type = 0;
            while (skiIndex < ski.length) {
                int tag = ski[skiIndex];
                skiIndex++;
                switch (tag) {
                    case GMT0016CspConstant.TAG_CONTAINER:
                        int nameLen = ski[skiIndex];
                        skiIndex++;
                        byte[] name = new byte[nameLen];
                        System.arraycopy(ski, skiIndex, name, 0, nameLen);
                        skiIndex += nameLen;
                        sContainerName = new String(name);
                        break;
                    case GMT0016CspConstant.TAG_PUBLICK_KEY_SIGN_FLAG:
                        int flagLen = ski[skiIndex];
                        skiIndex++;
                        bSignFlag = (ski[skiIndex] == (byte)1); //Sign Flag
                        skiIndex += flagLen;
                        break;
                    case GMT0016CspConstant.TAG_KEY_CIPHER_DATA:
                        int dataLen = ski[skiIndex];
                        skiIndex++;
                        skiIndex += dataLen;
                        break;
                    case GMT0016CspConstant.TAG_PUBLICK_KEY_HASH:
                        int hashLen = ski[skiIndex];
                        skiIndex++;
                        skiIndex += hashLen;
                        break;
                    case GMT0016CspConstant.TAG_KEY_TYPE:
                        int typelen = ski[skiIndex];
                        type = ski[++skiIndex];
                        skiIndex += typelen;
                        break;
                    default:
                        break;
                }
            }

            if(type != 2) {
                csplog.setLogMsg("[JC_SKF]: The Publickey is not sm2 type! Param Err!", 2, GMT0016Csp.class);
                throw new JavaChainException("[JC_SKF]: The Publickey is not sm2 type! Param Err!");
            }
            ECImpl ec = new ECImpl();
            byte[] hash = ec.getHash(msg, sm3opts.getMechanism(), sContainerName,
                    bSignFlag, sm3opts.getPubID(), gmt0016FactoryOpts);
            return hash;
        }
        else
        {
            GMDigest gmDigest = new GMDigest(opts.getAlgorithm());
            byte[] hash = gmDigest.getHash(msg, gmt0016FactoryOpts);
            return hash;
        }
    }


    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {

        IHash hash = new IHash() {
            private byte[] msg;
            public int write(byte[] p) {
                msg = new byte[p.length];
                System.arraycopy(p, 0, msg, 0, p.length);
                return p.length;
            }

            public byte[] sum(byte[] b) {
                byte[] data = new byte[msg.length + b.length];
                System.arraycopy(msg, 0, data, 0, msg.length);
                System.arraycopy(b, 0, data, msg.length, b.length);
                try {
                    byte[] digest = hash(data, opts);
                    return digest;
                }catch(JavaChainException ex) {
                    ;
                }
                return null;
            }

            public void reset() {
                byte[] buffer = new byte[GMT0016CspConstant.BUFFERSIZE];
                try {
                    byte[] digest = hash(buffer, opts);
                }catch(JavaChainException ex) {
                    ;
                }
            }

            public int size() {
                return 16;////??????????
            }

            public int blockSize() {
                return GMT0016CspConstant.BUFFERSIZE;
            }
        };

        return hash;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        String sContainerName = "";
        byte[] ski = key.ski();
        boolean bSignFlag = true;
        byte[] cipher;
        byte[] hash;
        int type = 0;
        //analyze ski
        int skiIndex = 0;
        while (skiIndex < ski.length) {
            int tag = ski[skiIndex];
            skiIndex++;
            switch (tag) {
                case GMT0016CspConstant.TAG_CONTAINER:
                    int nameLen = ski[skiIndex];
                    skiIndex++;
                    byte[] name = new byte[nameLen];
                    System.arraycopy(ski, skiIndex, name, 0, nameLen);
                    skiIndex += nameLen;
                    sContainerName = new String(name);
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_SIGN_FLAG:
                    int flagLen = ski[skiIndex];
                    skiIndex++;
                    bSignFlag = (ski[skiIndex] == (byte)1); //Sign Flag
                    skiIndex += flagLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_CIPHER_DATA:
                    int dataLen = ski[skiIndex];
                    skiIndex++;
                    cipher = new byte[dataLen];
                    System.arraycopy(ski, skiIndex, cipher, 0, dataLen);
                    skiIndex += dataLen;
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_HASH:
                    int hashLen = ski[skiIndex];
                    skiIndex++;
                    hash = new byte[hashLen];
                    System.arraycopy(ski, skiIndex, hash, 0, hashLen);
                    skiIndex += hashLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_TYPE:
                    int typelen = ski[skiIndex];
                    type = ski[++skiIndex];
                    skiIndex += typelen;
                    break;
                default:
                    break;
            }
        }

        if (type == 1 && bSignFlag)
        {
            RSAImpl rsa = new RSAImpl();
            byte[] signature = rsa.getRSASign(digest, sContainerName, gmt0016FactoryOpts);
            return signature;
        }else if (type == 2 && bSignFlag ){
            ECImpl ec = new ECImpl();
            byte[] signature = ec.getECSign(digest, sContainerName, gmt0016FactoryOpts);
            return signature;
        }

        csplog.setLogMsg("[JC_SKF]: Sign Param Err!", 2, GMT0016Csp.class);
        return null;
    }


    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        String sContainerName = "";
        byte[] ski = key.ski();
        boolean bSignFlag = true;
        byte[] cipher;
        byte[] hash;
        int type = 0;
        //analyze ski
        int skiIndex = 0;
        while (skiIndex < ski.length) {
            int tag = ski[skiIndex];
            skiIndex++;
            switch (tag) {
                case GMT0016CspConstant.TAG_CONTAINER:
                    int nameLen = ski[skiIndex];
                    skiIndex++;
                    byte[] name = new byte[nameLen];
                    System.arraycopy(ski, skiIndex, name, 0, nameLen);
                    skiIndex += nameLen;
                    sContainerName = new String(name);
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_SIGN_FLAG:
                    int flagLen = ski[skiIndex];
                    skiIndex++;
                    bSignFlag = (ski[skiIndex] == (byte)1); //Sign Flag
                    skiIndex += flagLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_CIPHER_DATA:
                    int dataLen = ski[skiIndex];
                    skiIndex++;
                    cipher = new byte[dataLen];
                    System.arraycopy(ski, skiIndex, cipher, 0, dataLen);
                    skiIndex += dataLen;
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_HASH:
                    int hashLen = ski[skiIndex];
                    skiIndex++;
                    hash = new byte[hashLen];
                    System.arraycopy(ski, skiIndex, hash, 0, hashLen);
                    skiIndex += hashLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_TYPE:
                    int typelen = ski[skiIndex];
                    type = ski[++skiIndex];
                    skiIndex += typelen;
                    break;
                default:
                    break;
            }
        }

        if (type == 1 && bSignFlag)
        {
            RSAImpl rsa = new RSAImpl();
            boolean rv = rsa.getRSAVerify(signature, digest, sContainerName, gmt0016FactoryOpts);
        }else if (type == 2 && bSignFlag ){
            ECImpl ec = new ECImpl();
            boolean rv = ec.getECverify(signature, digest, sContainerName, gmt0016FactoryOpts);
        }

        csplog.setLogMsg("[JC_SKF]: Verify Param Err!", 2, GMT0016Csp.class);
        return false;
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {

        if (key instanceof SymmCspKey) {
            SymmCspKey sKey = (SymmCspKey) key;
            long lSessionHandle = sKey.getHandle();
            SymmetryImpl sym = new SymmetryImpl();
            if (opts instanceof SM1Opts.SM1EncrypterOpts)
            {
                byte[] ciphertext = sym.SymmetryEncrypt(plaintext, lSessionHandle,
                        ((SM1Opts.SM1EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }
            else if (opts instanceof SM4Opts.SM4EncrypterOpts)
            {
                byte[] ciphertext = sym.SymmetryEncrypt(plaintext, lSessionHandle,
                        ((SM4Opts.SM4EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }
            else if (opts instanceof SSF33Opts.SSF33EncrypterOpts)
            {
                byte[] ciphertext = sym.SymmetryEncrypt(plaintext, lSessionHandle,
                        ((SSF33Opts.SSF33EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }else {
                csplog.setLogMsg("[JC_SKF]: No Support Opts", 2, GMT0016Csp.class);
                throw new JavaChainException("[JC_SKF]: No Support Opts, Param Err!");
            }
        } else {
            csplog.setLogMsg("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!", 2, GMT0016Csp.class);
            throw new JavaChainException("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!");
        }

    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {

        if (key instanceof SymmCspKey) {
            SymmCspKey sKey = (SymmCspKey) key;
            long lSessionHandle = sKey.getHandle();
            SymmetryImpl sym = new SymmetryImpl();
            if (opts instanceof SM1Opts.SM1DecrypterOpts)
            {
                byte[] plaintext = sym.SymmetryDecrypt(ciphertext, lSessionHandle,
                        ((SM1Opts.SM1DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }
            else if (opts instanceof SM4Opts.SM4DecrypterOpts)
            {
                byte[] plaintext = sym.SymmetryDecrypt(ciphertext, lSessionHandle,
                        ((SM4Opts.SM4DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }
            else if (opts instanceof SSF33Opts.SSF33DecrypterOpts)
            {
                byte[] plaintext = sym.SymmetryDecrypt(ciphertext, lSessionHandle,
                        ((SSF33Opts.SSF33DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }else{
                csplog.setLogMsg("[JC_SKF]: No Support Opts", 2, GMT0016Csp.class);
                throw new JavaChainException("[JC_SKF]: No Support Opts, Param Err!");
            }

        } else {
            csplog.setLogMsg("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!", 2, GMT0016Csp.class);
            throw new JavaChainException("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!");
        }

    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        //byte[] none=new SecureRandom().engineGenerateSeed(len);
        byte[] random = gmt0016FactoryOpts.getSKFFactory().SKF_GenRandom(gmt0016FactoryOpts.getDevHandle(), len);
        return random;
    }
}
