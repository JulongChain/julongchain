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

import static org.bcia.javachain.common.util.Convert.bytesToHexString;

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
    public void testrandomGenJni() {
        /*****************  正常用例集  **************/
        /******   case A-1 : length = 1   ******/
        int length = 1;
        testunit(length);
        /******   case A-2 : length = 16   ******/
        length = 16;
        testunit(length);
        /******   case A-3 : length = 128   ******/
        length = 128;
        testunit(length);
        /******   case A-3 : length = 1024   ******/
        length = 1024;
        testunit(length);

        /*****************  异常用例集  **************/
        /******   case B-1 : length = 0   ******/
        length = 0;
        testunit(length);
        /******   case B-2 : length = 1025   ******/
        length = 1025;
        testunit(length);

    }

    private void testunit(int length) {
        try {
            System.out.println("RandomGen test case :");
            System.out.println("   ===== data length : " + length);
            byte[] random = jni.RandomGen(length);

            System.out.println("   ===== random data : " + bytesToHexString(random));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2MakeKey()
    {
        System.out.println("===== SM2MakeKey test case :");
        byte[] sk = new byte[0];
        int caseIndex = 1;
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2MakeKey sk is null");
            byte[] sm2Key = jni.SM2MakeKey(null);
            System.out.println("    SM2 key : " + bytesToHexString(sm2Key));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2MakeKey sk is 0");
            byte[] sm2Key = jni.SM2MakeKey(sk);
            System.out.println("    SM2 key : " + bytesToHexString(sm2Key));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2KeyGen()
    {
        System.out.println("SM2KeyGen test case :");
        try {
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);
            System.out.println("   ===== sk data : " + bytesToHexString(sk));
            System.out.println("   ===== pk data : " + bytesToHexString(pk));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testKDF()
    {
        System.out.println("KeyDerive test case :");
        int caseIndex =  1;
        try {
            byte[] key = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            int length = 32;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(key, length);

            System.out.println("   ===== derive key data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2KDF key is null");
            int length = 32;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(null, length);

            System.out.println("   ===== derive key data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2KDF key is 0");
            byte[] key =new byte[0];
            int length = 32;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(key, length);

            System.out.println("   ===== derive key data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2KDF length is 0");
            byte[] key ={(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            int length = 0;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(key, length);

            System.out.println("   ===== derive key data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2KDF length is -1");
            byte[] key ={(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            int length = -1;
            System.out.println("   ===== derive key length : " + length);
            byte[] buff = jni.SM2KDF(key, length);

            System.out.println("   ===== derive key data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM3Hash()
    {
        System.out.println("SM3Hash test case :");
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            byte[] buff = jni.SM3Hash(msg);

            System.out.println("   ===== hash data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        int caseIndex = 1;
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3Hash msg is null");
            byte[] buff = jni.SM3Hash(null);

            System.out.println("   ===== hash data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] msg = new byte[0];
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3Hash msg is 0");
            byte[] buff = jni.SM3Hash(msg);

            System.out.println("   ===== hash data : " + bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignVerify() throws Exception {
        System.out.println("SM2 Sign and Verify test case :");
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        byte[] hash = jni.SM3Hash(msg);
        byte[] hash0 = new byte[0];
        byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
        byte[] sk0 = new byte[0];
        byte[] pk = jni.SM2MakeKey(sk);
        byte[] pk0 = new byte[0];
        byte[] random = jni.RandomGen(Constants.SM2_SIGN_RANDOM_LEN);
        byte[] random0 = new byte[0];

        try {
            byte[] sign = jni.SM2Sign(hash, random, sk);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
            int verifyResult = jni.SM2Verify(hash, pk, sign);
            System.out.println("   ===== signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        int caseIndex = 1;
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign hash is null");
            byte[] sign = jni.SM2Sign(null, random, sk);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign hash is 0");
            byte[] sign = jni.SM2Sign(hash0, random, sk);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign random is null");
            byte[] sign = jni.SM2Sign(hash, null, sk);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign random is 0");
            byte[] sign = jni.SM2Sign(hash, random0, sk);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign sk is null");
            byte[] sign = jni.SM2Sign(hash, random, null);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Sign sk is 0");
            byte[] sign = jni.SM2Sign(hash, random, sk0);
            System.out.println("   ===== signature data : " + bytesToHexString(sign));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        byte[] signdata = jni.SM2Sign(hash, random, sk);
        byte[] signdata0 = new byte[0];
        int verifyresult = 0;

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify hash is null");
            verifyresult = jni.SM2Verify(null, pk, signdata);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify hash is 0");
            verifyresult = jni.SM2Verify(hash0, pk, signdata);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify pk is null");
            verifyresult = jni.SM2Verify(hash, null, signdata);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify pk is 0");
            verifyresult = jni.SM2Verify(hash, pk0, signdata);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify signdata is null");
            verifyresult = jni.SM2Verify(hash, pk, null);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Verify signdata is 0");
            verifyresult = jni.SM2Verify(hash, pk, signdata0);
            System.out.println("   ===== verify result : " + verifyresult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void testSM2EncDec() throws Exception {
        System.out.println("SM2 encrypt and decrypt test case :");
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        byte[] msg0 = new byte[0];
        System.out.println("   ===== plain data : " + bytesToHexString(msg));
        byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
        byte[] sk0 = new byte[0];
        byte[] pk = jni.SM2MakeKey(sk);
        byte[] pk0 = new byte[0];
        byte[] random = jni.RandomGen(Constants.SM2_SIGN_RANDOM_LEN);
        byte[] random0 = new byte[0];
        byte[] cipherData = null;
        byte[] cipherData0 = new byte[0];
        byte[] plainData = null;

        try {
            cipherData = jni.SM2Encrypt(msg, random, pk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
            plainData = jni.SM2Decrypt(cipherData, sk);
            System.out.println("   ===== SM2Dec plain data : " + bytesToHexString(plainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        int caseIndex = 1 ;
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt msg is null");
            cipherData = jni.SM2Encrypt(null, random, pk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt msg is 0");
            cipherData = jni.SM2Encrypt(msg0, random, pk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt random is null");
            cipherData = jni.SM2Encrypt(msg, null, pk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt random is 0");
            cipherData = jni.SM2Encrypt(msg, random0, pk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt pk is null");
            cipherData = jni.SM2Encrypt(msg, random, null);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Encrypt pk is 0");
            cipherData = jni.SM2Encrypt(msg, random, pk0);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        cipherData =  jni.SM2Encrypt(msg, random, pk);
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Decrypt msg is null");
            plainData = jni.SM2Decrypt(null, sk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Decrypt msg is 0");
            plainData = jni.SM2Decrypt(cipherData0, sk);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Decrypt sk is null");
            plainData = jni.SM2Decrypt(cipherData, null);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2Decrypt sk is 0");
            plainData = jni.SM2Decrypt(cipherData, sk0);
            System.out.println("   ===== SM2Enc cipher data : " + bytesToHexString(cipherData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testECBEncDec() throws Exception {
        System.out.println("ECB encrypt and decrypt test case :");
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        byte[] msg0 = new byte[0];
        byte[] key = jni.RandomGen(16);
        byte[] key0 = new byte[0];
        byte[] ecbCipher = null;
        byte[] ecbPlain = null;
        int caseIndex = 1;

        try {
            System.out.println("   ===== plain data : " + bytesToHexString(msg));
            System.out.println("   ===== key data : " + bytesToHexString(key));
            ecbCipher = jni.SM4ECBEncrypt(key, msg);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
            ecbPlain = jni.SM4ECBDecrypt(key, ecbCipher);
            System.out.println("   ===== ECB plain data : " + bytesToHexString(ecbPlain));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBencrypt key is null");
            ecbCipher = jni.SM4ECBEncrypt(null, msg);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBencrypt key is 0");
            ecbCipher = jni.SM4ECBEncrypt(key0, msg);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBencrypt msg is null");
            ecbCipher = jni.SM4ECBEncrypt(key, null);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBencrypt msg is 0");
            ecbCipher = jni.SM4ECBEncrypt(key, msg0);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        ecbCipher = jni.SM4ECBEncrypt(key, msg);
        byte[] ecbCipher0 = new byte[0];
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBdecrypt key is null");
            ecbPlain = jni.SM4ECBDecrypt(null, ecbCipher);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBdecrypt key is 0");
            ecbPlain = jni.SM4ECBDecrypt(key0, ecbCipher);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBdecrypt ecbCipher is null");
            ecbPlain = jni.SM4ECBDecrypt(key, null);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4ECBdecrypt ecbCipher is 0");
            ecbPlain = jni.SM4ECBDecrypt(key, ecbCipher0);
            System.out.println("   ===== ECB cipher data : " + bytesToHexString(ecbCipher));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testCBCEncDec() throws Exception {
        System.out.println("CBC encrypt and decrypt test case :");
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        byte[] msg0 = new byte[0];
        byte[] key = jni.RandomGen(16);
        byte[] key0 = new byte[0];
        byte[] iv = jni.RandomGen(16);
        byte[] iv0 = new byte[0];
        SM4CBCResult cbcCipher = null;
        SM4CBCResult cbcPlain = null;

        int caseIndex = 1;
        try {
            System.out.println("   ===== plain data : " + bytesToHexString(msg));
            System.out.println("   ===== key data : " + bytesToHexString(key));
            System.out.println("   ===== iv data : " + bytesToHexString(iv));
            cbcCipher = jni.SM4CBCEncrypt(key, iv, msg);

            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
            cbcPlain = jni.SM4CBCDecrypt(key, iv, cbcCipher.getData());
            System.out.println("   ===== CBC plain data : " + bytesToHexString(cbcPlain.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt key is null");
            cbcCipher = jni.SM4CBCEncrypt(null, iv, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt key is 0");
            cbcCipher = jni.SM4CBCEncrypt(key0, iv, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt iv is null");
            cbcCipher = jni.SM4CBCEncrypt(key, null, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt iv is 0");
            cbcCipher = jni.SM4CBCEncrypt(key, iv0, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt msg is null");
            cbcCipher = jni.SM4CBCEncrypt(key, iv, null);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCencrypt msg is 0");
            cbcCipher = jni.SM4CBCEncrypt(key, iv, msg0);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        cbcCipher = jni.SM4CBCEncrypt(key, iv, msg);
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt key is null");
            cbcPlain = jni.SM4CBCDecrypt(null, iv, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt key is 0");
            cbcPlain = jni.SM4CBCDecrypt(key0, iv, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt iv is null");
            cbcPlain = jni.SM4CBCDecrypt(key, null, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt iv is 0");
            cbcPlain = jni.SM4CBCDecrypt(key, iv0, msg);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt msg is null");
            cbcPlain = jni.SM4CBCDecrypt(key, iv, null);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM4CBCdecrypt msg is 0");
            cbcPlain = jni.SM4CBCDecrypt(key, iv, msg0);
            System.out.println("   ===== CBC cipher data : " + bytesToHexString(cbcCipher.getData()));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void SM2EncryptCompareWithStandardData() {
        System.out.println("SM2 Encrypt compare with GB/T 32918.5-2017 :");
        try {
            //04ebfc71 8e8d1798 62043226 8e77feb6 415e2ede 0e073c0f 4f640ecd 2e149a73
            //e858f9d8 1e5430a5 7b36daab 8f950a3c 64e6ee6a 63094d99 283aff76 7e124df0
            //21886ca9 89ca9c7d 58087307 ca93092d 651efa59 983c18f8 09e26292 3c53aec2
            //95d30383 b54e39d6 09d160af cb1908d0 bd8766
            byte[] GBEncryptData = {(byte)0x04, (byte)0xeb, (byte)0xfc, (byte)0x71, (byte)0x8e, (byte)0x8d, (byte)0x17, (byte)0x98,
                    (byte)0x62, (byte)0x04, (byte)0x32, (byte)0x26, (byte)0x8e, (byte)0x77, (byte)0xfe, (byte)0xb6,
                    (byte)0x41, (byte)0x5e, (byte)0x2e, (byte)0xde, (byte)0x0e, (byte)0x07, (byte)0x3c, (byte)0x0f,
                    (byte)0x4f, (byte)0x64, (byte)0x0e, (byte)0xcd, (byte)0x2e, (byte)0x14, (byte)0x9a, (byte)0x73,
                    (byte)0xe8, (byte)0x58, (byte)0xf9, (byte)0xd8, (byte)0x1e, (byte)0x54, (byte)0x30, (byte)0xa5,
                    (byte)0x7b, (byte)0x36, (byte)0xda, (byte)0xab, (byte)0x8f, (byte)0x95, (byte)0x0a, (byte)0x3c,
                    (byte)0x64, (byte)0xe6, (byte)0xee, (byte)0x6a, (byte)0x63, (byte)0x09, (byte)0x4d, (byte)0x99,
                    (byte)0x28, (byte)0x3a, (byte)0xff, (byte)0x76, (byte)0x7e, (byte)0x12, (byte)0x4d, (byte)0xf0,
                    (byte)0x21, (byte)0x88, (byte)0x6c, (byte)0xa9, (byte)0x89, (byte)0xca, (byte)0x9c, (byte)0x7d,
                    (byte)0x58, (byte)0x08, (byte)0x73, (byte)0x07, (byte)0xca, (byte)0x93, (byte)0x09, (byte)0x2d,
                    (byte)0x65, (byte)0x1e, (byte)0xfa, (byte)0x59, (byte)0x98, (byte)0x3c, (byte)0x18, (byte)0xf8,
                    (byte)0x09, (byte)0xe2, (byte)0x62, (byte)0x92, (byte)0x3c, (byte)0x53, (byte)0xae, (byte)0xc2,
                    (byte)0x95, (byte)0xd3, (byte)0x03, (byte)0x83, (byte)0xb5, (byte)0x4e, (byte)0x39, (byte)0xd6,
                    (byte)0x09, (byte)0xd1, (byte)0x60, (byte)0xaf, (byte)0xcb, (byte)0x19, (byte)0x08, (byte)0xd0,
                    (byte)0xbd, (byte)0x87, (byte)0x66};
            byte[] plaintext ={(byte)0x65, (byte)0x6e, (byte)0x63, (byte)0x72, (byte)0x79, (byte)0x70, (byte)0x74, (byte)0x69,
                    (byte)0x6f, (byte)0x6e, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, (byte)0x6e, (byte)0x64,
                    (byte)0x61, (byte)0x72, (byte)0x64};
            byte[] random = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
                    (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
                    (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
                    (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
            byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
            byte[] Encryptresult = jni.SM2Encrypt(plaintext, random, pk);
            System.out.println("  ===== signature data : " + bytesToHexString(Encryptresult));
            if(Arrays.equals(Encryptresult, GBEncryptData)) {
                System.out.println("  ===== SM2 encrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM2 encrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2DecryptCompareWithStandardData() {
        System.out.println("SM2 Encrypt compare with GB/T 32918.5-2017 :");
        try {
            byte[] plaintext ={(byte)0x65, (byte)0x6e, (byte)0x63, (byte)0x72, (byte)0x79, (byte)0x70, (byte)0x74, (byte)0x69,
                    (byte)0x6f, (byte)0x6e, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, (byte)0x6e, (byte)0x64,
                    (byte)0x61, (byte)0x72, (byte)0x64};
            byte[] GBEncryptData = {(byte)0x04, (byte)0xeb, (byte)0xfc, (byte)0x71, (byte)0x8e, (byte)0x8d, (byte)0x17, (byte)0x98,
                    (byte)0x62, (byte)0x04, (byte)0x32, (byte)0x26, (byte)0x8e, (byte)0x77, (byte)0xfe, (byte)0xb6,
                    (byte)0x41, (byte)0x5e, (byte)0x2e, (byte)0xde, (byte)0x0e, (byte)0x07, (byte)0x3c, (byte)0x0f,
                    (byte)0x4f, (byte)0x64, (byte)0x0e, (byte)0xcd, (byte)0x2e, (byte)0x14, (byte)0x9a, (byte)0x73,
                    (byte)0xe8, (byte)0x58, (byte)0xf9, (byte)0xd8, (byte)0x1e, (byte)0x54, (byte)0x30, (byte)0xa5,
                    (byte)0x7b, (byte)0x36, (byte)0xda, (byte)0xab, (byte)0x8f, (byte)0x95, (byte)0x0a, (byte)0x3c,
                    (byte)0x64, (byte)0xe6, (byte)0xee, (byte)0x6a, (byte)0x63, (byte)0x09, (byte)0x4d, (byte)0x99,
                    (byte)0x28, (byte)0x3a, (byte)0xff, (byte)0x76, (byte)0x7e, (byte)0x12, (byte)0x4d, (byte)0xf0,
                    (byte)0x21, (byte)0x88, (byte)0x6c, (byte)0xa9, (byte)0x89, (byte)0xca, (byte)0x9c, (byte)0x7d,
                    (byte)0x58, (byte)0x08, (byte)0x73, (byte)0x07, (byte)0xca, (byte)0x93, (byte)0x09, (byte)0x2d,
                    (byte)0x65, (byte)0x1e, (byte)0xfa, (byte)0x59, (byte)0x98, (byte)0x3c, (byte)0x18, (byte)0xf8,
                    (byte)0x09, (byte)0xe2, (byte)0x62, (byte)0x92, (byte)0x3c, (byte)0x53, (byte)0xae, (byte)0xc2,
                    (byte)0x95, (byte)0xd3, (byte)0x03, (byte)0x83, (byte)0xb5, (byte)0x4e, (byte)0x39, (byte)0xd6,
                    (byte)0x09, (byte)0xd1, (byte)0x60, (byte)0xaf, (byte)0xcb, (byte)0x19, (byte)0x08, (byte)0xd0,
                    (byte)0xbd, (byte)0x87, (byte)0x66};
            byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] Decryptresult = jni.SM2Decrypt(GBEncryptData, sk);
            System.out.println("  ===== plain data : " + bytesToHexString(Decryptresult));
            if(Arrays.equals(Decryptresult, plaintext)) {
                System.out.println("  ===== SM2 decrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM2 decrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2SignDataCompareWithStandardData() {
        System.out.println("SM2 Sign data compare with GB/T 32918.5-2017 :");
        try {
            byte[] GBSignData = {(byte)0xF5, (byte)0xA0, (byte)0x3B, (byte)0x06, (byte)0x48, (byte)0xD2, (byte)0xC4, (byte)0x63,
                    (byte)0x0E, (byte)0xea, (byte)0xc5, (byte)0x13, (byte)0xe1, (byte)0xbb, (byte)0x81, (byte)0xa1,
                    (byte)0x59, (byte)0x44, (byte)0xda, (byte)0x38, (byte)0x27, (byte)0xd5, (byte)0xb7, (byte)0x41,
                    (byte)0x43, (byte)0xac, (byte)0x7e, (byte)0xac, (byte)0xee, (byte)0xe7, (byte)0x20, (byte)0xb3,
                    (byte)0xb1, (byte)0xb6, (byte)0xaa, (byte)0x29, (byte)0xdf, (byte)0x21, (byte)0x2f, (byte)0xd8,
                    (byte)0x76, (byte)0x31, (byte)0x82, (byte)0xbc, (byte)0x0d, (byte)0x42, (byte)0x1c, (byte)0xa1,
                    (byte)0xbb, (byte)0x90, (byte)0x38, (byte)0xfd, (byte)0x1f, (byte)0x7f, (byte)0x42, (byte)0xd4,
                    (byte)0x84, (byte)0x0b, (byte)0x69, (byte)0xc4, (byte)0x85, (byte)0xbb, (byte)0xc1, (byte)0xaa};
            /* b1b6aa29 df212fd8 763182bc 0d421ca1 bb9038fd 1f7f42d4 840b69c4 85bbc1aa*/
            byte[] hash ={(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94, (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
                    (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED, (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
                    (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A, (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
                    (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF, (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};
            byte[] random = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
                    (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
                    (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
                    (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
            byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] sign = jni.SM2Sign(hash, random, sk);
            System.out.println("  ===== signature data : " + bytesToHexString(sign));
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
    public void testSM2SignSpeed() {
        try {
            System.out.println("SM2SignSpeedTest:");
            byte[] hash ={(byte)0xB2, (byte)0xE1, (byte)0x4C, (byte)0x5C, (byte)0x79, (byte)0xC6, (byte)0xDF, (byte)0x5B,
                    (byte)0x85, (byte)0xF4, (byte)0xFE, (byte)0x7E, (byte)0xD8, (byte)0xDB, (byte)0x7A, (byte)0x26,
                    (byte)0x2B, (byte)0x9D, (byte)0xA7, (byte)0xE0, (byte)0x7C, (byte)0xCB, (byte)0x0E, (byte)0xA9,
                    (byte)0xF4, (byte)0x74, (byte)0x7B, (byte)0x8C, (byte)0xCD, (byte)0xA8, (byte)0xA4, (byte)0xF3};
            byte[] random = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
                    (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
                    (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
                    (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
            byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            long starttime = System.currentTimeMillis();

            for(int i = 0; i<10000;i++) {
                byte[] sign = jni.SM2Sign( hash, random, sk );
            }
            long endtime = System.currentTimeMillis();
            float speed = (float) (10000.00 / ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM2 Sign 10000 times need : " + (float) ((endtime - starttime)/1000.00 )+ "s");
            System.out.println("  ===== SM2 Sign speed : " + speed + "times per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2VerityDataCompareWithStandardData() {
        System.out.println("SM2 verify GB/T 32918.5-2017 sign  :");
        try {
            byte[] GBSignData = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] hash ={(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94, (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
                    (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED, (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
                    (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A, (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
                    (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF, (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};
            byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};

            int verifyResult = jni.SM2Verify(hash, pk, GBSignData);
            if(verifyResult == 1) {
                System.out.println("  ===== SM2Verify success!");
            } else {
                System.out.println("  ##### SM2Verify fail!");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifySpeed() {
        try {
            System.out.println("SM2VerifySpeedTest:");
            byte[] GBSignData = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            byte[] hash ={(byte)0xB2, (byte)0xE1, (byte)0x4C, (byte)0x5C, (byte)0x79, (byte)0xC6, (byte)0xDF, (byte)0x5B,
                    (byte)0x85, (byte)0xF4, (byte)0xFE, (byte)0x7E, (byte)0xD8, (byte)0xDB, (byte)0x7A, (byte)0x26,
                    (byte)0x2B, (byte)0x9D, (byte)0xA7, (byte)0xE0, (byte)0x7C, (byte)0xCB, (byte)0x0E, (byte)0xA9,
                    (byte)0xF4, (byte)0x74, (byte)0x7B, (byte)0x8C, (byte)0xCD, (byte)0xA8, (byte)0xA4, (byte)0xF3};
            byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
            int verifyResult = 0;
            long starttime = System.currentTimeMillis();

            for(int i = 0; i<1000000;i++) {
                verifyResult = jni.SM2Verify(hash, pk, GBSignData);
            }
            long endtime = System.currentTimeMillis();
            float speed = (float) (1000000.00 / ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM2 Sign spend : " + (endtime - starttime));
            System.out.println("  ===== SM2 Sign 1000000 times need : " +  (float)((endtime - starttime)/1000) + "s");
            System.out.println("  ===== SM2 Sign speed : " + speed + "times per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM3HashDataCompareWithStandardData() {
        System.out.println("SM3 hash data compare with GB/T 32905-2016  case 1:");
        try {   //66c7f0f4 62eeedd9 d1f2d46b dc10e4e2 4167c487 5cf2f7a2 297da02b 8f4ba8e0
            byte[] GBHashData = {(byte)0x66, (byte)0xC7, (byte)0xF0, (byte)0xF4, (byte)0x62, (byte)0xEE, (byte)0xED, (byte)0xD9,
                    (byte)0xD1, (byte)0xF2, (byte)0xD4, (byte)0x6B, (byte)0xDC, (byte)0x10, (byte)0xE4, (byte)0xE2,
                    (byte)0x41, (byte)0x67, (byte)0xC4, (byte)0x87, (byte)0x5C, (byte)0xF2, (byte)0xF7, (byte)0xA2,
                    (byte)0x29, (byte)0x7D, (byte)0xA0, (byte)0x2B, (byte)0x8F, (byte)0x4B, (byte)0xA8, (byte)0xE0};
            byte[] msg = {97,98,99};
            byte[] buff = jni.SM3Hash(msg);
            System.out.println("   ===== hash data : " + bytesToHexString(buff));

            if(Arrays.equals(buff, GBHashData)) {
                System.out.println("  ===== SM3 hash data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM3 hash data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("SM3 hash data compare with GB/T 32905-2016  case 2:");
        try {   //debe9ff9 2275b8a1 38604889 c18e5a4d 6fdb70e5 387e5765 293dcba3 9c0c5732
            byte[] GBHashData = {(byte)0xde, (byte)0xbe, (byte)0x9f, (byte)0xf9, (byte)0x22, (byte)0x75, (byte)0xb8, (byte)0xa1,
                    (byte)0x38, (byte)0x60, (byte)0x48, (byte)0x89, (byte)0xc1, (byte)0x8e, (byte)0x5a, (byte)0x4d,
                    (byte)0x6f, (byte)0xdb, (byte)0x70, (byte)0xe5, (byte)0x38, (byte)0x7e, (byte)0x57, (byte)0x65,
                    (byte)0x29, (byte)0x3d, (byte)0xcb, (byte)0xa3, (byte)0x9c, (byte)0x0c, (byte)0x57, (byte)0x32};
            byte[] msg = { 'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d'};
            byte[] buff = jni.SM3Hash(msg);
            System.out.println("   ===== hash data : " + bytesToHexString(buff));

            if(Arrays.equals(buff, GBHashData)) {
                System.out.println("  ===== SM3 hash data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM3 hash data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM3HashSpeed() {
        try {
            System.out.println("SM3HashSpeedTest:");
            byte[] msg ={'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d'};
            byte[] buff = null;
            long starttime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100;i++) {
                buff = jni.SM3Hash( msg );
            }
            long endtime = System.currentTimeMillis();
            System.out.println("  ===== endtime - starttime : " + (endtime - starttime));
            float speed = (float) (100.00/ ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM3 hash 100M data need : " + (float) (endtime - starttime)/1000.00 + "s");
            System.out.println("  ===== SM3 hash speed : " + speed + "MB per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBEncryptDataCompareWithStandardData() {
        try {
            System.out.println("SM4 ECB encrypt data one round compare with GB/T 32907-2016 case 1:");
            // 68 1e df 34 d2 06 96 5e 86 b3 e9 4f 53 6e 42 46
            byte[] GBecbCipherData = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== plain data : " + bytesToHexString(msg));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== key data : " + bytesToHexString(key));

            byte[] ecbCipher = jni.SM4ECBEncrypt(key, msg);
            System.out.println("  ===== ECB cipher data : " + bytesToHexString(ecbCipher));
            if(Arrays.equals(ecbCipher, GBecbCipherData)) {
                System.out.println("  ===== SM4 ECBEncrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 ECBEncrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }


        try {
            System.out.println("SM4 ECB decrypt data 1000000 rounds compare with GB/T 32907-2016 :");
            //59 52 98 c7 c6 fd 27 1f 04 02 f8 04 c3 3d 3f 66
            byte[] GBcbcCipherData = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xc7, (byte)0xc6, (byte)0xfd, (byte)0x27, (byte)0x1f,
                    (byte)0x04, (byte)0x02, (byte)0xf8, (byte)0x04, (byte)0xc3, (byte)0x3d, (byte)0x3f, (byte)0x66};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== plain data : " + bytesToHexString(msg));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== key  : " + bytesToHexString(key));
            byte[] ecbResult = null;

            for ( int i = 0;i<1000000;i++) {
                ecbResult = jni.SM4ECBEncrypt( key, msg );
                msg = ecbResult;
            }
            System.out.println("  ===== ecbResult  : " + bytesToHexString(msg));
            if(Arrays.equals(ecbResult, GBcbcCipherData) ) {
                System.out.println("  ===== SM4 ECBEncrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 ECBEncrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncryptSpeed() {
        try {
            System.out.println("SM4 ECB encrypt speed");
            byte[] msg = {'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d' };
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };

            byte[] ecbCipher = null;
            long starttime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100;i++) {
                 ecbCipher = jni.SM4ECBEncrypt(key, msg);
            }
            long endtime = System.currentTimeMillis();
            System.out.println("  ===== endtime - starttime : " + (endtime - starttime));
            float speed = (float) (100.00/ ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM4 ECB encrypt 100M data need : " + (float) (endtime - starttime)/1000.00 + "s");
            System.out.println("  ===== SM4 ECB encrypt speed : " + speed + "MB per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBDecryptDataCompareWithStandardData() {
        try {
            System.out.println("SM4 ECB decrypt data one round compare with GB/T 32907-2016 :");
            // 68 1e df 34 d2 06 96 5e 86 b3 e9 4f 53 6e 42 46
            byte[] GBecbCipherData = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== Cipher data : " + bytesToHexString(GBecbCipherData));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== key data : " + bytesToHexString(key));

            byte[] ecbplain = jni.SM4ECBDecrypt(key, GBecbCipherData);
            System.out.println("  ===== ECB plain data : " + bytesToHexString(ecbplain));
            if(Arrays.equals(ecbplain, msg)) {
                System.out.println("  ===== SM4 ECBDecrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 ECBDecrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("SM4 ECB decrypt data 1000000 round compare with GB/T 32907-2016 :");
            // 68 1e df 34 d2 06 96 5e 86 b3 e9 4f 53 6e 42 46
            byte[] GBcbcCipherData = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xc7, (byte)0xc6, (byte)0xfd, (byte)0x27, (byte)0x1f,
                    (byte)0x04, (byte)0x02, (byte)0xf8, (byte)0x04, (byte)0xc3, (byte)0x3d, (byte)0x3f, (byte)0x66};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== Cipher data : " + bytesToHexString(GBcbcCipherData));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("  ===== key data : " + bytesToHexString(key));

            byte[] ecbplain = null;
            for(int i=0;i<1000000;i++) {
                ecbplain = jni.SM4ECBDecrypt( key, GBcbcCipherData );
                GBcbcCipherData = ecbplain;
            }
            System.out.println("  ===== ECB plain data : " + bytesToHexString(GBcbcCipherData));
            if(Arrays.equals(GBcbcCipherData, msg)) {
                System.out.println("  ===== SM4 ECBDecrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 ECBDecrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecryptSpeed() {
        try {
            System.out.println("SM4 ECB decrypt speed");
            byte[] GBecbCipherData = {'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d' };
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };

            byte[] plaintext = null;
            long starttime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100;i++) {
                plaintext = jni.SM4ECBDecrypt(key, GBecbCipherData);
            }
            long endtime = System.currentTimeMillis();
            float speed = (float) (100.00/ ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM4 ECB decrypt 100M data need : " + (float) (endtime - starttime)/1000.00 + "s");
            System.out.println("  ===== SM4 ECB decrypt speed : " + speed + "M per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4CBCEncryptDataCompareWithStandardData() {
        try {
            System.out.println("SM4 CBC decrypt data compare with GB/T 32907-2016 :");
            //2F6E9981 953F273F 94E91EFD 3B52A25C 39FDB686 A512A610 C0FD1890 F3EB6766
            byte[] GBcbcCipherData = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
                    (byte)0x94, (byte)0xE9, (byte)0x1E, (byte)0xFD, (byte)0x3B, (byte)0x52, (byte)0xA2, (byte)0x5C,
                    (byte)0x39, (byte)0xFD, (byte)0xB6, (byte)0x86, (byte)0xA5, (byte)0x12, (byte)0xA6, (byte)0x10,
                    (byte)0xC0, (byte)0xFD, (byte)0x18, (byte)0x90, (byte)0xF3, (byte)0xEB, (byte)0x67, (byte)0x66};
            byte[] msg = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c,
                    (byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            System.out.println("  ===== plain data : " + bytesToHexString(msg));
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            System.out.println("  ===== key  : " + bytesToHexString(key));
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            System.out.println("  ===== iv  : " + bytesToHexString(iv));

            SM4CBCResult cbcResult = jni.SM4CBCEncrypt(key, iv, msg);
            System.out.println("  ===== cbcResult getData  : " + bytesToHexString(cbcResult.getData()));
            if(Arrays.equals(cbcResult.getData(), GBcbcCipherData) ) {
                System.out.println("  ===== SM4 CBCEncrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 CBCEncrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void testSM4CBCEncryptSpeed() {
        try {
            System.out.println("SM4 CBC encrypt speed");
            byte[] msg = {'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d' };
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            SM4CBCResult cbcResult ;
            long starttime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100;i++) {
                    cbcResult = jni.SM4CBCEncrypt( key, iv, msg );
                   // msg = cbcResult.getData();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("  ===== endtime - starttime : " + (endtime - starttime));
            float speed = (float) (100.00/ ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM4 CBC encrypt 100M data need : " + (float) (endtime - starttime)/1000.00 + "s");
            System.out.println("  ===== SM4 CBC encrypt speed : " + speed + "MB per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4CBCDecryptDataCompareWithStandardData() {
        try {
            System.out.println("  ===== SM4 CBCDecrypt one round");
            byte[] GBcbcCipherData = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
                    (byte)0x94, (byte)0xE9, (byte)0x1E, (byte)0xFD, (byte)0x3B, (byte)0x52, (byte)0xA2, (byte)0x5C,
                    (byte)0x39, (byte)0xFD, (byte)0xB6, (byte)0x86, (byte)0xA5, (byte)0x12, (byte)0xA6, (byte)0x10,
                    (byte)0xC0, (byte)0xFD, (byte)0x18, (byte)0x90, (byte)0xF3, (byte)0xEB, (byte)0x67, (byte)0x66};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            byte[] msg = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c,
                    (byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};

            SM4CBCResult cbcResult = jni.SM4CBCDecrypt(key, iv, GBcbcCipherData);
            System.out.println("  ===== cbcResult getData  : " + bytesToHexString(cbcResult.getData()));
            if(Arrays.equals(cbcResult.getData(), msg) ) {
                System.out.println("  ===== SM4 CBCDecrypt data is equal with GB/T 32918.5-2017");
            } else {
                System.out.println("  ##### SM4 CBCDecrypt data is not equal with GB/T 32918.5-2017");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void testSM4CBCDecryptSpeed() {
        try {
            System.out.println("SM4 CBC decrypt speed");
            byte[] msg = {'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d' };
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            SM4CBCResult cbcResult ;
            long starttime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100;i++) {
                cbcResult = jni.SM4CBCDecrypt( key, iv, msg );
                // msg = cbcResult.getData();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("  ===== endtime - starttime : " + (endtime - starttime));
            float speed = (float) (100.00/ ((endtime - starttime)/1000.00));
            System.out.println("  ===== SM4 CBC decrypt 100M data need : " + (float) (endtime - starttime)/1000.00 + "s");
            System.out.println("  ===== SM4 CBC decrypt speed : " + speed + "MB per sencond");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
