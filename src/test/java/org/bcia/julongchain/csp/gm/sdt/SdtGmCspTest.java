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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.RngOpts;
import org.bcia.julongchain.csp.gm.sdt.SM2.*;
import org.bcia.julongchain.csp.gm.sdt.SM3.SM3HashOpts;
import org.bcia.julongchain.csp.gm.sdt.SM4.*;
import org.bcia.julongchain.csp.gm.sdt.util.KeysStore;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * SDTGmCsp 单元测试
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SdtGmCspTest {

    private ICsp csp;

    @Before
    public void setUp() throws Exception {
        System.out.println("before test");
        SdtGmCspFactory factory = new SdtGmCspFactory();
        Assert.assertNotNull(factory);
        IFactoryOpts opts = new SdtGmFactoryOpts();
        csp = factory.getCsp(opts);
        Assert.assertNotNull(csp);
    }

    @After
    public void finalize() {
        System.out.println("finalize...");
    }

    @Test
    public void testKeyGen() {

        System.out.println("============= SdtGmCsp key gen test =============");
        int caseIndex = 1;

        try {
            System.out.println("\n**** case " + caseIndex++ + ": generate SM2 key pair  ****");
            IKey sm2Key = csp.keyGen(new SM2KeyGenOpts());
            System.out.println("[output data] SM2 private key : " + Convert.bytesToHexString(sm2Key.toBytes()));
            System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(sm2Key.getPublicKey().toBytes()));
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": generate SM4 key  ****");
            IKey sm4Key = csp.keyGen(new SM4KeyGenOpts());
            System.out.println("[output data] SM4 key : " + Convert.bytesToHexString(sm4Key.toBytes()));
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyDeriv() {
        System.out.println("TODO...");
    }

    @Test
    public void testKeyImport() {
        System.out.println("============= SdtGmCsp key import test =============");
        int caseIndex = 1;

        try {
            System.out.println("\n**** case " + caseIndex++ + ": import SM2 private key  ****");
            byte[] sm2RawDataPrivateKey = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F, (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                    (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A, (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                    (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            System.out.println("[output data] SM2 private key(raw data) : " + Convert.bytesToHexString(sm2RawDataPrivateKey));
            IKey sm2PrivateKeyEphemeral = csp.keyImport(sm2RawDataPrivateKey, new SM2PrivateKeyImportOpts(true));
            System.out.println("[output data] SM2 private key(from memory) : " + Convert.bytesToHexString(sm2PrivateKeyEphemeral.toBytes()));

            IKey sm2PrivateKeyPermanent = csp.keyImport(sm2RawDataPrivateKey, new SM2PrivateKeyImportOpts(false));
            byte[] sm2PrivateKeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm2PrivateKeyPermanent.ski(), KeysStore.KEY_TYPE_SK);
            System.out.println("[output data] SM2 private key(from file) : " + Convert.bytesToHexString(sm2PrivateKeyDataFromFile));

        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": import SM2 public key  ****");
            byte[] sm2RawDataPublicKey = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31, (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                    (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16, (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                    (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD, (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                    (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3, (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                    (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C, (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                    (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71, (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                    (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB, (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                    (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07, (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
            System.out.println("[output data] SM2 public key(raw data) : " + Convert.bytesToHexString(sm2RawDataPublicKey));
            IKey sm2PublicKeyEphemeral = csp.keyImport(sm2RawDataPublicKey, new SM2PublicKeyImportOpts(true));
            System.out.println("[output data] SM2 public key(from memory) : " + Convert.bytesToHexString(sm2PublicKeyEphemeral.toBytes()));

            IKey sm2PublicKeyPermanent = csp.keyImport(sm2RawDataPublicKey, new SM2PublicKeyImportOpts(false));
            byte[] sm2PublicKeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm2PublicKeyPermanent.ski(), KeysStore.KEY_TYPE_PK);
            System.out.println("[output data] SM2 public key(from file) : " + Convert.bytesToHexString(sm2PublicKeyDataFromFile));

        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": import SM4 key  ****");
            byte[] sm4RawDataKey = {(byte)0x56, (byte)0x15, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x22, (byte)0xF0, (byte)0x31, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            System.out.println("[output data] SM4 key(raw data) : " + Convert.bytesToHexString(sm4RawDataKey));
            IKey sm4KeyEphemeral = csp.keyImport(sm4RawDataKey, new SM4KeyImportOpts(true));
            System.out.println("[output data] SM4 key(from memory) : " + Convert.bytesToHexString(sm4KeyEphemeral.toBytes()));

            IKey sm4KeyPermanent = csp.keyImport(sm4RawDataKey, new SM4KeyImportOpts(false));
            byte[] sm4KeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm4KeyPermanent.ski(), KeysStore.KEY_TYPE_KEY);
            System.out.println("[output data] SM4 key(from file) : " + Convert.bytesToHexString(sm4KeyDataFromFile));

        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetKey() {
        System.out.println("============= SdtGmCsp get key test =============");
        int caseIndex = 1;

        try {
            System.out.println("\n**** case " + caseIndex++ + ": get SM2 private key  ****");
            String skSkiHex = "a7c7fedbfe14e989e9423d51d232b672da098b2a32eb1de842ce50fb62053ab7";
            byte[] skSki = Hex.decode(skSkiHex);
            IKey sm2PrivateKey = csp.getKey(skSki);
            if(null != sm2PrivateKey) {
                System.out.println("[output data] SM2 private key : " + Convert.bytesToHexString(sm2PrivateKey.toBytes()));
            } else {
                System.out.println("[**Error**] get SM2 private key failed, ski = " + skSkiHex);
            }
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": get SM2 public key  ****");
            String pkSkiHex = "3d92bed6d685f38bff5ccd052119e1ecc071c2f3d915245c8d9c5a2cb131a89d";
            byte[] pkSki = Hex.decode(pkSkiHex);
            IKey sm2PublicKey = csp.getKey(pkSki);
            if(null != sm2PublicKey) {
                System.out.println("[output data] SM2 public key : " + Convert.bytesToHexString(sm2PublicKey.toBytes()));
            } else {
                System.out.println("[**Error**] get SM2 public key failed, ski = " + pkSkiHex);
            }
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": get SM4 key  ****");
            String keySkiHex = "4d6f6c6918174d40aafcaa9ad21a28efff7d6b92f3056d73ad30867d8bc2dae0";
            byte[] keySki = Hex.decode(keySkiHex);
            IKey sm4Key = csp.getKey(keySki);
            if(null != sm4Key) {
                System.out.println("[output data] SM4 key : " + Convert.bytesToHexString(sm4Key.toBytes()));
            } else {
                System.out.println("[**Error**] get SM4 key failed, ski = " + keySkiHex);
            }
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHash() {
        System.out.println("============= SdtGmCsp hash test =============");
        int caseIndex = 1;
        int[] testMessageLen = {1, 16, 240, 2048};
        int messageLen = 0;
        for(int index = 0; index < testMessageLen.length; index ++) {
            try {
                messageLen = testMessageLen[index];
                byte[] msg = new byte[messageLen];
                for(int i = 0; i < messageLen; i++) {
                    msg[i] = (byte)(i+1);
                }
                System.out.println("\n**** case " + caseIndex++ + ": message length = "+ messageLen +"  ****");
                byte[] hash = csp.hash(msg, new SM3HashOpts());
                System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
                if(null != hash) {
                    System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));
                } else {
                    System.out.println("[**Error**] compute message hash failed");
                }
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetHash() {

    }

    @Test
    public void testSign() {
        System.out.println("============= SdtGmCsp sign test =============");
        byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F,
                (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A,
                (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69,
                (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF,
                (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
        byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31,
                (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16,
                (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD,
                (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3,
                (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C,
                (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71,
                (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB,
                (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07,
                (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
        byte[] digest = {(byte)0x99, (byte)0x50, (byte)0xE8, (byte)0x14,
                (byte)0x3C, (byte)0x39, (byte)0x33, (byte)0xFA,
                (byte)0xA8, (byte)0xB1, (byte)0xD4, (byte)0x12,
                (byte)0x85, (byte)0xEE, (byte)0xA9, (byte)0x09,
                (byte)0xD2, (byte)0x69, (byte)0x42, (byte)0xA7,
                (byte)0x6C, (byte)0x03, (byte)0x63, (byte)0x49,
                (byte)0x39, (byte)0xB4, (byte)0x22, (byte)0x55,
                (byte)0xC5, (byte)0x8E, (byte)0xD9, (byte)0x84};
        try {
            IKey sm2Key = new SM2Key(new SM2KeyPair(pk, sk));
            byte[] signData = csp.sign(sm2Key, digest, new SM2SignerOpts());
            System.out.println("[input data] sk data : " + Convert.bytesToHexString(sk));
            System.out.println("[input data] digest data : " + Convert.bytesToHexString(digest));
            if(null != signData) {
                System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
            } else {
                System.out.println("[**Error**] compute signature failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVerify() {
        System.out.println("============= SdtGmCsp verify test =============");
        byte[] sk = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F,
                (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
                (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A,
                (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
                (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69,
                (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF,
                (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
        byte[] pk = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31,
                (byte)0x1E, (byte)0x54, (byte)0x21, (byte)0xA1,
                (byte)0x50, (byte)0xDD, (byte)0x7D, (byte)0x16,
                (byte)0x1E, (byte)0x4B, (byte)0xC5, (byte)0xC6,
                (byte)0x72, (byte)0x17, (byte)0x9F, (byte)0xAD,
                (byte)0x18, (byte)0x33, (byte)0xFC, (byte)0x07,
                (byte)0x6B, (byte)0xB0, (byte)0x8F, (byte)0xF3,
                (byte)0x56, (byte)0xF3, (byte)0x50, (byte)0x20,
                (byte)0xCC, (byte)0xEA, (byte)0x49, (byte)0x0C,
                (byte)0xE2, (byte)0x67, (byte)0x75, (byte)0xA5,
                (byte)0x2D, (byte)0xC6, (byte)0xEA, (byte)0x71,
                (byte)0x8C, (byte)0xC1, (byte)0xAA, (byte)0x60,
                (byte)0x0A, (byte)0xED, (byte)0x05, (byte)0xFB,
                (byte)0xF3, (byte)0x5E, (byte)0x08, (byte)0x4A,
                (byte)0x66, (byte)0x32, (byte)0xF6, (byte)0x07,
                (byte)0x2D, (byte)0xA9, (byte)0xAD, (byte)0x13};
        byte[] digest = {(byte)0x99, (byte)0x50, (byte)0xE8, (byte)0x14,
                (byte)0x3C, (byte)0x39, (byte)0x33, (byte)0xFA,
                (byte)0xA8, (byte)0xB1, (byte)0xD4, (byte)0x12,
                (byte)0x85, (byte)0xEE, (byte)0xA9, (byte)0x09,
                (byte)0xD2, (byte)0x69, (byte)0x42, (byte)0xA7,
                (byte)0x6C, (byte)0x03, (byte)0x63, (byte)0x49,
                (byte)0x39, (byte)0xB4, (byte)0x22, (byte)0x55,
                (byte)0xC5, (byte)0x8E, (byte)0xD9, (byte)0x84};

        byte[] signData = {(byte)0x25, (byte)0x9E, (byte)0xE0, (byte)0x26,
                (byte)0x02, (byte)0x21, (byte)0xB6, (byte)0xD5,
                (byte)0x58, (byte)0xF3, (byte)0x47, (byte)0x28,
                (byte)0x5B, (byte)0xD0, (byte)0xB7, (byte)0x9A,
                (byte)0x02, (byte)0x3F, (byte)0xFB, (byte)0x1F,
                (byte)0x8B, (byte)0x45, (byte)0xEE, (byte)0x10,
                (byte)0xC2, (byte)0x0F, (byte)0xFC, (byte)0xE2,
                (byte)0xD4, (byte)0x09, (byte)0x08, (byte)0xE3,
                (byte)0xE0, (byte)0xDC, (byte)0xB1, (byte)0xC6,
                (byte)0xF2, (byte)0x84, (byte)0x19, (byte)0x1A,
                (byte)0xC3, (byte)0xFB, (byte)0xFB, (byte)0x49,
                (byte)0xDF, (byte)0xFA, (byte)0x05, (byte)0xB2,
                (byte)0xD0, (byte)0x27, (byte)0x3F, (byte)0x54,
                (byte)0xE1, (byte)0x89, (byte)0x4B, (byte)0xB9,
                (byte)0x2A, (byte)0x5E, (byte)0x27, (byte)0xC4,
                (byte)0x4B, (byte)0x4B, (byte)0x0C, (byte)0xB0};

        try {
            IKey sm2Key = new SM2Key(new SM2KeyPair(pk, sk));
            boolean result = csp.verify(sm2Key, signData, digest, new SM2SignerOpts());
            System.out.println("[input data] pk data : " + Convert.bytesToHexString(pk));
            System.out.println("[input data] digest data : " + Convert.bytesToHexString(digest));
            System.out.println("[input data] signature data : " + Convert.bytesToHexString(signData));
            if(result) {
                System.out.println("[output data] verify signature success");
            } else {
                System.out.println("[output data] verify signature failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEncrypt() {
        System.out.println("============= SdtGmCsp encrypt test =============");
        int caseIndex = 1;

        int[] testPlainDataLen = {1, 16, 240, 2048};
        int plainDataLen = 0;
        byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98,
                (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
        System.out.println("[input data] sm4 key data : " + Convert.bytesToHexString(key));
        for(int index = 0; index < testPlainDataLen.length; index ++) {
            try {
                plainDataLen = testPlainDataLen[index];
                byte[] plainData = new byte[plainDataLen];
                for(int i = 0; i < plainDataLen; i++) {
                    plainData[i] = (byte)(i+1);
                }
                System.out.println("\n**** case " + caseIndex++ + ": plain data length = "+ plainDataLen +"  ****");
                IKey sm4Key = new SM4Key(key);
                byte[] cipherData = csp.encrypt(sm4Key, plainData, new SM4EncrypterOpts());
                System.out.println("[input data] plain data : " + Convert.bytesToHexString(plainData));
                if(null != cipherData) {
                    System.out.println("[output data] cipher data : " + Convert.bytesToHexString(cipherData));
                    System.out.println("[output data] cipher data length : " + cipherData.length);
                } else {
                    System.out.println("[**Error**] data encrypt failed");
                }
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDecrypt() {
        System.out.println("============= SdtGmCsp decrypt test =============");
        int caseIndex = 1;
        byte[] key = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0xfe, (byte)0xdc, (byte)0xba, (byte)0x98,
                (byte)0x76, (byte)0x54, (byte)0x32, (byte)0x10 };
        System.out.println("[input data] sm4 key data : " + Convert.bytesToHexString(key));
        String[] cipherDataHexList = {"62a1af3b5d325afecec9a848f97d3be9",
                "d052dcc19d0eeb7a565720640d8b7e29002a8a4efa863ccad024ac0300bb40d2",
                "d052dcc19d0eeb7a565720640d8b7e29bd7b374d8df254e7b90c3bf03c814f7e449" +
                        "734911a4402599e9a30cbab1192bcf017c1c6d99648fe6ed93d8da0f3b54" +
                        "f6daaeb980f0c2a1a029477ab1abc41035102b910daef2eb209bbe716d5fc" +
                        "d5a505609154c7aa2465c66ad43d0875a70ead67d4f74f96940deae01b0fa" +
                        "218d3e1d19a9ca82b68172ef05ec595b44a080402a7d85ceb3756dfcd08185" +
                        "c1e617d170454952debb6fd4a15a63fe62b89dcee6d10634858c59f0890cb" +
                        "30244a0d1b0f9dcc630d2990e8b1df4af0a155e9fb3d731a0014ac89f4648" +
                        "cb4e827ac0d6f8a9682ba0e7ae0dd6fad77832923ce41de002a8a4efa863c" +
                        "cad024ac0300bb40d2",
                "d052dcc19d0eeb7a565720640d8b7e29bd7b374d8df254e7b90c3bf03c814f7e449734" +
                        "911a4402599e9a30cbab1192bcf017c1c6d99648fe6ed93d8da0f3b54f6daa" +
                        "eb980f0c2a1a029477ab1abc41035102b910daef2eb209bbe716d5fcd5a505" +
                        "609154c7aa2465c66ad43d0875a70ead67d4f74f96940deae01b0fa218d3e1" +
                        "d19a9ca82b68172ef05ec595b44a080402a7d85ceb3756dfcd08185c1e617d" +
                        "170454952debb6fd4a15a63fe62b89dcee6d10634858c59f0890cb30244a0d" +
                        "1b0f9dcc630d2990e8b1df4af0a155e9fb3d731a0014ac89f4648cb4e827ac" +
                        "0d6f8a9682ba0e7ae0dd6fad77832923ce41de5f1a1a8291764157874fcbe5" +
                        "96f211fcd052dcc19d0eeb7a565720640d8b7e29bd7b374d8df254e7b90c3b" +
                        "f03c814f7e449734911a4402599e9a30cbab1192bcf017c1c6d99648fe6ed9" +
                        "3d8da0f3b54f6daaeb980f0c2a1a029477ab1abc41035102b910daef2eb209" +
                        "bbe716d5fcd5a505609154c7aa2465c66ad43d0875a70ead67d4f74f96940d" +
                        "eae01b0fa218d3e1d19a9ca82b68172ef05ec595b44a080402a7d85ceb3756" +
                        "dfcd08185c1e617d170454952debb6fd4a15a63fe62b89dcee6d10634858c5" +
                        "9f0890cb30244a0d1b0f9dcc630d2990e8b1df4af0a155e9fb3d731a0014ac" +
                        "89f4648cb4e827ac0d6f8a9682ba0e7ae0dd6fad77832923ce41de5f1a1a82" +
                        "91764157874fcbe596f211fcd052dcc19d0eeb7a565720640d8b7e29bd7b37" +
                        "4d8df254e7b90c3bf03c814f7e449734911a4402599e9a30cbab1192bcf017" +
                        "c1c6d99648fe6ed93d8da0f3b54f6daaeb980f0c2a1a029477ab1abc410351" +
                        "02b910daef2eb209bbe716d5fcd5a505609154c7aa2465c66ad43d0875a70e" +
                        "ad67d4f74f96940deae01b0fa218d3e1d19a9ca82b68172ef05ec595b44a08" +
                        "0402a7d85ceb3756dfcd08185c1e617d170454952debb6fd4a15a63fe62b89" +
                        "dcee6d10634858c59f0890cb30244a0d1b0f9dcc630d2990e8b1df4af0a155" +
                        "e9fb3d731a0014ac89f4648cb4e827ac0d6f8a9682ba0e7ae0dd6fad778329" +
                        "23ce41de5f1a1a8291764157874fcbe596f211fcd052dcc19d0eeb7a565720" +
                        "640d8b7e29bd7b374d8df254e7b90c3bf03c814f7e449734911a4402599e9a" +
                        "30cbab1192bcf017c1c6d99648fe6ed93d8da0f3b54f6daaeb980f0c2a1a02" +
                        "9477ab1abc41035102b910daef2eb209bbe716d5fcd5a505609154c7aa2465" +
                        "c66ad43d0875a70ead67d4f74f96940deae01b0fa218d3e1d19a9ca82b6817" +
                        "2ef05ec595b44a080402a7d85ceb3756dfcd08185c1e617d170454952debb6" +
                        "fd4a15a63fe62b89dcee6d10634858c59f0890cb30244a0d1b0f9dcc630d29" +
                        "90e8b1df4af0a155e9fb3d731a0014ac89f4648cb4e827ac0d6f8a9682ba0e" +
                        "7ae0dd6fad77832923ce41de5f1a1a8291764157874fcbe596f211fcd052dc" +
                        "c19d0eeb7a565720640d8b7e29bd7b374d8df254e7b90c3bf03c814f7e4497" +
                        "34911a4402599e9a30cbab1192bcf017c1c6d99648fe6ed93d8da0f3b54f6d" +
                        "aaeb980f0c2a1a029477ab1abc41035102b910daef2eb209bbe716d5fcd5a5" +
                        "05609154c7aa2465c66ad43d0875a70ead67d4f74f96940deae01b0fa218d3" +
                        "e1d19a9ca82b68172ef05ec595b44a080402a7d85ceb3756dfcd08185c1e61" +
                        "7d170454952debb6fd4a15a63fe62b89dcee6d10634858c59f0890cb30244a" +
                        "0d1b0f9dcc630d2990e8b1df4af0a155e9fb3d731a0014ac89f4648cb4e827" +
                        "ac0d6f8a9682ba0e7ae0dd6fad77832923ce41de5f1a1a8291764157874fcb" +
                        "e596f211fcd052dcc19d0eeb7a565720640d8b7e29bd7b374d8df254e7b90c" +
                        "3bf03c814f7e449734911a4402599e9a30cbab1192bcf017c1c6d99648fe6e" +
                        "d93d8da0f3b54f6daaeb980f0c2a1a029477ab1abc41035102b910daef2eb2" +
                        "09bbe716d5fcd5a505609154c7aa2465c66ad43d0875a70ead67d4f74f9694" +
                        "0deae01b0fa218d3e1d19a9ca82b68172ef05ec595b44a080402a7d85ceb37" +
                        "56dfcd08185c1e617d170454952debb6fd4a15a63fe62b89dcee6d10634858" +
                        "c59f0890cb30244a0d1b0f9dcc630d2990e8b1df4af0a155e9fb3d731a0014" +
                        "ac89f4648cb4e827ac0d6f8a9682ba0e7ae0dd6fad77832923ce41de5f1a1a" +
                        "8291764157874fcbe596f211fcd052dcc19d0eeb7a565720640d8b7e29bd7b" +
                        "374d8df254e7b90c3bf03c814f7e449734911a4402599e9a30cbab1192bcf0" +
                        "17c1c6d99648fe6ed93d8da0f3b54f6daaeb980f0c2a1a029477ab1abc4103" +
                        "5102b910daef2eb209bbe716d5fcd5a505609154c7aa2465c66ad43d0875a7" +
                        "0ead67d4f74f96940deae01b0fa218d3e1d19a9ca82b68172ef05ec595b44a" +
                        "080402a7d85ceb3756dfcd08185c1e617d170454952debb6fd4a15a63fe62b" +
                        "89dcee6d10634858c59f0890cb30244a0d1b0f9dcc630d2990e8b1df4af0a1" +
                        "55e9fb3d731a0014ac89f4648cb4e827ac0d6f8a9682ba0e7ae0dd6fad7783" +
                        "2923ce41de5f1a1a8291764157874fcbe596f211fcd052dcc19d0eeb7a5657" +
                        "20640d8b7e29bd7b374d8df254e7b90c3bf03c814f7e449734911a4402599e" +
                        "9a30cbab1192bcf017c1c6d99648fe6ed93d8da0f3b54f6daaeb980f0c2a1a" +
                        "029477ab1abc41035102b910daef2eb209bbe716d5fcd5a505609154c7aa24" +
                        "65c66ad43d0875a70ead67d4f74f96940deae01b0fa218d3e1d19a9ca82b68" +
                        "172ef05ec595b44a080402a7d85ceb3756dfcd08185c1e617d170454952deb" +
                        "b6fd4a15a63fe62b89dcee6d10634858c59f0890cb30244a0d1b0f9dcc630d" +
                        "2990e8b1df4af0a155e9fb3d731a0014ac89f4648cb4e827ac0d6f8a9682ba" +
                        "0e7ae0dd6fad77832923ce41de5f1a1a8291764157874fcbe596f211fc002a" +
                        "8a4efa863ccad024ac0300bb40d2"};
        for(int index = 0; index < cipherDataHexList.length; index ++) {
            try {
                String cipherDataStr = cipherDataHexList[index];
                byte[] cipherData = Hex.decode(cipherDataStr);
                System.out.println("\n**** case " + caseIndex++ + ": cipher data length = "+ cipherData.length +"  ****");
                IKey sm4Key = new SM4Key(key);
                byte[] plainData = csp.decrypt(sm4Key, cipherData, new SM4DecrypterOpts());
                System.out.println("[input data] cipher data : " + Convert.bytesToHexString(cipherData));
                if(null != cipherData) {
                    System.out.println("[output data] plain data : " + Convert.bytesToHexString(plainData));
                } else {
                    System.out.println("[**Error**] data decrypt failed");
                }
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRng() {
        System.out.println("============= SdtGmCsp rng test =============");
        int caseIndex = 1;
        int[] randomLen = {1, 16, 240, 1024};
        for(int index = 0; index < randomLen.length; index ++) {
            try {
                int len = randomLen[index];
                System.out.println("\n**** case " + caseIndex++ + ": generate random length = "+ len +"  ****");
                byte[] random = csp.rng(len, new RngOpts());
                if(null != random) {
                    System.out.println("[output data] random data : " + Convert.bytesToHexString(random));
                } else {
                    System.out.println("[**Error**] generate random data failed");
                }
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        }
    }
}
