/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt.util;

import org.bcia.javachain.csp.gm.sdt.SM3.SM3;
import org.bcia.javachain.csp.gm.sdt.SM4.SM4;
import org.bcia.javachain.csp.gm.sdt.common.Constants;
import org.bcia.javachain.csp.gm.sdt.random.GmRandom;
import org.bcia.javachain.csp.intfs.IKey;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

/**
 * 密钥文件存储
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class KeysStore {

    /**
     * 公钥文件后缀
     */
    private final static String SUFFIX_PK = "_pk";
    /**
     * 私钥文件后缀
     */
    private final static String SUFFIX_SK = "_sk";
    /**
     * 密钥文件后缀
     */
    private final static String SUFFIX_KEY = "_key";

    public final static int KEY_TYPE_KEY = 0;
    public final static int KEY_TYPE_SK = 1;
    public final static int KEY_TYPE_PK = 2;

    private static SM3 sm3 = new SM3();
    private static SM4 sm4 = new SM4();
    private static GmRandom gmRandom = new GmRandom();

    public KeysStore() {
        this.sm3 = new SM3();
        this.sm4 = new SM4();
        this.gmRandom = new GmRandom();
    }

    public static void storeKey(String path, byte[] pwd, IKey key, int keyType) {
        String fileName = getFileNameByType(key.ski(), keyType);
        String pemObjectType = getPemObjectByType(keyType);

        String fullPath = fileName;
        if(null != path && !"".equals(path)) {
            File dir = new File(path);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            fullPath = path + File.separator + fullPath;
        }
        PemObject pemObject = null;
        byte[] keyContent = key.toBytes();
        //如果输入口令 则对密钥文件内容进行加密
        if(null != pwd && !"".equals(pwd)) {
            byte[] iv = gmRandom.rng(Constants.SM4_IV_LEN);
            byte[] cipherKey = deriveKey(pwd, iv);
            byte[] cipherContent = sm4.encryptCBC(keyContent, cipherKey, iv);
            byte[] content = new byte[Constants.SM4_IV_LEN+cipherContent.length];
            System.arraycopy(iv, 0, content, 0, Constants.SM4_IV_LEN);
            System.arraycopy(cipherContent, 0, content, Constants.SM4_IV_LEN, cipherContent.length);
            pemObject = new PemObject(pemObjectType, content);
        } else {
            pemObject = new PemObject(pemObjectType, keyContent);
        }
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        try {
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            str.close();
            PrintWriter pw = new PrintWriter(new FileOutputStream(fullPath));
            String keyString = new String(str.toString());
            pw.print(keyString);
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] loadKey(String path, byte[] pwd, byte[] ski, int keyType) {
        if(null == ski || 0 == ski.length) {
            return null;
        }
        String fileName = getFileNameByType(ski, keyType);
        String fullPath = fileName;
        if(null != path && !"".equals(path)) {
            fullPath = path + File.separator +fullPath;
        }
        File inFile = new File(fullPath);
        if(!inFile.exists()) {
            return null;
        }
        long fileLen = inFile.length();
        Reader fileReader = null;
        PemObject pemObject = null;
        try {
            fileReader = new FileReader(inFile);
            char[] content = new char[(int) fileLen];
            fileReader.read(content);
            String str = new String(content);
            StringReader stringreader = new StringReader(str);
            PemReader pem = new PemReader(stringreader);
            pemObject = pem.readPemObject();
            //如果文件未加密则返回文件内容
            if(null == pwd || "".equals(pwd)) {
                return pemObject.getContent();
            }
            //获取IV数据
            byte[] objectContent = pemObject.getContent();
            byte[] iv = new byte[Constants.SM4_IV_LEN];
            System.arraycopy(objectContent, 0, iv, 0, Constants.SM4_IV_LEN);
            byte[] cipherContent = new byte[objectContent.length-Constants.SM4_IV_LEN];
            System.arraycopy(objectContent, Constants.SM4_IV_LEN, cipherContent, 0, objectContent.length-Constants.SM4_IV_LEN);

            //解密文件内容
            byte[] cipherKey = deriveKey(pwd, iv);
            byte[] plainContent = sm4.decryptCBC(cipherContent, cipherKey, iv);
            return  plainContent;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] deriveKey(byte[] pwd, byte[] iv) {
        byte[] pwdBuf = new byte[pwd.length+8];
        System.arraycopy(pwd, 0, pwdBuf, 0, pwd.length);
        System.arraycopy(iv, 0, pwdBuf, pwd.length, 8);
        byte[] hash = sm3.hash(pwdBuf);
        byte[] cipherKey = new byte[Constants.SM4_KEY_LEN];
        System.arraycopy(hash, 0, cipherKey, 0, Constants.SM4_KEY_LEN);
        return cipherKey;
    }

    private static String getFileNameByType(byte[] ski, int keyType) {
        String fileName = Hex.toHexString(ski);
        switch (keyType) {
            case KEY_TYPE_KEY:
            {
                fileName = fileName + SUFFIX_KEY;
                break;
            }
            case KEY_TYPE_SK:
            {
                fileName = fileName + SUFFIX_SK;
                break;
            }
            case KEY_TYPE_PK:
            {
                fileName = fileName + SUFFIX_PK;
                break;
            }
            default:
                break;
        }
        return fileName;
    }

    private static String getPemObjectByType(int keyType) {
        String pemObjectType = "";
        switch (keyType) {
            case KEY_TYPE_KEY:
            {
                pemObjectType = "KEY";
                break;
            }
            case KEY_TYPE_SK:
            {
                pemObjectType = "PRIVATE KEY";
                break;
            }
            case KEY_TYPE_PK:
            {
                pemObjectType = "PUBLIC KEY";
                break;
            }
            default:
                break;
        }
        return pemObjectType;
    }
}
