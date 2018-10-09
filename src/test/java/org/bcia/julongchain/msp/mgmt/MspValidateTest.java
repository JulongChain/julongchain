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
 * msp验证测试
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

        String sk_path = MspValidateTest.class.getResource("/sk-dxtest_sk").getPath();

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
    public void certTest() throws IOException {
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgTchUuHEAckzfS16v\n" +
                "8hz4Rt9G+41OifbzAr9jM+JGxiygCgYIKoEcz1UBgi2hRANCAASDw0oz+lq1H8QM\n" +
                "8YaZSikOsCdbLR+sUd+hpzvDF1wmS3zVNqtKnTRzD3bVgR4AFljtBVmbXNmJdrno\n" +
                "C8r6EmyE";
        byte[] sk = org.bouncycastle.util.encoders.Base64.decode(privateKey);

        System.out.println("私钥长度" + sk.length);
        System.out.println(Hex.toHexString(sk));
        String cert_path = MspValidateTest.class.getResource("/szca/testsm2.pem").getPath();
        byte[] idBytes = FileUtils.readFileBytes(cert_path);
        Certificate certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
        byte[] publickey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

        System.out.println(certificate.getSubject());
        System.out.println("公钥：" + Hex.toHexString(publickey));
        System.out.println("公钥长度：" + publickey.length);
    }


    @Test
    public void base64() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CryptoException, CspException {
        Security.addProvider(new BouncyCastleProvider());
        String sk = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgTchUuHEAckzfS16v\n" +
                "8hz4Rt9G+41OifbzAr9jM+JGxiygCgYIKoEcz1UBgi2hRANCAASDw0oz+lq1H8QM\n" +
                "8YaZSikOsCdbLR+sUd+hpzvDF1wmS3zVNqtKnTRzD3bVgR4AFljtBVmbXNmJdrno\n" +
                "C8r6EmyE";
        KeyFactory keyf = keyf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(sk));
        BCECPrivateKey priKey = (BCECPrivateKey) keyf.generatePrivate(priPKCS8);
        System.out.println("16进制私钥:" + priKey.getD().toString(16));

        String cert_path = MspValidateTest.class.getResource("/szca/testsm2.pem").getPath();
        byte[] idBytes = FileUtils.readFileBytes(cert_path);
        Certificate certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
        byte[] pb = certificate.getTBSCertificate().getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        byte[] publickey = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

        System.out.println(certificate.getSubject());
        System.out.println("tbs 公钥" + Hex.toHexString(pb));
        System.out.println("公钥：" + Hex.toHexString(publickey));
        System.out.println("公钥长度：" + publickey.length);


        SM2 sm2 = new SM2();
        byte[] v = sm2.sign(priKey.getD().toByteArray(), "123".getBytes());
        System.out.println(sm2.verify(publickey, v, "123".getBytes()));

    }
}