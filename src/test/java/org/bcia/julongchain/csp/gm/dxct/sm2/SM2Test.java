package org.bcia.julongchain.csp.gm.dxct.sm2;

import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * @author zhangmingyang
 * @Date: 2018/4/3
 * @company Dingxuan
 */
public class SM2Test {
    private SM2 sm2;
    private SM2KeyPair sm2KeyPair;

    @Before
    public void setup() {
        sm2 = new SM2();
        sm2KeyPair = sm2.generateKeyPair();
    }

    @Test
    public void generateKeyPair() {
        System.out.println("-------------sm2keyPair generate test-------------");
        System.out.println("generate the sm2 publickey:" + Hex.toHexString(sm2KeyPair.getPublickey()));
        System.out.println("generate the sm2 privatekey:" + Hex.toHexString(sm2KeyPair.getPrivatekey()));
    }

    @Test
    public void standardDataSign() {

    }


    @Test
    public void validPramSign() {


    }


    @Test
    public void invalidPramSign() {

    }

    @Test
    public void diffSizeDataSignWithVerify() {
        //128 byte data
        long before128Sign = System.currentTimeMillis();
        byte[] plain128Text = CryptoUtil.genByteArray(128);
        byte[] sign128Value = sm2.sign(sm2KeyPair.getPrivatekey(), plain128Text);
        long after128Sign = System.currentTimeMillis();
        System.out.println(String.format("128 byte data sign expend the time %s ms", (after128Sign - before128Sign)));

        long before128Verify = System.currentTimeMillis();
        boolean verify128=sm2.verify(sm2KeyPair.getPublickey(), sign128Value, plain128Text);
        assertEquals(true,verify128);
        long after128Verify = System.currentTimeMillis();
        System.out.println(String.format("128 byte data verify expend the time %s ms", (after128Verify - before128Verify)));


        //256 byte data
        long before256Sign = System.currentTimeMillis();
        byte[] plain256Text = CryptoUtil.genByteArray(256);
        byte[] sign256Value = sm2.sign(sm2KeyPair.getPrivatekey(), plain256Text);
        long after256Sign = System.currentTimeMillis();
        System.out.println(String.format("256 byte data expend the time %s ms", (after256Sign - before256Sign)));

        long before256Verify = System.currentTimeMillis();
        boolean verify256=sm2.verify(sm2KeyPair.getPublickey(), sign256Value, plain256Text);
        long after256Verify = System.currentTimeMillis();
        assertEquals(true,verify256);
        System.out.println(String.format("256 byte data verify expend the time %s ms", (after256Verify - before256Verify)));


        //512 byte data
        long before512Sign = System.currentTimeMillis();
        byte[] plain512Text = CryptoUtil.genByteArray(512);
        byte[] sign512Value=sm2.sign(sm2KeyPair.getPrivatekey(), plain512Text);
        long after512Sign = System.currentTimeMillis();
        System.out.println(String.format("512 byte data expend the time %s ms", (after512Sign - before512Sign)));

        long before512Verify = System.currentTimeMillis();
        boolean verify512=sm2.verify(sm2KeyPair.getPublickey(), sign512Value, plain512Text);
        long after512Verify = System.currentTimeMillis();
        assertEquals(true,verify512);
        System.out.println(String.format("512 byte data verify expend the time %s ms", (after512Verify - before512Verify)));


        //1024 byte data
        long before1024Sign = System.currentTimeMillis();
        byte[] plain1024Text = CryptoUtil.genByteArray(1024);
        byte[] sign1024Value= sm2.sign(sm2KeyPair.getPrivatekey(), plain1024Text);
        long after1024Sign = System.currentTimeMillis();
        System.out.println(String.format("1024 byte data expend the time %s ms", (after1024Sign - before1024Sign)));

        long before1024Verify = System.currentTimeMillis();
        boolean verify1024=sm2.verify(sm2KeyPair.getPublickey(), sign1024Value, plain1024Text);
        long after1024Verify = System.currentTimeMillis();
        assertEquals(true,verify1024);
        System.out.println(String.format("1024 byte data verify expend the time %s ms", (after1024Verify - before1024Verify)));


    }

    @Test
    public void verify() {

    }
}