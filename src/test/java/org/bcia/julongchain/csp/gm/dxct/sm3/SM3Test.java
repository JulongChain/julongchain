package org.bcia.julongchain.csp.gm.dxct.sm3;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * sm3测试类
 *
 * @author zhangmingyang
 * @date 2018/08/29
 * @company Dingxuan
 */
public class SM3Test {

    private SM3 sm3;

    @Before
    public void setup() {
        sm3 = new SM3();
    }

    @Test
    public void sm3Test() throws NoSuchAlgorithmException {
        byte[] digest = sm3.hash("123".getBytes());
        System.out.println(Hex.toHexString(digest));
    }

    @Test
    public void standardDataTest() throws NoSuchAlgorithmException {
        byte[] testData = "abc".getBytes();
        String standardDigest = "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
        System.out.println("测试数据ASCII码" + Hex.toHexString(testData));
        byte[] hashValue = sm3.hash(testData);
        System.out.println(Hex.toHexString(hashValue));
        assertEquals(standardDigest, Hex.toHexString(hashValue));
    }

    @Test
    public void BigDataTest() throws NoSuchAlgorithmException {
        byte[] testData = Hex.decode("61626364616263646162636461626364616263646162636461626364616263646162636461626364616263646162636461626364616263646162636461626364");
        String standardDigest = "debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";
        System.out.println("测试数据ASCII码" + Hex.toHexString(testData));
        byte[] hashValue = sm3.hash(testData);
        System.out.println(Hex.toHexString(hashValue));
        assertEquals(standardDigest, Hex.toHexString(hashValue));
    }

    @Test
    public void NullDataTest() throws NoSuchAlgorithmException {
        byte[] testData = null;
        try {
            byte[] hashValue = sm3.hash(testData);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e.getMessage());
        }

    }
}