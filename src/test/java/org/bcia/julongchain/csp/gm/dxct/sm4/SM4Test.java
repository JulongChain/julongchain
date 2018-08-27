package org.bcia.julongchain.csp.gm.dxct.sm4;

import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public class SM4Test {

    @Test
    public void generateKey() throws IOException {
        System.out.println("------------------------------------------消息加密-----------------------------------------------------");
        String plainText="Ding xuan abccddee!!!!";
        System.out.println("消息原文："+plainText);
        System.out.println(Hex.toHexString(plainText.getBytes()));
        //byte[] sm4key = Hex.decode("63F99D448421CAE6D7BB80B8ACBF4E7E");

        SM4 sm4=new SM4();
        byte[] sm4key =SM4.generateKey();
        byte[] decryptData=  sm4.encryptECB(plainText.getBytes(),sm4key);
        System.out.println("加密后消息："+Hex.toHexString(decryptData));
        System.out.println("------------------------------------------解密数据-----------------------------------------------------");
        byte[] plainContent=sm4.decryptECB(decryptData,sm4key);
        String encryptBefore=new String(plainContent);
        System.out.println("原文数据："+encryptBefore);


        System.out.println("------------------------------------------写入密钥-----------------------------------------------------");
        PemObject pemObject = new PemObject("PRIVATE KEY", plainContent);
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        str.close();
        System.out.println("写入的密钥：");
        System.out.println(str.toString());

        StringReader reader = new StringReader(str.toString());
        PemReader pem = new PemReader(reader);
        PemObject pemObject1 = pem.readPemObject();
        String publiKey = String.valueOf(Hex.toHexString(pemObject1.getContent()));


        System.out.println("读取出的值：" + publiKey);
    }

    @Test
    public void encryptECB() {
        SM4 sm4=new SM4();
        byte[] sm4key =SM4.generateKey();
        byte[] content= CryptoUtil.genByteArray(512);
        long t1=System.currentTimeMillis();
        byte[] decryptData=sm4.encryptECB(content,sm4key);
        sm4.decryptECB(decryptData,sm4key);
        long t2=System.currentTimeMillis();
        System.out.println(t2-t1);

        byte[] content1= CryptoUtil.genByteArray(1024);
        long t3=System.currentTimeMillis();
        byte[] decryptData1=sm4.encryptECB(content1,sm4key);
        sm4.decryptECB(decryptData1,sm4key);
        long t4=System.currentTimeMillis();
        System.out.println(t4-t3);

        byte[] content2= CryptoUtil.genByteArray(2048);
        long t5=System.currentTimeMillis();
        byte[] decryptData2=sm4.encryptECB(content2,sm4key);
        sm4.decryptECB(decryptData2,sm4key);
        long t6=System.currentTimeMillis();
        System.out.println(t6-t5);

        byte[] content3= CryptoUtil.genByteArray(2097152);
        long t7=System.currentTimeMillis();
        byte[] decryptData3=sm4.encryptECB(content3,sm4key);
        sm4.decryptECB(decryptData3,sm4key);
        long t8=System.currentTimeMillis();
        System.out.println(t8-t7);

        byte[] content4= CryptoUtil.genByteArray(1048576);
        long t9=System.currentTimeMillis();
        byte[] decryptData4=sm4.encryptECB(content4,sm4key);
        sm4.decryptECB(decryptData4,sm4key);
        long t10=System.currentTimeMillis();
        System.out.println(t10-t9);



    }

    @Test
    public void decryptECB() {

    }
}