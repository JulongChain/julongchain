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
import org.bcia.javachain.csp.gm.sdt.SM4.SM4;
import org.bcia.javachain.csp.gm.sdt.common.Constants;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SM4算法单元测试
 *
 * @author tengxiumin
 * @date 5/16/18
 * @company SDT
 */
public class SM4Test {

    private SMJniApi jni = new SMJniApi();
    private SM4 sm4 = new SM4();

    @Before
    public void setUp() {

        System.out.println("setup...");
    }

    @After
    public void finalize(){

        System.out.println("finalize...");
    }

    @Test
    public void testSM4EncDec() throws Exception {
        System.out.println("Symm encrypt and decrypt test case :");
        /*****************  正常用例集  **************/
        byte[] msg1 = jni.RandomGen(1);
        byte[] msg16 = jni.RandomGen(16);
        byte[] msg32 = jni.RandomGen(32);
        byte[] msg64 = jni.RandomGen(64);
        byte[] msg128 = jni.RandomGen(128);
        byte[] msg1024 = jni.RandomGen(1024);
        int caseIndex = 1;
        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 1");
            /*byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A, (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                    (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B, (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1,
                    (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B};*/
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg1));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg1, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg1, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 16");
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg16));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg16, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg16, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 32");
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg32));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg32, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg32, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 64");
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg64));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg64, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg64, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 128");
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg128));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg128, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg128, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" : SM4 EncDec msg is 1024");
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg1024));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg1024, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg1024, key, iv);
            System.out.println("  ===== CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
            byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
            System.out.println("  ===== CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBInvalidParams() {
        /*****************  异常用例集  **************/
        byte[] key = sm4.SM4KeyGen();
        System.out.println("===== key data : " + Convert.bytesToHexString(key));
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        System.out.println("===== msg data : " + Convert.bytesToHexString(msg));

        byte[] encryptResult = null;
        byte[] decryptResult = null;
        int caseIndex = 1;

        byte[] tmpkey0 = new byte[0];
        byte[] tmpkey15 = new byte[15];
        System.arraycopy(key, 0, tmpkey15, 0, 15);
        byte[] tmpkey32 = new byte[32];
        System.arraycopy(key, 0, tmpkey32, 0, 16);
        System.arraycopy(key, 0, tmpkey32, 16, 16);

        byte[] tmpPlainData0 = new byte[0];

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb encrypt msg is null");
            encryptResult = sm4.encryptECB(null, key);
            System.out.println("    ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :ecb encrypt msg length is 0");
            encryptResult = sm4.encryptECB(tmpPlainData0, key);
            System.out.println("    ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb encrypt key is null");
            encryptResult = sm4.encryptECB(msg, null);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :ecb  encrypt key length is 0");
            encryptResult = sm4.encryptECB(msg, tmpkey0);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb encrypt key length is 15");
            encryptResult = sm4.encryptECB(msg, tmpkey15);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb encrypt key length is 32");
            encryptResult = sm4.encryptECB(msg, tmpkey32);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb  decrypt msg is null");
            decryptResult = sm4.decryptECB(null, key);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb decrypt msg length is 0");
            decryptResult = sm4.decryptECB(tmpPlainData0, key);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :ecb decrypt key is null");
            decryptResult = sm4.decryptECB(msg, null);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb decrypt key length is 0");
            decryptResult = sm4.decryptECB(msg, tmpkey0);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : ecb decrypt key length is 15");
            decryptResult = sm4.decryptECB(msg, tmpkey15);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :   ecb decrypt key length is 32");
            decryptResult = sm4.decryptECB(msg, tmpkey32);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCInvalidParams() {
        /*****************  异常用例集  **************/
        byte[] key = sm4.SM4KeyGen();
        System.out.println("===== key data : " + Convert.bytesToHexString(key));
        byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
        System.out.println("===== msg data : " + Convert.bytesToHexString(msg));
        byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
        System.out.println("===== iv data : " + Convert.bytesToHexString(iv));

        byte[] encryptResult = null;
        byte[] decryptResult = null;
        int caseIndex = 1;

        byte[] tmpkey0 = new byte[0];
        byte[] tmpkey15 = new byte[15];
        System.arraycopy(key, 0, tmpkey15, 0, 15);
        byte[] tmpkey32 = new byte[32];
        System.arraycopy(key, 0, tmpkey32, 0, 16);
        System.arraycopy(key, 0, tmpkey32, 16, 16);

        byte[] tmpPlainData0 = new byte[0];

        byte[] tmpiv0 = new byte[0];
        byte[] tmpiv15 = new byte[15];
        System.arraycopy(iv, 0, tmpiv15, 0, 15);
        byte[] tmpiv32 = new byte[32];
        System.arraycopy(iv, 0, tmpiv32, 0, 16);
        System.arraycopy(iv, 0, tmpiv32, 16, 16);


        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt msg is null");
            encryptResult = sm4.encryptCBC(null, key,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt msg is 0");
            encryptResult = sm4.encryptCBC(tmpPlainData0, key,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt key is null");
            encryptResult = sm4.encryptCBC(msg, null,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt key is 0");
            encryptResult = sm4.encryptCBC(msg, tmpkey0,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt key is 15");
            encryptResult = sm4.encryptCBC(msg, tmpkey15,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt key is 32");
            encryptResult = sm4.encryptCBC(msg, tmpkey32,iv);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt iv is null");
            encryptResult = sm4.encryptCBC(msg, key,null);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt iv is 0");
            encryptResult = sm4.encryptCBC(msg, key,tmpiv0);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt iv is 15");
            encryptResult = sm4.encryptCBC(msg, key,tmpiv15);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc encrypt iv is 32");
            encryptResult = sm4.encryptCBC(msg, key,tmpiv32);
            System.out.println("    CBC cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt msg is null");
            encryptResult = sm4.decryptCBC(null, key,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt msg is 0");
            decryptResult = sm4.decryptCBC(tmpPlainData0, key,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt key is null");
            decryptResult = sm4.decryptCBC(msg, null,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt key is 0");
            decryptResult = sm4.decryptCBC(msg, tmpkey0,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt key is 15");
            decryptResult = sm4.decryptCBC(msg, tmpkey15,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt key is 32");
            decryptResult = sm4.decryptCBC(msg, tmpkey32,iv);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt iv is null");
            decryptResult = sm4.decryptCBC(msg, key,null);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt iv is 0");
            decryptResult = sm4.decryptCBC(msg, key,tmpiv0);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt iv is 15");
            decryptResult = sm4.decryptCBC(msg, key,tmpiv15);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  cbc decrypt iv is 32");
            decryptResult = sm4.decryptCBC(msg, key,tmpiv32);
            System.out.println("    CBC plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }
    }

}
