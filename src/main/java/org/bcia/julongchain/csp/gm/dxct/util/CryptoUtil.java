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
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

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






    public static void publicKeyFileGen(String path,byte[] content){
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





    public static void privateKeyFileGen(String path,byte[] content){
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

    public static byte[] loadKeyFile(String filePath)   {

        File inFile = new File(filePath);
        long fileLen = inFile.length();
        Reader reader = null;
        PemObject pemObject=null;
        try {
            reader = new FileReader(inFile);

            char[] content = new char[(int) fileLen];
            reader.read(content);
            String str= new String(content);

            StringReader stringreader = new StringReader(str);
            PemReader pem = new PemReader(stringreader);
            pemObject = pem.readPemObject();

        } catch (Exception e) {
            e.printStackTrace();
        }



        return pemObject.getContent();
    }

}
