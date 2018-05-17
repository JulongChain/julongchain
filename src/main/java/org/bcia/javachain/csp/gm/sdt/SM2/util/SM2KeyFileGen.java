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
package org.bcia.javachain.csp.gm.sdt.SM2.util;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2Key;
import org.bouncycastle.util.encoders.Base64;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * SM2 Key file generate
 *
 * @author tengxiumin
 * @date 5/16/18
 * @company SDT
 */

public class SM2KeyFileGen {

    private static JavaChainLog log = JavaChainLogFactory.getLog( SM2KeyFileGen.class);

    private SM2Key sm2Key;

    public SM2KeyFileGen(SM2Key sm2Key) {
        this.sm2Key=sm2Key;
    }

    public void publicKeyFileGen(String path){

      PrintWriter pw = null;
      try {
          pw= new PrintWriter(new FileOutputStream(path));
        String publiKey=new String(Base64.encode( sm2Key.getPublicKey().toBytes()));
          pw.print(publiKey);
          pw.close();
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
    }
    public  void privateKeyFileGen(String path){

        PrintWriter pw= null;
        try {
            pw = new PrintWriter(new FileOutputStream(path));
            String privateKey= new String(Base64.encode( sm2Key.toBytes()));
            pw.print(privateKey);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
