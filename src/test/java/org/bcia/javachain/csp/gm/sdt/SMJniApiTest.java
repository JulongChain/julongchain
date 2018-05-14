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
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        /******   case A-3 : length = 2048   ******/
        length = 2048;
        unitTest(length);

        /*****************  异常用例集  **************/
        /******   case B-1 : length = 0   ******/
        length = 0;
        unitTest(length);
        /******   case B-2 : length = 2049   ******/
        length = 2049;
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
            byte[] buff = jni.SM2KeyGen();

            System.out.println("   ===== key data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void KeyDeriveTest()
    {
        System.out.println("KeyDerive test case :");
        try {
            byte[] key = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            int length = 32;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.KeyDerive(key, length);

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
    public void EccSignVerifyTest()
    {
        System.out.println("Ecc Sign and Verify test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            byte[] key = jni.SM2KeyGen();
            byte[] hash = jni.SM3Hash(msg);
            byte[] sk = new byte[32];
            System.arraycopy(key, 00, sk, 0,32);
            byte[] pk = new byte[64];
            System.arraycopy(key, 32, pk, 0,64);
            byte[] sign = jni.EccSign(hash, 32, sk, 32);

            System.out.println("   ===== signature data : " + Convert.bytesToHexString(sign));

            int verifyResult = jni.EccVerify(hash, 32, pk, 64, sign, sign.length);
            System.out.println("   ===== signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SymmEncTest()
    {
        System.out.println("Symm encrypt and decrypt test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B};
            System.out.println("   ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] key = jni.RandomGen(16);
            System.out.println("   ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipher = jni.SymmEncrypt(key, 16, 0, null, 0, msg, msg.length);
            System.out.println("   ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipher));
            byte[] ecbPlain = jni.SymmDecrypt(key, 16, 0, null, 0, ecbCipher, ecbCipher.length);
            System.out.println("   ===== ECB plain data : " + Convert.bytesToHexString(ecbPlain));

            byte[] iv = jni.RandomGen(16);
            System.out.println("   ===== iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipher = jni.SymmEncrypt(key, 16, 1, iv, 16, msg, msg.length);

            System.out.println("   ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipher));
            byte[] cbcPlain = jni.SymmDecrypt(key, 16, 1, iv, 16, cbcCipher, cbcCipher.length);
            System.out.println("   ===== CBC plain data : " + Convert.bytesToHexString(cbcPlain));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
