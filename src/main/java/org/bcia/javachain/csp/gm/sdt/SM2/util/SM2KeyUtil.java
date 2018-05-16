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

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2Key;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2KeyExport;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2KeyGenOpts;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.IKeyGenOpts;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.security.SecureRandom;

/**
 * SM2 Key utils
 *
 * @author tengxiumin
 * @date 5/16/18
 * @company SDT
 */

public class SM2KeyUtil {
    private static JavaChainLog log = JavaChainLogFactory.getLog( SM2KeyUtil.class);
    private static SecureRandom random = new SecureRandom();

    /**
     * 生成非对称密钥文件
     * @param key
     * @param privatePath
     * @param publicKey
     * @param opts
     */
    public static void keyFileGen(IKey key, String privatePath, String publicKey, IKeyGenOpts opts) {
        if(opts instanceof SM2KeyGenOpts){
            SM2Key sm2Key= (SM2Key) key;
            SM2KeyFileGen sm2KeyFileGen=new SM2KeyFileGen(sm2Key);
            sm2KeyFileGen.privateKeyFileGen(privatePath);
            sm2KeyFileGen.publicKeyFileGen(publicKey);
        }
    }

    /**
     * 获取非对称密钥文件
     * @param opts
     * @return
     * @throws JavaChainException
     */
    public static IKey getKey(IKeyGenOpts opts) throws JavaChainException {
        if(opts instanceof SM2KeyGenOpts){
            return new SM2KeyExport();
        }
        return null;
    }

    /**
     * 以16进制打印字节数组
     *
     * @param b
     */
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }
        System.out.println();
    }


    public static String readFile(String filePath) throws Exception {
        File inFile = new File(filePath);
        long fileLen = inFile.length();
        Reader reader = new FileReader(inFile);

        char[] content = new char[(int) fileLen];
        reader.read(content);
        return new String(content);
    }

}
