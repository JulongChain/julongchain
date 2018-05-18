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
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SM4算法单元测试
 *
 * @author tengxiumin
 * @date 2018/05/16
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
    public void testSM4KeyGen() {
        System.out.println("============ SM4 KeyGen test ============ ");
        try {
            byte[] key = sm4.SM4KeyGen();
            if (null != key) {
                System.out.println("[output data] key data : " + Convert.bytesToHexString(key));
            } else {
                System.out.println("[**Error**] compute hash data failed");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testSM4EncDec() {
        byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
        byte[] iv = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
        int[] messageLenList = {1, 16, 240, 360, 2048};
        unitTest(key, iv, messageLenList);
    }

    private void unitTest(byte[] key, byte[] iv, int[] lists) {
        int caseIndex = 1;
        for(int i = 0; i < lists.length; i++) {
            try {
                int msgLen = lists[i];
                System.out.println("\n===== case "+ caseIndex++ +" :  message length is " + msgLen);
                byte[] msg = new byte[msgLen];
                if(msgLen > 1024) {
                    int leftLen = msgLen;
                    while (leftLen > 0) {
                        int len = leftLen;
                        if(len > 1024) {
                            len = 1024;
                        }
                        byte[] randomData = jni.RandomGen(len);
                        System.arraycopy(randomData, 0, msg, msgLen-leftLen, len);
                        leftLen = leftLen - len;
                    }
                } else {
                    msg = jni.RandomGen(msgLen);
                }
                System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
                byte[] ecbCipherData = sm4.encryptECB(msg, key);
                if (null != ecbCipherData) {
                    System.out.println("[output data] SM4 ECB cipher data : " + Convert.bytesToHexString(ecbCipherData));
                } else {
                    System.out.println("[**Error**] SM4 ECB cipher data failed");
                }
                byte[] ecbPlainData = sm4.decryptECB(ecbCipherData, key);
                if (null != ecbPlainData) {
                    System.out.println("[output data] SM4 ECB plain data : " + Convert.bytesToHexString(ecbPlainData));
                } else {
                    System.out.println("[**Error**] SM4 ECB plain data failed");
                }
                byte[] cbcCipherData = sm4.encryptCBC(msg, key, iv);
                if (null != cbcCipherData) {
                    System.out.println("[output data] SM4 CBC cipher data : " + Convert.bytesToHexString(cbcCipherData));
                } else {
                    System.out.println("[**Error**] SM4 CBC cipher data failed");
                }
                byte[] cbcPlainData = sm4.decryptCBC(cbcCipherData, key, iv);
                if (null != cbcPlainData) {
                    System.out.println("[output data] SM4 CBC plain data : " + Convert.bytesToHexString(cbcPlainData));
                } else {
                    System.out.println("[**Error**] SM4 CBC plain data failed");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    public void testSM4ECBInvalidParams() {
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
