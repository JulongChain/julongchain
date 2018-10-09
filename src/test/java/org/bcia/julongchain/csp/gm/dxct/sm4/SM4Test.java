package org.bcia.julongchain.csp.gm.dxct.sm4;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.csp.pkcs11.util.SymmetryKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.KeySpec;

import static org.junit.Assert.assertEquals;

/**
 * SM4 测试类
 *
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public class SM4Test {
    private SM4 sm4;

    @Before
    public void setup() {
        sm4 = new SM4();
    }


    @Test
    public void DataTest() throws  CspException {
        System.out.println("------------------------------------------消息加密-----------------------------------------------------");
        String plainText = "Ding xuan abccddee!!!!";
        System.out.println("消息原文：" + plainText);
        System.out.println(Hex.toHexString(plainText.getBytes()));
        byte[] sm4key = SM4.generateKey();
        byte[] decryptData = sm4.encryptECB(plainText.getBytes(), sm4key);
        System.out.println("加密后消息：" + Hex.toHexString(decryptData));
        System.out.println("------------------------------------------解密数据-----------------------------------------------------");
        byte[] plainContent = sm4.decryptECB(decryptData, sm4key);
        String encryptBefore = new String(plainContent);
        System.out.println("原文数据：" + encryptBefore);
    }


    @Test
    public void standardDataTest() throws CspException {
        String plaintext = "0123456789abcdeffedcba9876543210";
        String expected = "681edf34d206965e86b3e94f536e4246";
        byte[] sm4key = Hex.decode("0123456789abcdeffedcba9876543210");
        byte[] result = SM4.ecbProcessData(Hex.decode(plaintext), sm4key, 1);
        System.out.println(Hex.toHexString(result));
        assertEquals(expected, Hex.toHexString(result));
    }

    @Test
    public void moreRoundStandardDataTest() throws CspException {
        String plaintext = "0123456789abcdeffedcba9876543210";
        String expected = "595298c7c6fd271f0402f804c33d3f66";
        byte[] sm4key = Hex.decode("0123456789abcdeffedcba9876543210");
        byte[] buf = new byte[16];
        System.arraycopy(Hex.decode(plaintext), 0, buf, 0, buf.length);
        for (int i = 0; i != 1000000; i++) {
            buf = SM4.ecbProcessData(buf, sm4key, 1);
        }
        assertEquals(expected, Hex.toHexString(buf));
    }


    @Test
    public void encryptECB() throws CspException {
        byte[] sm4key = SM4.generateKey();
        int[] testData = {1, 16, 32, 64, 128, 256, 512, 1024, 2048, 1048576, 2097152};
        for (int i = 0; i < testData.length; i++) {
            byte[] content = CryptoUtil.genByteArray(testData[i]);
            long t1 = System.currentTimeMillis();
            byte[] decryptData = sm4.encryptECB(content, sm4key);
            sm4.decryptECB(decryptData, sm4key);
            long t2 = System.currentTimeMillis();
            System.out.println(String.format("%s byte data expend the time %s:ms", testData[i], (t2 - t1)));
        }
    }
    @Test
    public void  decryptEcb() throws CspException {
        byte[] testData = Hex.decode("01234567454545");
        byte[] sm4key = SM4.generateKey();
        byte[] decryptData = sm4.encryptECB(testData, sm4key);
        byte[] plainText=sm4.decryptECB(decryptData, sm4key);
        System.out.println(Hex.toHexString(plainText));
    }

    @Test
    public void invalidParam() {
        //原文为null
        try {
            sm4.encryptECB(null, SM4.generateKey());
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]plainText is null", e.getMessage());
        }
        //密钥为null
        try {
            sm4.encryptECB("test".getBytes(), null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]sm4key is null", e.getMessage());
        }
        //密钥长度为大于16字节
        try {
            sm4.encryptECB("test".getBytes(), CryptoUtil.genByteArray(17));
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]sm4key's pattern is wrong!", e.getMessage());
        }

    }
}