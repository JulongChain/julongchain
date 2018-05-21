package org.bcia.javachain.csp.gm;

import io.netty.handler.ssl.PemPrivateKey;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.sm2.SM2KeyGenOpts;
import org.bcia.javachain.csp.gm.sm3.SM3HashOpts;
import org.bcia.javachain.csp.gm.sm4.SM4EncrypterOpts;
import org.bcia.javachain.csp.gm.sm4.SM4Key;
import org.bcia.javachain.csp.gm.sm4.SM4KeyGenOpts;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.IHashOpts;
import org.bcia.javachain.common.exception.JavaChainException;
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
import sun.security.util.Pem;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.bcia.javachain.csp.factory.CspManager.getDefaultCsp;


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
         IKey sm2key=getDefaultCsp().keyGen(new SM2KeyGenOpts());

         System.out.println("SM2 Privatekey:"+Hex.toHexString(sm2key.toBytes()));
         System.out.println("SM2 PublicKey:"+Hex.toHexString(sm2key.getPublicKey().toBytes()));
         IKey sm4key=getDefaultCsp().keyGen(new SM4KeyGenOpts());
         System.out.println("SM4 Key:"+Hex.toHexString(sm4key.toBytes()));
        } catch (JavaChainException e) {
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
        } catch (JavaChainException e) {
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
        } catch (JavaChainException e) {
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
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getHash() {


    }

    @Test
    public void sign() {

    }

    @Test
    public void verify() {
    }

    @Test
    public void encrypt() throws JavaChainException {
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
    public void rng() {

        //  SecureRandom csprng = new SecureRandom();

        //  byte[] randomBytes = new byte[24];
        // csprng.engineNextBytes();
        // byte[] rng = csprng.engineGenerateSeed(24);

        SecureRandom secureRandom = new SecureRandom();
        byte[] secureSeed = secureRandom.generateSeed(24);
        String s = new String(secureSeed);
        System.out.println(s);
        //csprng.nextBytes(randombytes);
    }
}