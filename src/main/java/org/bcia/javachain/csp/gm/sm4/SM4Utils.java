/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sm4;



import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;
import static org.bcia.javachain.csp.gm.sm4.SM4Key.getKeyByRaw;

/**
 * 类描述
 *
 * @author
 * @date 18-3-27
 * @company Dingxuan
 */
public class SM4Utils {
    private String secretKey = "";
    private String iv = "";
    private boolean hexString = false;

    public SM4Utils()
    {
    }

    public byte[] encryptData_ECB(byte[] plainText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            if (hexString)
            {
                keyBytes = Util.hexStringToBytes(secretKey);
            }
            else
            {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText);
            String cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0)
            {
                Pattern p = compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText.getBytes("GBK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptData_ECB(byte[] cipherText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            if (hexString)
            {
                keyBytes = Util.hexStringToBytes(secretKey);
            }
            else
            {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            return sm4.sm4_crypt_ecb(ctx, new BASE64Decoder().decodeBuffer(new String(cipherText,"GBK")));
            //return new String(decrypted, "GBK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptData_CBC(byte[] plainText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString)
            {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            }
            else
            {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText);
            String cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0)
            {
                Pattern p = compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText.getBytes("GBK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptData_CBC(byte[] cipherText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString)
            {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            }
            else
            {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            return sm4.sm4_crypt_cbc(ctx, ivBytes, new BASE64Decoder().decodeBuffer(new String(cipherText,"GBK")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) throws IOException
    {
        String plainText = "Dingxuan is awesome!";
        byte[] plainTextBytes=plainText.getBytes("GBK");

        SM4Utils sm4 = new SM4Utils();
//        SM4Key sm4Key = new SM4Key();

        sm4.secretKey=getKeyByRaw("asdfasdf");
        System.out.println(sm4.secretKey);
        sm4.hexString = true;

        System.out.println("ECB模式");
        byte[] cipherTextBytes = sm4.encryptData_ECB(plainTextBytes);
        String cipherText=new String(cipherTextBytes,"GBK");
        System.out.println("密文: " + cipherText);
        System.out.println("");

        plainTextBytes = sm4.decryptData_ECB(cipherTextBytes);
        plainText=new String(plainTextBytes,"GBK");
        System.out.println("明文: " + plainText);
        System.out.println("");


        System.out.println("CBC模式");
        sm4.iv = "C7272358A3FB70E207CDB0F616CA8685";
        cipherTextBytes = sm4.encryptData_CBC(plainTextBytes);
        cipherText=new String(cipherTextBytes,"GBK");
        System.out.println("密文: " + cipherText);
        System.out.println("");

        plainTextBytes = sm4.decryptData_CBC(cipherTextBytes);
        System.out.println("明文: " + plainText);
    }
}
