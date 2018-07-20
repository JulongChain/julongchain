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
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SM4CBCResult;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;

import java.util.Arrays;

/**
 * SDTSMJNI SM4接口单元测试
 *
 * @author tengxiumin
 * @date 2018/05/29
 * @company SDT
 */
public class SMJniSM4ApiTest {

    private SMJniApi jni = new SMJniApi();

    private byte[] testPlainText = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
            (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
            (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
            (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};

    @Test
    public void testSM4ECBEncryptAndDecrypt() {
        System.out.println("============= SMJniApi sm4ECBEncrypt and sm4ECBDecrypt test =============");
        try {
            byte[] key = jni.randomGen( Constants.SM4_KEY_LEN);
            System.out.println("\n**** case 1 sm4ECBEncrypt ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] cipherText = jni.sm4ECBEncrypt(key, testPlainText);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 ECB mode");
            }

            System.out.println("\n**** case 2 sm4ECBDecrypt ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] plainText = jni.sm4ECBDecrypt(key, cipherText);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncryptInvalidKey() {
        System.out.println("============= SMJniApi sm4ECBEncrypt invalid key test =============");

        byte[] key = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : null");
            byte[] cipherText = jni.sm4ECBEncrypt(null, testPlainText);
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
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key0 = new byte[0];
            System.out.println("[ input ] key : ");
            byte[] cipherText = jni.sm4ECBEncrypt(key0, testPlainText);
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
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] cipherText = jni.sm4ECBEncrypt(key15, testPlainText);
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
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] cipherText = jni.sm4ECBEncrypt(key17, testPlainText);
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
    public void testSM4ECBEncryptInvalidPlainText() {
        System.out.println("============= SMJniApi sm4ECBEncrypt invalid plainText test =============");
        byte[] key = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text is null ****");
            System.out.println("[ input ] plain text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] cipherText = jni.sm4ECBEncrypt(key, null);
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
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] cipherText = jni.sm4ECBEncrypt(key, plainText0);
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
    public void testSM4ECBDecryptInvalidKey() {
        System.out.println("============= SMJniApi sm4ECBDecrypt invalid key test =============");
        byte[] key = null;
        byte[] cipherText = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            cipherText = jni.sm4ECBEncrypt(key, testPlainText);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] key : null");
            byte[] plainText = jni.sm4ECBDecrypt(null, cipherText);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            byte[] key0 = new byte[0];
            System.out.println("[ input ] key : ");
            byte[] plainText = jni.sm4ECBDecrypt(key0, cipherText);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            byte[] plainText = jni.sm4ECBDecrypt(key15, cipherText);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            byte[] plainText = jni.sm4ECBDecrypt(key17, cipherText);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecryptInvalidCipherText() {
        System.out.println("============= SMJniApi sm4ECBDecrypt invalid cipherText test =============");
        byte[] key = null;
        byte[] cipherText = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            cipherText = jni.sm4ECBEncrypt(key, testPlainText);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text is null ****");
            System.out.println("[ input ] cipher text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] plainText = jni.sm4ECBDecrypt(key, null);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
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
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] plainText = jni.sm4ECBDecrypt(key, cipherText0);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 ECB mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptAndDecrypt() {
        System.out.println("============= SMJniApi sm4CBCEncrypt and sm4CBCDecrypt test =============");
        try {
            byte[] key = jni.randomGen(Constants.SM4_KEY_LEN);
            byte[] iv = jni.randomGen(Constants.SM4_IV_LEN);
            System.out.println("\n**** case 1 : sm4CBCEncrypt ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }

            System.out.println("\n**** case 2 : sm4CBCDecrypt ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptInvalidKey() {
        System.out.println("============= SMJniApi sm4CBCEncrypt invalid key test =============");
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : null");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(null, iv, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key0 = new byte[0];
            System.out.println("[ input ] key : ");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key0, iv, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[ input ] key: " + Convert.bytesToHexString(key15));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key15, iv, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key17, iv, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptInvalidIv() {
        System.out.println("============= SMJniApi sm4CBCEncrypt invalid iv test =============");
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : null");
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, null, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 0 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] iv0 = new byte[0];
            System.out.println("[ input ] iv : ");
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv0, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 15 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] iv15 = new byte[15];
            System.arraycopy(iv, 0, iv15, 0, 15);
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv15));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv15, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 17 ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            byte[] iv17 = new byte[17];
            System.arraycopy(iv, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv17));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv17, testPlainText);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptInvalidPlainText() {
        System.out.println("============= SMJniApi sm4CBCEncrypt invalid plainText test =============");
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text is null ****");
            System.out.println("[ input ] plain text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv, null);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text length is 0 ****");
            byte[] plainText0 = new byte[0];
            System.out.println("[ input ] plain text : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv: " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv, plainText0);
            if (null != cipherTextResult) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            } else {
                System.out.println("[** error **] failed encrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecryptInvalidKey() {
        System.out.println("============= SMJniApi sm4CBCDecrypt invalid key test =============");
        byte[] key = null;
        byte[] iv = null;
        SM4CBCResult cipherTextResult = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
            cipherTextResult = jni.sm4CBCEncrypt(key, iv, testPlainText);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : null");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(null, iv, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] key0 = new byte[0];
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : ");
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key0, iv, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0, 15);
            System.out.println("\n**** case " + caseIndex++ + ": key length is 15 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key15));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key15, iv, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("\n**** case " + caseIndex++ + ": key length is 17 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key17));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key17, iv, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecryptInvalidIv() {
        System.out.println("============= SMJniApi sm4CBCDecrypt invalid iv test =============");
        byte[] key = null;
        byte[] iv = null;
        SM4CBCResult cipherTextResult = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
            cipherTextResult = jni.sm4CBCEncrypt(key, iv, testPlainText);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": iv is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : null");

            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, null, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] iv0 = new byte[0];
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 0 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : ");
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv0, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] iv15 = new byte[15];
            System.arraycopy(iv, 0, iv15, 0, 15);
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 15 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv15));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv15, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            byte[] iv17 = new byte[17];
            System.arraycopy(key, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("\n**** case " + caseIndex++ + ": iv length is 17 ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv17));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv17, cipherTextResult.getData());
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecryptInvalidCipherText() {
        System.out.println("============= SMJniApi sm4CBCDecrypt invalid cipherText test =============");

        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.randomGen(Constants.SM4_KEY_LEN);
            iv = jni.randomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text is null ****");
            System.out.println("[ input ] cipher text : null");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv, null);
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text length is 0 ****");
            byte[] cipherText0 = new byte[0];
            System.out.println("[ input ] cipher text : ");
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv, cipherText0);
            if (null != plainTextResult) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            } else {
                System.out.println("[** error **] failed decrypting data with SM4 CBC mode");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBEncryptDataCompareWithStandardData() {
        System.out.println("============= SMJniApi sm4ECBEncrypt result compare with GM/T 0002-2012 standard data =============");
        System.out.println("\n**** case 1 : 1 round ****");
        try {
            byte[] gmtECBCipherText = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] gmtECBPlainText = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(gmtECBPlainText));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));

            byte[] cipherText = jni.sm4ECBEncrypt(key, gmtECBPlainText);
            System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            if(Arrays.equals(gmtECBCipherText, cipherText)) {
                System.out.println("[ compare result | equal ] sm4ECBEncrypt result is equal with GM/T 0002-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm4ECBEncrypt result is not equal with GM/T 0002-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        System.out.println("\n**** case 2 : 1000000 rounds  ****");
        try {
            byte[] gmtECBCipherText = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xC7, (byte)0xC6, (byte)0xFD, (byte)0x27, (byte)0x1F,
                    (byte)0x04, (byte)0x02, (byte)0xF8, (byte)0x04, (byte)0xC3, (byte)0x3D, (byte)0x3F, (byte)0x66};
            byte[] gmtECBPlainText = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(gmtECBPlainText));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));

            byte[] cipherText = null;
            byte[] tmpPlainText = new byte[16];
            System.arraycopy(gmtECBPlainText, 0, tmpPlainText, 0, 16);
            for ( int i = 0;i < 1000000; i++) {
                cipherText = jni.sm4ECBEncrypt(key, tmpPlainText);
                System.arraycopy(cipherText, 0, tmpPlainText, 0, 16);
            }
            System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            if(Arrays.equals(gmtECBCipherText, cipherText) ) {
                System.out.println("[ compare result | equal ] sm4ECBEncrypt result is equal with GM/T 0002-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm4ECBEncrypt result is not equal with GM/T 0002-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncryptSpeed() {
        System.out.println("============= SMJniApi sm4ECBEncrypt speed test =============");
        try {
            byte[] plainText = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                plainText[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};

            byte[] cipherText = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                cipherText = jni.sm4ECBEncrypt(key, plainText);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm4ECBEncrypt " + (100*num) + "MB data need : " +
                                (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[ speed ] sm4ECBEncrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecryptResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm4ECBDecrypt result compare with GM/T 0002-2012 standard data =============");
        System.out.println("\n**** case 1 : 1 round ****");
        try {
            byte[] gmtECBCipherText = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] gmtECBPlainText = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(gmtECBCipherText));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));

            byte[] plainText = jni.sm4ECBDecrypt(key, gmtECBCipherText);
            System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            if(Arrays.equals(plainText, gmtECBPlainText)) {
                System.out.println("[ compare result | equal ] sm4ECBDecrypt result is equal with GM/T 0002-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm4ECBDecrypt result is not equal with GM/T 0002-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        System.out.println("\n**** case 2 : 1000000 round ****");
        try {
            System.out.println("sm4ECBDecrypt 1000000 round compare with GM/T 0002-2012 :");
            byte[] gmtECBCipherText = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xC7, (byte)0xC6, (byte)0xFD, (byte)0x27, (byte)0x1F,
                    (byte)0x04, (byte)0x02, (byte)0xF8, (byte)0x04, (byte)0xC3, (byte)0x3D, (byte)0x3F, (byte)0x66};
            byte[] gmtECBPlainText = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(gmtECBCipherText));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10};
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));

            byte[] plainText = null;
            byte[] tmpCipherText = new byte[16];
            System.arraycopy(gmtECBCipherText, 0, tmpCipherText, 0, 16);
            for(int i = 0; i < 1000000; i++) {
                plainText = jni.sm4ECBDecrypt(key, tmpCipherText);
                System.arraycopy(plainText, 0, tmpCipherText, 0, 16);
            }
            System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            if(Arrays.equals(plainText, gmtECBPlainText)) {
                System.out.println("[ compare result | equal ] sm4ECBDecrypt result is equal with GM/T 0002-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm4ECBDecrypt result is not equal with GM/T 0002-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecryptSpeed() {
        System.out.println("============= SMJniApi sm4ECBDecrypt speed test =============");
        try {
            byte[] plainText = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                plainText[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
                    (byte)0xFE, (byte)0xDC, (byte)0xBA, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            byte[] cipherText = jni.sm4ECBEncrypt(key, plainText);

            byte[] decryptedPlainText = null;
            long startTime = System.currentTimeMillis();
            int num = 5;
            for(int i = 0; i < 1024*100*num; i++) {
                decryptedPlainText = jni.sm4ECBDecrypt(key, cipherText);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm4ECBDecrypt " + (100*num) + "MB data need : " +
                                (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[ speed ] sm4ECBDecrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptResulltCompareWithStandardData() {
        System.out.println("============= SMJniApi sm4CBCEncrypt result compare with third party software result =============");
        try {
            byte[] refCipherText = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
                    (byte)0x94, (byte)0xE9, (byte)0x1E, (byte)0xFD, (byte)0x3B, (byte)0x52, (byte)0xA2, (byte)0x5C,
                    (byte)0x39, (byte)0xFD, (byte)0xB6, (byte)0x86, (byte)0xA5, (byte)0x12, (byte)0xA6, (byte)0x10,
                    (byte)0xC0, (byte)0xFD, (byte)0x18, (byte)0x90, (byte)0xF3, (byte)0xEB, (byte)0x67, (byte)0x66};
            byte[] refPlainText = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C,
                    (byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(refPlainText));

            byte[] key = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));

            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));

            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv, refPlainText);
            System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherTextResult.getData()));
            if(Arrays.equals(cipherTextResult.getData(), refCipherText) ) {
                System.out.println("[ compare result | equal ] sm4CBCEncrypt result is equal with third party software result");
            } else {
                System.out.println("[ compare result | unequal ] sm4CBCEncrypt result is not equal with third party software result");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptSpeed() {
        System.out.println("============= SMJniApi sm4CBCEncrypt speed test =============");
        try {
            byte[] plainText = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                plainText[i] = (byte)((i+1)%255);
            }
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            byte[] key = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            SM4CBCResult cipherTextResult = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                cipherTextResult = jni.sm4CBCEncrypt(key, iv, plainText);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm4CBCEncrypt " + (100*num) + "MB data need : " +
                                    (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[ speed ] sm4CBCEncrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecryptResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm4CBCDecrypt result compare with third party software result =============");
        try {
            byte[] refCipherText = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
                    (byte)0x94, (byte)0xE9, (byte)0x1E, (byte)0xFD, (byte)0x3B, (byte)0x52, (byte)0xA2, (byte)0x5C,
                    (byte)0x39, (byte)0xFD, (byte)0xB6, (byte)0x86, (byte)0xA5, (byte)0x12, (byte)0xA6, (byte)0x10,
                    (byte)0xC0, (byte)0xFD, (byte)0x18, (byte)0x90, (byte)0xF3, (byte)0xEB, (byte)0x67, (byte)0x66};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            byte[] key = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            byte[] refPlainText = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C,
                    (byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(refCipherText));
            System.out.println("[ input ] key : " + Convert.bytesToHexString(key));
            System.out.println("[ input ] iv : " + Convert.bytesToHexString(iv));

            SM4CBCResult plainTextResult = jni.sm4CBCDecrypt(key, iv, refCipherText);
            System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainTextResult.getData()));
            if(Arrays.equals(plainTextResult.getData(), refPlainText) ) {
                System.out.println("[ compare result | equal ] sm4CBCDecrypt result is equal with third party software result");
            } else {
                System.out.println("[ compare result | unequal ] sm4CBCDecrypt result is not equal with third party software result");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

    }

    @Test
    public void testSM4CBCDecryptSpeed() {
        System.out.println("============= SMJniApi sm4CBCDecrypt speed test =============");
        try {
            byte[] plainText = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                plainText[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x48,(byte)0x0E,(byte)0x6F,(byte)0x3A,(byte)0xC7,(byte)0x58,(byte)0x0E,(byte)0x67,
                    (byte)0x74,(byte)0xAD,(byte)0xDC,(byte)0x42,(byte)0xD7,(byte)0x3C,(byte)0xFB,(byte)0x4C};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            SM4CBCResult cipherTextResult = jni.sm4CBCEncrypt(key, iv, plainText);
            SM4CBCResult plainTextResult = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                plainTextResult = jni.sm4CBCDecrypt(key, iv, cipherTextResult.getData());
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm4CBCDecrypt " + (100*num) + "MB data need : " +
                                (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[ speed ] sm4CBCDecrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
}
