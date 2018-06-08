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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


import javax.crypto.KeyGenerator;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaKeyOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaKeyOpts;

import org.bcia.julongchain.csp.pkcs11.util.SymmetryKey;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import sun.security.ec.ECPublicKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.util.DerOutputStream;
import sun.security.util.ECUtil;


import org.springframework.util.Base64Utils;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class GenerateKeyImpl {

    private static final String encodeRules = "fendo";

    public static void writefile(byte[] raw, String path) {
        try {
            String keyencode= HexBin.encode(raw);
            File file=new File(path);
            OutputStream outputStream=new FileOutputStream(file);
            outputStream.write(keyencode.getBytes());
            outputStream.close();
            System.out.println(keyencode+" -----> key保存成功");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static byte[] readfile(String path) {
        try {
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);//文件内容的字节流
            InputStreamReader inputStreamReader= new InputStreamReader(inputStream); //得到文件的字符流
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader); //放入读取缓冲区
            String readd="";
            StringBuffer stringBuffer=new StringBuffer();
            while ((readd=bufferedReader.readLine())!=null) {
                stringBuffer.append(readd);
            }
            inputStream.close();
            String keystr=stringBuffer.toString();
            System.out.println(keystr+" -----> key读取成功");  //读取出来的key是编码之后的 要进行转码
            byte[] keybyte= HexBin.decode(keystr);
            return keybyte;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }



    private String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public void SaveKeyPair(String path, byte[] publicencode, byte[] privateencode) throws IOException {

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicencode);
        FileOutputStream fos = new FileOutputStream(path + "/public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateencode);
        fos = new FileOutputStream(path + "/private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    public KeyPair LoadKeyPair(String path, String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(path + "/public.key");
        FileInputStream fis = new FileInputStream(path + "/public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(path + "/private.key");
        fis = new FileInputStream(path + "/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    public PublicKey LoadPublicKey(String path, String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(path + "/public.key");
        FileInputStream fis = new FileInputStream(path + "/public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return publicKey;
    }

    public PrivateKey LoadPrivateKey(String path, String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Private Key.
        File filePrivateKey = new File(path + "/private.key");
        FileInputStream fis = new FileInputStream(path + "/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return privateKey;
    }

    public static void savePublicKeyAsPEM(byte[] encode, String path) throws JavaChainException {
        try {
            String content = Base64Utils.encode(encode).toString();
            File file = new File(path + "/public.pem");
            if ( file.isFile() && file.exists() )
                throw new IOException("file already exists");
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                randomAccessFile.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
                int i = 0;
                for (; i<(content.length() - (content.length() % 64)); i+=64) {
                    randomAccessFile.write(content.substring(i, i + 64).getBytes());
                    randomAccessFile.write('\n');
                }

                randomAccessFile.write(content.substring(i, content.length()).getBytes());
                randomAccessFile.write('\n');

                randomAccessFile.write("-----END PUBLIC KEY-----".getBytes());
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:FileNotFoundException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(IOException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:IOException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }

    public static void savePrivateKeyAsPEM(byte[] encode, String path) throws JavaChainException {
        try {
            String content = Base64Utils.encode(encode).toString();
            File file = new File(path + "/private.pem");
            if ( file.isFile() && file.exists() )
                throw new JavaChainException("file already exists");
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                randomAccessFile.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
                int i = 0;
                for (; i<(content.length() - (content.length() % 64)); i+=64) {
                    randomAccessFile.write(content.substring(i, i + 64).getBytes());
                    randomAccessFile.write('\n');
                }

                randomAccessFile.write(content.substring(i, content.length()).getBytes());
                randomAccessFile.write('\n');

                randomAccessFile.write("-----END PRIVATE KEY-----".getBytes());
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:FileNotFoundException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(IOException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:IOException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }


    public PrivateKey LoadPrivateKeyAsPEM(String path,  String algorithm)
            throws IOException, NoSuchAlgorithmException,InvalidKeySpecException {

        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);//文件内容的字节流
        InputStreamReader inputStreamReader= new InputStreamReader(inputStream); //得到文件的字符流
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader); //放入读取缓冲区
        String readd="";
        StringBuffer stringBuffer=new StringBuffer();
        while ((readd=bufferedReader.readLine())!=null) {
            stringBuffer.append(readd);
        }
        inputStream.close();
        String content=stringBuffer.toString();

        String privateKeyPEM = content.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "").replace("\n", "");
        byte[] asBytes = Base64Utils.decode(privateKeyPEM.getBytes());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(asBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(spec);
    }

    public PublicKey LoadPublicKeyAsPEM(String path,  String algorithm)
            throws IOException, NoSuchAlgorithmException,InvalidKeySpecException {

        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);//文件内容的字节流
        InputStreamReader inputStreamReader= new InputStreamReader(inputStream); //得到文件的字符流
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader); //放入读取缓冲区
        String readd="";
        StringBuffer stringBuffer=new StringBuffer();
        while ((readd=bufferedReader.readLine())!=null) {
            stringBuffer.append(readd);
        }
        inputStream.close();
        String content=stringBuffer.toString();

        String strPublicKey = content.replace("-----BEGIN PUBLIC KEY-----\n", "")
                .replace("-----END PUBLIC KEY-----", "").replace("\n", "");
        byte[] asBytes = Base64Utils.decode(strPublicKey.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(asBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(spec);
    }

    public static IKey genDESedeKey() throws JavaChainException{

        try {
            //1.初始化key秘钥
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey= keyGenerator.generateKey();
            //转换key秘钥
            DESedeKeySpec deSedeKeySpec=new DESedeKeySpec(secretKey.getEncoded());
            SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("DESede");
            Key key= secretKeyFactory.generateSecret(deSedeKeySpec);

            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(key.getEncoded());
            byte[] bytehash = shahash.digest();

            SymmetryKey.DESedePriKey desedekey = new SymmetryKey.DESedePriKey(key.getEncoded(), bytehash, true);

            return desedekey;
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeyException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeyException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeySpecException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }

    }

    public IKey genAESKey(int size) throws JavaChainException{

        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(size, random);
            //3.产生原始对称密钥
            SecretKey key = keygen.generateKey();

            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(key.getEncoded());
            byte[] bytehash = shahash.digest();
            SymmetryKey.AESPriKey aeskey = new SymmetryKey.AESPriKey(key.getEncoded(), null, true);
            return aeskey;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }


    public IKey genRsaKey(int size) throws JavaChainException{
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(size, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();

            //publicder
            RSAPublicKeyImpl rsapublickeyimpl = (RSAPublicKeyImpl)pair.getPublic();
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            DerOutputStream out = new DerOutputStream();
            out.putInteger(rsapublickeyimpl.getModulus());
            out.putInteger(rsapublickeyimpl.getPublicExponent());
            shahash.update(out.toByteArray());
            byte[] pubhash = shahash.digest();

            IKey ikey = new RsaKeyOpts.RsaPriKey(pubhash, pair.getPrivate().getEncoded(),
                    new RsaKeyOpts.RsaPubKey(pubhash, pair.getPublic().getEncoded()));

            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:IOException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }
    }


    public IKey genEcdsaKey(String curveName) throws JavaChainException{
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC","SunEC");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(curveName);
            keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // SKI
            byte[] ecpointdata;
            if (keyPair.getPublic() instanceof ECPublicKeyImpl) {
                ecpointdata = ((ECPublicKeyImpl)keyPair.getPublic()).getEncodedPublicValue();
            } else { // instanceof ECPublicKey
                ECPublicKey ecpubkey = (ECPublicKey)keyPair.getPublic();
                ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
            }

            byte[] tempecpt = data(ecpointdata);
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(tempecpt);
            byte[] pubhash = shahash.digest();

            IKey ikey = new EcdsaKeyOpts.EcdsaPriKey(pubhash, keyPair.getPrivate().getEncoded(),
                    new EcdsaKeyOpts.EcdsaPubKey(pubhash, keyPair.getPublic().getEncoded()));

            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidAlgorithmParameterException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidAlgorithmParameterException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchProviderException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchProviderException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }


    }

    public static byte[] data(byte[] tempecpt) {

        int len = tempecpt.length;
        byte[] tempdata = new byte[len];
        if(0 == (len % 2) &&
                (tempecpt[0] == (byte) 0x04)&&
                (tempecpt[len-1] == (byte) 0x04))
        {
            // Trim trailing 0x04
            System.arraycopy(tempecpt, 0, tempdata, 0, len-1);
        }
        else if((tempecpt[0] == (byte) 0x04) &&
                (tempecpt[2] == (byte) 0x04))
        {
            System.arraycopy(tempecpt, 2, tempdata, 0, len-2);
        }
        else
            tempdata = tempecpt;

        return tempdata;
    }

    public IKey getDESedeKey(byte[] keybyte) throws JavaChainException{
        try {
            //通过读取到的key  获取到key秘钥对象
            DESedeKeySpec deSedeKeySpec=new DESedeKeySpec(keybyte);
            SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("DESede");
            Key key= secretKeyFactory.generateSecret(deSedeKeySpec); //获取到key秘钥

            SymmetryKey.DESedePriKey desedekey = new SymmetryKey.DESedePriKey(key.getEncoded(), null, true);
            return desedekey;

        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeyException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeyException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeySpecException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }


    public IKey getAESKey(byte[] keybyte) {

        //通过读取到的key  获取到key秘钥对象
        SecretKey aeskey = new SecretKeySpec(keybyte, "AES");//获取到key秘钥
        SymmetryKey.AESPriKey key = new SymmetryKey.AESPriKey(aeskey.getEncoded(), null, true);
        return key;
    }


    public IKey getRsaKey(byte[] publicder, byte[] privateder) throws JavaChainException{
        try {
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(publicder);
            byte[] pubhash = shahash.digest();
            IKey ikey = new RsaKeyOpts.RsaPriKey(pubhash, privateder,
                    new RsaKeyOpts.RsaPubKey(pubhash, publicder));
            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }
    }

    public IKey getRsaKey(byte[] publicder) throws JavaChainException{
        try {
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(publicder);
            byte[] pubhash = shahash.digest();
            IKey ikey = new RsaKeyOpts.RsaPubKey(pubhash, publicder);
            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }
    }


    public IKey getEcdsaKey(byte[] publicder, byte[] privateder) throws JavaChainException{
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicder);
            ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(keySpec);

            // SKI
            byte[] ecpointdata;
            if (ecpubkey instanceof ECPublicKeyImpl) {
                ecpointdata = ((ECPublicKeyImpl)ecpubkey).getEncodedPublicValue();
            } else { // instanceof ECPublicKey
                ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
            }

            byte[] tempecpt = data(ecpointdata);
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(tempecpt);
            byte[] pubhash = shahash.digest();

            IKey ikey = new EcdsaKeyOpts.EcdsaPriKey(pubhash, privateder,
                    new EcdsaKeyOpts.EcdsaPubKey(pubhash, publicder));
            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeySpecException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }


    public IKey getEcdsaKey(byte[] publicder) throws JavaChainException{
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicder);
            ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(keySpec);

            // SKI
            byte[] ecpointdata;
            if (ecpubkey instanceof ECPublicKeyImpl) {
                ecpointdata = ((ECPublicKeyImpl)ecpubkey).getEncodedPublicValue();
            } else { // instanceof ECPublicKey
                ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
            }

            byte[] tempecpt = data(ecpointdata);
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(tempecpt);
            byte[] pubhash = shahash.digest();

            IKey ikey = new EcdsaKeyOpts.EcdsaPubKey(pubhash, publicder);
            return ikey;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeySpecException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }
}
