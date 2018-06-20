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
import org.bcia.julongchain.csp.gm.sdt.SM2.SM2;
import org.bcia.julongchain.csp.gm.sdt.SM2.SM2KeyPair;
import org.bcia.julongchain.csp.gm.sdt.SM3.SM3;
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
    private SM3 sm3 = new SM3();

    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void testSM2KeyGen() {
        System.out.println("===== SM2KeyGen test ===== ");
        try {
            SM2KeyPair KeyPair = sm2.generateKeyPair();

            System.out.println("[output data] SM2 private key : " + bytesToHexString(KeyPair.getPrivateKey()));
            System.out.println("[output data] SM2 public key : " + bytesToHexString(KeyPair.getPublicKey()));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Test
    public void testSM2SignVerify() {
        System.out.println("============ SM2 sign and verify test ============");
        try {

            byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A, (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                    (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B, (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};
            SM2KeyPair sm2KeyPair = sm2.generateKeyPair();
            byte[] hash = sm3.hash(msg);
            byte[] sign = sm2.sign(hash, sm2KeyPair.getPrivateKey());

            System.out.println("[output data] signature data : " + bytesToHexString(sign));
            int verifyResult = sm2.verify(hash, sm2KeyPair.getPublicKey(), sign);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM2SignVerifyInvalidParameters() {
        byte[] msg = {(byte) 0xF1, (byte) 0x5D, (byte) 0x12, (byte) 0x7A, (byte) 0x02, (byte) 0xBC, (byte) 0x65, (byte) 0x89,
                (byte) 0x60, (byte) 0xA0, (byte) 0x71, (byte) 0x6B, (byte) 0x3F, (byte) 0x8E, (byte) 0x3C, (byte) 0xA1};

        SM2KeyPair keyPair = null;
        byte[] hash = null;
        byte[] signData = null;
        int verifyResult = 1;

        try {
            keyPair= sm2.generateKeyPair();
            hash = sm3.hash(msg);
        } catch (Exception e) {
            System.out.println("generate SM2 key pair and hash data error.");
            return;
        }


        byte[] hash0 = new byte[0];
        byte[] hash31 = new byte[31];
        System.arraycopy(hash, 0, hash31, 0, 31);
        byte[] hash33 = new byte[33];
        System.arraycopy(hash, 0, hash33, 0, 32);
        hash33[32] = (byte)0x89;

        byte[] sk0 = new byte[0];
        byte[] sk31 = new byte[31];
        System.arraycopy(keyPair.getPrivateKey(), 0, sk31, 0, 31);
        byte[] sk33 = new byte[33];
        System.arraycopy(hash, 0, sk33, 0, 32);
        sk33[32] = (byte)0x89;

        byte[] pk0 = new byte[0];
        byte[] pk63 = new byte[63];
        System.arraycopy(keyPair.getPublicKey(), 0, pk63, 0, 63);
        byte[] pk65 = new byte[65];
        System.arraycopy(keyPair.getPublicKey(), 0, pk65, 0, 64);
        pk65[64] = (byte)0x89;

        try {
            signData = sm2.sign(hash, keyPair.getPrivateKey());
        } catch (Exception e) {
            System.out.println("sign data error.");
            return;
        }

        byte[] signData0 = new byte[0];
        byte[] signData63 = new byte[63];
        System.arraycopy(signData, 0, signData63, 0, 63);
        byte[] signData65 = new byte[65];
        System.arraycopy(signData, 0, signData65, 0, 64);
        signData65[64] = (byte)0x89;

        int caseIndex = 1;
        /*****************  异常用例集  **************/
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign hash is null");
            signData = sm2.sign(null, keyPair.getPrivateKey());
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign hash length is 0");
            signData = sm2.sign(hash0, keyPair.getPrivateKey());
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign hash length is 31");
            signData = sm2.sign(hash31, keyPair.getPrivateKey());
            System.out.println("[output data] signature data: " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign hash length is 33");
            signData = sm2.sign(hash33, keyPair.getPrivateKey());
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign sk is null");
            signData = sm2.sign(hash, null);
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign sk length is 0");
            signData = sm2.sign(hash, sk0);
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign sk length is 31");
            signData = sm2.sign(hash, sk31);
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 sign sk length is 33");
            signData = sm2.sign(hash, sk33);
            System.out.println("[output data] signature data : " + Convert.bytesToHexString(signData));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            signData = sm2.sign(hash, keyPair.getPrivateKey());
        } catch (Exception e) {
            System.out.println("sign data error.");
            return;
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify hash is null");
            verifyResult = sm2.verify(null, keyPair.getPublicKey(), signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify hash is 0");
            verifyResult = sm2.verify(hash0, keyPair.getPublicKey(), signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify hash is 31");
            verifyResult = sm2.verify(hash31, keyPair.getPublicKey(), signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify hash is 33");
            verifyResult = sm2.verify(hash33, keyPair.getPublicKey(), signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify pk is null");
            verifyResult = sm2.verify(hash, null, signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify pk is 0");
            verifyResult = sm2.verify(hash, pk0, signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify pk is 63");
            verifyResult = sm2.verify(hash, pk63, signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify pk is 65");
            verifyResult = sm2.verify(hash, pk65, signData);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify signData is null");
            verifyResult = sm2.verify(hash, keyPair.getPublicKey(), null);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify signData is 0");
            verifyResult = sm2.verify(hash, keyPair.getPublicKey(), signData0);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify signData is 63");
            verifyResult = sm2.verify(hash, keyPair.getPublicKey(), signData63);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM2 verify signData is 65");
            verifyResult = sm2.verify(hash, keyPair.getPublicKey(), signData65);
            System.out.println("[output data] signature verify return : " + verifyResult);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

