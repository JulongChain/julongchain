package org.bcia.julongchain.csp.gm.dxct;

import org.apache.commons.lang.RandomStringUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.GmCsp;
import org.bcia.julongchain.csp.gm.dxct.GmCspFactory;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyGenOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyImport;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2SignerOpts;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3HashOpts;
import org.bcia.julongchain.csp.gm.dxct.sm4.SM4EncrypterOpts;
import org.bcia.julongchain.csp.gm.dxct.sm4.SM4Key;
import org.bcia.julongchain.csp.gm.dxct.sm4.SM4KeyGenOpts;
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import static org.bcia.julongchain.csp.factory.CspManager.getDefaultCsp;


/**
 * Copyright BCIA. All Rights Reserved.
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

/**
 * @author zhanglin
 * @purpose Define the interface, DecrypterOpts
 * @date 2018-01-25
 * @company Dingxuan
 */

public class GmCspTest {
    private ICsp csp;

    @Before
    public void setUp() throws Exception {
        System.out.println("before test");
        System.out.println("set up...");
        GmCspFactory factory = new GmCspFactory();
        Assert.assertNotNull(factory);
        // IFactoryOpts opts = new GmFactoryOpts(256, "SM3", true, true, "",true);
        IFactoryOpts opts = new GmFactoryOpts();
        csp = factory.getCsp(opts);
        Assert.assertNotNull(csp);
    }

    @After
    public void finalize() {
        System.out.println("finalize...");
    }

    @Test
    public void keyGen() {
        System.out.println("test keyGen...");
        try {
            IKey sm2key = getDefaultCsp().keyGen(new SM2KeyGenOpts());

            System.out.println("generate the SM2 Privatekey:" + Hex.toHexString(sm2key.toBytes()));
            System.out.println("generate the SM2 PublicKey:" + Hex.toHexString(sm2key.getPublicKey().toBytes()));
            IKey sm4key = getDefaultCsp().keyGen(new SM4KeyGenOpts());
            System.out.println("SM4 Key:" + Hex.toHexString(sm4key.toBytes()));
        } catch (JulongChainException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void keyDeriv() {
    }

    @Test
    public void keyImport() {
    }

    @Test
    public void getKey() {
    }

    //ＧＭ/T 0004-2012 ＜ＳＭ３密码杂凑算法＞附录Ａ　示例１
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
        } catch (JulongChainException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    //ＧＭ/T 0004-2012 ＜ＳＭ３密码杂凑算法＞附录Ａ　示例２
    @Test
    public void hashUnitTest2() {
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
        } catch (JulongChainException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //根据第三方软件进行对比测试
    @Test
    public void hashUnitTestByTester() {
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
        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getHash() {


    }

    @Test
    public void sign() throws JulongChainException {
        byte[] prik=Hex.decode("44c5ff2006af4b4d3e97c721be0e446c56939bb7d02debc16db7f535446850fe");
        byte[] pubK=Hex.decode("047e371a5c8a01fca82820e8fa6d5ba8faee4cae4ee3ef65160c1ebdf5a7b0bbf2f0670e3496e8344df3065e549fdad924e1cf9c96e8e6e62b925c046bac25ea43");
        IKey sk=new SM2KeyImport(prik,null);
        byte[] hash=Hex.decode("f12bcfd72e000c7e8a3499821694b208d745f72172f173a2e595207bf66d48f9");
        byte[] signValue=csp.sign(sk,hash,new SM2SignerOpts());
        byte[] sign=Hex.decode("304402200f988879e415dbf972d62914d7e8c531424586f440ca1b12dabacc4cbb0cef1e02203d1e07a8c1c9277c1c91e25b2f92c72efd45e7493ac9082472e20a1bf884e2ac");
        IKey pk=new SM2KeyImport(null,pubK);
        System.out.println(csp.verify(pk, sign, hash, new SM2SignerOpts()));
    }

    @Test
    public void verify() {
    }

    @Test
    public void encrypt() throws JulongChainException {
        GmFactoryOpts opts = new GmFactoryOpts();
        GmCsp gmCsp = new GmCsp(opts);
        SM4Key sm4Key = new SM4Key();
        gmCsp.encrypt(sm4Key, "abc".getBytes(), new SM4EncrypterOpts());
    }

    @Test
    public void decrypt() throws IOException {
        // PemObject pemObject=new PemObject("privatekey","123".getBytes());
        //PemWriter pemWriter=new PemWriter();

        // new PemWriter().writeObject(pemObject);
        // PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec();
        PKCS8Key pkcs8Key = new PKCS8Key();
        PemObject pemObject = new PemObject("privatekey", "123".getBytes());
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        pemWriter.writeObject(pemObject);
        //pemWriter.writeObject(pemObject);
        pemWriter.close();
        str.close();

        System.out.println(str.toString());
        //return str.toString();
        PemReader pemReader;
        PemObjectParser pemObjectParser;
    }

    @Test
    public void rng() throws JulongChainException {
        for (int i = 0; i < 100; i++) {
            long t1 = System.currentTimeMillis();
            byte[] secureSeed=csp.rng(24,null);
            long t2 = System.currentTimeMillis();
            System.out.println(String.format("随机数长度：%s",secureSeed.length));
            System.out.println(Hex.toHexString(secureSeed));
            System.out.println(String.format("生成随机数消耗时间%s ms", (t2 - t1)));
        }

    }
}