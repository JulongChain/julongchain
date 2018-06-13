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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2Key;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyExport;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyGenOpts;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bouncycastle.asn1.*;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author zhangmingyang
 * @Date: 2018/4/2
 * @company Dingxuan
 */
public class SM2KeyUtil {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SM2KeyUtil.class);
    private static SecureRandom random = new SecureRandom();

    /**
     * 生成非对称密钥文件
     * @param k
     * @param privatePath
     * @param publicKey
     * @param opts
     */
    public static void keyFileGen(IKey k, String privatePath, String publicKey, IKeyGenOpts opts) {
        if(opts instanceof SM2KeyGenOpts){
            SM2Key sm2Key= (SM2Key) k;
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

    /**
     * 将字节数组转换为BigInteger数组
     * @param sig
     * @return
     */
    public static BigInteger[] decode(byte[] sig) {
        ASN1Sequence s = ASN1Sequence.getInstance(sig);

        return new BigInteger[]{ASN1Integer.getInstance(s.getObjectAt(0)).getValue(),
                ASN1Integer.getInstance(s.getObjectAt(1)).getValue()};
    }

    /**
     * 转换签名值r和s为字节数组
     * @param r
     * @param s
     * @return
     * @throws IOException
     */
    public static byte[] derEncode(BigInteger r, BigInteger s)
            throws IOException {

        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        return new DERSequence(v).getEncoded(ASN1Encoding.DER);
    }

    /**
     * 随机数生成器
     *
     * @param max
     * @return
     */
    public static BigInteger random(BigInteger max) {

        BigInteger r = new BigInteger(256, random);
        // int count = 1;

        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
            // count++;
        }
        return r;
    }


    /**
     * 判断字节数组是否全0
     *
     * @param buffer
     * @return
     */
    public static boolean allZero(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * 判断是否在范围内
     *
     * @param param
     * @param min
     * @param max
     * @return
     */
    public static boolean between(BigInteger param, BigInteger min, BigInteger max) {
        if (param.compareTo(min) >= 0 && param.compareTo(max) < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字节数组拼接
     *
     * @param params
     * @return
     */
    public static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            for (int i = 0; i < params.length; i++) {
                baos.write(params[i]);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
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
