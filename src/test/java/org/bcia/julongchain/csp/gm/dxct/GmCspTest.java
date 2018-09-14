/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.csp.gm.dxct;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.*;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3HashOpts;
import org.bcia.julongchain.csp.gm.dxct.sm4.*;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.security.pkcs.PKCS8Key;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import static org.bcia.julongchain.csp.factory.CspManager.getDefaultCsp;

/**
 * @author zhanglin, zhangmingyang
 * @date 2018/01/25
 * @company Dingxuan
 */

public class GmCspTest {
    private ICsp csp;

    @Before
    public void setUp() {
        System.out.println("before test");
        System.out.println("set up...");
        GmCspFactory factory = new GmCspFactory();
        Assert.assertNotNull(factory);
        IFactoryOpts opts = new GmFactoryOpts();
        csp = factory.getCsp(opts);
        Assert.assertNotNull(csp);
    }

    @After
    public void finalize() {
        System.out.println("finalize...");
    }

    /**
     * 密钥生成测试
     *
     * @throws CspException
     */
    @Test
    public void keyGenTest() throws CspException {
        System.out.println("test keyGen...");
        try {
            IKey sm2key = csp.keyGen(new SM2KeyGenOpts());
            IKey sm4key = csp.keyGen(new SM4KeyGenOpts());
            if (sm2key instanceof SM2Key) {
                System.out.println("generate the key's type is SM2");
                System.out.println("SM2 publicKey:" + Hex.toHexString(sm2key.getPublicKey().toBytes()));
                System.out.println("SM2 privateKey:" + Hex.toHexString(sm2key.toBytes()));
            }
            if (sm4key instanceof SM4Key) {
                System.out.println("generate the key's type is SM4");
                System.out.println("SM4 Key:" + Hex.toHexString(sm4key.toBytes()));
            }
        } catch (CspException e) {
            throw new CspException(e);
        }
    }

    /**
     * 密钥导入测试
     *
     * @throws CspException
     */
    @Test
    public void keyImportTest() throws CspException {
        byte[] sm2PK = Hex.decode("048708c51d06cd3e2c183499812aec48825ef56039bbfdc2c7023cefbc304d6c17b4746403d0254b9a33472002a84432b77ca0972c8fc56d97dd600293c35e0293");
        byte[] sm2SK = Hex.decode("612c7ab32011048529173c1186110a1dd0de433af0eb70ceef84f10aa44e16de");
        byte[] sm4Key = Hex.decode("0123456789abcdeffedcba9876543210");
        IKey sm2PrivateKey = csp.keyImport(sm2SK, new SM2PrivateKeyImportOpts(true));
        IKey sm2PublicKey = csp.keyImport(sm2PK, new SM2PrivateKeyImportOpts(true));
        IKey sm4Sk = csp.keyImport(sm4Key, new SM4KeyImportOpts(true));
        if (sm2PrivateKey instanceof SM2KeyImport) {
            System.out.println("The key type is SM2KeyImport");
        }
        if (sm2PublicKey instanceof SM2KeyImport) {
            System.out.println("The key type is SM2KeyImport");
        }
        if (sm4Sk instanceof SM4KeyImport) {
            System.out.println("The key type is SM4KeyImport");
        }
    }

    /**
     * ＧＭ/T 0004-2012 ＜ＳＭ３密码杂凑算法＞附录Ａ　示例１
     */
    @Test
    public void hashUnitTest1() {
        try {
            String message = "abc";
            byte[] msg = message.getBytes("ASCII");
            String encodedHexMsg = Hex.toHexString(msg);
            System.out.println("To Hash Message:" + encodedHexMsg);
            String expectedResult = "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
            byte[] expectedHashMsg = Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = csp.hash(msg, hashOpts);
            String encodedHexDigests = Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests, expectedHashMsg);
            System.out.println("Hash Unit Test 1 passed!");
        } catch (CspException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * ＧＭ/T 0004-2012 ＜ＳＭ３密码杂凑算法＞附录Ａ　示例２
     *
     * @throws CspException
     */
    @Test
    public void hashUnitTest2() throws CspException {
        try {
            String message = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd";
            byte[] msg = message.getBytes("ASCII");
            String encodedHexMsg = Hex.toHexString(msg);
            System.out.println("To Hash Message:" + encodedHexMsg);
            String expectedResult = "debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";
            byte[] expectedHashMsg = Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = csp.hash(msg, hashOpts);
            String encodedHexDigests = Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests, expectedHashMsg);
            System.out.println("Hash Unit Test 2 passed!");
        } catch (UnsupportedEncodingException e) {
            throw new CspException(e);
        }
    }

    /**
     * 根据第三方软件进行对比测试
     */
    @Test
    public void hashUnitTestByTester() throws CspException {
        try {
            String message = "4865243A4EF5FFA94C";
            byte[] msg = Hex.decode(message);
            System.out.println("To Hash Message:" + message);
            String expectedResult = "CDB84682A5C8036E692156E635EED508A2B5D41D1AC5A467432C620307DE8184";
            byte[] expectedHashMsg = Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = csp.hash(msg, hashOpts);
            String encodedHexDigests = Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests, expectedHashMsg);
            System.out.println("Hash Unit By Tester passed!");
        } catch (CspException e) {
            e.printStackTrace();
        }
    }

    /**
     * 签名测试
     *
     * @throws CspException
     */
    @Test
    public void signAndVerfiyTest() throws CspException {
        byte[] prik = Hex.decode("44c5ff2006af4b4d3e97c721be0e446c56939bb7d02debc16db7f535446850fe");
        byte[] pubK = Hex.decode("047e371a5c8a01fca82820e8fa6d5ba8faee4cae4ee3ef65160c1ebdf5a7b0bbf2f0670e3496e8344df3065e549fdad924e1cf9c96e8e6e62b925c046bac25ea43");
        IKey sk = new SM2KeyImport(prik, null);
        byte[] data = Hex.decode("f12bcfd72e000c7e8a3499821694b208d745f72172f173a2e595207bf66d48f9");
        byte[] signValue = csp.sign(sk, data, new SM2SignerOpts());
        IKey pk = new SM2KeyImport(null, pubK);
        System.out.println(csp.verify(pk, signValue, data, new SM2SignerOpts()));
    }


    @Test
    public void encryptAndDecryptTest() throws CspException {
        byte[] testData = Hex.decode("01234567454545");
        IKey sm4Key = csp.keyGen(new SM4KeyGenOpts());
        byte[] encryptData = csp.encrypt(sm4Key, testData, new SM4EncrypterOpts());
        byte[] plainText = csp.decrypt(sm4Key, encryptData, new SM4DecrypterOpts());
        Assert.assertArrayEquals(testData, plainText);
        System.out.println("plainText:" + Hex.toHexString(plainText));
    }

    @Test
    public void rngTest() throws CspException {
        for (int i = 0; i < 100; i++) {
            long t1 = System.currentTimeMillis();
            byte[] secureSeed = csp.rng(24, null);
            long t2 = System.currentTimeMillis();
            System.out.println(String.format("随机数长度：%s", secureSeed.length));
            System.out.println(Hex.toHexString(secureSeed));
            System.out.println(String.format("生成随机数消耗时间%s ms", (t2 - t1)));
        }

    }
}