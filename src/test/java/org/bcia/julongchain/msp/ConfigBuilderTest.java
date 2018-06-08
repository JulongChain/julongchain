package org.bcia.julongchain.msp;

import org.bcia.julongchain.msp.util.LoadLocalMspFiles;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import java.security.spec.PKCS8EncodedKeySpec;



/**
 * @author zhangmingyang
 * @Date: 2018/4/8
 * @company Dingxuan
 */
public class ConfigBuilderTest {

    @Test
    public void iteratorPath() {

       PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec("123".getBytes());

        byte[] bytes=pkcs8EncodedKeySpec.getEncoded();

        java.lang.String publiKey=java.lang.String.valueOf(Base64.encode(bytes));
            //    new String(Base64.encode(bytes));
       // Base64.encode(bytes);
        System.out.println(publiKey);

    }

    @Test
    public void init() {
        LoadLocalMspFiles.init("E:/msp");
        System.out.println(LoadLocalMspFiles.mspMap.get("E:\\msp\\keystore\\privatekey.pem"));
    }
}