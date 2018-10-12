package org.bcia.julongchain.csp.gm.dxct.sm2;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * sm2 测试类
 *
 * @author zhangmingyang
 * @Date: 2018/4/3
 * @company Dingxuan
 */
public class SM2Test {
    private SM2 sm2;
    private SM2KeyPair sm2KeyPair;

    @Before
    public void setup() {
        System.out.println("before test");
        System.out.println("set up...");
        sm2 = new SM2();
        sm2KeyPair = sm2.generateKeyPair();
    }
    @After
    public void finalize() {
        System.out.println("finalize...");
    }

    @Test
    public void generateKeyPair() {
        System.out.println("-------------sm2keyPair generate test-------------");
        System.out.println("generate the sm2 publickey:" + Hex.toHexString(sm2KeyPair.getPublickey()));
        System.out.println("generate the sm2 privatekey:" + Hex.toHexString(sm2KeyPair.getPrivatekey()));
    }

    @Test
    public void SignTest() throws CspException {
        byte[] content = CryptoUtil.genByteArray(32);
        byte[] privateKey = sm2KeyPair.getPrivatekey();
        byte[] publicKey = sm2KeyPair.getPublickey();
        byte[] singnature = sm2.sign(privateKey, content);
        boolean result = sm2.verify(publicKey, singnature, content);
        System.out.println("Validation results：" + result);
    }

    @Test
    public void invalidKeySignTest(){
        byte[] error=Hex.decode("612c7ab32011048529173c1186110a1dd0de433af0eb70ceef84f10aa44e16de");
        try {
            sm2.sign(error,error);
        } catch (CspException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void invalidParamSignTest() {
        byte[] testData = null;
        byte[] zeroData = new byte[0];
        byte[] privateKey = sm2KeyPair.getPrivatekey();
        try {
            sm2.sign(privateKey, null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]plainText is null", e.getMessage());
        }
        try {
            sm2.sign(zeroData, null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]privateKey's length is 0", e.getMessage());
        }
        try {
            sm2.sign(privateKey, zeroData);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]plainText's length is 0", e.getMessage());
        }
        try {
            sm2.sign(null, CryptoUtil.genByteArray(12));
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]privateKey is null", e.getMessage());
        }

    }

    @Test
    public void invalidParamVerify() {
        byte[] testData = null;
        byte[] zeroData = new byte[0];
        byte[] publicKey = sm2KeyPair.getPublickey();
        try {
            sm2.verify(null, null, null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]publicKey is null", e.getMessage());
        }
        try {
            sm2.verify(publicKey, null, null);
        } catch (CspException e) {
            System.out.println(e.getMessage());
            assertEquals("[Csp]signValue is null", e.getMessage());
        }

    }


    @Test
    public void publicKeyEncrypt() throws CspException {
        byte[] testData = Hex.decode("01234567");
        byte[] publicKey = sm2KeyPair.getPublickey();
        byte[] privateKey = sm2KeyPair.getPrivatekey();
        byte[] encryptData = sm2.encrypt(testData, publicKey);
        byte[] plainData=sm2.decrypt(encryptData,privateKey);
        assertEquals("01234567",Hex.toHexString(plainData));
    }


    @Test
    public void diffSizeDataSignWithVerify() throws CspException {
        int[] testData = {1, 16, 32, 64, 128, 256, 512, 1024, 2048, 1048576, 2097152};

        for (int i = 0; i < testData.length; i++) {
            byte[] content = CryptoUtil.genByteArray(testData[i]);
            byte[] privateKey = sm2KeyPair.getPrivatekey();
            byte[] publicKey = sm2KeyPair.getPublickey();
            long t1 = System.currentTimeMillis();
            byte[] singnature = sm2.sign(privateKey, content);
            sm2.verify(publicKey, singnature, content);
            long t2 = System.currentTimeMillis();
            System.out.println(String.format("%s byte data expend the time %s:ms", testData[i], (t2 - t1)));
        }
    }

}