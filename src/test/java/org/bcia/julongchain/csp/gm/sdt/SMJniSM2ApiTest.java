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
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;

import java.util.Arrays;

import static org.bcia.julongchain.common.util.Convert.bytesToHexString;

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
        System.out.println("============= SMJniApi sm2MakeKey test =============");
        try {
            byte[] privateKey = jni.randomGen(Constants.SM2_PRIVATEKEY_LEN);
            System.out.println("[ input ] SM2 private key : " + Convert.bytesToHexString(privateKey));
            byte[] publicKey = jni.sm2MakeKey(privateKey);
            if (null != publicKey) {
                System.out.println("[ output ] SM2 public key : " + Convert.bytesToHexString(publicKey));
            } else {
                System.out.println("[** error **] failed generating SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2MakeKeyInvalidParams()
    {
        System.out.println("============= SMJniApi sm2MakeKey invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key is null ****");
            System.out.println("[ input ] SM2 private key : null " );
            byte[] publicKey = jni.sm2MakeKey(null);
            if (null != publicKey) {
                System.out.println("[ output ] SM2 public key : " + Convert.bytesToHexString(publicKey));
            } else {
                System.out.println("[** error **] failed generating SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 0 ****");
            byte[] privateKey0 = new byte[0];
            System.out.println("[ input ] SM2 private key : ");
            byte[] publicKey = jni.sm2MakeKey(privateKey0);
            if (null != publicKey) {
                System.out.println("[ output ] SM2 public key : " + Convert.bytesToHexString(publicKey));
            } else {
                System.out.println("[** error **] failed generating SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 31 ****");
            byte[] privateKey31 = jni.randomGen(31);
            System.out.println("[ input ] SM2 private key : " + Convert.bytesToHexString(privateKey31));
            byte[] publicKey = jni.sm2MakeKey(privateKey31);
            if (null != publicKey) {
                System.out.println("[ output ] SM2 public key : " + Convert.bytesToHexString(publicKey));
            } else {
                System.out.println("[** error **] failed generating SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 33 ****");
            byte[] privateKey33 = jni.randomGen(33);
            System.out.println("[ input ] SM2 private key : " + Convert.bytesToHexString(privateKey33));
            byte[] publicKey = jni.sm2MakeKey(privateKey33);
            if (null != publicKey) {
                System.out.println("[ output ] SM2 public key : " + Convert.bytesToHexString(publicKey));
            } else {
                System.out.println("[** error **] failed generating SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2KDF()
    {
        System.out.println("============= SMJniApi sm2KDF test =============");
        int[] keyLenList = {1, 16, 128, 256, 384};
        int[] kdfKeyLenList = {1, 16, 128, 512, 1024};
        kdfUnitTest(keyLenList, kdfKeyLenList);
    }

    @Test
    public void testSM2KDFInvalidParams()
    {
        System.out.println("============= SMJniApi sm2KDF invalid parameters test =============");
        int caseIndex = 1;
        int kdfKeyLen = 32;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": key is null ****");
            byte[] kdfKey = jni.sm2KDF(null, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 0 ****");
            byte[] key0 = new byte[0];
            byte[] kdfKey = jni.sm2KDF(key0, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": key length is 385 ****");
            byte[] key385 = new byte[385];
            for(int i = 0; i < 385; i++) {
                key385[i] = (byte)((i+1)%255);
            }
            System.out.println("[ input ] source key : " + Convert.bytesToHexString(key385));
            byte[] kdfKey = jni.sm2KDF(key385, kdfKeyLen);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        byte[] key = new byte[16];
        for(int k = 0; k < 16; k++) {
            key[k] = (byte)((k+1));
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": length is -1 ****");
            System.out.println("[ input ] source key : " + Convert.bytesToHexString(key));
            byte[] kdfKey = jni.sm2KDF(key, -1);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": length is 0 ****");
            System.out.println("[ input ] source key : " + Convert.bytesToHexString(key));
            byte[] kdfKey = jni.sm2KDF(key, 0);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": length is 1025 ****");
            System.out.println("[ input ] source key : " + Convert.bytesToHexString(key));
            byte[] kdfKey = jni.sm2KDF(key, 1025);
            if (null != kdfKey) {
                System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
            } else {
                System.out.println("[** error **] failed getting the SM2 KDF key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
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
                    System.out.println("\n**** case " + caseIndex++ + ": key length is "+ keyLen + "; KDF key length = " + kdfKeyLen +" ****");
                    System.out.println("[ input ] source key : " + Convert.bytesToHexString(key));
                    byte[] kdfKey = jni.sm2KDF(key, kdfKeyLen);
                    if (null != kdfKey) {
                        System.out.println("[ output ] SM2 KDF key : " + Convert.bytesToHexString(kdfKey));
                    } else {
                        System.out.println("[** error **] failed getting the SM2 KDF key");
                    }
                } catch (Exception e) {
                    System.out.println("[## exception ##] " + e.getMessage());
                }
            }
        }
    }

    private byte[] gmtDigest ={(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94, (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
            (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED, (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
            (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A, (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
            (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF, (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};
    private byte[] gmtRandom = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
            (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
            (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
            (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
    private byte[] gmtPrivateKey = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
            (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
            (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
            (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
    private byte[] gmtPublicKey = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
            (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
            (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
            (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
            (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
            (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
            (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
            (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
    private byte[] gmtSignature = {(byte)0xF5, (byte)0xA0, (byte)0x3B, (byte)0x06, (byte)0x48, (byte)0xD2, (byte)0xC4, (byte)0x63,
            (byte)0x0E, (byte)0xEA, (byte)0xC5, (byte)0x13, (byte)0xE1, (byte)0xBB, (byte)0x81, (byte)0xA1,
            (byte)0x59, (byte)0x44, (byte)0xDA, (byte)0x38, (byte)0x27, (byte)0xD5, (byte)0xB7, (byte)0x41,
            (byte)0x43, (byte)0xAC, (byte)0x7E, (byte)0xAC, (byte)0xEE, (byte)0xE7, (byte)0x20, (byte)0xB3,
            (byte)0xB1, (byte)0xB6, (byte)0xAA, (byte)0x29, (byte)0xDF, (byte)0x21, (byte)0x2F, (byte)0xD8,
            (byte)0x76, (byte)0x31, (byte)0x82, (byte)0xBC, (byte)0x0D, (byte)0x42, (byte)0x1C, (byte)0xA1,
            (byte)0xBB, (byte)0x90, (byte)0x38, (byte)0xFD, (byte)0x1F, (byte)0x7F, (byte)0x42, (byte)0xD4,
            (byte)0x84, (byte)0x0B, (byte)0x69, (byte)0xC4, (byte)0x85, (byte)0xBB, (byte)0xC1, (byte)0xAA};
    private byte[] testPlainText = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A,
            (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
            (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B,
            (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};

    @Test
    public void testSM2SignAndSM2Verify()
    {
        System.out.println("============= SMJniApi sm2Sign and sm2Verify test =============");
        try {
            byte[] privateKey = jni.randomGen(Constants.SM2_PRIVATEKEY_LEN);
            byte[] publicKey = jni.sm2MakeKey(privateKey);
            System.out.println("\n**** case 1 : sm2Sign ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, privateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }

            System.out.println("\n**** case 2 : sm2Verify ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(signature));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey));
            int verify = jni.sm2Verify(gmtDigest, publicKey, signature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidDigest() {
        System.out.println("============= SMJniApi sm2Sign invalid digest test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest is null ****");
            System.out.println("[ input ] digest : null");
            System.out.println("[ input ] random : " + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(null, gmtRandom, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 0 ****");
            byte[] digest0 = new byte[0];
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(digest0));
            System.out.println("[ input ] random : " + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(digest0, gmtRandom, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 31 ****");
            byte[] digest31 = new byte[31];
            System.arraycopy(gmtDigest, 0, digest31, 0, 31);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(digest31));
            System.out.println("[ input ] random : " + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(digest31, gmtRandom, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 33 ****");
            byte[] digest33 = new byte[33];
            System.arraycopy(gmtDigest, 0, digest33, 0, 32);
            digest33[32] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(digest33));
            System.out.println("[ input ] random : " + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(digest33, gmtRandom, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidRandom() {
        System.out.println("============= SMJniApi sm2Sign invalid random test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": random is null ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random : null");
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, null, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random length is 0 ****");
            byte[] random0 = new byte[0];
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random : ");
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, random0, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random length is 31 ****");
            byte[] random31 = new byte[31];
            System.arraycopy(gmtRandom, 0, random31, 0, 31);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random : " + Convert.bytesToHexString(random31));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, random31, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random length is 33 ****");
            byte[] random33 = new byte[33];
            System.arraycopy(gmtRandom, 0, random33, 0, 32);
            random33[32] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random : " + Convert.bytesToHexString(random33));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, random33, gmtPrivateKey);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignInvalidPrivateKey() {
        System.out.println("============= SMJniApi sm2Sign invalid privateKey test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key is null ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : null");
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, null);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 0 ****");
            byte[] privateKey0 = new byte[0];
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : ");
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, privateKey0);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 31 ****");
            byte[] privateKey31 = new byte[31];
            System.arraycopy(gmtPrivateKey, 0, privateKey31, 0, 31);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey31));
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, privateKey31);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 33 ****");
            byte[] privateKey33 = new byte[33];
            System.arraycopy(gmtPrivateKey, 0, privateKey33, 0, 32);
            privateKey33[32] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey33));
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, privateKey33);
            if (null != signature) {
                System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
            } else {
                System.out.println("[** error **] failed signing the digest");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidDigest() {
        System.out.println("============= SMJniApi sm2Verify invalid digest test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest is null ****");
            System.out.println("[ input ] digest : null");
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(null, gmtPublicKey, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 0 ****");
            byte[] digest0 = new byte[0];
            System.out.println("[ input ] digest : ");
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(digest0, gmtPublicKey, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 31 ****");
            byte[] digest31 = new byte[31];
            System.arraycopy(gmtDigest, 0, digest31, 0, 31);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(digest31));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(digest31, gmtPublicKey, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 33 ****");
            byte[] digest33 = new byte[33];
            System.arraycopy(gmtDigest, 0, digest33, 0, 32);
            digest33[32] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(digest33));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(digest33, gmtPublicKey, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidPublicKey() {
        System.out.println("============= SMJniApi sm2Verify invalid public key test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key is null ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : null");
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(gmtDigest, null, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 0 ****");
            byte[] publicKey0 = new byte[0];
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : ");
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(gmtDigest, publicKey0, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 63 ****");
            byte[] publicKey63 = new byte[63];
            System.arraycopy(gmtPublicKey, 0, publicKey63, 0, 63);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey63));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(gmtDigest, publicKey63, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 65 ****");
            byte[] publicKey65 = new byte[65];
            System.arraycopy(gmtPublicKey, 0, publicKey65, 0, 64);
            publicKey65[64] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey65));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verify = jni.sm2Verify(gmtDigest, publicKey65, gmtSignature);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidSignature() {
        System.out.println("============= SMJniApi sm2Verify invalid signature test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature is null ****");
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : null");
            int verify = jni.sm2Verify(gmtDigest, gmtPublicKey, null);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 0 ****");
            byte[] signature0 = new byte[0];
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : ");
            int verify = jni.sm2Verify(gmtDigest, gmtPublicKey, signature0);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 63 ****");
            byte[] signature63 = new byte[63];
            System.arraycopy(gmtSignature, 0, signature63, 0, 63);
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(signature63));
            int verify = jni.sm2Verify(gmtDigest, gmtPublicKey, signature63);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 65 ****");
            byte[] signature65 = new byte[65];
            System.arraycopy(gmtSignature, 0, signature65, 0, 64);
            signature65[64] = 0x33;
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(signature65));
            int verify = jni.sm2Verify(gmtDigest, gmtPublicKey, signature65);
            if (0 == verify) {
                System.out.println("[ output ] succeed verifying the signature");
            } else {
                System.out.println("[** error **] failed verifying the signature");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptAndSM2Decrypt() {

        System.out.println("============= SMJniApi sm2Encrypt and sm2Decrypt test =============");

        try {
            byte[] privateKey = jni.randomGen(Constants.SM2_PRIVATEKEY_LEN);
            byte[] publicKey = jni.sm2MakeKey(privateKey);
            System.out.println("\n**** case 1 : sm2Encrypt ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, publicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }

            System.out.println("\n**** case 2 : sm2Decrypt ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey));
            byte[] plainText = jni.sm2Decrypt(cipherText, privateKey);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidPlainText() {
        System.out.println("============= SMJniApi sm2Encrypt invalid plainText test =============");

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text is null ****");
            System.out.println("[ input ] plain text : null");
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(null, gmtRandom, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": plain text length is 0 ****");
            byte[] plainText0 = new byte[0];
            System.out.println("[ input ] plain text : ");
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(plainText0, gmtRandom, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidRandom() {
        System.out.println("============= SMJniApi sm2Encrypt invalid random test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": random is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random : null");
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, null, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random length is 0 ****");
            byte[] random0 = new byte[0];
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random : ");
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, random0, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random length is 31 ****");
            byte[] random31 = new byte[31];
            System.arraycopy(gmtRandom, 0, random31, 0, 31);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(random31));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, random31, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": random is length is 33 ****");
            byte[] random33 = new byte[33];
            System.arraycopy(gmtRandom, 0, random33, 0, 32);
            random33[32] = 0x33;
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(random33));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, random33, gmtPublicKey);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2EncryptInvalidPublicKey() {
        System.out.println("============= SMJniApi sm2Encrypt invalid public key test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key is null ****");
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : null");
            byte[] cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, null);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 0 ****");
            byte[] publicKey0 = new byte[0];
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : ");
            byte[] cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, publicKey0);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 63 ****");
            byte[] publicKey63 = new byte[63];
            System.arraycopy(gmtPublicKey, 0, publicKey63, 0, 63);
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey63));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, publicKey63);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 65 ****");
            byte[] publicKey65 = new byte[65];
            System.arraycopy(gmtPublicKey, 0, publicKey65, 0, 64);
            publicKey65[64] = 0x33;
            System.out.println("[ input ] plain text : " + Convert.bytesToHexString(testPlainText));
            System.out.println("[ input ] random :" + Convert.bytesToHexString(gmtRandom));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(publicKey65));
            byte[] cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, publicKey65);
            if (null != cipherText) {
                System.out.println("[ output ] cipher text : " + Convert.bytesToHexString(cipherText));
            } else {
                System.out.println("[** error **] failed encrypting data with SM2 public key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2DecryptInvalidCipherText() {
        System.out.println("============= SMJniApi sm2Decrypt invalid cipher text test =============");
        byte[] cipherText = null;
        try {
            cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, gmtPublicKey);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text is null ****");
            System.out.println("[ input ] cipher text : null");
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] plainText = jni.sm2Decrypt(null, gmtPrivateKey);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": cipher text length is 0 ****");
            byte[] cipherText0 = new byte[0];
            System.out.println("[ input ] cipher text : ");
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(gmtPrivateKey));
            byte[] plainText = jni.sm2Decrypt(cipherText0, gmtPrivateKey);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2DecryptInvalidPrivateKey() {
        System.out.println("============= SMJniApi sm2Decrypt invalid private key test =============");
        byte[] cipherText = null;
        try {
            cipherText = jni.sm2Encrypt(testPlainText, gmtRandom, gmtPublicKey);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
            return;
        }

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key is null ****");
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] private key : null");
            byte[] plainText = jni.sm2Decrypt(cipherText, null);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 0 ****");
            byte[] privateKey0 = new byte[0];
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] private key : ");
            byte[] plainText = jni.sm2Decrypt(cipherText, privateKey0);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 31 ****");
            byte[] privateKey31 = new byte[31];
            System.arraycopy(gmtPrivateKey, 0, privateKey31, 0, 31);
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey31));
            byte[] plainText = jni.sm2Decrypt(cipherText, privateKey31);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 33 ****");
            byte[] privateKey33 = new byte[33];
            System.arraycopy(gmtPrivateKey, 0, privateKey33, 0, 32);
            privateKey33[32] = 0x33;
            System.out.println("[ input ] cipher text : " + Convert.bytesToHexString(cipherText));
            System.out.println("[ input ] private key : " + Convert.bytesToHexString(privateKey33));
            byte[] plainText = jni.sm2Decrypt(cipherText, privateKey33);
            if (null != plainText) {
                System.out.println("[ output ] plain text : " + Convert.bytesToHexString(plainText));
            } else {
                System.out.println("[** error **] failed decrypting data with SM2 private key");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void SM2EncryptResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm2Encrypt result compare with GM/T 0003.5-2012 =============");
        try {
            byte[] gmtCipherText = {(byte)0x04, (byte)0xEB, (byte)0xFC, (byte)0x71, (byte)0x8E, (byte)0x8D, (byte)0x17, (byte)0x98,
                    (byte)0x62, (byte)0x04, (byte)0x32, (byte)0x26, (byte)0x8E, (byte)0x77, (byte)0xFE, (byte)0xB6,
                    (byte)0x41, (byte)0x5E, (byte)0x2E, (byte)0xDE, (byte)0x0E, (byte)0x07, (byte)0x3C, (byte)0x0F,
                    (byte)0x4F, (byte)0x64, (byte)0x0E, (byte)0xCD, (byte)0x2E, (byte)0x14, (byte)0x9A, (byte)0x73,
                    (byte)0xE8, (byte)0x58, (byte)0xF9, (byte)0xD8, (byte)0x1E, (byte)0x54, (byte)0x30, (byte)0xA5,
                    (byte)0x7B, (byte)0x36, (byte)0xDA, (byte)0xAB, (byte)0x8F, (byte)0x95, (byte)0x0A, (byte)0x3C,
                    (byte)0x64, (byte)0xE6, (byte)0xEE, (byte)0x6A, (byte)0x63, (byte)0x09, (byte)0x4D, (byte)0x99,
                    (byte)0x28, (byte)0x3A, (byte)0xFF, (byte)0x76, (byte)0x7E, (byte)0x12, (byte)0x4D, (byte)0xF0,
                    (byte)0x21, (byte)0x88, (byte)0x6C, (byte)0xA9, (byte)0x89, (byte)0xCA, (byte)0x9C, (byte)0x7D,
                    (byte)0x58, (byte)0x08, (byte)0x73, (byte)0x07, (byte)0xCA, (byte)0x93, (byte)0x09, (byte)0x2D,
                    (byte)0x65, (byte)0x1E, (byte)0xFA, (byte)0x59, (byte)0x98, (byte)0x3C, (byte)0x18, (byte)0xF8,
                    (byte)0x09, (byte)0xE2, (byte)0x62, (byte)0x92, (byte)0x3C, (byte)0x53, (byte)0xAE, (byte)0xC2,
                    (byte)0x95, (byte)0xD3, (byte)0x03, (byte)0x83, (byte)0xB5, (byte)0x4E, (byte)0x39, (byte)0xD6,
                    (byte)0x09, (byte)0xD1, (byte)0x60, (byte)0xAF, (byte)0xCB, (byte)0x19, (byte)0x08, (byte)0xD0,
                    (byte)0xBD, (byte)0x87, (byte)0x66};
            byte[] gmtPlainText ={(byte)0x65, (byte)0x6E, (byte)0x63, (byte)0x72, (byte)0x79, (byte)0x70, (byte)0x74, (byte)0x69,
                    (byte)0x6F, (byte)0x6E, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, (byte)0x6E, (byte)0x64,
                    (byte)0x61, (byte)0x72, (byte)0x64};
            byte[] random = {(byte)0x59, (byte)0x27, (byte)0x6E, (byte)0x27, (byte)0xD5, (byte)0x06, (byte)0x86, (byte)0x1A,
                    (byte)0x16, (byte)0x68, (byte)0x0F, (byte)0x3A, (byte)0xD9, (byte)0xC0, (byte)0x2D, (byte)0xCC,
                    (byte)0xEF, (byte)0x3C, (byte)0xC1, (byte)0xFA, (byte)0x3C, (byte)0xDB, (byte)0xE4, (byte)0xCE,
                    (byte)0x6D, (byte)0x54, (byte)0xB8, (byte)0x0D, (byte)0xEA, (byte)0xC1, (byte)0xBC, (byte)0x21};
            byte[] publicKey = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
            System.out.println("[ input ] plain text : " + bytesToHexString(gmtPlainText));
            System.out.println("[ input ] random : " + bytesToHexString(random));
            System.out.println("[ input ] public key : " + bytesToHexString(publicKey));
            byte[] cipherText = jni.sm2Encrypt(gmtPlainText, random, publicKey);
            System.out.println("[ output ] cipher text : " + bytesToHexString(cipherText));
            if(Arrays.equals(cipherText, gmtCipherText)) {
                System.out.println("[ compare result | equal ] sm2Encrypt result is equal with GM/T 0003.5-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm2Encrypt result is not equal with GM/T 0003.5-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void SM2DecryptResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm2Decrypt result with GM/T 0003.5-2012 =============");
        try {
            byte[] gmtPlainText ={(byte)0x65, (byte)0x6E, (byte)0x63, (byte)0x72, (byte)0x79, (byte)0x70, (byte)0x74, (byte)0x69,
                    (byte)0x6F, (byte)0x6E, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, (byte)0x6E, (byte)0x64,
                    (byte)0x61, (byte)0x72, (byte)0x64};
            byte[] gmtCipherText = {(byte)0x04, (byte)0xEB, (byte)0xFC, (byte)0x71, (byte)0x8E, (byte)0x8D, (byte)0x17, (byte)0x98,
                    (byte)0x62, (byte)0x04, (byte)0x32, (byte)0x26, (byte)0x8E, (byte)0x77, (byte)0xFE, (byte)0xB6,
                    (byte)0x41, (byte)0x5E, (byte)0x2E, (byte)0xDE, (byte)0x0E, (byte)0x07, (byte)0x3C, (byte)0x0F,
                    (byte)0x4F, (byte)0x64, (byte)0x0E, (byte)0xCD, (byte)0x2E, (byte)0x14, (byte)0x9A, (byte)0x73,
                    (byte)0xE8, (byte)0x58, (byte)0xF9, (byte)0xD8, (byte)0x1E, (byte)0x54, (byte)0x30, (byte)0xA5,
                    (byte)0x7B, (byte)0x36, (byte)0xDA, (byte)0xAB, (byte)0x8F, (byte)0x95, (byte)0x0A, (byte)0x3C,
                    (byte)0x64, (byte)0xE6, (byte)0xEE, (byte)0x6A, (byte)0x63, (byte)0x09, (byte)0x4D, (byte)0x99,
                    (byte)0x28, (byte)0x3A, (byte)0xFF, (byte)0x76, (byte)0x7E, (byte)0x12, (byte)0x4D, (byte)0xF0,
                    (byte)0x21, (byte)0x88, (byte)0x6C, (byte)0xA9, (byte)0x89, (byte)0xCA, (byte)0x9C, (byte)0x7D,
                    (byte)0x58, (byte)0x08, (byte)0x73, (byte)0x07, (byte)0xCA, (byte)0x93, (byte)0x09, (byte)0x2D,
                    (byte)0x65, (byte)0x1E, (byte)0xFA, (byte)0x59, (byte)0x98, (byte)0x3C, (byte)0x18, (byte)0xF8,
                    (byte)0x09, (byte)0xE2, (byte)0x62, (byte)0x92, (byte)0x3C, (byte)0x53, (byte)0xAE, (byte)0xC2,
                    (byte)0x95, (byte)0xD3, (byte)0x03, (byte)0x83, (byte)0xB5, (byte)0x4E, (byte)0x39, (byte)0xD6,
                    (byte)0x09, (byte)0xD1, (byte)0x60, (byte)0xAF, (byte)0xCB, (byte)0x19, (byte)0x08, (byte)0xD0,
                    (byte)0xBD, (byte)0x87, (byte)0x66};
            byte[] privateKey = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            System.out.println("[ input ] cipher text : " + bytesToHexString(gmtCipherText));
            System.out.println("[ input ] private key : " + bytesToHexString(privateKey));
            byte[] plainText = jni.sm2Decrypt(gmtCipherText, privateKey);
            System.out.println("[ output ] plain text : " + bytesToHexString(plainText));
            if(Arrays.equals(plainText, gmtPlainText)) {
                System.out.println("[ compare result | equal ] sm2Decrypt result is equal with GM/T 0003.5-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm2Decrypt result is not equal with GM/T 0003.5-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void SM2SignResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm2Sign result compare with GM/T 0003.5-2012 =============");
        try {
            System.out.println("[ input ] digest : " + bytesToHexString(gmtDigest));
            System.out.println("[ input ] random : " + bytesToHexString(gmtRandom));
            System.out.println("[ input ] private key : " + bytesToHexString(gmtPrivateKey));
            byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, gmtPrivateKey);
            System.out.println("[ output ] signature : " + bytesToHexString(signature));
            if(Arrays.equals(signature, gmtSignature)) {
                System.out.println("[ compare result | equal ] sm2Sign result is equal with GM/T 0003.5-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm2Sign result is not equal with GM/T 0003.5-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignSpeed() {
        System.out.println("============= SMJniApi sm2Sign speed test =============");
        try {
            long startTime = System.currentTimeMillis();
            for(int i = 0; i < 10000; i++) {
                byte[] signature = jni.sm2Sign(gmtDigest, gmtRandom, gmtPrivateKey);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (10000.00 / ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm2Sign 10000 times need : " +
                                (float) ((endTime - startTime)/1000.00 )+ "s");
            System.out.println("[ speed ] sm2Sign speed : " + speed + " times/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerityUseStandardData() {
        System.out.println("============= SMJniApi Ssm2Verify use GM/T 0003.5-2012 standard data =============");
        try {
            System.out.println("[ input ] digest : " + Convert.bytesToHexString(gmtDigest));
            System.out.println("[ input ] public key : " + Convert.bytesToHexString(gmtPublicKey));
            System.out.println("[ input ] signature : " + Convert.bytesToHexString(gmtSignature));
            int verifyResult = jni.sm2Verify(gmtDigest, gmtPublicKey, gmtSignature);
            if(verifyResult == 0) {
                System.out.println("[ verify result | succeed ] succeed verifying the SM2 signature");
            } else {
                System.out.println("[ verify result | failed ]");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifySpeed() {
        System.out.println("============= SMJniApi sm2Verify speed test =============");
        try {
            int verifyResult = 0;
            long startTime = System.currentTimeMillis();
            int totalRound = 10000;
            for(int i = 0; i < totalRound; i++) {
                verifyResult = jni.sm2Verify(gmtDigest, gmtPublicKey, gmtSignature);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (totalRound / ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm2Verify " + totalRound + " times need : " +
                                (float)((endTime - startTime)/1000) + "s");
            System.out.println("[ speed ] sm2Verify speed : " + speed + " times/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
}
