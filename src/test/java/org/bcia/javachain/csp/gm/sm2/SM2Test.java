package org.bcia.javachain.csp.gm.sm2;

import org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;

import static org.bcia.javachain.common.util.FileUtils.writeFileBytes;
import static org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil.printHexString;
import static org.junit.Assert.*;

/**
 * @author zhangmingyang
 * @Date: 2018/4/3
 * @company Dingxuan
 */
public class SM2Test {

    @Test
    public void encrypt() throws FileNotFoundException {
    }

    @Test
    public void decrypt() {

    }

    @Test
    public void generateKeyPair() {

    }

    @Test
    public void exportPublicKey() {
    }

    @Test
    public void importPublicKey() {
    }

    @Test
    public void importPrivateKey() {
    }

    @Test
    public void sign() {
        SM2 sm2 = new SM2();
        SM2KeyPair sm2KeyPair = sm2.generateKeyPair();
        sm2KeyPair.getPrivatekey();
        long t1 = System.currentTimeMillis();
        byte[] signValue = sm2.sign(sm2KeyPair.getPrivatekey(), " a  this is a message sdfdee".getBytes());

        long t2 = System.currentTimeMillis();
        System.out.println("签名时间为：" + (t2 - t1));
        System.out.println(Hex.toHexString(signValue));
        long t3 = System.currentTimeMillis();
        boolean verify = sm2.verify(sm2KeyPair.getPublickey(), signValue, " a  this is a message sdfdee".getBytes());

        long t4 = System.currentTimeMillis();
        System.out.println("验签时间为：" + (t4 - t3));
        System.out.println("总时间为：" + (t4 - t1));
        System.out.println(verify);
        System.out.println("-------------------数据加密----------------");
        System.out.println("加密前的数据" + Hex.toHexString("12389897979".getBytes()));
        byte[] enc = sm2.encrypt("12389897979".getBytes(), sm2KeyPair.getPublickey());
        System.out.println("加密后的数据" + Hex.toHexString(enc));
        byte[] dec = sm2.decrypt(enc, sm2KeyPair.getPrivatekey());
        System.out.println("解密后的数据：" + Hex.toHexString(dec));
    }

    @Test
    public void verify() {
    }

    @Test
    public void byte2ECpoint() {
    }

    @Test
    public void decode() {
    }
}