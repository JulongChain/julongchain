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
    public void testSM4EncDec() {
        System.out.println("Symm encrypt and decrypt test case :");
        /*****************  正常用例集  **************/
        /******   case A-1 : keyLen = 16   alqmode = 0  ivLen = 0  msg.length = 20 ******/
        try {
            byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A, (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                    (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B, (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1,
                    (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B};
            System.out.println("  ===== plain data : " + Convert.bytesToHexString(msg));
            byte[] key = sm4.SM4KeyGen();
            System.out.println("  ===== key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = sm4.encryptECB(msg, key);
            System.out.println("  ===== ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
            byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
            System.out.println("  ===== ECB plain data : " + Convert.bytesToHexString(ecbPlainData));

            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("  ===== ECB iv data : " + Convert.bytesToHexString(iv));
            byte[] cbcCipherData = sm4.encryptCBC(msg, key, iv);
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
        /******   case B-1 :  msg = null ******/
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
            System.out.println("\n===== case B-"+ caseIndex++ +" :  encrypt msg is null");
            encryptResult = sm4.encryptECB(null, key);
            System.out.println("    ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : encrypt msg length is 0");
            encryptResult = sm4.encryptECB(tmpPlainData0, key);
            System.out.println("    ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  encrypt key is null");
            encryptResult = sm4.encryptECB(msg, null);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  encrypt key length is 0");
            encryptResult = sm4.encryptECB(msg, tmpkey0);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  encrypt key length is 15");
            encryptResult = sm4.encryptECB(msg, tmpkey15);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  encrypt key length is 32");
            encryptResult = sm4.encryptECB(msg, tmpkey32);
            System.out.println("   ECB cipher data : " + Convert.bytesToHexString(encryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        encryptResult = sm4.encryptECB(msg, key);
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  decrypt msg is null");
            decryptResult = sm4.decryptECB(null, key);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : decrypt msg length is 0");
            decryptResult = sm4.decryptECB(tmpPlainData0, key);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" : decrypt key is null");
            decryptResult = sm4.decryptECB(tmpPlainData0, null);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  decrypt key length is 0");
            decryptResult = sm4.decryptECB(msg, tmpkey0);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  decrypt key length is 15");
            decryptResult = sm4.decryptECB(msg, tmpkey15);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  decrypt key length is 32");
            decryptResult = sm4.decryptECB(msg, tmpkey32);
            System.out.println("   ECB plain data : " + Convert.bytesToHexString(decryptResult));
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }
    }

}
