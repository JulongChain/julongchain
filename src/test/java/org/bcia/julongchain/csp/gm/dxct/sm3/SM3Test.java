package org.bcia.julongchain.csp.gm.dxct.sm3;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
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
    public void sm3Test() throws  CspException {
        byte[] digest = sm3.hash("123".getBytes());
        System.out.println(Hex.toHexString(digest));
    }

    @Test
    public void standardDataTest() throws CspException {
        byte[] testData = "abc".getBytes();
        String standardDigest = "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
        System.out.println("测试数据ASCII码" + Hex.toHexString(testData));
        byte[] hashValue = sm3.hash(testData);
        System.out.println(hashValue.length);
        System.out.println(Hex.toHexString(hashValue));
        assertEquals(standardDigest, Hex.toHexString(hashValue));
    }

    @Test
    public void BigDataTest() throws CspException {
        //256bit 标准数据
        byte[] testData = Hex.decode("61626364616263646162636461626364616263646162636461626364616263646162636461626364616263646162636461626364616263646162636461626364");
        String standardDigest = "debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";
        System.out.println("测试数据ASCII码:" + Hex.toHexString(testData));
        byte[] hashValue = sm3.hash(testData);
        System.out.println("hash值:"+Hex.toHexString(hashValue));
        assertEquals(standardDigest, Hex.toHexString(hashValue));
    }

    @Test
    public void NullDataTest() {
        byte[] testData = null;
        try {
            sm3.hash(testData);
        } catch (CspException e) {
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void shortDataTest() {
        byte[] testData = "".getBytes();
        try {
            sm3.hash(testData);
        } catch (CspException e) {
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void performanceTest() throws CspException {
        //128字节
        byte[] testData1= CryptoUtil.genByteArray(128);
        long t1=System.currentTimeMillis();
        sm3.hash(testData1);
        long t2=System.currentTimeMillis();
        System.out.println(String.format("%s byte data expend time %s ms",testData1.length,(t2-t1)));
        //1024字节
        byte[] testData2= CryptoUtil.genByteArray(1024);
        long t3=System.currentTimeMillis();
        sm3.hash(testData1);
        long t4=System.currentTimeMillis();
        System.out.println(String.format("%s byte data expend time %s ms",testData2.length,(t4-t3)));
        //1Mb
        byte[] testData3= CryptoUtil.genByteArray(1048576);
        long t5=System.currentTimeMillis();
        sm3.hash(testData1);
        long t6=System.currentTimeMillis();
        System.out.println(String.format("%s byte data expend time %s ms",testData3.length,(t6-t5)));
        //10 Mb
        byte[] testData4= CryptoUtil.genByteArray(10485760);
        long t7=System.currentTimeMillis();
        sm3.hash(testData1);
        long t8=System.currentTimeMillis();
        System.out.println(String.format("%s byte data expend time %s ms",testData4.length,(t8-t7)));
    }


}