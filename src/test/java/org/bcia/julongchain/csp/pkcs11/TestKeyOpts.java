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
package org.bcia.julongchain.csp.pkcs11;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesEncrypterOpts;
import org.bcia.julongchain.csp.pkcs11.aes.AesOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaSignOpts;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11KeyData;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaEncrypterOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaSignOpts;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11HashOpts;
import org.junit.Before;
import org.junit.Test;

/**
 * Class description
 *
 * @author
 * @date 5/27/18
 * @company FEITIAN
 */
public class TestKeyOpts {
    int secLevel= 5;
    String hashFamily="MD";
    String keyStorePath=null;
    String Library=null;
    String Label=null;
    String SN=null;
    String Pin=null;
    boolean bSensitive=false;
    boolean bSoftVerify=false;
    PKCS11Lib findlib = null;
    PKCS11Config findconf = null;
    IPKCS11FactoryOpts iPKCS11FactoryOpts = null;
    PKCS11CspFactory cspfactory = null;
    ICsp csp = null;
    @Before
    public void before() {
        try {
            findlib = new PKCS11Lib(Library, Label, SN, Pin);
            findconf = new PKCS11Config(secLevel, hashFamily, bSoftVerify, bSensitive);
            iPKCS11FactoryOpts = new PKCS11FactoryOpts(findlib, findconf);
            cspfactory = new PKCS11CspFactory();
            csp = cspfactory.getCsp(iPKCS11FactoryOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static boolean compereByteArray(byte[] b1, byte[] b2) {
	
	       if(b1.length == 0 || b2.length == 0 ){
	           return false;
	       }
	
	       if (b1.length != b2.length) {
	           return false;
	       }
	
	       boolean isEqual = true;
	       for (int i = 0; i < b1.length && i < b2.length; i++) {
	           if (b1[i] != b2[i]) {
	               System.out.println("different");
	               isEqual = false;
	               break;
	           }
	       }
	       return isEqual;
    }

    @Test
    public void test1() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
			if(!compereByteArray(mykey.toBytes(),mykey1.toBytes()))
			{
				 return;
			}
			
			String input = "Hello world !";
			String input1 = "TOM";
			String input2 = "Hello world !TOM";			
			PKCS11HashOpts.SHA1Opts hashopt_sha1 = new PKCS11HashOpts.SHA1Opts();			
			byte[] bytehash = csp.hash(input2.getBytes(), hashopt_sha1);
			IHash myhash = csp.getHash(hashopt_sha1);
			int len = myhash.write(input.getBytes());
			byte[] bytehash1 = myhash.sum(input1.getBytes());
			if(!compereByteArray(bytehash, bytehash1))
			{
				return;
			}
			
			byte[] signature = csp.sign(mykey, bytehash, RsaSignOpts.SHA1);
			boolean bverify = csp.verify(mykey, signature, bytehash, RsaSignOpts.SHA1);
			return ;
        } catch (JulongChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
    @Test
    public void test2() {

        try {
        	
        	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(key.getPrivate().getEncoded());
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
        	
        	String input2 = "Hello world !TOM";			
			PKCS11HashOpts.SHA1Opts hashopt_sha1 = new PKCS11HashOpts.SHA1Opts();			
			byte[] bytehash = csp.hash(input2.getBytes(), hashopt_sha1);
			
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(key.getPrivate());			
            signature.update(bytehash);
		    byte[] signvalue1 = signature.sign();		
			
        	byte[] signvalue = csp.sign(mykey, bytehash, RsaSignOpts.SHA1);        	
        	if(!compereByteArray(signvalue, signvalue1))
			{
				return;
			}
			boolean bverify = csp.verify(mykey, signvalue, bytehash, RsaSignOpts.SHA1);
			
			signature.initVerify(key.getPublic());  
			signature.update(bytehash); 
			boolean bverify1 = signature.verify(signvalue);
			
			return;
            
        } catch (JulongChainException | InvalidKeyException |NoSuchAlgorithmException| SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test3() {
    	
    	try {
    		IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
			if(!compereByteArray(mykey.toBytes(),mykey1.toBytes()))
			{
				 return;
			}
            
            String input = "Hello world !";
			String input1 = "TOM";
			String input2 = "Hello world !TOM";		
			
			byte[] ciphertext = csp.encrypt(mykey, input2.getBytes(), RsaEncrypterOpts.PKCS1_Pub);			
			byte[] data = csp.decrypt(mykey, ciphertext, RsaDecrypterOpts.PKCS1_Prv);
			boolean rv = compereByteArray(input2.getBytes(),data);
			
			//no support private key encrypt
			/*
			byte[] ciphertext1 = csp.encrypt(mykey, input2.getBytes(), RsaEncrypterOpts.PKCS1_Prv);
			byte[] data1 = csp.decrypt(mykey, ciphertext1, RsaDecrypterOpts.PKCS1_Pub);
			boolean rv1 = compereByteArray(input2.getBytes(),data1);
			*/
			return ;
    	}catch(JulongChainException e) {
    		e.printStackTrace();
    	}
    }
    
    @Test
    public void test4() {
    	try {
        	
        	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(key.getPrivate().getEncoded());
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			
			IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			
        	
			String input = "Hello world !";
			String input1 = "TOM";
			String input2 = "Hello world !TOM";		
			
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key.getPublic()); 
			//cipher.update(input2.getBytes());
			byte[] ciphertext1 = cipher.doFinal(input2.getBytes());
			
			cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
			cipher.update(ciphertext1); 
			byte[] data1 = cipher.doFinal();
			
			byte[] ciphertext = csp.encrypt(mykey, input2.getBytes(), RsaEncrypterOpts.NoPad_Pub);
			byte[] data = csp.decrypt(mykey, ciphertext, RsaDecrypterOpts.NoPad_Prv);
			boolean rv1 = compereByteArray(ciphertext,ciphertext1);
			boolean rv2 = compereByteArray(data,data1);
			boolean rv = compereByteArray(input2.getBytes(),data);
			return;
			
    	} catch (JulongChainException | NoSuchAlgorithmException|NoSuchPaddingException
    			|InvalidKeyException|BadPaddingException|IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test5() {

    	try {
            IKeyGenOpts opts = new EcdsaOpts.ECDSA256KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
            
            String input = "Hello world !";
			String input1 = "TOM";
			String input2 = "Hello world !TOM";			
			PKCS11HashOpts.SHA1Opts hashopt_sha1 = new PKCS11HashOpts.SHA1Opts();			
			byte[] bytehash = csp.hash(input2.getBytes(), hashopt_sha1);
			IHash myhash = csp.getHash(hashopt_sha1);
			int len = myhash.write(input.getBytes());
			byte[] bytehash1 = myhash.sum(input1.getBytes());
			if(!compereByteArray(bytehash, bytehash1))
			{
				return;
			}
			
			byte[] signvalue = csp.sign(mykey, bytehash, EcdsaSignOpts.SHA1); 
			boolean bverify = csp.verify(mykey, signvalue, bytehash, EcdsaSignOpts.SHA1);
			return;
			
        } catch (JulongChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test6() {

		try {
		    //soft gen keypair    	
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
		    ECGenParameterSpec ecsp;
		    ecsp = new ECGenParameterSpec("secp256r1");
		    kpg.initialize(ecsp);
		    KeyPair key = kpg.genKeyPair();
			//import keypair to key
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(key.getPrivate().getEncoded());
			keyraw.setRawPub(key.getPublic().getEncoded());			
			IKeyImportOpts opts = new EcdsaOpts.ECDSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			//hash
			String input2 = "Hello world !TOM";			
			PKCS11HashOpts.SHA1Opts hashopt_sha1 = new PKCS11HashOpts.SHA1Opts();			
			byte[] bytehash = csp.hash(input2.getBytes(), hashopt_sha1);
			//sign and verify
			byte[] signvalue = csp.sign(mykey, bytehash, EcdsaSignOpts.SHA1);        
			boolean bverify = csp.verify(mykey, signvalue, bytehash, EcdsaSignOpts.SHA1);
			//make sign ans.1 for soft verify
			byte[] temp = new byte[72]; // maybe 70 len
			temp[0] = 0x30;
			temp[1] = 0x46;
			temp[2] = 0x02;
			temp[3] = 0x21;
			temp[4] = 0x00;		// maybe no need
			System.arraycopy(signvalue, 0, temp, 5, 32);				
			temp[37] = 0x02;
			temp[38] = 0x21;
			temp[39] = 0x00;	// maybe no need			
			System.arraycopy(signvalue, 32, temp, 40, 32);			
			//soft sign and verify
			Signature signature = Signature.getInstance("SHA1withECDSA");
			signature.initSign(key.getPrivate());			
		    signature.update(input2.getBytes());
		    byte[] signvalue1 = signature.sign();
			signature.initVerify(key.getPublic());  
			signature.update(input2.getBytes()); 
			boolean bverify1 = signature.verify(signvalue1);
			// verfiy hard sign value
			boolean bverify2 = signature.verify(temp);
			return;
		    
		} catch (JulongChainException | InvalidKeyException|InvalidAlgorithmParameterException
				|NoSuchAlgorithmException| SignatureException|NoSuchProviderException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
    }
    
    @Test
    public void test7() {
    	try {
    		IKeyGenOpts opts = new EcdsaOpts.ECDSA256KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mynewkey = csp.keyDeriv(mykey, new EcdsaOpts.EcdsaHardPriKeyOpts(false));
            
            String input = "Hello world !";
			String input1 = "TOM";
			String input2 = "Hello world !TOM";			
			PKCS11HashOpts.SHA1Opts hashopt_sha1 = new PKCS11HashOpts.SHA1Opts();			
			byte[] bytehash = csp.hash(input2.getBytes(), hashopt_sha1);
			IHash myhash = csp.getHash(hashopt_sha1);
			int len = myhash.write(input.getBytes());
			byte[] bytehash1 = myhash.sum(input1.getBytes());
			if(!compereByteArray(bytehash, bytehash1))
			{
				return;
			}
			
			byte[] signvalue = csp.sign(mynewkey, bytehash, EcdsaSignOpts.SHA1); 
			boolean bverify = csp.verify(mynewkey, signvalue, bytehash, EcdsaSignOpts.SHA1);
            return ;
    	} catch (JulongChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test8() {
    	
    	try {
    		IKeyGenOpts opts = new AesOpts.AES128KeyGenOpts(false);
    		IKey mykey = csp.keyGen(opts);
    		IKey mykey1 = csp.getKey(mykey.ski());
    		
    		//byte[] random = csp.rng(250, null);
    		
    		String input2 = "Hello world !TOM";	
    		byte[] ciphertext = csp.encrypt(mykey, input2.getBytes(), AesEncrypterOpts.NoPad_ECB);
    		byte[] data = csp.decrypt(mykey, ciphertext, AesDecrypterOpts.NoPad_ECB);
    		boolean rv = compereByteArray(input2.getBytes(),data);
    		
    		SecretKey secretKey = new SecretKeySpec(mykey.toBytes(), "AES");
    		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey); 
			//cipher.update(input2.getBytes());
			byte[] ciphertext1 = cipher.doFinal(input2.getBytes());
			
    		return;
    	} catch (JulongChainException |NoSuchAlgorithmException| NoSuchPaddingException
    			|InvalidKeyException|BadPaddingException|IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
 


}
