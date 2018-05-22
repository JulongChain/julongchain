package org.bcia.javachain.csp.gm.dxct.sm4;

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

    }

    @Test
    public void decryptECB() {

    }
}