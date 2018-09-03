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
package org.bcia.julongchain.csp.gm.sdt;

import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.gm.sdt.sm4.SM4;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
    public void testGenerateKey() {
        System.out.println("============ SM4 generateKey test ============ ");
        try {
            byte[] key = sm4.generateKey();
            if (null != key) {
                System.out.println("[ output ] SM4 key : " + Convert.bytesToHexString(key));
            } else {
                System.out.println("[** error **] failed generating SM4 key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
    private byte[] testKey = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
            (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
            (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98,
            (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
    private byte[] testIv = {(byte)0xA2, (byte)0x45, (byte)0x1D, (byte)0x5F,
            (byte)0x34, (byte)0xA6, (byte)0xEA, (byte)0xD2,
            (byte)0x4E, (byte)0xDA, (byte)0xA3, (byte)0x98,
            (byte)0x6B, (byte)0x02, (byte)0xB1, (byte)0xF2};
    private byte[] testPlainText = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
            (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
            (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
            (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};

    @Test
    public void testEncryptECBAndDecryptECB() {
        System.out.println("============ SM4 encryptECB and decryptECB test ============ ");
        int[] plainTextLenList = {1, 16, 240, 360, 2048};
        unitTestEncryptECBAndDecryptECB(testKey, plainTextLenList);
    }

    private void unitTestEncryptECBAndDecryptECB(byte[] key, int[] lists) {
        int caseIndex = 1;
        for(int i = 0; i < lists.length; i++) {
            try {
                int msgLen = lists[i];
                System.out.println("\n**** case " + caseIndex++ + ": plain text length is " + msgLen);
                byte[] msg = new byte[msgLen];
                if(msgLen > 1024) {
                    int leftLen = msgLen;
                    while (leftLen > 0) {
                        int len = leftLen;
                        if(len > 1024) {
                            len = 1024;
                        }
                        byte[] randomData = jni.randomGen(len);
                        System.arraycopy(randomData, 0, msg, msgLen-leftLen, len);
                        leftLen = leftLen - len;
                    }
                } else {
                    msg = jni.randomGen(msgLen);
                }
                System.out.println("[ input ] plain text (before encryption) : " + Convert.bytesToHexString(msg));
                byte[] cipherText = sm4.encryptECB(msg, key);
                if (null != cipherText) {
                    System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
                } else {
                    System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
                }
                byte[] plainText = sm4.decryptECB(cipherText, key);
                if (null != plainText) {
                    System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
                    if(Arrays.equals(plainText, msg)) {
                        System.out.println("[ compare result | equal ] the decrypted result is equal with the original plain text");
                    } else {
                        System.out.println("[ compare result | unequal ] the decrypted result is not equal with the original plain text");
                    }
                } else {
                    System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
                }
            } catch (Exception e) {
                System.out.println("[** exception **] " + e.getMessage());
            }
        }
    }

    @Test
    public void testEncryptCBCAndDecryptCBC() {
        System.out.println("============ SM4 encryptCBC and decryptCBC test ============ ");
        int[] plainTextLenList = {1, 16, 240, 360, 2048};
        unitTestEncryptCBCAndDecryptCBC(testKey, testIv, plainTextLenList);
    }

    private void unitTestEncryptCBCAndDecryptCBC(byte[] key, byte[] iv, int[] lists) {
        int caseIndex = 1;
        for(int i = 0; i < lists.length; i++) {
            try {
                int msgLen = lists[i];
                System.out.println("\n**** case " + caseIndex++ + ": plain text length is " + msgLen);
                byte[] msg = new byte[msgLen];
                if(msgLen > 1024) {
                    int leftLen = msgLen;
                    while (leftLen > 0) {
                        int len = leftLen;
                        if(len > 1024) {
                            len = 1024;
                        }
                        byte[] randomData = jni.randomGen(len);
                        System.arraycopy(randomData, 0, msg, msgLen-leftLen, len);
                        leftLen = leftLen - len;
                    }
                } else {
                    msg = jni.randomGen(msgLen);
                }
                System.out.println("[ input ] plain text (before encryption) : " + Convert.bytesToHexString(msg));
                byte[] cipherText = sm4.encryptCBC(msg, key, iv);
                if (null != cipherText) {
                    System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
                } else {
                    System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
                }
                byte[] plainText = sm4.decryptCBC(cipherText, key, iv);
                if (null != plainText) {
                    System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
                    if(Arrays.equals(plainText, msg)) {
                        System.out.println("[ compare result | equal ] the decrypted result is equal with the original plain text");
                    } else {
                        System.out.println("[ compare result | unequal ] the decrypted result is not equal with the original plain text");
                    }
                } else {
                    System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
                }
            } catch (Exception e) {
                System.out.println("[## exception ##] " + e.getMessage());
            }
        }
    }

    @Test
    public void testEncryptECBInvalidKey() {
        System.out.println("============= SM4 encryptECB invalid key test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : null");
            byte[] cipherText = sm4.encryptECB(testPlainText, null);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : ");
            byte[] cipherText = sm4.encryptECB(testPlainText, key0);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(testKey, 0, key15, 0, 15);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] cipherText = sm4.encryptECB(testPlainText,key15);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(testKey, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] cipherText = sm4.encryptECB(testPlainText,key17);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testEncryptECBInvalidPlainText() {
        System.out.println("============= SM4 encryptECB invalid plainText test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text is null ****");
            System.out.println("[ input ] plain text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptECB(null,testPlainText);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text length is 0 ****");
            byte[] plainText0 = new byte[0];
            System.out.println("[ input ] plain text : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptECB(plainText0,testPlainText);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testDecryptECBInvalidKey() {
        System.out.println("============= SM4 decryptECB invalid key test =============");
        byte[] cipherText = null;
        try {
            cipherText = sm4.encryptECB(testPlainText, testKey);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : null");
            byte[] plainData = sm4.decryptECB(cipherText,null);
            if (null != plainData) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : ");
            byte[] plainData = sm4.decryptECB(cipherText, key0);
            if (null != plainData) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(testKey, 0, key15, 0, 15);
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] plainData = sm4.decryptECB(cipherText, key15);
            if (null != plainData) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(testKey, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] plainData = sm4.decryptECB(cipherText, key17);
            if (null != plainData) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testDecryptECBInvalidCipherText() {
        System.out.println("============= SM4 decryptECB invalid cipherText test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text is null ****");
            System.out.println("[ input ] cipher text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptECB(null, testKey);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text length is 0 ****");
            byte[] cipherText0 = new byte[0];
            System.out.println("[ input ] cipher text : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptECB(cipherText0, testKey);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testEncryptCBCInvalidKey() {
        System.out.println("============= SM4 encryptCBC invalid key test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : null");
            byte[] cipherText = sm4.encryptCBC(testPlainText,null, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : ");
            byte[] cipherText = sm4.encryptCBC(testPlainText, key0, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(testKey, 0, key15, 0, 15);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] cipherText = sm4.encryptCBC(testPlainText, key15, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(testKey, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] cipherText = sm4.encryptCBC(testPlainText, key17, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testEncryptCBCInvalidIv() {
        System.out.println("============= SM4 encryptCBC invalid iv test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(testPlainText, testKey, null);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 0 ****");
            byte[] iv0 = new byte[0];
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(testPlainText, testKey, iv0);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 15 ****");
            byte[] iv15 = new byte[15];
            System.arraycopy(testIv, 0, iv15, 0, 15);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv15));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(testPlainText, testKey, iv15);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 17 ****");
            byte[] iv17 = new byte[17];
            System.arraycopy(testIv, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.arraycopy(testIv, 0, iv17, 0, 15);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv17));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(testPlainText, testKey, iv17);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testEncryptCBCInvalidPlainText() {
        System.out.println("============= SM4 encryptCBC invalid plainText test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text is null ****");
            System.out.println("[ input ] plain text : null");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(null, testKey, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[ input ] plain text : ");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] cipherText = sm4.encryptCBC(msg0, testKey, testIv);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testDecryptCBCInvalidKey() {
        System.out.println("============= SM4 decryptCBC invalid key test =============");
        byte[] cipherText = null;
        try {
            cipherText = sm4.encryptCBC(testPlainText, testKey, testIv);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : null");
            byte[] plainText = sm4.decryptCBC(cipherText, null, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            byte[] key0 = new byte[0];
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : ");
            byte[] plainText = sm4.decryptCBC(cipherText, key0, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            byte[] key15 = new byte[15];
            System.arraycopy(testKey, 0, key15, 0, 15);
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] plainText = sm4.decryptCBC(cipherText, key15, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting cipher text with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            byte[] key17 = new byte[17];
            System.arraycopy(testKey, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] plainText = sm4.decryptCBC(cipherText, key17, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testDecryptCBCInvalidIv() {
        System.out.println("============= SM4 decryptCBC invalid iv test =============");
        byte[] cipherText = null;
        try {
            cipherText = sm4.encryptCBC(testPlainText, testKey, testIv);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(cipherText, testKey, null);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 0 ****");
            byte[] iv0 = new byte[0];
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(cipherText, testKey, iv0);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 15 ****");
            byte[] iv15 = new byte[15];
            System.arraycopy(testIv, 0, iv15, 0, 15);
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv15));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(cipherText, testKey, iv15);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 17 ****");
            byte[] iv17 = new byte[17];
            System.arraycopy(testIv, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv17));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(cipherText, testKey, iv17);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testDecryptCBCInvalidCipherText() {
        System.out.println("============= SM4 decryptCBC invalid cipherText test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text is null ****");
            System.out.println("[ input ] cipher text : null");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(null, testKey, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher data length is 0 ****");
            byte[] cipherData0 = new byte[0];
            System.out.println("[ input ] cipher text : ");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(testIv));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(testKey));
            byte[] plainText = sm4.decryptCBC(cipherData0, testKey, testIv);
            if (null != plainText) {
                System.out.println("[ output ] plain text (after decryption) : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
}
