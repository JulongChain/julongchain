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
    private byte[] TEST_KEY = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
            (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
            (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98,
            (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
    private byte[] TEST_IV = {(byte)0xA2, (byte)0x45, (byte)0x1D, (byte)0x5F,
            (byte)0x34, (byte)0xA6, (byte)0xEA, (byte)0xD2,
            (byte)0x4E, (byte)0xDA, (byte)0xA3, (byte)0x98,
            (byte)0x6B, (byte)0x02, (byte)0xB1, (byte)0xF2};
    private byte[] TEST_MSG = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
            (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
            (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
            (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};

    @Test
    public void testSM4EncDec() {
        System.out.println("============ SM4 encryptECB decryptECB encryptCBC decryptCBC test ============ ");
        int[] messageLenList = {1, 16, 240, 360, 2048};
        unitTest(TEST_KEY, TEST_IV, messageLenList);
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
    public void testSM4ECBEncInvalidKeyParams() {
        System.out.println("============= SM4 encryptECB invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] key data: null");
            byte[] cipherData = sm4.encryptECB(TEST_MSG,null);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] cipherData = sm4.encryptECB(TEST_MSG, key0);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(TEST_KEY, 0, key15, 0, 15);
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] cipherData = sm4.encryptECB(TEST_MSG, key15);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(TEST_KEY, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] cipherData = sm4.encryptECB(TEST_MSG, key17);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncInvalidMsgParams() {
        System.out.println("============= SM4 encryptECB invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB message is null ****");
            System.out.println("[input data] message data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptECB(null,TEST_KEY);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB message length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptECB(msg0, TEST_KEY);
            if (null != cipherData) {
                System.out.println("[output data] encryptECB cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptECB cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecInvalidKeyParams() {
        System.out.println("============= SM4 decryptECB invalid parameters test =============");
        byte[] cipherData = null;
        try {
            cipherData = sm4.encryptECB(TEST_MSG, TEST_KEY);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptECB key is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data: null");
            byte[] plainData = sm4.decryptECB(cipherData,null);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptECB key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] plainData = sm4.decryptECB(cipherData, key0);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(TEST_KEY, 0, key15, 0, 15);
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] plainData = sm4.decryptECB(cipherData, key15);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptECB key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(TEST_KEY, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] plainData = sm4.decryptECB(cipherData, key17);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecInvalidCipherDataParams() {
        System.out.println("============= SM4 decryptECB invalid parameters test =============");
        byte[] cipherData = null;
        try {
            cipherData = sm4.encryptECB(TEST_MSG, TEST_KEY);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptECB cipher data is null ****");
            System.out.println("[input data] cipher data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptECB(null,TEST_KEY);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptECB cipher data length is 0 ****");
            byte[] cipherData0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptECB(cipherData0, TEST_KEY);
            if (null != plainData) {
                System.out.println("[output data] decryptECB plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptECB plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidKeyParams() {
        System.out.println("============= SM4 encryptCBC invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC key is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: null");
            byte[] cipherData = sm4.encryptCBC(TEST_MSG,null, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, key0, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(TEST_KEY, 0, key15, 0, 15);
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, key15, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(TEST_KEY, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, key17, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidIvParams() {
        System.out.println("============= SM4 encryptCBC invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC iv is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG,TEST_KEY, null);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC iv length is 0 ****");
            byte[] iv0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, iv0);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC iv length is 15 ****");
            byte[] iv15 = new byte[15];
            System.arraycopy(TEST_IV, 0, iv15, 0, 15);
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv15));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, iv15);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC iv length is 17 ****");
            byte[] iv17 = new byte[17];
            System.arraycopy(TEST_IV, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("[input data] message data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv17));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, iv17);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidMsgParams() {
        System.out.println("============= SM4 encryptCBC invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC message is null ****");
            System.out.println("[input data] message data : null");
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(null,TEST_KEY, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": encryptCBC message length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] cipherData = sm4.encryptCBC(msg0, TEST_KEY, TEST_IV);
            if (null != cipherData) {
                System.out.println("[output data] encryptCBC cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] encryptCBC cipher data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidKeyParams() {
        System.out.println("============= SM4 decryptCBC invalid parameters test =============");
        byte[] cipherData = null;
        try {
            cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, TEST_IV);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC key is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: null");
            byte[] plainData = sm4.decryptCBC(cipherData,null, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] plainData = sm4.decryptCBC(cipherData, key0, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(TEST_KEY, 0, key15, 0, 15);
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] plainData = sm4.decryptCBC(cipherData, key15, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(TEST_KEY, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] plainData = sm4.decryptCBC(cipherData, key17, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidIvParams() {
        System.out.println("============= SM4 decryptCBC invalid parameters test =============");
        byte[] cipherData = null;
        try {
            cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, TEST_IV);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC iv is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(cipherData,TEST_KEY, null);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC iv length is 0 ****");
            byte[] iv0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(cipherData, TEST_KEY, iv0);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC iv length is 15 ****");
            byte[] iv15 = new byte[15];
            System.arraycopy(TEST_IV, 0, iv15, 0, 15);
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(TEST_MSG));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv15));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(cipherData, TEST_KEY, iv15);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC iv length is 17 ****");
            byte[] iv17 = new byte[17];
            System.arraycopy(TEST_IV, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv17));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(cipherData, TEST_KEY, iv17);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidCipherDataParams() {
        System.out.println("============= SM4 decryptCBC invalid parameters test =============");
        byte[] cipherData = null;
        try {
            cipherData = sm4.encryptCBC(TEST_MSG, TEST_KEY, TEST_IV);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC cipher data is null ****");
            System.out.println("[input data] cipher data : null");
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(null,TEST_KEY, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": decryptCBC cipher data length is 0 ****");
            byte[] cipherData0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData0));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(TEST_IV));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(TEST_KEY));
            byte[] plainData = sm4.decryptCBC(cipherData0, TEST_KEY, TEST_IV);
            if (null != plainData) {
                System.out.println("[output data] decryptCBC plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] decryptCBC plain data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
