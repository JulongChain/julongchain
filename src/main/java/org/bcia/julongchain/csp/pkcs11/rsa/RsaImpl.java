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

package org.bcia.julongchain.csp.pkcs11.rsa;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CspLog;
import org.bcia.julongchain.csp.pkcs11.util.DataUtil;
import static org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant.*;


import sun.security.pkcs11.wrapper.*;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;


/**
 * RSA密钥实现
 *
 * @author Ying Xu
 * @date 5/20/18
 * @company FEITIAN
 */
public class RsaImpl {
    /**
     * 生成RSA密钥对
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class GenerateRSA{

    	private static byte[] byteSKI;
        private static byte[] pubder;
        
        /**
         * 生成rsa密钥
         *
         * @param keySize     模长
         * @param ephemeral   临时标记
         * @param opts        基于PKCS11的工厂
         * @return 
		 * @throws CspException
         */
        public void generateRsa(int keySize, boolean ephemeral, IPKCS11FactoryOpts opts) throws CspException {

            try {
                opts.getPKCS11().C_Login(opts.getSessionhandle(), PKCS11Constants.CKU_USER, opts.getPin());
                //create bigint
                int iValue =  new Random().nextInt(100);
                String strDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                long lBigInt = Long.parseLong(strDate + iValue);
                String publabel = String.format("BCPUB%s", Long.toHexString(lBigInt));
                String prvlabel = String.format("BCPRV%s", Long.toHexString(lBigInt));

                //create a pubkey attribute
                CK_MECHANISM ckm = new CK_MECHANISM();
                ckm.mechanism = CKM_RSA_PKCS_KEY_PAIR_GEN;
                CK_ATTRIBUTE[] rsaPubObject = new CK_ATTRIBUTE[10];
                rsaPubObject[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY);
                rsaPubObject[1] = new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral);
                rsaPubObject[2] = new CK_ATTRIBUTE(CKA_ENCRYPT, true);
                rsaPubObject[3] = new CK_ATTRIBUTE(CKA_VERIFY, true);
                rsaPubObject[4] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_RSA);
                rsaPubObject[5] = new CK_ATTRIBUTE(CKA_WRAP, true);
                rsaPubObject[6] = new CK_ATTRIBUTE(CKA_MODULUS_BITS, keySize);
                rsaPubObject[7] = new CK_ATTRIBUTE(CKA_ID, publabel);
                rsaPubObject[8] = new CK_ATTRIBUTE(CKA_PUBLIC_EXPONENT, lBigInt);
                rsaPubObject[9] = new CK_ATTRIBUTE(CKA_LABEL, publabel);

                CK_ATTRIBUTE[] rsaPrvObject = new CK_ATTRIBUTE[10];
                rsaPrvObject[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_PRIVATE_KEY);
                rsaPrvObject[1] = new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral);
                rsaPrvObject[2] = new CK_ATTRIBUTE(CKA_DECRYPT, true);
                rsaPrvObject[3] = new CK_ATTRIBUTE(CKA_SIGN, true);
                rsaPrvObject[4] = new CK_ATTRIBUTE(CKA_SIGN_RECOVER, true);
                rsaPrvObject[5] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_RSA);
                rsaPrvObject[6] = new CK_ATTRIBUTE(CKA_MODULUS_BITS, keySize);
                rsaPrvObject[7] = new CK_ATTRIBUTE(CKA_ID, prvlabel);
                rsaPrvObject[8] = new CK_ATTRIBUTE(CKA_LABEL, prvlabel);
                rsaPrvObject[9] = new CK_ATTRIBUTE(CKA_PRIVATE, true);

                long[] keypair =opts.getPKCS11().C_GenerateKeyPair(opts.getSessionhandle(), ckm, rsaPubObject, rsaPrvObject);

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_MODULUS),
                        new CK_ATTRIBUTE(CKA_PUBLIC_EXPONENT),
                };
                opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),keypair[0],
                        attributes);

                BigInteger n = attributes[0].getBigInteger();
                BigInteger e = attributes[1].getBigInteger();
                byteSKI = getPublicHash(n, e);
                pubder =  getPublicDer(n, e);

                CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                setski[0] = new CK_ATTRIBUTE(CKA_ID, byteSKI);
                setski[1] = new CK_ATTRIBUTE(CKA_LABEL, Convert.bytesToHexString(byteSKI));
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[0],setski);
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[1],setski);

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();                
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_GENKEY);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, CLS_GENKEY);
                throw new CspException(err, ex.getCause());
            }
        }
        
        public IKey getIKey(){
        	IKey ikey = new RsaKeyOpts.RsaPubKey(byteSKI, pubder);
        	return ikey;
        }
    }


    /**
     * 导入rsa密钥对
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class ImportKeyRSA{


        /**
         * 导入rsa密钥
         *
         * @param PriRaw        私钥DER编码
         * @param PubRaw        公钥DER编码
         * @param ephemeral     临时标记
         * @param opts          基于PKCS11的工厂
         * @param flagpubkey    公钥标记
         * @return 公钥摘要(cka_id value)
		 * @throws CspException
         */
        public static byte[] importRsaKey(byte[] PriRaw, byte[] PubRaw, boolean ephemeral,
                                          IPKCS11FactoryOpts opts, boolean flagpubkey) throws CspException{

            byte[] byteSKI;
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();

                if(flagpubkey)
                {
                    // decode public key
                    X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(PubRaw);
                    RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
                    // SKI
                    byte[] PublicHash = getPublicHash(pubKey.getModulus(), pubKey.getPublicExponent());
                    byteSKI = new byte[PublicHash.length];
                    System.arraycopy(PublicHash, 0, byteSKI, 0, PublicHash.length);

                    BigInteger bit = pubKey.getModulus().multiply(new BigInteger("8"));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_RSA));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_MODULUS, pubKey.getModulus()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PUBLIC_EXPONENT, pubKey.getPublicExponent()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_ID, PublicHash));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_MODULUS_BITS, bit));
                }
                else
                {
                    opts.getPKCS11().C_Login(opts.getSessionhandle(), PKCS11Constants.CKU_USER, opts.getPin());
                    byteSKI = importRsaKey(null,PubRaw,ephemeral,opts, true);
                    // decode private key
                    //PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(PriRaw);
                    //RSAPrivateKey privKey =  (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
                    RSAPrivateKey privKey = RSAPrivateCrtKeyImpl.newKey(PriRaw);
                    //RSAPrivateCrtKeyImpl privatekeyimpl = (RSAPrivateCrtKeyImpl)privKey1;
                    RSAPrivateCrtKey privatectrlkey = (RSAPrivateCrtKey)privKey;
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_RSA));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PUBLIC_EXPONENT, privatectrlkey.getPublicExponent()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_MODULUS, privatectrlkey.getModulus()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIVATE_EXPONENT, privatectrlkey.getPrivateExponent()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIME_1, privatectrlkey.getPrimeP()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIME_2, privatectrlkey.getPrimeQ()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EXPONENT_1, privatectrlkey.getPrimeExponentP()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EXPONENT_2, privatectrlkey.getPrimeExponentQ()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_COEFFICIENT, privatectrlkey.getCrtCoefficient()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_ID, byteSKI));

                }

                opts.getPKCS11().C_CreateObject(opts.getSessionhandle(), keyTemplate.toArray(new CK_ATTRIBUTE[0]));
                return byteSKI;

            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(InvalidKeySpecException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(PKCS11Exception ex){
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(InvalidKeyException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeyException ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }

        }

        public IKey getKey(byte[] ski, byte[] prider, byte[] pubder) {
            IKey ikey = new RsaKeyOpts.RsaPriKey(ski, prider, new RsaKeyOpts.RsaPubKey(ski, pubder));
            return ikey;
        }

        public IKey getKey(byte[] ski, byte[] pubder) {
            IKey ikey = new RsaKeyOpts.RsaPubKey(ski, pubder);
            return ikey;
        }
    }


    /**
     * 获取RSA密钥
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static final class GetkeyRSA{

        private static byte[] ski;
        private static IPKCS11FactoryOpts opts;

        public GetkeyRSA(byte[] ski, IPKCS11FactoryOpts opts){
            GetkeyRSA.ski = ski;
            GetkeyRSA.opts = opts;
        }

        public static IKey getkey() throws CspException {

            try {
                long[] keypbu = findKeypairFromSKI(opts, false, ski);
                if (keypbu==null || keypbu.length==0) {
                	String str = String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerInfo(str, CLS_GETKEY);
                    return null;
                }
                else
                {
                    CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_MODULUS),
                            new CK_ATTRIBUTE(CKA_PUBLIC_EXPONENT),
                    };
                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(), keypbu[0], attributes);
                    BigInteger n = attributes[0].getBigInteger();
                    BigInteger e = attributes[1].getBigInteger();
                    byte[] pubder =  getPublicDer(n, e);
                    byte[] byteSKI = getPublicHash(n, e);
                    if(!DataUtil.compareByteArray(ski, byteSKI)) {
                    	String str = String.format("[JC_PKCS]:No Find Key");
                        RsaImpl.setLoggerInfo(str, CLS_GETKEY);
                        return null;
                    }
                    IKey ikey = new RsaKeyOpts.RsaPubKey(ski, pubder);
                    return ikey;
                }
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_GETKEY);
                throw new CspException(err, ex.getCause());
            }
        }
    }



    /**
     * 使用私钥签名
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class SignRSAKey{

        public SignRSAKey() {

        }


        /**
         * 签名
         *
         * @param ski           索引值，即cka_id值
         * @param digest        摘要信息
         * @param newMechanism  签名算法
         * @param opts          基于PKCS11的工厂
		 * @return 签名数据
		 * @throws CspException
         */
        public static byte[] signRSA(byte[] ski, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws CspException{
            try {
                opts.getPKCS11().C_Login(opts.getSessionhandle(), PKCS11Constants.CKU_USER, opts.getPin());
                long[] privatekey = findKeypairFromSKI(opts, true, ski);
                if (privatekey==null || privatekey.length==0)
                {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, CLS_SIGN);
                    throw new CspException(str);
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_SignInit(opts.getSessionhandle(), ckMechanism, privatekey[0]);
                byte[] signvalue = opts.getPKCS11().C_Sign(opts.getSessionhandle(), digest);
                return signvalue;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_SIGN);
                throw new CspException(err, ex.getCause());
            }
        }
    }


    /**
     * 使用公钥验签
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class VerifyRSAKey{

        public VerifyRSAKey() {

        }


        /**
         * 验签
         *
         * @param ski           索引值，即cka_id值
         * @param signature     签名数据
         * @param digest        摘要信息
         * @param newMechanism  签名算法
         * @param opts          基于PKCS11的工厂
		 * @return 验签成功
		 * @throws CspException
         */
        public boolean verifyRSA(byte[] ski, byte[] signature, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws CspException{

            try {
                long[] publickey = findKeypairFromSKI(opts, false, ski);
                if (publickey==null || publickey.length==0) {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, CLS_VERIFY);
                    throw new CspException(str);
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_VerifyInit(opts.getSessionhandle(), ckMechanism, publickey[0]);
                opts.getPKCS11().C_Verify(opts.getSessionhandle(), digest, signature);
                return true;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_VERIFY);
                return false;
            }

        }

    }



    /**
     * 指定KEY进行加密
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class EncryptRSAKey{

        public EncryptRSAKey() {

        }

        /**
         * 加密
         *
         * @param ski           索引值，即cka_id值
         * @param plaintext     数据原文
         * @param flagpub       密钥标识（公钥:true 私钥:false）
         * @param mechanism     加密算法
         * @param opts          基于PKCS11的工厂
         * @return 加密数据
		 * @throws CspException
         */
        public byte[] encryptRSA(byte[] ski, byte[] plaintext, boolean flagpub, long mechanism, IPKCS11FactoryOpts opts) throws CspException {
            try {
                long[] enckey;
                if (flagpub) {
                    enckey = findKeypairFromSKI(opts, false, ski);
                }else{
                    opts.getPKCS11().C_Login(opts.getSessionhandle(), PKCS11Constants.CKU_USER, opts.getPin());
                    enckey = findKeypairFromSKI(opts, true, ski);
                }

                if (enckey==null || enckey.length==0) {

                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, CLS_ENCRYPT);
                    throw new CspException(str);
                }

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_MODULUS_BITS),
                };
                opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(), enckey[0], attributes);

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = mechanism;
                opts.getPKCS11().C_EncryptInit(opts.getSessionhandle(), ckMechanism, enckey[0]);

                int outlen =  (int)attributes[0].getLong()/8;
                byte[] cipher = new byte[outlen];
                opts.getPKCS11().C_Encrypt(opts.getSessionhandle(), plaintext, 0, plaintext.length, cipher, 0, outlen);
                return cipher;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_ENCRYPT);
                throw new CspException(err, ex.getCause());
            }

        }

    }



    /**
     * 指定KEY进行解密
     *
     * @author Ying Xu
     * @date 5/20/18
     * @company FEITIAN
     */
    public static class DecryptRSAKey{

        public DecryptRSAKey() {}

        /**
         * 解密
         *
         * @param ski           索引值，即cka_id值
         * @param ciphertext    加密数据
         * @param flagpub       密钥标识（公钥:true 私钥:false）
         * @param mechanism     解密算法
         * @param opts          基于PKCS11的工厂
         * @return plaintext	原文数据
		 * @throws CspException
         */
        public static byte[] decryptRSA(byte[] ski, byte[] ciphertext, boolean flagpub, long mechanism, IPKCS11FactoryOpts opts) throws CspException{
            try {
                long[] deckey;
                if (flagpub) {
                    deckey = findKeypairFromSKI(opts, false, ski);
                }else{
                    opts.getPKCS11().C_Login(opts.getSessionhandle(), PKCS11Constants.CKU_USER, opts.getPin());
                    deckey = findKeypairFromSKI(opts, true, ski);
                }

                if (deckey==null || deckey.length==0) {

                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, CLS_DECRYPT);
                    throw new CspException(str);
                }

                int outlen = 512;

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = mechanism;
                byte[] out = new byte[outlen];

                opts.getPKCS11().C_DecryptInit(opts.getSessionhandle(), ckMechanism, deckey[0]);
                int len = opts.getPKCS11().C_Decrypt(opts.getSessionhandle(), ciphertext, 0, ciphertext.length, out, 0, outlen);
                byte[] data = new byte[len];
                System.arraycopy(out, 0, data, 0, len);

                return data;
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, CLS_DECRYPT);
                throw new CspException(err, ex.getCause());
            }

        }
    }


    /**
     * 根据索引值查找指定的KEY
     * @param opts  基于PKCS11的工厂
     * @param bPri  密钥标识（私钥：true 公钥: false）
     * @param ski   索引值，即cka_id值
     * @return  对象句柄
     * @throws CspException
     */

    private static long[] findKeypairFromSKI(IPKCS11FactoryOpts opts, boolean bPri, byte[] ski) throws CspException{

        long keyclass = CKO_PUBLIC_KEY;
        if(bPri)
        {
            keyclass = CKO_PRIVATE_KEY;
        }

        CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[3];
        template[0] = new CK_ATTRIBUTE(CKA_CLASS, keyclass);
        template[1] = new CK_ATTRIBUTE(CKA_ID, ski);
        template[2] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_RSA);

        try {
            opts.getPKCS11().C_FindObjectsInit(opts.getSessionhandle(), template);
            long[] keypair = opts.getPKCS11().C_FindObjects(opts.getSessionhandle(), 1);
            opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
            return keypair;
        }catch(PKCS11Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            setLoggerErr(err, 0);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new CspException(err, ex.getCause());
        }
    }


    /**
     * 获取ASN.1 格式公钥编码
     * @param n     N
     * @param e     E
     * @return 公钥编码
     * @throws CspException
     */
    private static byte[] getPublicDer(BigInteger n, BigInteger e) throws CspException{
        try {
            RSAPublicKeyImpl rsapublickeyimpl = new RSAPublicKeyImpl(n, e);
            return rsapublickeyimpl.getEncoded();
        }catch( InvalidKeyException ex){
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new CspException(err, ex.getCause());
        }
    }


    /**
     * 获取公钥hash值
     * @param n     N
     * @param e     E
     * @return  公钥hash值
     * @throws CspException
     */
    private static byte[] getPublicHash(BigInteger n, BigInteger e) throws CspException{

        try {
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            byte[] out = getPublicDer(n, e);
            shahash.update(out);
            return shahash.digest();
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new CspException(err, ex.getCause());
        }
    }
    
    
    private static void setLoggerDebug(String msg, int classNO){

        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.GenerateRSA.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.ImportKeyRSA.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.GetkeyRSA.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.SignRSAKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.VerifyRSAKey.class);
                break;
            case CLS_ENCRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.EncryptRSAKey.class);
                break;
            case CLS_DECRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, RsaImpl.class);
                break;
        }
    }

    private static void setLoggerInfo(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.GenerateRSA.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.ImportKeyRSA.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.GetkeyRSA.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.SignRSAKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.VerifyRSAKey.class);
                break;
            case CLS_ENCRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.EncryptRSAKey.class);
                break;
            case CLS_DECRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, RsaImpl.class);
                break;
        }
    }

    private static void setLoggerErr(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.GenerateRSA.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.ImportKeyRSA.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.GetkeyRSA.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.SignRSAKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.VerifyRSAKey.class);
                break;
            case CLS_ENCRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.EncryptRSAKey.class);
                break;
            case CLS_DECRYPT:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, RsaImpl.class);
                break;
        }
    }

}
