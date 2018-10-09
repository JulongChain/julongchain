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

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECImpl;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.AnalyzeSKI;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016KeyData;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAImpl;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAImportOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.*;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMDigest;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMHashOpts;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;

import java.util.UUID;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.TYPE_RSA;
import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.TYPE_SM2;

/**
 * 基于GMT0016实现的CSP实现
 *
 * @author Ying Xu
 * @date 7/3/18
 * @company FEITIAN
 */
public class GMT0016Csp implements IGMT0016Csp {

    private IGMT0016FactoryOpts gmt0016FactoryOpts;
    GMT0016CspLog csplog = new GMT0016CspLog();

    /**
     * 初始cspfactory
     * @param gmt0016FactoryOpts
     */
    GMT0016Csp(IGMT0016FactoryOpts gmt0016FactoryOpts)
    {
        this.gmt0016FactoryOpts = gmt0016FactoryOpts;
    }

    @Override
    public void finalized() throws CspException {

        gmt0016FactoryOpts.getSKFFactory().SKF_DisconnectDev(gmt0016FactoryOpts.getDevHandle());
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws CspException {
        if (opts == null) {
            csplog.setLogMsg("[JC_SKF]:keyGenParam Err!", csplog.LEVEL_ERROR, GMT0016Csp.class);
            throw new CspException("[JC_SKF]:keyGenParam Err!");
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

        csplog.setLogMsg("[JC_SKF]:No Support KeyGen Opts!", csplog.LEVEL_INFO, GMT0016Csp.class);
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws CspException {

        throw new CspException("[JC_SKF]:No Support keyDeriv Impl!");

    }


    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws CspException {

        if(raw == null || opts == null)
        {
            csplog.setLogMsg("[JC_SKF]:keyImportParam Err!", csplog.LEVEL_ERROR, GMT0016Csp.class);
            throw new CspException("[JC_SKF]:Param Err!");
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

        csplog.setLogMsg("[JC_SKF]:No Support KeyImport Opts!", csplog.LEVEL_INFO, GMT0016Csp.class);
        return null;
    }


    @Override
    public IKey getKey(byte[] ski) throws CspException {

        int type = 0;
        //解析 ski
        AnalyzeSKI alzSKI = new AnalyzeSKI();
        alzSKI.analyzeSKI(ski);
        String sContainerName = alzSKI.getContainerName();
        boolean bSignFlag = alzSKI.getSignFlag();
        //byte[] cipher = alzSKI.getCipher();
        //byte[] hash = alzSKI.getHash();

        if (type == TYPE_RSA)
        {
            RSAImpl rsa = new RSAImpl();
            IKey key = rsa.getRSAKey(sContainerName, bSignFlag, gmt0016FactoryOpts);
            return key;
        }else if(type == TYPE_SM2){
            ECImpl ecc = new ECImpl();
            IKey key = ecc.getECKey(sContainerName, bSignFlag, gmt0016FactoryOpts);
            return key;
        }

        return null;
    }


    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws CspException {

        if(opts instanceof GMHashOpts.SM3SignPreOpts)
        {
            GMHashOpts.SM3SignPreOpts sm3opts = (GMHashOpts.SM3SignPreOpts)opts;
            byte[] ski = sm3opts.getSki();
            //解析 ski
            AnalyzeSKI alzSKI = new AnalyzeSKI();
            alzSKI.analyzeSKI(ski);
            String sContainerName = alzSKI.getContainerName();
            boolean bSignFlag = alzSKI.getSignFlag();
            //byte[] cipher = alzSKI.getCipher();
            //byte[] hash = alzSKI.getHash();
            int type = alzSKI.getType();

            if(type != TYPE_SM2) {
                csplog.setLogMsg("[JC_SKF]: The Publickey is not sm2 type! Param Err!", csplog.LEVEL_ERROR, GMT0016Csp.class);
                throw new CspException("[JC_SKF]: The Publickey is not sm2 type! Param Err!");
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
    public IHash getHash(IHashOpts opts) throws CspException {

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
                }catch(CspException ex) {
                    ;
                }
                return null;
            }

            public void reset() {
                byte[] buffer = new byte[GMT0016CspConstant.BUFFERSIZE];
                try {
                    byte[] digest = hash(buffer, opts);
                }catch(CspException ex) {
                    ex.printStackTrace();
                    String err = String.format("[JC_SKF]:SarException ErrMessage: %s", ex.getMessage());
                    csplog.setLogMsg(err, csplog.LEVEL_ERROR, GMT0016Csp.class);
                }
            }

            public int size() {
                return 16;
            }

            public int blockSize() {
                return GMT0016CspConstant.BUFFERSIZE;
            }
        };

        return hash;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws CspException {

        byte[] ski = key.ski();
        //解析 ski
        AnalyzeSKI alzSKI = new AnalyzeSKI();
        alzSKI.analyzeSKI(ski);
        String sContainerName = alzSKI.getContainerName();
        boolean bSignFlag = alzSKI.getSignFlag();
        //byte[] cipher = alzSKI.getCipher();
        //byte[] hash = alzSKI.getHash();
        int type = alzSKI.getType();

        if (type == TYPE_RSA && bSignFlag)
        {
            RSAImpl rsa = new RSAImpl();
            byte[] signature = rsa.getRSASign(digest, sContainerName, gmt0016FactoryOpts);
            return signature;
        }else if (type == TYPE_SM2 && bSignFlag ){
            ECImpl ec = new ECImpl();
            byte[] signature = ec.getECSign(digest, sContainerName, gmt0016FactoryOpts);
            return signature;
        }

        csplog.setLogMsg("[JC_SKF]: Sign Param Err!",  csplog.LEVEL_ERROR, GMT0016Csp.class);
        return null;
    }


    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws CspException {

        byte[] ski = key.ski();
        //解析 ski
        AnalyzeSKI alzSKI = new AnalyzeSKI();
        alzSKI.analyzeSKI(ski);
        String sContainerName = alzSKI.getContainerName();
        boolean bSignFlag = alzSKI.getSignFlag();
        //byte[] cipher = alzSKI.getCipher();
        //byte[] hash = alzSKI.getHash();
        int type = alzSKI.getType();

        if (type == TYPE_RSA && bSignFlag)
        {
            RSAImpl rsa = new RSAImpl();
            boolean rv = rsa.getRSAVerify(signature, digest, sContainerName, gmt0016FactoryOpts);
            return rv;
        }else if (type == TYPE_SM2 && bSignFlag ){
            ECImpl ec = new ECImpl();
            boolean rv = ec.getECVerify(signature, digest, sContainerName, gmt0016FactoryOpts);
            return rv;
        }

        csplog.setLogMsg("[JC_SKF]: Verify Param Err!", csplog.LEVEL_ERROR, GMT0016Csp.class);
        return false;
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws CspException {

        if (key instanceof SymmCspKey) {
            SymmCspKey sKey = (SymmCspKey) key;
            long lSessionHandle = sKey.getHandle();
            SymmetryImpl sym = new SymmetryImpl();
            if (opts instanceof SM1Opts.SM1EncrypterOpts)
            {
                byte[] ciphertext = sym.symmetryEncrypt(plaintext, lSessionHandle,
                        ((SM1Opts.SM1EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }
            else if (opts instanceof SM4Opts.SM4EncrypterOpts)
            {
                byte[] ciphertext = sym.symmetryEncrypt(plaintext, lSessionHandle,
                        ((SM4Opts.SM4EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }
            else if (opts instanceof SSF33Opts.SSF33EncrypterOpts)
            {
                byte[] ciphertext = sym.symmetryEncrypt(plaintext, lSessionHandle,
                        ((SSF33Opts.SSF33EncrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return ciphertext;
            }else {
                csplog.setLogMsg("[JC_SKF]: No Support Opts", csplog.LEVEL_ERROR, GMT0016Csp.class);
                throw new CspException("[JC_SKF]: No Support Opts, Param Err!");
            }
        } else {
            csplog.setLogMsg("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!", csplog.LEVEL_ERROR, GMT0016Csp.class);
            throw new CspException("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!");
        }

    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws CspException {

        if (key instanceof SymmCspKey) {
            SymmCspKey sKey = (SymmCspKey) key;
            long lSessionHandle = sKey.getHandle();
            SymmetryImpl sym = new SymmetryImpl();
            if (opts instanceof SM1Opts.SM1DecrypterOpts)
            {
                byte[] plaintext = sym.symmetryDecrypt(ciphertext, lSessionHandle,
                        ((SM1Opts.SM1DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }
            else if (opts instanceof SM4Opts.SM4DecrypterOpts)
            {
                byte[] plaintext = sym.symmetryDecrypt(ciphertext, lSessionHandle,
                        ((SM4Opts.SM4DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }
            else if (opts instanceof SSF33Opts.SSF33DecrypterOpts)
            {
                byte[] plaintext = sym.symmetryDecrypt(ciphertext, lSessionHandle,
                        ((SSF33Opts.SSF33DecrypterOpts) opts).getBlockChipher(),gmt0016FactoryOpts);
                return plaintext;
            }else{
                csplog.setLogMsg("[JC_SKF]: No Support Opts", csplog.LEVEL_ERROR, GMT0016Csp.class);
                throw new CspException("[JC_SKF]: No Support Opts, Param Err!");
            }

        } else {
            csplog.setLogMsg("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!", csplog.LEVEL_ERROR, GMT0016Csp.class);
            throw new CspException("[JC_SKF]: Ikey is not the instance of SymmCspKey, No support!");
        }

    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws CspException {
        //byte[] none=new SecureRandom().engineGenerateSeed(len);
        byte[] random = gmt0016FactoryOpts.getSKFFactory().SKF_GenRandom(gmt0016FactoryOpts.getDevHandle(), len);
        return random;
    }
    

}
