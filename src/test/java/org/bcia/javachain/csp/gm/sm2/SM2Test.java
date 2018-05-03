package org.bcia.javachain.csp.gm.sm2;

import org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
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

        SM2 sm02 = new SM2();


//         SM2KeyPair keyPair = sm02.generateKeyPair();
//		 ECPoint publicKey=keyPair.getPublicKey();
//		 BigInteger privateKey=keyPair.getPrivateKey();
//		byte[] publicKeyEncoded= publicKey.getEncoded(false);
//		byte[]  privateKeyArray=privateKey.toByteArray();
//		//printHexString(publicKeyEncoded);
//        String publickeyString= new String(Base64.encode(publicKeyEncoded));
//        String privatekeyString= new String(Base64.encode(privateKeyArray));
//        System.out.println("公钥字符串："+publickeyString);
//        PrintWriter pw1 = new PrintWriter(new FileOutputStream("D:/publicKey.txt"));
//        PrintWriter pw2 = new PrintWriter(new FileOutputStream("D:/privateKey.txt"));
//        pw1.print(publickeyString);
//        pw2.print(privatekeyString);
//        pw1.close();
//        pw2.close();


       // writeFileBytes("E:/public.txt",publicKeyEncoded);
        //writeFileBytes("E:/private.txt",privateKeyArray);
        System.out.println("-----------------公钥加密与解密-----------------");

//        ECPoint publicKey = sm02.importPublicKey("E:/publickey.pem");
//        BigInteger privateKey = sm02.importPrivateKey("E:/privatekey.pem");
//        SM2KeyPair keyPair = new SM2KeyPair();
//        keyPair.setPrivateKey(privateKey);
//        keyPair.setPublicKey(publicKey);
//        System.out.println("公钥：" + publicKey.getEncoded(false));
//        byte[] data = sm02.encrypt("测试加密aaaaaaaaaaa123aabb", publicKey);
//        System.out.print("密文:");
//        System.out.println("解密后明文:" + sm02.decrypt(data, privateKey));
        System.out.println("-----------------签名与验签-----------------");
//        String IDA = "Heartbeats";
//        String msg = "要签名的信息";
//        byte[] M = msg.getBytes();
//        byte[] signvalue = sm02.sign(M, IDA, keyPair);
//        System.out.println("用户标识:" + IDA);
//        System.out.println("签名信息:" + M);
//        BigInteger[] rs = SM2KeyUtil.decode(signvalue);
//        System.out.println("签名值1：" + rs[0]);
//        System.out.println("签名值2：" + rs[1]);
//        System.out.println("验证签名:" + sm02.verify(M, signvalue, IDA, publicKey));
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