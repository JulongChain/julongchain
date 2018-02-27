package org.bcia.javachain.bccsp.gm;

import org.bcia.javachain.bccsp.factory.IFactoryOpts;
import org.bcia.javachain.bccsp.intfs.IBccsp;
import org.bcia.javachain.bccsp.intfs.opts.IHashOpts;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;


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

public class GmBccspTest {
    private IBccsp bccsp;

    @Before
    public void setUp() throws Exception{
        System.out.println("before test");
        System.out.println("set up...");
        GmBccspFactory factory = new GmBccspFactory();
        Assert.assertNotNull(factory);
        IFactoryOpts opts = new GmFactoryOpts(256, "SM3", true, true, "");
        bccsp=factory.getBccsp(opts);
        Assert.assertNotNull(bccsp);
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void keyGen()  { System.out.println("test keyGen..."); }

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
            String message="abc";
            byte[] msg=message.getBytes("ASCII");
            String encodedHexMsg= Hex.toHexString(msg);
            System.out.println("To Hash Message:"+encodedHexMsg);
            String expectedResult="66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
            byte[] expectedHashMsg=Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = bccsp.hash(msg, hashOpts);
            String encodedHexDigests= Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests,expectedHashMsg);
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
            String message="abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd";
            byte[] msg=message.getBytes("ASCII");
            String encodedHexMsg=Hex.toHexString(msg);
            System.out.println("To Hash Message:"+encodedHexMsg);
            String expectedResult="debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";
            byte[] expectedHashMsg=Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = bccsp.hash(msg, hashOpts);
            String encodedHexDigests= Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests,expectedHashMsg);
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
            String message="4865243A4EF5FFA94C";
            byte[] msg=Hex.decode(message);
            System.out.println("To Hash Message:"+message);
            String expectedResult="CDB84682A5C8036E692156E635EED508A2B5D41D1AC5A467432C620307DE8184";
            byte[] expectedHashMsg=Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = bccsp.hash(msg, hashOpts);
            String encodedHexDigests= Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests,expectedHashMsg);
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
    public void encrypt() {
    }

    @Test
    public void decrypt() {
    }

    @Test
    public void rng() {
    }
}