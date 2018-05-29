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
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;

import java.util.Arrays;

import static org.bcia.javachain.common.util.Convert.bytesToHexString;

/**
 * SDTSMJNI SM2接口单元测试
 *
 * @author tengxiumin
 * @date 2018/05/29
 * @company SDT
 */
public class SMJniSM2ApiTest {

    private SMJniApi jni = new SMJniApi();

    @Test
    public void testSM2MakeKey()
    {
        System.out.println("============= SMJniApi SM2MakeKey test =============");
        try {
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            System.out.println("[input data] SM2 private data : " + Convert.bytesToHexString(sk));
            byte[] pk = jni.SM2MakeKey(sk);
            if (null != pk) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(pk));
            } else {
                System.out.println("[**Error**] generate SM2 public key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2MakeKeyInvalidParams()
    {
        System.out.println("============= SMJniApi SM2MakeKey invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2MakeKey private key is null ****");
            System.out.println("[input data] SM2 private key : null " );
            byte[] pk = jni.SM2MakeKey(null);
            if (null != pk) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(pk));
            } else {
                System.out.println("[**Error**] generate SM2 public key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2MakeKey private key length is 0 ****");
            byte[] sk0 = new byte[0];
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk0));
            byte[] pk = jni.SM2MakeKey(sk0);
            if (null != pk) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(pk));
            } else {
                System.out.println("[**Error**] generate SM2 public key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2MakeKey private key length is 31 ****");
            byte[] sk31 = jni.RandomGen(31);
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk31));
            byte[] pk = jni.SM2MakeKey(sk31);
            if (null != pk) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(pk));
            } else {
                System.out.println("[**Error**] generate SM2 public key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2MakeKey private key length is 33 ****");
            byte[] sk33 = jni.RandomGen(33);
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk33));
            byte[] pk = jni.SM2MakeKey(sk33);
            if (null != pk) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(pk));
            } else {
                System.out.println("[**Error**] generate SM2 public key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2KDF()
    {
        System.out.println("============= SMJniApi SM2KDF test =============");
        int[] keyLenList = {1, 16, 128, 256, 384};
        int[] kdfKeyLenList = {1, 16, 128, 512, 1024};
        kdfUnitTest(keyLenList, kdfKeyLenList);
    }

    @Test
    public void testSM2KDFInvalidParams()
    {
        System.out.println("============= SMJniApi SM2KDF invalid parameters test =============");
        int caseIndex = 1;
        int kdfKeyLen = 32;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2KDF key is null ****");
            byte[] kdfKey = jni.SM2KDF(null, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[output data] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[**Error**] SM2 KDF key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2KDF key length is 0 ****");
            byte[] key0 = new byte[0];
            byte[] kdfKey = jni.SM2KDF(key0, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[output data] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[**Error**] SM2 KDF key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2KDF key length is 385 ****");
            byte[] key385 = new byte[385];
            for(int i = 0; i < 385; i++) {
                key385[i] = (byte)((i+1)%255);
            }
            System.out.println("[input data] source key data : " + Convert.bytesToHexString(key385));
            byte[] kdfKey = jni.SM2KDF(key385, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[output data] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[**Error**] SM2 KDF key failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private void kdfUnitTest(int[] keyLenList, int[] kdfKeyLenList) {
        int caseIndex = 1;
        for(int i = 0; i < keyLenList.length; i++) {
            int keyLen = keyLenList[i];
            byte[] key = new byte[keyLen];
            for(int k = 0; k < keyLen; k++) {
                key[k] = (byte)((k+1)%255);
            }
            for(int j = 0; j < kdfKeyLenList.length; j++) {
                int kdfKeyLen = kdfKeyLenList[j];
                try {
                    System.out.println("\n**** case " + caseIndex++ + ": SM2KDF key length is "+ keyLen + "; KDF key length = " + kdfKeyLen +" ****");
                    System.out.println("[input data] source key data : " + Convert.bytesToHexString(key));
                    byte[] kdfKey = jni.SM2KDF(key, kdfKeyLen);
                    if (null != kdfKey) {
                        System.out.println("[output data] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
                    } else {
                        System.out.println("[**Error**] SM2 KDF key failed");
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }
    }

    private byte[] GMHash ={(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94, (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
            (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED, (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
            (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A, (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
            (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF, (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};
    private byte[] GMRandom = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
            (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
            (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
            (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
    private byte[] GMSk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
            (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
            (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
            (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
    private byte[] GMPk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
            (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
            (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
            (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
            (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
            (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
            (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
            (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
    private byte[] GMSignData = {(byte)0xF5, (byte)0xA0, (byte)0x3B, (byte)0x06, (byte)0x48, (byte)0xD2, (byte)0xC4, (byte)0x63,
            (byte)0x0E, (byte)0xea, (byte)0xc5, (byte)0x13, (byte)0xe1, (byte)0xbb, (byte)0x81, (byte)0xa1,
            (byte)0x59, (byte)0x44, (byte)0xda, (byte)0x38, (byte)0x27, (byte)0xd5, (byte)0xb7, (byte)0x41,
            (byte)0x43, (byte)0xac, (byte)0x7e, (byte)0xac, (byte)0xee, (byte)0xe7, (byte)0x20, (byte)0xb3,
            (byte)0xb1, (byte)0xb6, (byte)0xaa, (byte)0x29, (byte)0xdf, (byte)0x21, (byte)0x2f, (byte)0xd8,
            (byte)0x76, (byte)0x31, (byte)0x82, (byte)0xbc, (byte)0x0d, (byte)0x42, (byte)0x1c, (byte)0xa1,
            (byte)0xbb, (byte)0x90, (byte)0x38, (byte)0xfd, (byte)0x1f, (byte)0x7f, (byte)0x42, (byte)0xd4,
            (byte)0x84, (byte)0x0b, (byte)0x69, (byte)0xc4, (byte)0x85, (byte)0xbb, (byte)0xc1, (byte)0xaa};

    @Test
    public void testSM2SignVerify()
    {
        System.out.println("============= SMJniApi SM2Sign and SM2Verify test =============");
        try {
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);
            System.out.println("\n**** case 1 SM2Sign ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk));
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, sk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }

            System.out.println("\n**** case 2 SM2Verify ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(signData));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk));
            int verify = jni.SM2Verify(GMHash, pk, signData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidHashParams() {
        System.out.println("============= SMJniApi SM2Sign invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign hash is null ****");
            System.out.println("[input data] hash data : null");
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(null, GMRandom, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign hash length is 0 ****");
            byte[] hash0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash0));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(hash0, GMRandom, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign hash length is 31 ****");
            byte[] hash31 = new byte[31];
            System.arraycopy(GMHash, 0, hash31, 0, 31);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash31));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(hash31, GMRandom, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign hash length is 33 ****");
            byte[] hash33 = new byte[33];
            System.arraycopy(GMHash, 0, hash33, 0, 32);
            hash33[32] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash33));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(hash33, GMRandom, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidRandomParams() {
        System.out.println("============= SMJniApi SM2Sign invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign random is null ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : null");
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(GMHash, null, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign random length is 0 ****");
            byte[] random0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random0));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(GMHash, random0, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign random length is 31 ****");
            byte[] random31 = new byte[31];
            System.arraycopy(GMRandom, 0, random31, 0, 31);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random31));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(GMHash, random31, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign random length is 33 ****");
            byte[] random33 = new byte[33];
            System.arraycopy(GMRandom, 0, random33, 0, 32);
            random33[32] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random33));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] signData = jni.SM2Sign(GMHash, random33, GMSk);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidSkParams() {
        System.out.println("============= SMJniApi SM2Sign invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign sk is null ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : null");
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, null);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign sk length is 0 ****");
            byte[] sk0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk0));
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, sk0);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign sk length is 31 ****");
            byte[] sk31 = new byte[31];
            System.arraycopy(GMSk, 0, sk31, 0, 31);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk31));
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, sk31);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Sign sk length is 33 ****");
            byte[] sk33 = new byte[33];
            System.arraycopy(GMSk, 0, sk33, 0, 32);
            sk33[32] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk33));
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, sk33);
            if (null != signData) {
                System.out.println("[output data] SM2 signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] SM2 sign data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidHashParams() {
        System.out.println("============= SMJniApi SM2Verify invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify hash is null ****");
            System.out.println("[input data] hash data : null");
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(null, GMPk, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify hash length is 0 ****");
            byte[] hash0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash0));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(hash0, GMPk, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify hash length is 31 ****");
            byte[] hash31 = new byte[31];
            System.arraycopy(GMHash, 0, hash31, 0, 31);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash31));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(hash31, GMPk, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify hash length is 33 ****");
            byte[] hash33 = new byte[33];
            System.arraycopy(GMHash, 0, hash33, 0, 32);
            hash33[32] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(hash33));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(hash33, GMPk, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidPkParams() {
        System.out.println("============= SMJniApi SM2Verify invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify pk is null ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : null");
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(GMHash, null, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify pk length is 0 ****");
            byte[] pk0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk0));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(GMHash, pk0, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify pk length is 63 ****");
            byte[] pk63 = new byte[63];
            System.arraycopy(GMPk, 0, pk63, 0, 63);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk63));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(GMHash, pk63, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify hash length is 65 ****");
            byte[] pk65 = new byte[65];
            System.arraycopy(GMPk, 0, pk65, 0, 64);
            pk65[64] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk65));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(GMSignData));
            int verify = jni.SM2Verify(GMHash, pk65, GMSignData);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidSigndataParams() {
        System.out.println("============= SMJniApi SM2Sign invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify signdata is null ****");
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : null");
            int verify = jni.SM2Verify(GMHash, GMHash, null);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify signdata length is 0 ****");
            byte[] sign0 = new byte[0];
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(sign0));
            int verify = jni.SM2Verify(GMHash, GMHash, sign0);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify signdata length is 63 ****");
            byte[] sign63 = new byte[63];
            System.arraycopy(GMSignData, 0, sign63, 0, 63);
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(sign63));
            int verify = jni.SM2Verify(GMHash, GMHash, sign63);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Verify signdata length is 65 ****");
            byte[] sign65 = new byte[65];
            System.arraycopy(GMSignData, 0, sign65, 0, 64);
            sign65[64] = 0x33;
            System.out.println("[input data] hash data : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(sign65));
            int verify = jni.SM2Verify(GMHash, GMHash, sign65);
            if (0 == verify) {
                System.out.println("[output data] SM2 verify signature success ");
            } else {
                System.out.println("[**Error**] SM2 verify signature failed ");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncDec() {

        System.out.println("============= SMJniApi SM2Encrypt and SM2Decrypt test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        try {
            byte[] sk = jni.RandomGen(Constants.SM2_SK_LEN);
            byte[] pk = jni.SM2MakeKey(sk);
            System.out.println("\n**** case 1 SM2Encrypt ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk));
            byte[] cipherData = jni.SM2Encrypt(msg, GMRandom, pk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }

            System.out.println("\n**** case 2 SM2Decrypt ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk));
            byte[] plainData = jni.SM2Decrypt(cipherData, sk);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidMessageParams() {
        System.out.println("============= SMJniApi SM2Encrypt invalid parameters test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt message is null ****");
            System.out.println("[input data] message data : null");
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(null, GMRandom, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt message is length is 0 ****");
            byte[] msg0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg0));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(msg0, GMRandom, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidRandomParams() {
        System.out.println("============= SMJniApi SM2Encrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt random is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : null");
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(msg, null, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt random is length is 0 ****");
            byte[] random0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random0));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(msg, random0, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt random is length is 31 ****");
            byte[] random31 = new byte[31];
            System.arraycopy(GMRandom, 0, random31, 0, 31);
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random31));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(msg, random31, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt random is length is 33 ****");
            byte[] random33 = new byte[33];
            System.arraycopy(GMRandom, 0, random33, 0, 32);
            random33[32] = 0x33;
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(random33));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(GMPk));
            byte[] cipherData = jni.SM2Encrypt(msg, random33, GMPk);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidPkParams() {
        System.out.println("============= SMJniApi SM2Encrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt pk is null ****");
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : null");
            byte[] cipherData = jni.SM2Encrypt(msg, GMRandom, null);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt pk is length is 0 ****");
            byte[] pk0 = new byte[0];
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk0));
            byte[] cipherData = jni.SM2Encrypt(msg, GMRandom, pk0);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt pk is length is 63 ****");
            byte[] pk63 = new byte[63];
            System.arraycopy(GMPk, 0, pk63, 0, 63);
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk63));
            byte[] cipherData = jni.SM2Encrypt(msg, GMRandom, pk63);
            if (null != cipherData) {
                System.out.println("[output data] cipherdata : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Encrypt pk is length is 65 ****");
            byte[] pk65 = new byte[65];
            System.arraycopy(GMPk, 0, pk65, 0, 64);
            pk65[64] = 0x33;
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            System.out.println("[input data] random data : " + Convert.bytesToHexString(GMRandom));
            System.out.println("[input data] SM2 public key : " + Convert.bytesToHexString(pk65));
            byte[] cipherData = jni.SM2Encrypt(msg, GMRandom, pk65);
            if (null != cipherData) {
                System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
            } else {
                System.out.println("[**Error**] SM2 encrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2DecryptInvalidCipherDataParams() {
        System.out.println("============= SMJniApi SM2Decrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] cipherData = null;
        try {
            cipherData = jni.SM2Encrypt(msg, GMRandom, GMPk);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt cipher data is null ****");
            System.out.println("[input data] cipher data : null" + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] plainData = jni.SM2Decrypt(null, GMSk);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt cipher data is length is 0 ****");
            byte[] cipherData0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData0));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(GMSk));
            byte[] plainData = jni.SM2Decrypt(cipherData0, GMSk);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2DecryptInvalidSkParams() {
        System.out.println("============= SMJniApi SM2Decrypt invalid parameters test =============");
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
                (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
                (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
        byte[] cipherData = null;
        try {
            cipherData = jni.SM2Encrypt(msg, GMRandom, GMPk);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt sk is null ****");
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : null");
            byte[] plainData = jni.SM2Decrypt(cipherData, null);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt sk length is 0 ****");
            byte[] sk0 = new byte[0];
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk0));
            byte[] plainData = jni.SM2Decrypt(cipherData, sk0);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt sk length is 31 ****");
            byte[] sk31 = new byte[31];
            System.arraycopy(GMSk, 0, sk31, 0, 31);
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk31));
            byte[] plainData = jni.SM2Decrypt(cipherData, sk31);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM2Decrypt sk length is 33 ****");
            byte[] sk33 = new byte[33];
            System.arraycopy(GMSk, 0, sk33, 0, 32);
            sk33[32] = 0x33;
            System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
            System.out.println("[input data] SM2 private key : " + Convert.bytesToHexString(sk33));
            byte[] plainData = jni.SM2Decrypt(cipherData, sk33);
            if (null != plainData) {
                System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
            } else {
                System.out.println("[**Error**] SM2 decrypt data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2EncryptCompareWithStandardData() {
        System.out.println("============= SMJniApi SM2 Encrypt compare with GM/T 0003.5-2012 =============");
        try {
            byte[] GMCipherData = {(byte)0x04, (byte)0xeb, (byte)0xfc, (byte)0x71, (byte)0x8e, (byte)0x8d, (byte)0x17, (byte)0x98,
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
            byte[] cipherData = jni.SM2Encrypt(plaintext, random, pk);
            System.out.println("[output data] cipher data : " + bytesToHexString(cipherData));
            if(Arrays.equals(cipherData, GMCipherData)) {
                System.out.println("[compare result equal] SM2 encrypt data is equal with GM/T 0003.5-2012");
            } else {
                System.out.println("[compare result not equal] SM2 encrypt data is not equal with GM/T 0003.5-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2DecryptCompareWithStandardData() {
        System.out.println("============= SMJniApi SM2 Decrypt compare with GM/T 0003.5-2012 =============");
        try {
            byte[] GMPlainData ={(byte)0x65, (byte)0x6e, (byte)0x63, (byte)0x72, (byte)0x79, (byte)0x70, (byte)0x74, (byte)0x69,
                    (byte)0x6f, (byte)0x6e, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, (byte)0x6e, (byte)0x64,
                    (byte)0x61, (byte)0x72, (byte)0x64};
            byte[] GBCipherData = {(byte)0x04, (byte)0xeb, (byte)0xfc, (byte)0x71, (byte)0x8e, (byte)0x8d, (byte)0x17, (byte)0x98,
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
            byte[] plainData = jni.SM2Decrypt(GBCipherData, sk);
            System.out.println("[output data] plain data : " + bytesToHexString(plainData));
            if(Arrays.equals(plainData, GMPlainData)) {
                System.out.println("[compare result equal] SM2 decrypt data is equal with GM/T 0003.5-2012");
            } else {
                System.out.println("[compare result not equal] SM2 decrypt data is not equal with GM/T 0003.5-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2SignDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM2 Sign data compare with GM/T 0003.5-2012 =============");
        try {
            byte[] signData = jni.SM2Sign(GMHash, GMRandom, GMSk);
            System.out.println("[output data] signature data : " + bytesToHexString(signData));
            if(Arrays.equals(signData, GMSignData)) {
                System.out.println("[compare result equal] SM2 signature data is equal with GM/T 0003.5-2012");
            } else {
                System.out.println("[compare result not equal] SM2 signature data is not equal with GM/T 0003.5-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignSpeed() {
        System.out.println("============= SMJniApi SM2 sign speed test =============");
        try {

            long startTime = System.currentTimeMillis();

            for(int i = 0; i < 10000; i++) {
                byte[] sign = jni.SM2Sign(GMHash, GMRandom, GMSk);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (10000.00 / ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM2 Sign 10000 times need : " + (float) ((endTime - startTime)/1000.00 )+ "s");
            System.out.println("[speed] SM2 Sign speed : " + speed + " /s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM2VerityDataUseStandardData() {
        System.out.println("============= SMJniApi SM2 verify use GM/T 0003.5-2012 data =============");
        try {
            System.out.println("[input data] hash : " + Convert.bytesToHexString(GMHash));
            System.out.println("[input data] pk : " + Convert.bytesToHexString(GMPk));
            System.out.println("[input data] signature : " + Convert.bytesToHexString(GMSignData));
            int verifyResult = jni.SM2Verify(GMHash, GMPk, GMSignData);
            if(verifyResult == 0) {
                System.out.println("[verify result success] SM2 verify signature data success.");
            } else {
                System.out.println("[verify result error]");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifySpeed() {
        System.out.println("============= SMJniApi SM2 verify speed test =============");
        try {
            int verifyResult = 0;
            long startTime = System.currentTimeMillis();
            int totalRound = 10000;
            for(int i = 0; i < totalRound; i++) {
                verifyResult = jni.SM2Verify(GMHash, GMPk, GMSignData);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (totalRound / ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM2 verify " + totalRound + " times need : " +  (float)((endTime - startTime)/1000) + "s");
            System.out.println("[speed] SM2 verify speed : " + speed + "/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
