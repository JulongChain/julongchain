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
import org.bcia.julongchain.csp.pkcs11.rsa.RsaSignOpts;
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
 * 国密Csp测试
 *
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


    @Test
    public void invalidParamHashTest(){
        byte[] testData=Hex.decode("");
        byte[] nullData=null;
        //待hash数据为null
        try {
            csp.hash(nullData,null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]Invalid data. It must not be null.",e.getMessage());
        }

        //待hash数据长度为0
        try {
            csp.hash(testData,null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]Invalid data. Cannot be empty.",e.getMessage());
        }
    }

    /**
     * 签名测试
     */
    @Test
    public void signTest() {
        //正确的参数
        byte[] prik = Hex.decode("44c5ff2006af4b4d3e97c721be0e446c56939bb7d02debc16db7f535446850fe");
        IKey sk = new SM2KeyImport(prik, null);
        byte[] data = Hex.decode("f12bcfd72e000c7e8a3499821694b208d745f72172f173a2e595207bf66d48f9");
        byte[] zeroLengthData = Hex.decode("");
        byte[] nullData = null;
        try {
            byte[] signature = csp.sign(sk, data, new SM2SignerOpts());
            System.out.println("sm2签名值:" + Hex.toHexString(signature));
        } catch (CspException e) {
            System.out.println(e.getMessage());
        }
        //null的签名选项
        try {
            csp.sign(sk, data, null);
        } catch (CspException e) {
            System.out.println(e);
            Assert.assertEquals("[Csp]Invalid ISignerOpts. It must not be null.", e.getMessage());
        }

        //为null签名数据
        try {
            csp.sign(sk, nullData, new SM2SignerOpts());
        } catch (CspException e) {
            System.out.println(e);
            Assert.assertEquals("[Csp]Invalid plaintext. Cannot be null.", e.getMessage());
        }

        //原文长度为0
        try {
            csp.sign(sk, zeroLengthData, new SM2SignerOpts());
        } catch (CspException e) {
            System.out.println(e);
            Assert.assertEquals("[Csp]Invalid plaintext. plaintext's length is zero.", e.getMessage());
        }
    }


    /**
     * 验证签名测试
     */

    @Test
    public void verifyTest() throws CspException {
        byte[] signature = Hex.decode("3046022100c988ccc735ddcadb4b3ad50e35c9754117fc63b4c0b939c1225f317dc229b97f022100d431c67b4f796a048ec03150bd7edd680198913487fee640b183d1014176e2ba");
        byte[] invalidSignature=Hex.decode("3046022100c988ccc735ddcadb4b3ad50e35c9754117fc63b4c0b939c1225f317dc229b97f022100d431c67b4f796a048ec03150bd7edd680198fee640b183d1014176e2ba");
        byte[] data = Hex.decode("f12bcfd72e000c7e8a3499821694b208d745f72172f173a2e595207bf66d48f9");
        byte[] pubK = Hex.decode("047e371a5c8a01fca82820e8fa6d5ba8faee4cae4ee3ef65160c1ebdf5a7b0bbf2f0670e3496e8344df3065e549fdad924e1cf9c96e8e6e62b925c046bac25ea43");
        byte[] invaildPubK = Hex.decode("047e371a5c8a01fca82820e8fa6d5ba8faee4cae4ee3ef65160c1ebdf5a7b0bbf2f0670e3496e8344df3065e987fdad924e1cf9c96e8e6e62b925c046bac25ea43");
        byte[] nullPk = null;
        boolean result = false;
        IKey nullPublicKey = new SM2KeyImport(null, null);
        IKey invaildPk = new SM2KeyImport(null, invaildPubK);
        IKey pk = new SM2KeyImport(null, pubK);
        //正确的选项
        try {
            result = csp.verify(pk, signature, data, new SM2SignerOpts());
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
        }

        //null的公钥
        try {
            result = csp.verify(nullPublicKey, signature, data, new SM2SignerOpts());
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]publicKey is null", e.getMessage());
        }

        //null的签名选项

        try {
            result = csp.verify(pk, signature, data, null);
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]Invalid ISignerOpts. It must not be null.", e.getMessage());
        }

        //为null的原文数据
        try {
            result = csp.verify(pk, signature, null, new SM2SignerOpts());
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]plainText is null", e.getMessage());
        }

        //长度为0的原文长度
        try {
            result = csp.verify(pk, signature, Hex.decode(""), new SM2SignerOpts());
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]plainText's length is 0", e.getMessage());
        }

        //无效的签名值
        try {
            result = csp.verify(pk, invalidSignature, data, new SM2SignerOpts());
            System.out.println("sm2验签结果：" + result);
        } catch (CspException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 加密测试
     * @throws CspException
     */

    @Test
    public void encryptTest() throws CspException {
        //标准数据
        byte[] sm4KeyBytes = Hex.decode("0123456789abcdeffedcba9876543210");
        byte[] testData = Hex.decode("01234567454545");
        byte[] nullData=null;
        IKey sm4Key=new SM4KeyImport(sm4KeyBytes);
        IKey zeroLengthSm4Key=new SM4KeyImport(Hex.decode(""));
        IKey errorSm4Key=new SM4KeyImport(Hex.decode("0123456789abcdeffedcba9876543210547858"));
        byte[] encryptData = csp.encrypt(sm4Key, testData, new SM4EncrypterOpts());
        System.out.println("加密后的数据："+Hex.toHexString(encryptData));
        byte[] plainText = csp.decrypt(sm4Key, encryptData, new SM4DecrypterOpts());

        Assert.assertArrayEquals(testData, plainText);
        System.out.println("plainText:" + Hex.toHexString(plainText));

        //待加密数据为null
        try {
            csp.encrypt(sm4Key, nullData, new SM4EncrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]plainText is null",e.getMessage());
        }

        //sm4密钥为null
        try {
            csp.encrypt(null, testData, new SM4EncrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]Invalid Key. It must not be nil.",e.getMessage());
        }

        //sm4密钥的长度为0
        try {
            csp.encrypt(zeroLengthSm4Key, testData, new SM4EncrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]sm4key's pattern is wrong!",e.getMessage());
        }
        //错误的sm4密钥
        try {
            csp.encrypt(errorSm4Key, testData, new SM4EncrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]sm4key's pattern is wrong!",e.getMessage());
        }
    }

    /**
     * 解密测试
     * @throws CspException
     */
    @Test
    public void DecryptTest() throws CspException{
        //标准数据验证
        byte[] testData = Hex.decode("01234567454545");
        byte[] sm4KeyBytes = Hex.decode("0123456789abcdeffedcba9876543210");
        IKey zeroLengthSm4Key=new SM4KeyImport(Hex.decode(""));
        IKey errorSm4Key=new SM4KeyImport(Hex.decode("0123456789abcdeffedcba9876543210547858"));
        IKey sm4Key=new SM4KeyImport(sm4KeyBytes);
        byte[] encryptData=Hex.decode("598bf709bb7aa0a669bc95bdc14474d2");
        byte[] plainText = csp.decrypt(sm4Key, encryptData, new SM4DecrypterOpts());
        System.out.println("plainText:" + Hex.toHexString(plainText));
        Assert.assertArrayEquals(testData, plainText);

        //加密数据为null
        try {
            csp.decrypt(sm4Key,null,new SM4DecrypterOpts());
        } catch (CspException e) {
            Assert.assertEquals("[Csp]Invalid ciphertext.It must not be nil",e.getMessage());
        }

        //加密数据长度为0
        try {
            csp.decrypt(sm4Key,Hex.decode(""),new SM4DecrypterOpts());
        } catch (CspException e) {
            Assert.assertEquals("[Csp]Invalid ciphertext. Cannot be empty.",e.getMessage());
        }

        //sm4密钥长度为0
        try {
            csp.decrypt(zeroLengthSm4Key,encryptData,new SM4DecrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            Assert.assertEquals("[Csp]SM4 requires a 128 bit key",e.getMessage());
        }

        //错误的sm4密钥
        try {
            csp.decrypt(errorSm4Key,encryptData,new SM4DecrypterOpts());
        } catch (CspException e) {
            System.out.println(e.getMessage());
           Assert.assertEquals("[Csp]SM4 requires a 128 bit key",e.getMessage());
        }

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