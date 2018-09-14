package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Test;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 类描述
 *
 * @author zhangmingyang
 * @date 2018/07/10
 * @company Dingxuan
 */
public class MspValidateTest {

    @Test
    public void validateIdentity() {

    }

    @Test
    public void skKeyTest() throws IOException, JulongChainException, NoSuchAlgorithmException, InvalidKeySpecException {
        SM2 sm2 = new SM2();
        SM3 sm3 = new SM3();
//        String cert_path = MspValidateTest.class.getResource("/peer0-cert.pem").getPath();
//        // String cert_path = CertTest.class.getResource("/dxtest.pem").getPath();
//
//        //  String sk_path = CertTest.class.getResource("/sk-dxtest_sk").getPath();
//        byte[] idBytes = FileUtils.readFileBytes(cert_path);
//        Certificate certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
//        byte[] publickey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
//
//        System.out.println("颁发者：" + certificate.getIssuer().toString());
//        System.out.println("使用者：" + certificate.getSubject().toString());
//        System.out.println("证书结束时间：" + certificate.getEndDate().getDate().toString());
//        System.out.println(certificate.getIssuer());
//
//
//        byte[] secondCaSignValue = certificate.getSignature().getBytes();
//
//
//        System.out.println(Hex.toHexString(sm3.hash(publickey)));
//        //IKey key= CspHelper.loadPrivateKey(sk_path);
//
//
//        System.out.println(certificate.getEncoded());
//
//
//        Certificate codeCert = Certificate.getInstance(certificate.getEncoded());
//        System.out.println("转换后的" + codeCert.getIssuer());


        String sk_path = MspValidateTest.class.getResource("/sk-dxtest_sk").getPath();
        //byte[] rawPrivateKey = CryptoUtil.readSkFile(sk_path);


        //System.out.println("privateKey hash " + Hex.toHexString(sm3.hash(rawPrivateKey)));


//            File file = new File(sk_path);
//            InputStream inputStream = new FileInputStream(file);//文件内容的字节流
//            InputStreamReader inputStreamReader= new InputStreamReader(inputStream); //得到文件的字符流
//            BufferedReader bufferedReader=new BufferedReader(inputStreamReader); //放入读取缓冲区
//            String readd="";
//            StringBuffer stringBuffer=new StringBuffer();
//            while ((readd=bufferedReader.readLine())!=null) {
//                stringBuffer.append(readd);
//            }
//            inputStream.close();
//            String content=stringBuffer.toString();
//
//            String privateKeyPEM = content.replace("-----BEGIN PRIVATE KEY-----\n", "")
//                    .replace("-----END PRIVATE KEY-----", "").replace("\n", "");
//            byte[] asBytes = Base64.decode(privateKeyPEM.getBytes());
        File inFile = new File(sk_path);
        long fileLen = inFile.length();
        Reader reader = null;
        PemObject pemObject = null;
        reader = new FileReader(inFile);
        char[] content = new char[(int) fileLen];
        reader.read(content);
        String str = new String(content);
        StringReader stringreader = new StringReader(str);
        PemReader pem = new PemReader(stringreader);
        pemObject = pem.readPemObject();

        System.out.println(Hex.toHexString(pemObject.getContent()));
    }



    @Test
    public  void certTest() throws IOException {
//        SM2 sm2=new SM2();
//        String sk_path = MspValidateTest.class.getResource("/sk-dxtest_sk").getPath();
//
//        File inFile = new File(sk_path);
//        long fileLen = inFile.length();
//        Reader reader = null;
//        PemObject pemObject = null;
//        reader = new FileReader(inFile);
//        char[] content = new char[(int) fileLen];
//        reader.read(content);
//        String str = new String(content);
//        StringReader stringreader = new StringReader(str);
//        PemReader pem = new PemReader(stringreader);
//        pemObject = pem.readPemObject();


//
        String privateKey="MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgTchUuHEAckzfS16v\n" +
                          "8hz4Rt9G+41OifbzAr9jM+JGxiygCgYIKoEcz1UBgi2hRANCAASDw0oz+lq1H8QM\n" +
                          "8YaZSikOsCdbLR+sUd+hpzvDF1wmS3zVNqtKnTRzD3bVgR4AFljtBVmbXNmJdrno\n" +
                          "C8r6EmyE";
//        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//        byte[] pk= decoder.decodeBuffer(privateKey);
//
//
//        byte[] pk1=Base64.decode(privateKey);
//        Assert.assertEquals(Hex.toHexString(pk),Hex.toHexString(pk1));
//        System.out.println(Hex.toHexString(pk));
//
//        System.out.println(Hex.toHexString(pemObject.getContent()));

       byte[] sk=org.bouncycastle.util.encoders.Base64.decode(privateKey);

        System.out.println("私钥长度"+sk.length);
        System.out.println(Hex.toHexString(sk));
//
//
//        System.out.println(Hex.toHexString(pk));

       // Assert.assertEquals("308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420013C84EBB065ABB44FBAD720",Hex.toHexString(pk));
 //       byte[] pk=Hex.decode("308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420013C84EBB065ABB44FBAD720");


//        byte[] sign=  sm2.sign(sk,"123".getBytes());
//
//
        String cert_path = MspValidateTest.class.getResource("/szca/testsm2.pem").getPath();
        byte[] idBytes = FileUtils.readFileBytes(cert_path);
        Certificate certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
        byte[] publickey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

        System.out.println(certificate.getSubject());
        System.out.println("公钥："+Hex.toHexString(publickey));
        System.out.println("公钥长度："+publickey.length);
//
//
//        boolean v=sm2.verify(publickey,sign,"123".getBytes());
//        System.out.println(v);
    }




    @Test
    public  void base64() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CryptoException, CspException {
//        final Base64.Decoder decoder =  Base64.Decoder.getDecoder();
//        final Base64.Encoder encoder = Base64.getEncoder();
//        final String text = "字串文字";
//        final byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
////编码
//        final String encodedText = encoder.encodeToString(textByte);
//        System.out.println(encodedText);
////解码
//        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
//
//        final Base64.Decoder decoder = Base64.getDecoder();
//        final Base64.Encoder encoder = Base64.getEncoder();
//        final String text = "字串文字";
//        final byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
////编码
//        final String encodedText = encoder.encodeToString(textByte);
//        System.out.println(encodedText);
////解码
//        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));

//
//        String publicKey="MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgUknV1kq2rTemVbRO" +
//                "1ZjbQe3An8ptqnU57GBbZ8puagagCgYIKoEcz1UBgi2hRANCAATgoLuTDQFO9dCa" +
//                "Y4x7/uJTkQlziebgTHxCulhBKtHopkqloKChVR8HUU/e6Qh9Q+Acb+gq/P4uJRyv" +
//                "sn/9Fl73";
//
//        byte[] pk= Base64.decode(publicKey);
//        System.out.println(Hex.toHexString(pk));
//
//
//
//
//        byte[] pvk=Hex.decode("049e51d3564e707ae4eea3b88b61df38468f65b54732a60aff8b92a56821c8b275092471d854e9ca1aa3cc2a39b9634e74162cc178ab9655aedb7e26822cd838b6");
//
//        String s = new String(Base64.encode(pvk));
//
//        System.out.println(s);
        Security.addProvider(new BouncyCastleProvider());
        String sk="MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgTchUuHEAckzfS16v\n" +
               "8hz4Rt9G+41OifbzAr9jM+JGxiygCgYIKoEcz1UBgi2hRANCAASDw0oz+lq1H8QM\n" +
               "8YaZSikOsCdbLR+sUd+hpzvDF1wmS3zVNqtKnTRzD3bVgR4AFljtBVmbXNmJdrno\n" +
               "C8r6EmyE";
        KeyFactory keyf = keyf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(sk));
        BCECPrivateKey priKey = (BCECPrivateKey)keyf.generatePrivate(priPKCS8);
        System.out.println("16进制私钥:" + priKey.getD().toString(16));

        String cert_path = MspValidateTest.class.getResource("/szca/testsm2.pem").getPath();
        byte[] idBytes = FileUtils.readFileBytes(cert_path);
        Certificate certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
        byte[] pb=  certificate.getTBSCertificate().getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        byte[] publickey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

        System.out.println(certificate.getSubject());
        System.out.println("tbs 公钥"+Hex.toHexString(pb));
        System.out.println("公钥："+Hex.toHexString(publickey));
        System.out.println("公钥长度："+publickey.length);


        SM2 sm2=new SM2();
        byte[] v=sm2.sign(priKey.getD().toByteArray(),"123".getBytes());
        System.out.println(sm2.verify(publickey, v, "123".getBytes()));

    }
}