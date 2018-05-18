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

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.util.Convert;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2KeyGenOpts;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2PrivateKeyImportOpts;
import org.bcia.javachain.csp.gm.sdt.SM2.SM2PublicKeyImportOpts;
import org.bcia.javachain.csp.gm.sdt.SM4.SM4KeyGenOpts;
import org.bcia.javachain.csp.gm.sdt.SM4.SM4KeyImportOpts;
import org.bcia.javachain.csp.gm.sdt.util.KeysStore;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IKey;
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
public class GmCspTest {

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
            System.out.println("\n  **** case " + caseIndex++ + ": generate SM2 key pair  ****");
            IKey sm2Key = csp.keyGen(new SM2KeyGenOpts());
            System.out.println("      SM2 private key : " + Convert.bytesToHexString(sm2Key.toBytes()));
            System.out.println("      SM2 public key : " + Convert.bytesToHexString(sm2Key.getPublicKey().toBytes()));
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n  **** case " + caseIndex++ + ": generate SM4 key  ****");
            IKey sm4Key = csp.keyGen(new SM4KeyGenOpts());
            System.out.println("      SM4 key : " + Convert.bytesToHexString(sm4Key.toBytes()));
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
            System.out.println("     SM2 private key(raw data) : " + Convert.bytesToHexString(sm2RawDataPrivateKey));
            IKey sm2PrivateKeyEphemeral = csp.keyImport(sm2RawDataPrivateKey, new SM2PrivateKeyImportOpts(true));
            System.out.println("     SM2 private key(from memory) : " + Convert.bytesToHexString(sm2PrivateKeyEphemeral.toBytes()));

            IKey sm2PrivateKeyPermanent = csp.keyImport(sm2RawDataPrivateKey, new SM2PrivateKeyImportOpts(false));
            byte[] sm2PrivateKeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm2PrivateKeyPermanent.ski(), KeysStore.KEY_TYPE_SK);
            System.out.println("     SM2 private key(from file) : " + Convert.bytesToHexString(sm2PrivateKeyDataFromFile));

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
            System.out.println("     SM2 public key(raw data) : " + Convert.bytesToHexString(sm2RawDataPublicKey));
            IKey sm2PublicKeyEphemeral = csp.keyImport(sm2RawDataPublicKey, new SM2PublicKeyImportOpts(true));
            System.out.println("     SM2 public key(from memory) : " + Convert.bytesToHexString(sm2PublicKeyEphemeral.toBytes()));

            IKey sm2PublicKeyPermanent = csp.keyImport(sm2RawDataPublicKey, new SM2PublicKeyImportOpts(false));
            byte[] sm2PublicKeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm2PublicKeyPermanent.ski(), KeysStore.KEY_TYPE_PK);
            System.out.println("     SM2 public key(from file) : " + Convert.bytesToHexString(sm2PublicKeyDataFromFile));

        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": import SM4 key  ****");
            byte[] sm4RawDataKey = {(byte)0x56, (byte)0x15, (byte)0x93, (byte)0x69, (byte)0x28, (byte)0x60, (byte)0xB5, (byte)0x1A,
                    (byte)0x22, (byte)0xF0, (byte)0x31, (byte)0xEF, (byte)0x4D, (byte)0xF7, (byte)0xC5, (byte)0xB8};
            System.out.println("     SM4 key(raw data) : " + Convert.bytesToHexString(sm4RawDataKey));
            IKey sm4KeyEphemeral = csp.keyImport(sm4RawDataKey, new SM4KeyImportOpts(true));
            System.out.println("     SM4 key(from memory) : " + Convert.bytesToHexString(sm4KeyEphemeral.toBytes()));

            IKey sm4KeyPermanent = csp.keyImport(sm4RawDataKey, new SM4KeyImportOpts(false));
            byte[] sm4KeyDataFromFile = KeysStore.loadKey("/opt/msp/keystore/sdt/", null, sm4KeyPermanent.ski(), KeysStore.KEY_TYPE_KEY);
            System.out.println("     SM4 key(from file) : " + Convert.bytesToHexString(sm4KeyDataFromFile));

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
                System.out.println("     SM2 private key : " + Convert.bytesToHexString(sm2PrivateKey.toBytes()));
            } else {
                System.out.println("     ****get SM2 private key failed, ski = " + skSkiHex);
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
                System.out.println("     SM2 public key : " + Convert.bytesToHexString(sm2PublicKey.toBytes()));
            } else {
                System.out.println("     ****get SM2 public key failed, ski = " + pkSkiHex);
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
                System.out.println("     SM4 key : " + Convert.bytesToHexString(sm4Key.toBytes()));
            } else {
                System.out.println("     ****get SM4 key failed, ski = " + keySkiHex);
            }
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHash() {

    }

    @Test
    public void sign() {
        System.out.println("============= SdtGmCsp sign test =============");
        int caseIndex = 1;
    }

    @Test
    public void verify() {
        System.out.println("============= SdtGmCsp verify test =============");
        int caseIndex = 1;
    }

    @Test
    public void encrypt() {
        System.out.println("============= SdtGmCsp encrypt test =============");
        int caseIndex = 1;
    }

    @Test
    public void decrypt() {
        System.out.println("============= SdtGmCsp decrypt test =============");
        int caseIndex = 1;
    }

    @Test
    public void rng() {
        System.out.println("============= SdtGmCsp rng test =============");
        int caseIndex = 1;
    }
}
