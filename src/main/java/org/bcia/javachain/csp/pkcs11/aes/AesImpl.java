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
package org.bcia.javachain.csp.pkcs11.aes;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.javachain.csp.pkcs11.util.SymmetryKey;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class AesImpl {
	private static JavaChainLog logger;

    public static class GenerateAES{

        public static IKey generateAES(int size, boolean ephemeral, IPKCS11FactoryOpts opts) throws JavaChainException {
            try {
                //create a key attribute
                CK_MECHANISM ckm = new CK_MECHANISM();
                ckm.mechanism = CKM_AES_KEY_GEN;
                CK_ATTRIBUTE[] aesObject = new CK_ATTRIBUTE[7];
                aesObject[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_SECRET_KEY);
                aesObject[1] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_AES);
                aesObject[2] = new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral);
                aesObject[3] = new CK_ATTRIBUTE(CKA_PRIVATE, true);
                aesObject[4] = new CK_ATTRIBUTE(CKA_ENCRYPT, true);
                aesObject[5] = new CK_ATTRIBUTE(CKA_DECRYPT, true);
                aesObject[6] = new CK_ATTRIBUTE(CKA_VALUE_LEN, size);

                long keyhandle = opts.getPKCS11().C_GenerateKey(opts.getSessionhandle(), ckm, aesObject);

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_VALUE),
                };
                opts.getPKCS11().C_GetAttributeValue
                        (opts.getSessionhandle(), keyhandle, attributes);
                byte[] raw = attributes[0].getByteArray();

                MessageDigest shahash = MessageDigest.getInstance("SHA-1");
                shahash.update(raw);
                byte[] bytehash = shahash.digest();

                SymmetryKey.AESPriKey aeskey = new SymmetryKey.AESPriKey(raw, bytehash, true);

                return aeskey;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }
    
    
    public static class ImoprtAESKey{
    	
    	public static IKey importAES(byte[] keyvalue, boolean ephemeral, IPKCS11FactoryOpts opts) throws JavaChainException {
    		
    		try {
    			//create a pubkey attribute			
		        CK_MECHANISM ckm = new CK_MECHANISM();
		        ckm.mechanism = CKM_AES_KEY_GEN;
		        CK_ATTRIBUTE[] aesObject = new CK_ATTRIBUTE[8];
		        aesObject[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_SECRET_KEY);
		        aesObject[1] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_AES);
		        aesObject[2] = new CK_ATTRIBUTE(CKA_TOKEN, false);
		        aesObject[3] = new CK_ATTRIBUTE(CKA_PRIVATE, true);
		        aesObject[4] = new CK_ATTRIBUTE(CKA_ENCRYPT, true);
		        aesObject[5] = new CK_ATTRIBUTE(CKA_DECRYPT, true);
		        aesObject[6] = new CK_ATTRIBUTE(CKA_VALUE_LEN, keyvalue.length*8);
		        aesObject[7] = new CK_ATTRIBUTE(CKA_VALUE, keyvalue);
		        
		        long keyhandle = opts.getPKCS11().C_CreateObject(opts.getSessionhandle(), aesObject);
		        
		        MessageDigest shahash = MessageDigest.getInstance("SHA-1");
                shahash.update(keyvalue);
                byte[] bytehash = shahash.digest();

                SymmetryKey.AESPriKey aeskey = new SymmetryKey.AESPriKey(keyvalue, bytehash, true);
		        return aeskey;
		        
    		}catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
    	}
    }


    public static class GetAESKey{

        public static IKey getAES(byte[] ski, IPKCS11FactoryOpts opts) throws JavaChainException {
            try {

                long[] keyhandle = findKeyFromSKI(opts, ski);
                if(keyhandle == null ||(keyhandle!=null && keyhandle.length==0))
                {
                	String str=null;
                    str=String.format("[JC_PKCS]:No Find Key");
                    logger.error(str);
                    return null;
                }

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_VALUE),
                };
                opts.getPKCS11().C_GetAttributeValue
                        (opts.getSessionhandle(), keyhandle[0], attributes);
                byte[] raw = attributes[0].getByteArray();

                MessageDigest shahash = MessageDigest.getInstance("SHA-1");
                shahash.update(raw);
                byte[] bytehash = shahash.digest();

                SymmetryKey.AESPriKey aeskey = new SymmetryKey.AESPriKey(raw, bytehash, true);
                return aeskey;


            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }

    public static class EncryptAES{

        public EncryptAES() {

        }

        public static byte[] encrtyptWithAES(byte[] ski, long mechanism, byte[] plaintext, IPKCS11FactoryOpts opts) throws JavaChainException {
            try {
                long[] key = findKeyFromSKI(opts, ski);
                if(key == null ||(key!=null && key.length==0))
                {
                    return null;
                }
                CK_MECHANISM ckm = new CK_MECHANISM();
                ckm.mechanism = mechanism;
                opts.getPKCS11().C_EncryptInit(opts.getSessionhandle(), ckm, key[0]);

                int n = plaintext.length / 16;
                if(plaintext.length%6 > 0)
                    n++;
                int outlen = n*32;
                byte[] out = new byte[outlen];
                int enclen = opts.getPKCS11().C_Encrypt(opts.getSessionhandle(), plaintext, 0, plaintext.length, out, 0, outlen);
                byte[] cipher = new byte[enclen];
                System.arraycopy(out, 0, cipher, 0, enclen);
                return cipher;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }

    }


    public static class DecryptAES{

        public static byte[] decryptWithAES(byte[] ski, long mechanism, byte[] ciphertext, IPKCS11FactoryOpts opts) throws JavaChainException {
            try {
                long[] key = findKeyFromSKI(opts, ski);
                if(key == null ||(key!=null && key.length==0))
                {
                    return null;
                }
                CK_MECHANISM ckm = new CK_MECHANISM();
                ckm.mechanism = mechanism;
                byte[] out = new byte[ciphertext.length];
                int outlen = ciphertext.length;
                opts.getPKCS11().C_DecryptInit(opts.getSessionhandle(), ckm, key[0]);
                int declen = opts.getPKCS11().C_Decrypt(opts.getSessionhandle(), ciphertext, 0, ciphertext.length, out, 0, outlen);
                byte[] plain = new byte[declen];
                System.arraycopy(out, 0, plain, 0, declen);
                return plain;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }

    public static long[] findKeyFromSKI(IPKCS11FactoryOpts opts, byte[] ski) throws JavaChainException{


        long keyclass = PKCS11Constants.CKO_SECRET_KEY;
        CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[2];
        template[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, keyclass);
        template[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_AES);

        try {
            opts.getPKCS11().C_FindObjectsInit(opts.getSessionhandle(), template);
            do {

                long[] keypair = opts.getPKCS11().C_FindObjects(opts.getSessionhandle(), 1);
                if(keypair.length <= 0)
                {
                    CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_VALUE),
                    };
                    opts.getPKCS11().C_GetAttributeValue
                            (opts.getSessionhandle(), keypair[0], attributes);
                    byte[] raw = attributes[0].getByteArray();

                    MessageDigest shahash = MessageDigest.getInstance("SHA-1");
                    shahash.update(raw);
                    byte[] bytehash = shahash.digest();
                    if(compereByteArray(bytehash, ski)) {
                        opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
                        return keypair;
                    }
                }else {
                	opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
                	return null;
                }
            }while(true);            
            

        }catch(PKCS11Exception ex) {

            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
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
}
