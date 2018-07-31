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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CspLog;
import org.bcia.julongchain.csp.pkcs11.util.DataUtil;


import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Exception;
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
 * RSA Keypair Impl
 *
 * @author xuying
 * @date 2018/05/20
 * @company FEITIAN
 */
public class RsaImpl {

    private static void setLoggerDebug(String msg, int classNO){

        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case 1:
                csplog.setLogMsg(msg, 0, RsaImpl.GenerateRSA.class);
                break;
            case 2:
                csplog.setLogMsg(msg, 0, RsaImpl.ImportKeyRSA.class);
                break;
            case 3:
                csplog.setLogMsg(msg, 0, RsaImpl.GetkeyRSA.class);
                break;
            case 4:
                csplog.setLogMsg(msg, 0, RsaImpl.SignRSAKey.class);
                break;
            case 5:
                csplog.setLogMsg(msg, 0, RsaImpl.VerifyRSAKey.class);
                break;
            case 6:
                csplog.setLogMsg(msg, 0, RsaImpl.EncryptRSAKey.class);
                break;
            case 7:
                csplog.setLogMsg(msg, 0, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, 0, RsaImpl.class);
                break;
        }
    }

    private static void setLoggerInfo(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case 1:
                csplog.setLogMsg(msg, 1, RsaImpl.GenerateRSA.class);
                break;
            case 2:
                csplog.setLogMsg(msg, 1, RsaImpl.ImportKeyRSA.class);
                break;
            case 3:
                csplog.setLogMsg(msg, 1, RsaImpl.GetkeyRSA.class);
                break;
            case 4:
                csplog.setLogMsg(msg, 1, RsaImpl.SignRSAKey.class);
                break;
            case 5:
                csplog.setLogMsg(msg, 1, RsaImpl.VerifyRSAKey.class);
                break;
            case 6:
                csplog.setLogMsg(msg, 1, RsaImpl.EncryptRSAKey.class);
                break;
            case 7:
                csplog.setLogMsg(msg, 1, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, 1, RsaImpl.class);
                break;
        }
    }

    private static void setLoggerErr(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case 1:
                csplog.setLogMsg(msg, 2, RsaImpl.GenerateRSA.class);
                break;
            case 2:
                csplog.setLogMsg(msg, 2, RsaImpl.ImportKeyRSA.class);
                break;
            case 3:
                csplog.setLogMsg(msg, 2, RsaImpl.GetkeyRSA.class);
                break;
            case 4:
                csplog.setLogMsg(msg, 2, RsaImpl.SignRSAKey.class);
                break;
            case 5:
                csplog.setLogMsg(msg, 2, RsaImpl.VerifyRSAKey.class);
                break;
            case 6:
                csplog.setLogMsg(msg, 2, RsaImpl.EncryptRSAKey.class);
                break;
            case 7:
                csplog.setLogMsg(msg, 2, RsaImpl.DecryptRSAKey.class);
                break;
            default:
                csplog.setLogMsg(msg, 2, RsaImpl.class);
                break;
        }
    }
    /**
     * Generate RSA Keypair
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class GenerateRSA{

        public GenerateRSA() {

        }

        /**
         * 生成rsa密钥
         *
         * @param keySize     模长
         * @param ephemeral   临时标记
         * @param opts        p11 factory
         * @return key
         */
        public static IKey generateRsa(int keySize, boolean ephemeral, IPKCS11FactoryOpts opts) throws JavaChainException {

            try {
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
                byte[] PublicHash = getPublicHash(n, e);
                byte[] pubder =  getPublicDer(n, e);

                CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                setski[0] = new CK_ATTRIBUTE(CKA_ID, PublicHash);
                setski[1] = new CK_ATTRIBUTE(CKA_LABEL, DataUtil.MyByteToHex(PublicHash));
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[0],setski);
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[1],setski);

                IKey ikey = new RsaKeyOpts.RsaPubKey(PublicHash, pubder);
                return ikey;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();                
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 1);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, 1);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }


    /**
     * Import RSA Keypair
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class ImportKeyRSA{


        /**
         * 导入rsa密钥
         *
         * @param PriRaw        私钥DER编码
         * @param PubRaw        公钥DER编码
         * @param ephemeral     临时标记
         * @param opts          p11 factory
         * @param flagpubkey    公钥标记
         * @return 公钥摘要(cka_id value)
         */
        public static byte[] importRsaKey(byte[] PriRaw, byte[] PubRaw, boolean ephemeral,
                                          IPKCS11FactoryOpts opts, boolean flagpubkey) throws JavaChainException{

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
                RsaImpl.setLoggerErr(err, 2);
                throw new JavaChainException(err, ex.getCause());
            }catch(InvalidKeySpecException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, 2);
                throw new JavaChainException(err, ex.getCause());
            }catch(PKCS11Exception ex){
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 2);
                throw new JavaChainException(err, ex.getCause());
            }catch(InvalidKeyException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeyException ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, 2);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                RsaImpl.setLoggerErr(err, 2);
                throw new JavaChainException(err, ex.getCause());
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
     * Get RSA PublicKey
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static final class GetkeyRSA{

        private static byte[] ski;
        private static IPKCS11FactoryOpts opts;

        public GetkeyRSA(byte[] ski, IPKCS11FactoryOpts opts){
            this.ski = ski;
            this.opts = opts;
        }

        public static IKey getkey() throws JavaChainException {

            try {
                long[] keypbu = findKeypairFromSKI(opts, false, ski);
                if (keypbu==null || (keypbu!=null && keypbu.length==0)) {
                	String str = String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerInfo(str, 3);
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

                    IKey ikey = new RsaKeyOpts.RsaPubKey(ski, pubder);
                    return ikey;
                }
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 3);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }



    /**
     * Use the private key signature of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class SignRSAKey{

        public SignRSAKey() {

        }


        /**
         * sign data
         *
         * @param ski           cka_id value
         * @param digest        digest data
         * @param newMechanism  the alg
         * @param opts          p11factory
         */
        public static byte[] signRSA(byte[] ski, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws JavaChainException{
            try {
                long[] privatekey = findKeypairFromSKI(opts, true, ski);
                if (privatekey==null || (privatekey!=null && privatekey.length==0))
                {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, 4);
                    throw new JavaChainException(str);
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_SignInit(opts.getSessionhandle(), ckMechanism, privatekey[0]);
                byte[] signvalue = opts.getPKCS11().C_Sign(opts.getSessionhandle(), digest);
                return signvalue;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 4);
                throw new JavaChainException(err, ex.getCause());
            }


        }
    }


    /**
     * Use the public key verify signature of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class VerifyRSAKey{

        public VerifyRSAKey() {

        }


        /**
         * verify signature
         *
         * @param ski           cka_id value
         * @param signature     signature data
         * @param digest        digest data
         * @param newMechanism  the alg
         * @param opts          p11factory
         */
        public boolean verifyRSA(byte[] ski, byte[] signature, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws JavaChainException{

            try {
                long[] publickey = findKeypairFromSKI(opts, false, ski);
                if (publickey==null || (publickey!=null && publickey.length==0)) {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, 5);
                    throw new JavaChainException(str);
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_VerifyInit(opts.getSessionhandle(), ckMechanism, publickey[0]);
                opts.getPKCS11().C_Verify(opts.getSessionhandle(), digest, signature);
                return true;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 5);
                return false;
            }

        }

    }



    /**
     * Use the key encrypt of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class EncryptRSAKey{

        public EncryptRSAKey() {

        }

        /**
         * encrypt data
         *
         * @param ski           cka_id value
         * @param plaintext     plain data
         * @param flagpub       public flag
         * @param mechanism     the alg
         * @param opts          p11factory
         * @return ciphertext
         */
        public byte[] encryptRSA(byte[] ski, byte[] plaintext, boolean flagpub, long mechanism, IPKCS11FactoryOpts opts) throws JavaChainException {
            try {
                long[] enckey;
                if (flagpub)
                    enckey = findKeypairFromSKI(opts, false, ski);
                else
                    enckey = findKeypairFromSKI(opts, true, ski);

                if (enckey==null || (enckey!=null && enckey.length==0)) {

                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, 6);
                    throw new JavaChainException(str);
                }

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_MODULUS_BITS),
                };
                opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(), enckey[0], attributes);

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = mechanism;
                opts.getPKCS11().C_EncryptInit(opts.getSessionhandle(), ckMechanism, enckey[0]);

                int outlen =  (int)attributes[0].getLong()/8;
                byte[] out1 = new byte[outlen];
                int rv = opts.getPKCS11().C_Encrypt(opts.getSessionhandle(), plaintext, 0, plaintext.length, out1, 0, outlen);
                return out1;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 6);
                throw new JavaChainException(err, ex.getCause());
            }

        }

    }



    /**
     * Use the key decrypt of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class DecryptRSAKey{

        public DecryptRSAKey() {}

        /**
         * decrypt data
         *
         * @param ski           cka_id value
         * @param ciphertext    cipher data
         * @param flagpub       public flag
         * @param mechanism     the alg
         * @param opts          p11factory
         * @return plaintext
         */
        public static byte[] decryptRSA(byte[] ski, byte[] ciphertext, boolean flagpub, long mechanism, IPKCS11FactoryOpts opts) throws JavaChainException{
            try {
                long[] deckey;
                if (flagpub)
                    deckey = findKeypairFromSKI(opts, false, ski);
                else
                    deckey = findKeypairFromSKI(opts, true, ski);

                if (deckey==null || (deckey!=null && deckey.length==0)) {

                    String str=String.format("[JC_PKCS]:No Find Key");
                    RsaImpl.setLoggerErr(str, 7);
                    throw new JavaChainException(str);
                }

                int outlen = 512;

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = mechanism;
                byte[] out = new byte[outlen];

                opts.getPKCS11().C_DecryptInit(opts.getSessionhandle(), ckMechanism, deckey[0]);
                int len = opts.getPKCS11().C_Decrypt(opts.getSessionhandle(), ciphertext, /*ciphertext.length*/0, ciphertext.length, out, 0, outlen);
                byte[] data = new byte[len];
                System.arraycopy(out, 0, data, 0, len);

                return data;
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                RsaImpl.setLoggerErr(err, 7);
                throw new JavaChainException(err, ex.getCause());
            }

        }
    }




    /**
     * Find the key of the specified SKI
     *
     */
    private static long[] findKeypairFromSKI(IPKCS11FactoryOpts opts, boolean bPri, byte[] ski) throws JavaChainException{

        long keyclass = CKO_PUBLIC_KEY;
        if(bPri)
            keyclass = CKO_PRIVATE_KEY;

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
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new JavaChainException(err, ex.getCause());
        }
    }


    /**
     * get public key der code
     *
     */
    private static byte[] getPublicDer(BigInteger n, BigInteger e) throws JavaChainException{
        try {
            RSAPublicKeyImpl rsapublickeyimpl = new RSAPublicKeyImpl(n, e);
            return rsapublickeyimpl.getEncoded();
        }catch( InvalidKeyException ex){
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new JavaChainException(err, ex.getCause());
        }
    }


    /**
     * get public key hash data
     *
     */
    private static byte[] getPublicHash(BigInteger n, BigInteger e) throws JavaChainException{

        try {
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            byte[] out = getPublicDer(n, e);
            shahash.update(out);
            return shahash.digest();
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, 0);
            throw new JavaChainException(err, ex.getCause());
        }
    }

}
