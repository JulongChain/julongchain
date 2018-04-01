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
package org.bcia.javachain.csp.gm.sm2;

import org.bcia.javachain.csp.intfs.IKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URL;

/**
 * @author zhangmingyang
 * @Date: 2018/4/1
 * @company Dingxuan
 */
public class SM2KeyFileGen {
    private  SM2Key k;
    public SM2KeyFileGen( SM2Key k) {
    this.k=k;
    }
  public   void publicKeyFileGen(){
        //byte[] publicKey=k.toBytes();
        //URL url=SM2KeyFileGen.class.getResource("publickey.pem");
        File file = new File("E:/publickey.pem");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] buffer=k.getPublicKey().toBytes();
            //byte buffer[] = publicKey.getEncoded(false);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void privateKeyFileGen(){
        BigInteger privateKey = new BigInteger( k.toBytes());
      //  URL url=SM2KeyFileGen.class.getResource("privatekey.pem");
        File file = new File("E:/privatekey.pem");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(privateKey);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
