/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.csp.gm.dxct.util;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.util.FileUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.security.util.Debug;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

/**
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public class CryptoUtil {

    /**
     * 公钥后缀
     */
    private final static String PK = "_pk";
    /**
     * 私钥后缀
     */
    private final static String SK = "_sk";


    public static void publicKeyFileGen(String path, byte[] content) {
        PemObject pemObject = new PemObject("PUBLIC KEY", content);
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        try {
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            str.close();
            PrintWriter pw = new PrintWriter(new FileOutputStream(path + PK));
            String publiKey = new String(str.toString());
            pw.print(publiKey);
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void privateKeyFileGen(String path, byte[] content) {
        PemObject pemObject = new PemObject("PRIVATE KEY", content);
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        try {
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            str.close();
            PrintWriter pw = new PrintWriter(new FileOutputStream(path + SK));
            String publiKey = new String(str.toString());
            pw.print(publiKey);
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] loadKeyFile(String filePath) {

        File inFile = new File(filePath);
        long fileLen = inFile.length();
        Reader reader = null;
        PemObject pemObject = null;
        try {
            reader = new FileReader(inFile);

            char[] content = new char[(int) fileLen];
            reader.read(content);
            String str = new String(content);

            StringReader stringreader = new StringReader(str);
            PemReader pem = new PemReader(stringreader);
            pemObject = pem.readPemObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pemObject.getContent();
    }


    /**
     * 读取
     * @param sk_path
     * @return
     * @throws CspException
     * @throws IOException
     */
    public static byte[] readSkFile(String sk_path) throws CspException, IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(sk_path));
        PemReader pemReader = new PemReader(reader);
        PemObject pemObject = pemReader.readPemObject();
        reader.close();
        byte[] encodedData = pemObject.getContent();
        DerValue derValue = new DerValue(new ByteArrayInputStream(encodedData));
        byte[] rawPrivateKey = null;
        if (derValue.tag != 48) {
            throw new CspException("invalid key format");
        } else {
            BigInteger version = derValue.data.getBigInteger();
            if (!version.equals(BigInteger.ZERO)) {
                throw new CspException("version mismatch: (supported: " + Debug.toHexString(BigInteger.ZERO) + ", parsed: " + Debug.toHexString(version));
            } else {
                AlgorithmId algId = AlgorithmId.parse(derValue.data.getDerValue());
                rawPrivateKey = derValue.data.getOctetString();
            }
            return rawPrivateKey;
        }
    }

    //读取sm2证书
    public static byte[] readPem(String certPath) throws IOException {
        byte[] Bytes = FileUtils.readFileBytes(certPath);
        byte[] certBytes = new PemReader(new InputStreamReader(new ByteArrayInputStream(Bytes))).readPemObject().getContent();
        return certBytes;
    }

    /**
     * 生成随机的字节数组
     * @param size
     * @return
     */
    public static byte[] genByteArray(int size){
        byte [] rdBytes=new byte[size];
        Random random=new Random();
        random.nextBytes(rdBytes);
        return rdBytes;
    }


    /**
     * 从pem私钥文件中获取sk
     * @return
     */
    public static byte[] getPrivateKey(String filePath)throws Exception{
        File inFile = new File(filePath);
        long fileLen = inFile.length();
        Reader reader = null;
        PemObject pemObject = null;
        reader = new FileReader(inFile);
        char[] content = new char[(int) fileLen];
        reader.read(content);
        String str = new String(content);
        String privateKeyPEM = str.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "").replace("\n", "");
        Security.addProvider(new BouncyCastleProvider());
        KeyFactory keyf = keyf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKeyPEM) );
        BCECPrivateKey priKey = (BCECPrivateKey)keyf.generatePrivate(priPKCS8);
        return priKey.getD().toByteArray();
    }


    /**
     *  CSR中获取公钥
     * @return
     */
    public static byte[] getPublicKey(String csr) throws IOException {

        PKCS10CertificationRequest p10 = new PKCS10CertificationRequest(Base64.decode(csr));
        byte[] publicKeyBytes = p10.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        return publicKeyBytes;
    }



}
