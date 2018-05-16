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
package org.bcia.javachain.csp.gm.sdt;

import org.bcia.javachain.common.util.Convert;
import org.bcia.javachain.csp.gm.sdt.common.Constants;
import org.bcia.javachain.csp.gm.sdt.jni.SM4CBCResult;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;

import java.util.Arrays;

/**
 * 类描述
 *
 * @author tengxiumin
 * @date 4/24/18
 * @company SDT
 */
public class SMJniApiTest {

    private SMJniApi jni = new SMJniApi();

    @Test
    public void randomGenJniTest() {
        /*****************  正常用例集  **************/
        /******   case A-1 : length = 1   ******/
        int length = 1;
        unitTest(length);
        /******   case A-2 : length = 16   ******/
        length = 16;
        unitTest(length);
        /******   case A-3 : length = 128   ******/
        length = 128;
        unitTest(length);
        /******   case A-3 : length = 1024   ******/
        length = 1024;
        unitTest(length);

        /*****************  异常用例集  **************/
        /******   case B-1 : length = 0   ******/
        length = 0;
        unitTest(length);
        /******   case B-2 : length = 1025   ******/
        length = 1025;
        unitTest(length);

    }

    private void unitTest(int length) {
        try {
            System.out.println("RandomGen test case :");
            System.out.println("   ===== data length : " + length);
            byte[] random = jni.RandomGen(length);

            System.out.println("   ===== random data : " + Convert.bytesToHexString(random));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2KeyGenTest()
    {
        System.out.println("SM2KeyGen test case :");
        try {
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);
            System.out.println("   ===== sk data : " + Convert.bytesToHexString(sk));
            System.out.println("   ===== pk data : " + Convert.bytesToHexString(pk));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void KDFTest()
    {
        System.out.println("KeyDerive test case :");
        try {
            byte[] key = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            int length = 32;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(key, length);

            System.out.println("   ===== derive key data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM3HashTest()
    {
        System.out.println("SM3Hash test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            byte[] buff = jni.SM3Hash(msg);

            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2SignVerifyTest()
    {
        System.out.println("SM2 Sign and Verify test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);
            byte[] hash = jni.SM3Hash(msg);
            byte[] random = jni.RandomGen(Constants.SM2_SIGN_RANDOM_LEN);
            byte[] sign = jni.SM2Sign(hash, random, sk);

            System.out.println("   ===== signature data : " + Convert.bytesToHexString(sign));

            int verifyResult = jni.SM2Verify(hash, pk, sign);
            System.out.println("   ===== signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2EncDecTest()
    {
        System.out.println("SM2 encrypt and decrypt test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            System.out.println("   ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);

            byte[] random = jni.RandomGen(Constants.SM2_SIGN_RANDOM_LEN);
            byte[] cipherData = jni.SM2Encrypt(msg, random, pk);

            System.out.println("   ===== cipher data : " + Convert.bytesToHexString(cipherData));

            byte[] plainData = jni.SM2Decrypt(cipherData, sk);
            System.out.println("   ===== decrypt plain data : " + Convert.bytesToHexString(plainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void ECBEncDecTest()
    {
        System.out.println("ECB encrypt and decrypt test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            System.out.println("   ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] key = jni.RandomGen(16);
            System.out.println("   ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipher = jni.SM4ECBEncrypt(key, msg);
            System.out.println("   ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipher));
            byte[] ecbPlain = jni.SM4ECBDecrypt(key, ecbCipher);
            System.out.println("   ===== ECB plain data : " + Convert.bytesToHexString(ecbPlain));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void CBCEncDecTest()
    {
        System.out.println("CBC encrypt and decrypt test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            System.out.println("   ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] key = jni.RandomGen(16);
            System.out.println("   ===== key data : " + Convert.bytesToHexString(key));

            byte[] iv = jni.RandomGen(16);
            System.out.println("   ===== iv data : " + Convert.bytesToHexString(iv));
            SM4CBCResult cbcCipher = jni.SM4CBCEncrypt(key, iv, msg);

            System.out.println("   ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipher.getData()));
            SM4CBCResult cbcPlain = jni.SM4CBCDecrypt(key, iv, cbcCipher.getData());
            System.out.println("   ===== CBC plain data : " + Convert.bytesToHexString(cbcPlain.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2SignDataCompareWithStandardData() {
        System.out.println("SM2 Sign data compare with GB/T 32918.5-2017 :");
        try {
            //byte[] hash ={(byte)0xB2, (byte)0xE1, (byte)0x4C, (byte)0x5C, (byte)0x79, (byte)0xC6, (byte)0xDF, (byte)0x5B,
            //  (byte)0x85, (byte)0xF4, (byte)0xFE, (byte)0x7E, (byte)0xD8, (byte)0xDB, (byte)0x7A, (byte)0x26,
            //  (byte)0x2B, (byte)0x9D, (byte)0xA7, (byte)0xE0, (byte)0x7C, (byte)0xCB, (byte)0x0E, (byte)0xA9,
            //   (byte)0xF4, (byte)0x74, (byte)0x7B, (byte)0x8C, (byte)0xCD, (byte)0xA8, (byte)0xA4, (byte)0xF3};
            byte[] GBSignData = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] hash ={(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94, (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
                    (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED, (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
                    (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A, (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
                    (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF, (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};
            byte[] random = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
            byte[] sign = jni.SM2Sign(hash, random, sk);
            System.out.println("  ===== signature data : " + Convert.bytesToHexString(sign));
            if(Arrays.equals(sign, GBSignData)) {
                System.out.println("  ===== SM2 signature data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM2 signature data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM3HashDataCompareWithStandardData() {
        try {
            System.out.println("SM3 hash data compare with GB/T 32905-2016:");
            byte[] msg = {97,98,99};
            System.out.println("   ===== message data : " + Convert.bytesToHexString(msg));
            byte[] buff = jni.SM3Hash(msg);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBEncryptDataCompareWithStandardData() {
        try {
            System.out.println("SM4 ECB encrypt data compare with GB/T 32907-2016 :");
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipher = jni.SM4ECBEncrypt(key, msg);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipher));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
