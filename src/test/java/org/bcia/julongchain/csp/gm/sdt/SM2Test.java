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
import org.bcia.julongchain.csp.gm.sdt.sm2.SM2;
import org.bcia.julongchain.csp.gm.sdt.sm2.SM2KeyPair;
import org.bcia.julongchain.csp.gm.sdt.sm3.SM3;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.bcia.julongchain.common.util.Convert.bytesToHexString;


/**
 * SM2 算法单元测试
 *
 * @author tengxiumin
 * @date 5/16/18
 * @company SDT
 */

public class SM2Test {

    private SM2 sm2 = new SM2();

    private byte[] testDigest = {(byte)0xF0, (byte)0xB4, (byte)0x3E, (byte)0x94,
            (byte)0xBA, (byte)0x45, (byte)0xAC, (byte)0xCA,
            (byte)0xAC, (byte)0xE6, (byte)0x92, (byte)0xED,
            (byte)0x53, (byte)0x43, (byte)0x82, (byte)0xEB,
            (byte)0x17, (byte)0xE6, (byte)0xAB, (byte)0x5A,
            (byte)0x19, (byte)0xCE, (byte)0x7B, (byte)0x31,
            (byte)0xF4, (byte)0x48, (byte)0x6F, (byte)0xDF,
            (byte)0xC0, (byte)0xD2, (byte)0x86, (byte)0x40};

    private byte[] testPrivateKey = {(byte)0x39, (byte)0x45, (byte)0x20, (byte)0x8F,
            (byte)0x7B, (byte)0x21, (byte)0x44, (byte)0xB1,
            (byte)0x3F, (byte)0x36, (byte)0xE3, (byte)0x8A,
            (byte)0xC6, (byte)0xD3, (byte)0x9F, (byte)0x95,
            (byte)0x88, (byte)0x93, (byte)0x93, (byte)0x69,
            (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
            (byte)0x42, (byte)0xFB, (byte)0x81, (byte)0xEF,
            (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};

    private byte[] testPublicKey = {(byte)0x09, (byte)0xF9, (byte)0xDF, (byte)0x31,
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

    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void testGenerateKeyPair() {
        System.out.println("===== SM2 generateKeyPair test ===== ");
        try {
            SM2KeyPair KeyPair = sm2.generateKeyPair();

            System.out.println("[ output ] SM2 private key : " + bytesToHexString(KeyPair.getPrivateKey()));
            System.out.println("[ output ] SM2 public key : " + bytesToHexString(KeyPair.getPublicKey()));

        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSignAndVerify() {
        System.out.println("============ SM2 sign and verify test ============");
        try {
            System.out.println("\n**** case 1 :sign  ****");
            System.out.println("[ input ] digest : " + bytesToHexString(testDigest));
            System.out.println("[ input ] private key : " + bytesToHexString(testPrivateKey));
            byte[] signature = sm2.sign(testDigest, testPrivateKey);
            System.out.println("[ output ] signature : " + bytesToHexString(signature));

            System.out.println("\n**** case 2 : verify  ****");
            System.out.println("[ input ] digest : " + bytesToHexString(testDigest));
            System.out.println("[ input ] public key : " + bytesToHexString(testPublicKey));
            System.out.println("[ input ] signature : " + bytesToHexString(signature));
            int verifyResult = sm2.verify(testDigest, testPublicKey, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSignInvalidParameters() {

        System.out.println("============= SM2 sign invalid parameters test =============");

        byte[] signature = null;

        byte[] digest0 = new byte[0];
        byte[] digest31 = new byte[31];
        System.arraycopy(testDigest, 0, digest31, 0, 31);
        byte[] digest33 = new byte[33];
        System.arraycopy(testDigest, 0, digest33, 0, 32);
        digest33[32] = (byte)0x89;

        byte[] privateKey0 = new byte[0];
        byte[] privateKey31 = new byte[31];
        System.arraycopy(testPrivateKey, 0, privateKey31, 0, 31);
        byte[] privateKey33 = new byte[33];
        System.arraycopy(testPrivateKey, 0, privateKey33, 0, 32);
        privateKey33[32] = (byte)0x89;

        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest is null  ****");
            signature = sm2.sign(null, testPrivateKey);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 0  ****");
            signature = sm2.sign(digest0, testPrivateKey);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 31  ****");
            signature = sm2.sign(digest31, testPrivateKey);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 33  ****");
            signature = sm2.sign(digest33, testPrivateKey);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key is null  ****");
            signature = sm2.sign(testDigest, null);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 0  ****");
            signature = sm2.sign(testDigest, privateKey0);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 31  ****");
            signature = sm2.sign(testDigest, privateKey31);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": private key length is 33  ****");
            signature = sm2.sign(testDigest, privateKey33);
            System.out.println("[ output ] signature : " + Convert.bytesToHexString(signature));
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM2VerifyInvalidParameters() {

        System.out.println("============= SM2 verify invalid parameters test =============");

        byte[] digest0 = new byte[0];
        byte[] digest31 = new byte[31];
        System.arraycopy(testDigest, 0, digest31, 0, 31);
        byte[] digest33 = new byte[33];
        System.arraycopy(testDigest, 0, digest33, 0, 32);
        digest33[32] = (byte)0x89;

        byte[] publicKey0 = new byte[0];
        byte[] publicKey63 = new byte[63];
        System.arraycopy(testPublicKey, 0, publicKey63, 0, 63);
        byte[] publicKey65 = new byte[65];
        System.arraycopy(testPublicKey, 0, publicKey65, 0, 64);
        publicKey65[64] = (byte)0x89;

        byte[] signature = null;
        int verifyResult = 1;

        try {
            signature = sm2.sign(testDigest, testPrivateKey);
        } catch (Exception e) {
            System.out.println("[## exception ##] failed signing the digest");
            return;
        }

        byte[] signature0 = new byte[0];
        byte[] signature63 = new byte[63];
        System.arraycopy(signature, 0, signature63, 0, 63);
        byte[] signature65 = new byte[65];
        System.arraycopy(signature, 0, signature65, 0, 64);
        signature65[64] = (byte)0x89;

        int caseIndex = 1;

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest is null  ****");
            verifyResult = sm2.verify(null, testPublicKey, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 0  ****");
            verifyResult = sm2.verify(digest0, testPublicKey, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 31  ****");
            verifyResult = sm2.verify(digest31, testPublicKey, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": digest length is 33  ****");
            verifyResult = sm2.verify(digest33, testPublicKey, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key is null  ****");
            verifyResult = sm2.verify(testDigest, null, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 0  ****");
            verifyResult = sm2.verify(testDigest, publicKey0, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 63  ****");
            verifyResult = sm2.verify(testDigest, publicKey63, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": public key length is 65  ****");
            verifyResult = sm2.verify(testDigest, publicKey65, signature);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature is null  ****");
            verifyResult = sm2.verify(testDigest, testPublicKey, null);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 0  ****");
            verifyResult = sm2.verify(testDigest, testPublicKey, signature0);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 63  ****");
            verifyResult = sm2.verify(testDigest, testPublicKey, signature63);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": signature length is 65  ****");
            verifyResult = sm2.verify(testDigest, testPublicKey, signature65);
            System.out.println("[ output ] SM2 verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
}

