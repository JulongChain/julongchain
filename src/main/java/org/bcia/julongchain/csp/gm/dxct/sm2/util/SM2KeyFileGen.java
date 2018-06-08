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
package org.bcia.julongchain.csp.gm.dxct.sm2.util;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2Key;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;

/**
 * @author zhangmingyang
 * @Date: 2018/4/1
 * @company Dingxuan
 */
public class SM2KeyFileGen {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SM2KeyFileGen.class);
    private SM2Key k;
    public SM2KeyFileGen( SM2Key k) {
    this.k=k;
    }
  public   void publicKeyFileGen(String path){

      PrintWriter pw = null;
      try {
          pw= new PrintWriter(new FileOutputStream(path));
        String publiKey=new String(Base64.encode( k.getPublicKey().toBytes()));
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
            String privateKey= new String(Base64.encode( k.toBytes()));
            pw.print(privateKey);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
