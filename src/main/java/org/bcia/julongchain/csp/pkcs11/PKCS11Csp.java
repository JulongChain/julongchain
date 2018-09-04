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
package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;
import org.bcia.julongchain.csp.pkcs11.aes.AesDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesEncrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesImpl;
import org.bcia.julongchain.csp.pkcs11.aes.AesOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaImpl;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaKeyOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaSignOpts;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11KeyData;
import org.bcia.julongchain.csp.pkcs11.rsa.*;
import org.bcia.julongchain.csp.pkcs11.util.DataUtil;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11Digest;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11HashOpts;

import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 * Class description
 *
 * @author Ying Xu
 * @date 2018/05/20
 * @company FEITIAN
 */
public class PKCS11Csp implements IPKCS11Csp {

    PKCS11CspLog csplog = new PKCS11CspLog();
    private IPKCS11FactoryOpts PKCS11FactoryOpts;

    PKCS11Csp(IPKCS11FactoryOpts PKCS11FactoryOpts) {
        this.PKCS11FactoryOpts=PKCS11FactoryOpts;
    }

    @Override
    public void finalized() throws CspException {
        
        PKCS11FactoryOpts.optFinalized();        
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws CspException {
        if (opts == null) {
            csplog.setLogMsg("[JC_PKCS]:KeyGen Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        	throw new CspException("[JC_PKCS]:Param Err!");
        }

        RsaImpl.GenerateRSA genkey;
        IKey key;

        if (opts instanceof RsaOpts.RSA1024KeyGenOpts)
        {
            genkey = new RsaImpl.GenerateRSA();
            genkey.generateRsa(1024, opts.isEphemeral(), PKCS11FactoryOpts);
            key = genkey.getIKey();
            return key;
        }

        if (opts instanceof RsaOpts.RSA2048KeyGenOpts)
        {
            genkey = new RsaImpl.GenerateRSA();
            genkey.generateRsa(2048, opts.isEphemeral(), PKCS11FactoryOpts);
            key = genkey.getIKey();
            return key;
        }
/*
        if (opts instanceof RsaOpts.RSA3072KeyGenOpts)
        {
            genkey = new RsaImpl.GenerateRSA();
            key =  genkey.generateRsa(3072, opts.isEphemeral(), PKCS11FactoryOpts);
            return key;
        }

        if (opts instanceof RsaOpts.RSA4096KeyGenOpts)
        {
            genkey = new RsaImpl.GenerateRSA();
            key =  genkey.generateRsa(4096, opts.isEphemeral(), PKCS11FactoryOpts);
            return key;
        }
*/
        if (opts instanceof EcdsaOpts.ECDSA192KeyGenOpts)
        {
            EcdsaImpl.GenerateECKey eckey = new EcdsaImpl.GenerateECKey();
            eckey.generateECKey(192, opts.isEphemeral(), PKCS11FactoryOpts);
            key = eckey.getIKey();
            return key;
        }
        
        if (opts instanceof EcdsaOpts.ECDSA256KeyGenOpts)
        {
            EcdsaImpl.GenerateECKey eckey = new EcdsaImpl.GenerateECKey();
            eckey.generateECKey(256, opts.isEphemeral(), PKCS11FactoryOpts);
            key = eckey.getIKey();
            return key;
        }
/*
        if (opts instanceof EcdsaOpts.ECDSA384KeyGenOpts)
        {
            EcdsaImpl.GenerateECKey eckey = new EcdsaImpl.GenerateECKey
            eckey.generateECKey(384, opts.isEphemeral(), PKCS11FactoryOpts);
            key = eckey.getIKey();

            return key;
        }
*/
        if (opts instanceof AesOpts.AES128KeyGenOpts) {
            AesImpl.GenerateAES aeskey = new AesImpl.GenerateAES();
            key = aeskey.generateAES(16, opts.isEphemeral(), PKCS11FactoryOpts);
            return key;
        }

        if (opts instanceof AesOpts.AES192KeyGenOpts) {
            AesImpl.GenerateAES aeskey = new AesImpl.GenerateAES();
            key = aeskey.generateAES(24, opts.isEphemeral(), PKCS11FactoryOpts);
            return key;
        }

        if (opts instanceof AesOpts.AES256KeyGenOpts) {
            AesImpl.GenerateAES aeskey = new AesImpl.GenerateAES();
            key = aeskey.generateAES(32, opts.isEphemeral(), PKCS11FactoryOpts);
            return key;
        }

        csplog.setLogMsg("[JC_PKCS]:No Support the Opts for Genkey!", csplog.LEVEL_INFO, PKCS11Csp.class);
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws CspException {
    	
    	if(key instanceof EcdsaKeyOpts.EcdsaPubKey)
    	{
    		if(opts instanceof EcdsaOpts.ECDSAReRandKeyOpts)
    		{    	        
    			EcdsaImpl.DeriveECKey deriv = new EcdsaImpl.DeriveECKey();
    			if(key.ski().length <= 0)
    	        {
                    csplog.setLogMsg("[JC_PKCS]:keyDeriv Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
    	        	throw new CspException("[JC_PKCS]:Param Err!");
    	        }
    	        byte[] byski = new byte[key.ski().length-1];
    	        System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
    	        
    	        EcdsaImpl.ImportECKey importkey = new EcdsaImpl.ImportECKey();
    	        byte[] newski = importkey.importECKey(null,key.toBytes(),opts.isEphemeral(),PKCS11FactoryOpts,false);  
    	        if(!DataUtil.compareByteArray(newski, byski))
    			{
                    csplog.setLogMsg("[JC_PKCS]:Import key value err! Can not continue to KeyDeriv!", csplog.LEVEL_ERROR, PKCS11Csp.class);
    				throw new CspException("[JC_PKCS]:Import key value err! Can not continue to KeyDeriv!");
    			}
    	        
    			IKey mykey = deriv.deriveKey(byski, opts.isEphemeral(), false, PKCS11FactoryOpts);
    			return mykey;
    		}else if(opts instanceof EcdsaOpts.EcdsaHardPubKeyOpts){
    			EcdsaImpl.DeriveECKey deriv = new EcdsaImpl.DeriveECKey();
    			if(key.ski().length <= 0)
    	        {
                    csplog.setLogMsg("[JC_PKCS]:keyDeriv Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
    	        	throw new CspException("[JC_PKCS]:Param Err!");
    	        }
    	        byte[] byski = new byte[key.ski().length-1];
    	        System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
    			IKey mykey = deriv.deriveKey(byski, opts.isEphemeral(), false, PKCS11FactoryOpts);
    			return mykey;
    		}else if(opts instanceof EcdsaOpts.EcdsaHardPriKeyOpts){
    			EcdsaImpl.DeriveECKey deriv = new EcdsaImpl.DeriveECKey();
    			if(key.ski().length <= 0)
    	        {
                    csplog.setLogMsg("[JC_PKCS]:keyDeriv Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
    	        	throw new CspException("[JC_PKCS]:Param Err!");
    	        }
    	        byte[] byski = new byte[key.ski().length-1];
    	        System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
    			IKey mykey = deriv.deriveKey(byski, opts.isEphemeral(), true, PKCS11FactoryOpts);
    			return mykey;
    		}
    		else {
                csplog.setLogMsg("[JC_PKCS]:No Support the Opts for keyDeriv!", csplog.LEVEL_INFO, PKCS11Csp.class);
    			return null;
    		}
    	}
    	else if(key instanceof EcdsaKeyOpts.EcdsaPriKey) {
    		
    		if(opts instanceof EcdsaOpts.ECDSAReRandKeyOpts)
    		{
    			EcdsaImpl.DeriveECKey deriv = new EcdsaImpl.DeriveECKey();
    			if(key.ski().length <= 0)
    	        {
                    csplog.setLogMsg("[JC_PKCS]:keyDeriv Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
    	        	throw new CspException("[JC_PKCS]:keyDeriv Param Err!");
    	        }
    	        byte[] byski = new byte[key.ski().length-1];
    	        System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
    	        
    	        EcdsaImpl.ImportECKey importkey = new EcdsaImpl.ImportECKey();
    			byte[] newski = importkey.importECKey(key.toBytes(),key.getPublicKey().toBytes(),opts.isEphemeral(),PKCS11FactoryOpts,false);
    			if(!DataUtil.compareByteArray(newski, byski))
    			{
                    csplog.setLogMsg("[JC_PKCS]:Import key value err! Can not continue to KeyDeriv!", 2, PKCS11Csp.class);
                    throw new CspException("[JC_PKCS]:Import key value err! Can not continue to KeyDeriv!");
    			}
    			
    			IKey mykey = deriv.deriveKey(byski, opts.isEphemeral(), true, PKCS11FactoryOpts);
    			return mykey;
    		}else {
                csplog.setLogMsg("[JC_PKCS]:No Support the Opts for keyDeriv!", csplog.LEVEL_INFO, PKCS11Csp.class);
    			return null;
    		}
    	}

        csplog.setLogMsg("[JC_PKCS]:The Key's Type not EC Key. The Impl Only support EC Key!", csplog.LEVEL_INFO, PKCS11Csp.class);
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws CspException {

        if(raw == null || opts == null)
        {
            csplog.setLogMsg("[JC_PKCS]:KeyImport Param Err!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:Param Err!");
        }

        byte[] ski;
        EcdsaImpl.ImportECKey importkey = new EcdsaImpl.ImportECKey();
        RsaImpl.ImportKeyRSA importrsakey = new RsaImpl.ImportKeyRSA();
        AesImpl.ImoprtAESKey importaeskey = new AesImpl.ImoprtAESKey();
        PKCS11KeyData keyraw = (PKCS11KeyData) raw;
        String Type = opts.getClass().getSimpleName();
        byte[] pubder = (byte[])keyraw.getRawPub();
        byte[] prider = (byte[])keyraw.getRawPri();
        switch(Type) {
            case "ECDSAPrivateKeyImportOpts":
                if(pubder.length<=0 && prider.length<=0)
                {
                    return null;
                }
                ski = importkey.importECKey(prider,pubder,opts.isEphemeral(),PKCS11FactoryOpts,false);
                return importkey.getKey(ski, pubder, prider);
            case "ECDSAPublicKeyImportOpts":
                if(pubder.length<=0)
                {
                    return null;
                }
                ski = importkey.importECKey(null,pubder,opts.isEphemeral(),PKCS11FactoryOpts,true);
                return importkey.getKey(ski, pubder, null);
            case "RSAPublicKeyImportOpts":
                if(pubder.length<=0)
                {
                    return null;
                }
                ski = importrsakey.importRsaKey(null, pubder, opts.isEphemeral(), PKCS11FactoryOpts, true);
                return importrsakey.getKey(ski, pubder);
            case "RSAPrivateKeyImportOpts":
                if(pubder.length<=0 && prider.length<=0)
                {
                    return null;
                }
                ski = importrsakey.importRsaKey(prider, pubder, opts.isEphemeral(), PKCS11FactoryOpts, false);
                return importrsakey.getKey(ski, prider, pubder);
            case "AESKeyImportOpts":
            	if(prider.length<=0)
            	{
            		return null;
            	}
            	return importaeskey.importAES(prider, opts.isEphemeral(), PKCS11FactoryOpts);
        }

        csplog.setLogMsg("[JC_PKCS]:No Support the Opts!", csplog.LEVEL_INFO, PKCS11Csp.class);
        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws CspException {
        IKey key = null;

        if(ski.length <= 0)
        {
            csplog.setLogMsg("The Param Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param Error!");
        }
        byte[] byski = new byte[ski.length-1];
        System.arraycopy(ski, 1, byski, 0, ski.length-1);
        
        if(ski[0] == 0x01) {
            // Find RSA
            RsaImpl.GetkeyRSA getrsa = new RsaImpl.GetkeyRSA(byski, PKCS11FactoryOpts);
            key = getrsa.getkey();
        }

        if(ski[0] == 0x02)
        {
            //Find ECDSA
            EcdsaImpl.GetkeyEcKey getecdsa = new EcdsaImpl.GetkeyEcKey(byski, PKCS11FactoryOpts);
            key = getecdsa.getkey();
        }

        if(ski[0] == 0x03)
        {
            AesImpl.GetAESKey getaes = new AesImpl.GetAESKey();
            key = getaes.getAES(byski, PKCS11FactoryOpts);
        }        
        
        return key;
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws CspException {
        if(opts == null || msg == null)
        {
            csplog.setLogMsg("The Param Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param Error!");
        }

        PKCS11Digest p11digest = null;

        byte[] value;
        //byte[] value1;
        if(opts instanceof PKCS11HashOpts.MD2Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.MD2Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.MD5Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.MD5Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA1Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA1Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA256Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA256Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA384Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA384Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA3_256Opts)
        {
            csplog.setLogMsg("[JC_PKCS]:No Support the Opts with Hardware!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            return null;
        }
        if(opts instanceof PKCS11HashOpts.SHA3_384Opts)
        {
            csplog.setLogMsg("[JC_PKCS]:No Support the Opts with Hardware!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            return null;
        }

        value =  p11digest.getDigest(msg, PKCS11FactoryOpts);
        //value1 =  p11digest.getDigestwithUpdate(msg, PKCS11FactoryOpts);

        return  value;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws CspException {
        if(opts == null)
        {
            csplog.setLogMsg("The Param Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param Error!");
        }

        PKCS11Digest p11digest = null;
        if(opts instanceof PKCS11HashOpts.MD2Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.MD2Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.MD5Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.MD5Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA1Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA1Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA256Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA256Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA384Opts)
        {
            p11digest = new PKCS11Digest(((PKCS11HashOpts.SHA384Opts) opts).getMechanism());
        }
        if(opts instanceof PKCS11HashOpts.SHA3_256Opts)
        {
            //Hard No Support
            csplog.setLogMsg("[JC_PKCS]:No Support the Opts with Hardware!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            return null;
        }
        if(opts instanceof PKCS11HashOpts.SHA3_384Opts)
        {
        	//Hard No Support
            csplog.setLogMsg("[JC_PKCS]:No Support the Opts with Hardware!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            return null;
        }

        IHash hash = p11digest.getHash(PKCS11FactoryOpts);

        return hash;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws CspException {
        if(opts == null)
        {
            csplog.setLogMsg("The Param For Sign is Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param For Sign is Error!");
        }

        // RSA sign
        if (opts instanceof RsaSignOpts) {
            RsaSignOpts rsaopts = (RsaSignOpts)opts;
            RsaImpl.SignRSAKey rsasign = new RsaImpl.SignRSAKey();
            
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] signature = rsasign.signRSA(byski, digest, rsaopts.getmechanism(), PKCS11FactoryOpts);
            //byte[] signature1 = rsasign.signRSA(key, digest, rsaopts.getAlgorithm(), PKCS11FactoryOpts);
            return signature;
        }

        // ECDSA
        if (opts instanceof EcdsaSignOpts) {
            EcdsaSignOpts ecdsaopts = (EcdsaSignOpts)opts;
            EcdsaImpl.SignECKey ecdsasign = new EcdsaImpl.SignECKey();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] signature = ecdsasign.signECDSA(byski, digest, ecdsaopts.getmechanism(), PKCS11FactoryOpts);
            //byte[] signature = ecdsasign.signECDSA(key, digest, ecdsaopts.getAlgorithm(), PKCS11FactoryOpts);
            return signature;
        }

        csplog.setLogMsg("No Support the Opts for Sign!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        throw new CspException("[JC_PKCS]:No Support the Opts for Sign!");

    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws CspException {
        if(opts == null)
        {
            csplog.setLogMsg("The Param For Verify is Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param For Verify is Error!");
        }

        // RSA
        if (opts instanceof RsaSignOpts) {
            RsaSignOpts rsaopts = (RsaSignOpts)opts;
            RsaImpl.VerifyRSAKey rsaverify = new RsaImpl.VerifyRSAKey();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            boolean bver = rsaverify.verifyRSA(byski, signature, digest, rsaopts.getmechanism(), PKCS11FactoryOpts);
            return bver;
        }

        // ECDSA
        if (opts instanceof EcdsaSignOpts) {
            EcdsaSignOpts ecdsaopts = (EcdsaSignOpts)opts;
            EcdsaImpl.VerifyECKey ecdsaverify = new EcdsaImpl.VerifyECKey();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            boolean bver = ecdsaverify.verifyECDSA(byski, signature, digest, ecdsaopts.getmechanism(), PKCS11FactoryOpts);
            return bver;
        }

        csplog.setLogMsg("No Support the Opts for Verify!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        throw new CspException("[JC_PKCS]:No Support the Opts for Verify!");
    }


    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws CspException {
        if(opts == null)
        {
        	csplog.setLogMsg("The Param For Encrypt is Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        	throw new CspException("[JC_PKCS]:The Param For Encrypt is Error!");
        }

        if (opts instanceof RsaEncrypterOpts) {
            RsaEncrypterOpts rsaencopt = (RsaEncrypterOpts)opts;
            RsaImpl.EncryptRSAKey rsaencrypt = new RsaImpl.EncryptRSAKey();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] ciphertext = rsaencrypt.encryptRSA(byski, plaintext, rsaencopt.getFlagpub(), rsaencopt.getMechanism(), PKCS11FactoryOpts);
            return ciphertext;
        }

        if (opts instanceof AesEncrypterOpts) {
            AesEncrypterOpts aesencopt = (AesEncrypterOpts)opts;
            AesImpl.EncryptAES aesencrypt = new AesImpl.EncryptAES();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] ciphertext = aesencrypt.encrtyptWithAES(byski, aesencopt.getMechanism(), plaintext, PKCS11FactoryOpts);
            return ciphertext;
        }

        csplog.setLogMsg("No Support the Opts for Encrypt!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        throw new CspException("[JC_PKCS]:No Support the Opts for Encrypt!");

    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws CspException {
        if(opts == null)
        {
            csplog.setLogMsg("The Param For Decrypt is Error!", csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException("[JC_PKCS]:The Param For Decrypt is Error!");
        }

        if (opts instanceof RsaDecrypterOpts) {
            RsaDecrypterOpts rsadecopt = (RsaDecrypterOpts)opts;
            RsaImpl.DecryptRSAKey rsadecrypt = new RsaImpl.DecryptRSAKey();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] plaintext = rsadecrypt.decryptRSA(byski, ciphertext, rsadecopt.getFlagpub(), rsadecopt.getMechanism(), PKCS11FactoryOpts);
            return plaintext;
        }

        if (opts instanceof AesDecrypterOpts) {
            AesDecrypterOpts aesdecopt = (AesDecrypterOpts)opts;
            AesImpl.DecryptAES aesdecrypt = new AesImpl.DecryptAES();
            byte[] byski = new byte[key.ski().length-1];
            System.arraycopy(key.ski(), 1, byski, 0, key.ski().length-1);
            byte[] plaintext = aesdecrypt.decryptWithAES(byski, aesdecopt.getMechanism(), ciphertext, PKCS11FactoryOpts);
            return plaintext;
        }

        csplog.setLogMsg("No Support the Opts for Decrypt!", csplog.LEVEL_ERROR, PKCS11Csp.class);
        throw new CspException("[JC_PKCS]:No Support the Opts for Decrypt!");
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws CspException {
        //byte[] none=new SecureRandom().engineGenerateSeed(len);
        //return none;
    	try {
	    	byte[] random = new byte[len];
	    	//PKCS11FactoryOpts.getPKCS11().C_SeedRandom(PKCS11FactoryOpts.getSessionhandle(), arg1);
	    	PKCS11FactoryOpts.getPKCS11().C_GenerateRandom(PKCS11FactoryOpts.getSessionhandle(), random);
	    	return random;
    	} catch(PKCS11Exception ex) {
    		ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, PKCS11Csp.class);
            throw new CspException(err, ex.getCause());
    	}
    	
    }
}
