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
 * SDTSMJNI SM4接口单元测试
 *
 * @author tengxiumin
 * @date 2018/05/29
 * @company SDT
 */
public class SMJniSM4ApiTest {

    private SMJniApi jni = new SMJniApi();

    @Test
    public void testSM4ECBEncDec() {
        System.out.println("============= SMJniApi SM4EcbEnc and SM4EcbDec test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        try {
            byte[] key = jni.RandomGen( Constants.SM4_KEY_LEN);
            System.out.println("\n**** case 1 SM4ECBEncrypt ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));
            byte[] cipherData = jni.SM4ECBEncrypt(key, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECB encrypt data failed");
            }

            System.out.println("\n**** case 2 SM4ECBDecrypt ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));
            byte[] plainData = jni.SM4ECBDecrypt(key, cipherData);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECB decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncInvalidKeyParams() {
        System.out.println("============= SMJniApi SM4ECBEncrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt key is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data: null");
            byte[] cipherData = jni.SM4ECBEncrypt(null, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt key length is 0 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] key0 = new byte[0];
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] cipherData = jni.SM4ECBEncrypt(key0, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt key length is 15 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] cipherData = jni.SM4ECBEncrypt(key15, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt key length is 17 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] cipherData = jni.SM4ECBEncrypt(key17, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncInvalidMsgParams() {
        System.out.println("============= SMJniApi SM4ECBEncrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt message is null ****");
            System.out.println("[input data] message data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] cipherData = jni.SM4ECBEncrypt(key, null);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBEncrypt message length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] cipherData = jni.SM4ECBEncrypt(key, msg0);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM4ECBEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecInvalidKeyParams() {
        System.out.println("============= SMJniApi SM4ECBDecrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] cipherData = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            cipherData = jni.SM4ECBEncrypt(key, msg);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt key is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] key data: null");
            byte[] plainData = jni.SM4ECBDecrypt(null, cipherData);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt key length is 0 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            byte[] key0 = new byte[0];
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            byte[] plainData = jni.SM4ECBDecrypt(key0, cipherData);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt key length is 15 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            byte[] plainData = jni.SM4ECBDecrypt(key15, cipherData);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt key length is 17 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            byte[] plainData = jni.SM4ECBDecrypt(key17, cipherData);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecInvalidCipherDataParams() {
        System.out.println("============= SMJniApi SM4ECBDecrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] cipherData = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            cipherData = jni.SM4ECBEncrypt(key, msg);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt cipher data is null ****");
            System.out.println("[input data] cipher data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] plainData = jni.SM4ECBDecrypt(key, null);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4ECBDecrypt cipher data length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] plainData = jni.SM4ECBDecrypt(key, msg0);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM4ECBDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncDec() {
        System.out.println("============= SMJniApi SM4CbcEnc and SM4CbcDec test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        try {
            byte[] key = jni.RandomGen(Constants.SM4_KEY_LEN);
            byte[] iv = jni.RandomGen(Constants.SM4_IV_LEN);
            System.out.println("\n**** case 1 SM4CBCEncrypt ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherDataResult = jni.SM4CBCEncrypt(key, iv, msg);
            if (null != cipherDataResult) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherDataResult.getData()));
            } else {
                System.out.println("[**Error**] SM4CBC encrypt data failed");
            }

            System.out.println("\n**** case 2 SM4CBCDecrypt ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherDataResult.getData()));
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            SM4CBCResult plainDataResult = jni.SM4CBCDecrypt(key, iv, cipherDataResult.getData());
            if (null != plainDataResult) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainDataResult.getData()));
            } else {
                System.out.println("[**Error**] SM4CBC decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidKeyParams() {
        System.out.println("============= SMJniApi SM4CBCEncrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt key is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            System.out.println("[input data] key data: null");
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(null, iv, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt key length is 0 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            byte[] key0 = new byte[0];
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key0, iv, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt key length is 15 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0,15);
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key15, iv, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt key length is 17 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0,16);
            key17[16] = 0x17;
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key17, iv, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidIvParams() {
        System.out.println("============= SMJniApi SM4CBCEncrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt iv is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data : null");
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, null, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt iv length is 0 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] iv0 = new byte[0];
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv0));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, iv0, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt iv length is 15 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] iv15 = new byte[15];
            System.arraycopy(iv, 0, iv15, 0, 15);
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv15));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, iv15, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt iv length is 17 ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            byte[] iv17 = new byte[17];
            System.arraycopy(iv, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv17));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, iv17, msg);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncInvalidMsgParams() {
        System.out.println("============= SMJniApi SM4CBCEncrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt message is null ****");
            System.out.println("[input data] message data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data: " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, iv, null);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCEncrypt message length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data: " + Convert.bytesToHexString(iv));
            SM4CBCResult cipherData = jni.SM4CBCEncrypt(key, iv, msg0);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCEncrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidKeyParams() {
        System.out.println("============= SMJniApi SM4CBCDecrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        SM4CBCResult cipherData = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
            cipherData = jni.SM4CBCEncrypt(key, iv, msg);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt key is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            System.out.println("[input data] key data: null");
            SM4CBCResult plainData = jni.SM4CBCDecrypt(null, iv, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] key0 = new byte[0];
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt key length is 0 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key0));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key0, iv, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] key15 = new byte[15];
            System.arraycopy(key, 0, key15, 0, 15);
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt key length is 15 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key15));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key15, iv, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] key17 = new byte[17];
            System.arraycopy(key, 0, key17, 0, 16);
            key17[16] = 0x17;
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt key length is 17 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key17));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key17, iv, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidIvParams() {
        System.out.println("============= SMJniApi SM4CBCDecrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        SM4CBCResult cipherData = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
            cipherData = jni.SM4CBCEncrypt(key, iv, msg);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt iv is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, null, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] iv0 = new byte[0];
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt iv length is 0 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, iv0, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] iv15 = new byte[15];
            System.arraycopy(iv, 0, iv15, 0, 15);
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt iv length is 15 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv15));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, iv15, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            byte[] iv17 = new byte[17];
            System.arraycopy(key, 0, iv17, 0, 16);
            iv17[16] = 0x17;
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt iv length is 17 ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData.getData()));
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv17));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, iv17, cipherData.getData());
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCDecInvalidCipherDataParams() {
        System.out.println("============= SMJniApi SM4CBCDecrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] key = null;
        byte[] iv = null;
        SM4CBCResult cipherData = null;
        try {
            key = jni.RandomGen(Constants.SM4_KEY_LEN);
            iv = jni.RandomGen(Constants.SM4_IV_LEN);
            cipherData = jni.SM4CBCEncrypt(key, iv, msg);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt cipher data is null ****");
            System.out.println("[input data] cipher data : null");
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data: " + Convert.bytesToHexString(iv));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, iv, null);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM4CBCDecrypt cipher data length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] key data: " + Convert.bytesToHexString(key));
            System.out.println("[input data] iv data: " + Convert.bytesToHexString(iv));
            SM4CBCResult plainData = jni.SM4CBCDecrypt(key, iv, msg0);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData.getData()));
            } else {
                System.out.println("[**Error**] SM4CBCDecrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBEncryptDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM4 ECB encrypt data compare with GM/T 0002-2012 =============");
        System.out.println("**** case 1: one round ****");
        try {
            byte[] GMEcbCipherData = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] plain data : " + Convert.bytesToHexString(msg));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));

            byte[] ecbCipherData = jni.SM4ECBEncrypt(key, msg);
            System.out.println("[output data] cipher data : " + Convert.bytesToHexString(ecbCipherData));
            if(Arrays.equals(ecbCipherData, GMEcbCipherData)) {
                System.out.println("[compare result equal] SM4 ECBEncrypt data is equal with GM/T 0002-2012");
            } else {
                System.out.println("[compare result not equal] SM4 ECBEncrypt data is not equal with GM/T 0002-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("**** case 2: 1000000 rounds  ****");
        try {
            byte[] GMEcbCipherData = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xc7, (byte)0xc6, (byte)0xfd, (byte)0x27, (byte)0x1f,
                    (byte)0x04, (byte)0x02, (byte)0xf8, (byte)0x04, (byte)0xc3, (byte)0x3d, (byte)0x3f, (byte)0x66};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] plain data : " + Convert.bytesToHexString(msg));
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));
            byte[] ecbCipherResult = null;

            for ( int i = 0;i < 1000000; i++) {
                ecbCipherResult = jni.SM4ECBEncrypt(key, msg);
                msg = ecbCipherResult;
            }
            System.out.println("[output data] cipher data : " + Convert.bytesToHexString(ecbCipherResult));
            if(Arrays.equals(ecbCipherResult, GMEcbCipherData) ) {
                System.out.println("[compare result equal] SM4 ECBEncrypt data is equal with GM/T 0002-2012");
            } else {
                System.out.println("[compare result not equal] SM4 ECBEncrypt data is not equal with GM/T 0002-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBEncryptSpeed() {
        System.out.println("============= SMJniApi SM4 ECB encrypt speed test =============");
        try {
            byte[] testPlainData = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testPlainData[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };

            byte[] ecbCipherData = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                ecbCipherData = jni.SM4ECBEncrypt(key, testPlainData);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM4 ECB encrypt " + (100*num) + "MB data need : " + (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[speed] SM4 ECB encrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4ECBDecryptDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM4 ECB decrypt data compare with GM/T 0002-2012 =============");
        System.out.println("**** case 1: one round ****");
        try {
            byte[] GMEcbCipherData = {(byte)0x68, (byte)0x1E, (byte)0xDF, (byte)0x34, (byte)0xD2, (byte)0x06, (byte)0x96, (byte)0x5E,
                    (byte)0x86, (byte)0xB3, (byte)0xE9, (byte)0x4F, (byte)0x53, (byte)0x6E, (byte)0x42, (byte)0x46};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(GMEcbCipherData));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));

            byte[] ecbPlainData = jni.SM4ECBDecrypt(key, GMEcbCipherData);
            System.out.println("[output data] plain data : " + Convert.bytesToHexString(ecbPlainData));
            if(Arrays.equals(ecbPlainData, msg)) {
                System.out.println("[compare result equal] SM4 ECBDecrypt data is equal with GM/T 0002-2012");
            } else {
                System.out.println("[compare result not equal] SM4 ECBDecrypt data is not equal with GM/T 0002-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("**** case 2: 1000000 round ****");
        try {
            System.out.println("SM4 ECB decrypt data 1000000 round compare with GM/T 0002-2012 :");
            byte[] GMEcbCipherData = {(byte)0x59, (byte)0x52, (byte)0x98, (byte)0xc7, (byte)0xc6, (byte)0xfd, (byte)0x27, (byte)0x1f,
                    (byte)0x04, (byte)0x02, (byte)0xf8, (byte)0x04, (byte)0xc3, (byte)0x3d, (byte)0x3f, (byte)0x66};
            byte[] msg = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(GMEcbCipherData));

            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));

            byte[] ecbPlainData = null;
            for(int i = 0; i < 1000000; i++) {
                ecbPlainData = jni.SM4ECBDecrypt(key, GMEcbCipherData);
                GMEcbCipherData = ecbPlainData;
            }
            System.out.println("[output data] plain data : " + Convert.bytesToHexString(GMEcbCipherData));
            if(Arrays.equals(GMEcbCipherData, msg)) {
                System.out.println("[compare result equal] SM4 ECBDecrypt data is equal with GM/T 0002-2012");
            } else {
                System.out.println("[compare result not equal] SM4 ECBDecrypt data is not equal with GM/T 0002-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4ECBDecryptSpeed() {
        System.out.println("============= SMJniApi SM4 ECB decrypt speed test =============");
        try {
            byte[] testPlainData = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testPlainData[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                    (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98, (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
            byte[] ecbCipherData = jni.SM4ECBEncrypt(key, testPlainData);

            byte[] ecbPlainData = null;
            long startTime = System.currentTimeMillis();
            int num = 5;
            for(int i = 0; i < 1024*100*num; i++) {
                ecbPlainData = jni.SM4ECBDecrypt(key, ecbCipherData);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM4 ECB decrypt " + (100*num) + "MB data need : " + (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[speed] SM4 ECB decrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4CBCEncryptDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM4 ECB encrypt data compare with third party software =============");
        try {
            byte[] RefCipherData = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
                    (byte)0x94, (byte)0xE9, (byte)0x1E, (byte)0xFD, (byte)0x3B, (byte)0x52, (byte)0xA2, (byte)0x5C,
                    (byte)0x39, (byte)0xFD, (byte)0xB6, (byte)0x86, (byte)0xA5, (byte)0x12, (byte)0xA6, (byte)0x10,
                    (byte)0xC0, (byte)0xFD, (byte)0x18, (byte)0x90, (byte)0xF3, (byte)0xEB, (byte)0x67, (byte)0x66};
            byte[] msg = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c,
                    (byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            System.out.println("[input data] plain data : " + Convert.bytesToHexString(msg));

            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            System.out.println("[input data] key data : " + Convert.bytesToHexString(key));

            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            System.out.println("[input data] iv data : " + Convert.bytesToHexString(iv));

            SM4CBCResult cbcCipherResult = jni.SM4CBCEncrypt(key, iv, msg);
            if(Arrays.equals(cbcCipherResult.getData(), RefCipherData) ) {
                System.out.println("[compare result equal] SM4 CBCEncrypt data is equal with third party software");
            } else {
                System.out.println("[compare result not equal] SM4 CBCEncrypt data is not equal with third party software");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM4CBCEncryptSpeed() {
        System.out.println("============= SMJniApi SM4 CBC encrypt speed test =============");
        try {
            byte[] testPlainData = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testPlainData[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            SM4CBCResult cbcCipherResult = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                cbcCipherResult = jni.SM4CBCEncrypt(key, iv, testPlainData);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM4 CBC encrypt " + (100*num) + "MB data need : " + (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[speed] SM4 CBC encrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM4CBCDecryptDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM4 CBC decrypt data compare with third party software =============");
        try {
            byte[] RefCipherData = {(byte)0x2F, (byte)0x6E, (byte)0x99, (byte)0x81, (byte)0x95, (byte)0x3F, (byte)0x27, (byte)0x3F,
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

            SM4CBCResult cbcPlainResult = jni.SM4CBCDecrypt(key, iv, RefCipherData);
            System.out.println("[input data] plain data : " + Convert.bytesToHexString(cbcPlainResult.getData()));
            if(Arrays.equals(cbcPlainResult.getData(), msg) ) {
                System.out.println("[compare result equal] SM4 CBCDecrypt data is equal with third party software");
            } else {
                System.out.println("[compare result not equal] SM4 CBCDecrypt data is not equal with third party software");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void testSM4CBCDecryptSpeed() {
        System.out.println("============= SMJniApi SM4 CBC decrypt speed test =============");
        try {
            byte[] testPlainData = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testPlainData[i] = (byte)((i+1)%255);
            }
            byte[] key = {(byte)0x48,(byte)0x0e,(byte)0x6f,(byte)0x3a,(byte)0xc7,(byte)0x58,(byte)0x0e,(byte)0x67,
                    (byte)0x74,(byte)0xad,(byte)0xdc,(byte)0x42,(byte)0xd7,(byte)0x3c,(byte)0xfb,(byte)0x4c};
            byte[] iv = {(byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,
                    (byte)0xF1,(byte)0x5D,(byte)0x12,(byte)0x7A,(byte)0x02,(byte)0xBC,(byte)0x65,(byte)0x89,};
            SM4CBCResult cbcCipherResult = jni.SM4CBCEncrypt(key, iv, testPlainData);
            SM4CBCResult cbcPlainResult = null;
            int num = 5;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                cbcPlainResult = jni.SM4CBCDecrypt(key, iv, cbcCipherResult.getData());
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM4 CBC decrypt " + (100*num) + "MB data need : " + (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[speed] SM4 CBC decrypt speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
