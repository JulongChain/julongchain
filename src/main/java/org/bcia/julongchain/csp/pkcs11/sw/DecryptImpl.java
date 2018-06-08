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
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaKeyOpts;
import org.bcia.julongchain.csp.pkcs11.util.SymmetryKey;
import sun.security.rsa.RSAPrivateKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class DecryptImpl {

    public byte[] decryptData(IKey key, byte[] ciphervalue, String mode, String padding) throws JavaChainException {
        try {
            if(key instanceof SymmetryKey.DESedePriKey)
            {
                DESedeKeySpec deSedeKeySpec=new DESedeKeySpec(key.toBytes());
                SecretKeyFactory secretKeyFactory= SecretKeyFactory.getInstance("DESede");
                Key desedekey= secretKeyFactory.generateSecret(deSedeKeySpec); //获取到key秘钥

                //3.进行解密
                //DESede/ECB/PKCS5Padding
                String type = String.format("AES/%s/%s", mode, padding);
                Cipher cipher= Cipher.getInstance(type);
                cipher.init(Cipher.DECRYPT_MODE, desedekey);
                byte[] result= cipher.doFinal(ciphervalue);
                System.out.println("接收方进行解密："+new String(result));
                return result;
            }
            else if(key instanceof SymmetryKey.AESPriKey)
            {
                //1.根据字节数组生成AES密钥
                SecretKey aeskey = new SecretKeySpec(key.toBytes(), "AES");
                //2.根据指定算法AES自成密码器
                String type = String.format("AES/%s/%s", mode, padding);
                Cipher cipher = Cipher.getInstance(type);
                //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
                cipher.init(Cipher.DECRYPT_MODE, aeskey);
                /*
                 * 解密
                 */
                byte[] byte_decode = cipher.doFinal(ciphervalue);
                //String AES_decode = new String(byte_decode, "utf-8");
                return byte_decode;
            }
            return null;
        }catch(InvalidKeyException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeySpecException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchPaddingException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchPaddingException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(BadPaddingException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:BadPaddingException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(IllegalBlockSizeException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:IllegalBlockSizeException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }

    }


    public byte[] decryptData(IKey key, byte[] ciphervalue, String mode, String padding, boolean pubflag) throws JavaChainException{
        try {
            if(key instanceof RsaKeyOpts.RsaPriKey) {
                if(pubflag)
                {
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getPublicKey().toBytes());
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPublicKeyImpl rsakey = (RSAPublicKeyImpl)keyFactory.generatePublic(spec);

                    // specify mode and padding instead of relying on defaults (use OAEP if available!)
                    String type = String.format("RSA/%s/%s", mode, padding);
                    Cipher encrypt=Cipher.getInstance(type);
                    // init with the *public key*!
                    encrypt.init(Cipher.DECRYPT_MODE, rsakey);
                    // encrypt with known character encoding, you should probably use hybrid cryptography instead
                    byte[] byte_decode = encrypt.doFinal(ciphervalue);
                    return byte_decode;
                }
                else
                {
                    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.toBytes());
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPrivateKeyImpl rsakey = (RSAPrivateKeyImpl)keyFactory.generatePrivate(spec);

                    // specify mode and padding instead of relying on defaults (use OAEP if available!)
                    String type = String.format("RSA/%s/%s", mode, padding);
                    Cipher encrypt=Cipher.getInstance(type);
                    // init with the *public key*!
                    encrypt.init(Cipher.DECRYPT_MODE, rsakey);
                    // encrypt with known character encoding, you should probably use hybrid cryptography instead
                    byte[] byte_decode = encrypt.doFinal(ciphervalue);
                    return byte_decode;
                }


            }else if(key instanceof RsaKeyOpts.RsaPubKey) {

                if(pubflag)
                {
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getPublicKey().toBytes());
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPublicKeyImpl rsakey = (RSAPublicKeyImpl)keyFactory.generatePublic(spec);

                    // specify mode and padding instead of relying on defaults (use OAEP if available!)
                    String type = String.format("RSA/%s/%s", mode, padding);
                    Cipher encrypt=Cipher.getInstance(type);
                    // init with the *public key*!
                    encrypt.init(Cipher.DECRYPT_MODE, rsakey);
                    // encrypt with known character encoding, you should probably use hybrid cryptography instead
                    byte[] byte_decode = encrypt.doFinal(ciphervalue);
                    return byte_decode;
                }

            }
            return null;
        }catch(InvalidKeyException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeySpecException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchPaddingException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchPaddingException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(BadPaddingException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:BadPaddingException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(IllegalBlockSizeException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:IllegalBlockSizeException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }
    }
}
