package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.intfs.opts.IRngOpts;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11KeyData;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaEncrypterOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaSignOpts;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11HashOpts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class PKCS11CspTest {

    int secLevel= 5;
    String hashFamily="MD";
    String keyStorePath=null;
    String Library=null;
    String Label=null;
    String SN=null;
    String Pin=null;
    boolean bSensitive=false;
    boolean bSoftVerify=false;
    PKCS11Lib findlib = null;
    PKCS11Config findconf = null;
    IPKCS11FactoryOpts iPKCS11FactoryOpts = null;
    PKCS11CspFactory cspfactory = null;
    ICsp csp = null;
    byte[] msg = "www.ftsafe.com".getBytes();
    PKCS11CspLog cspLog = new PKCS11CspLog();
    @Before
    public void before() {
        try {
            findlib = new PKCS11Lib(Library, Label, SN, Pin);
            findconf = new PKCS11Config(secLevel, hashFamily, bSoftVerify, bSensitive);
            iPKCS11FactoryOpts = new PKCS11FactoryOpts(findlib, findconf);
            cspfactory = new PKCS11CspFactory();
            csp = cspfactory.getCsp(iPKCS11FactoryOpts);
        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyGen() {

        try {
            IKeyGenOpts opts = new EcdsaOpts.ECDSA256KeyGenOpts(false);
            IKey myKey = csp.keyGen(opts);

            Assert.assertNotNull(myKey);
        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyDeriv() {
        try {
            IKeyGenOpts opts = new EcdsaOpts.ECDSA256KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mynewkey = csp.keyDeriv(mykey, new EcdsaOpts.EcdsaHardPriKeyOpts(false));

            Assert.assertNotNull(mynewkey);
        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyImport() {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair key = keyGen.generateKeyPair();
            cspLog.setLogMsg("PrivateKey："+key.getPrivate().getEncoded(), cspLog.LEVEL_INFO, PKCS11CspTest.class);
            cspLog.setLogMsg("PublicKey："+key.getPublic().getEncoded(), cspLog.LEVEL_INFO, PKCS11CspTest.class);

            PKCS11KeyData keyraw = new PKCS11KeyData();
            keyraw.setRawPri(key.getPrivate().getEncoded());
            keyraw.setRawPub(key.getPublic().getEncoded());

            IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
            IKey mykey = csp.keyImport(keyraw, opts);
            Assert.assertNotNull(mykey);

        } catch (JulongChainException  e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair key = keyGen.generateKeyPair();

            PKCS11KeyData keyraw = new PKCS11KeyData();
            keyraw.setRawPri(key.getPrivate().getEncoded());
            keyraw.setRawPub(key.getPublic().getEncoded());

            IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
            IKey mykey = csp.keyImport(keyraw, opts);
            cspLog.setLogMsg("PublicKey："+key.getPublic().getEncoded(), cspLog.LEVEL_INFO, PKCS11CspTest.class);
            cspLog.setLogMsg("PrivateKey："+key.getPrivate().getEncoded(), cspLog.LEVEL_INFO, PKCS11CspTest.class);
            Assert.assertNotNull(csp.getKey(mykey.ski()));

        } catch (JulongChainException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHash() {
        try {
            IHashOpts opts = new PKCS11HashOpts.MD2Opts();
            byte[] hash = csp.hash(msg, opts);
            cspLog.setLogMsg("hashValue："+hash, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            Assert.assertNotNull(hash);

        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetHash() {

        try {
            IHashOpts opts = new PKCS11HashOpts.SHA1Opts();
            IHash test = csp.getHash(opts);

            Assert.assertNotNull(test);

            String tt = "1234567890";
            String tt1 = "1111";
            int len = test.write(tt.getBytes());
            cspLog.setLogMsg("[[ getHashLength ]] {1234567890} ："+len, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            Assert.assertEquals(len,10);
            len = test.write(tt1.getBytes());
            cspLog.setLogMsg("[[ getHashLength ]] {1234567890+1111} ："+len, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            Assert.assertEquals(len,14);

        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSign() {
        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey myKey = csp.keyGen(opts);

            String input2 = "Hello world";
            PKCS11HashOpts.SHA1Opts hashOptSha1 = new PKCS11HashOpts.SHA1Opts();
            byte[] byteHash = csp.hash(input2.getBytes(), hashOptSha1);
            cspLog.setLogMsg("[[ byteHash ]] {Hello world} ："+byteHash, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            byte[] signature = csp.sign(myKey, byteHash, RsaSignOpts.SHA1);

            Assert.assertNotNull(signature);

        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVerify() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey myKey = csp.keyGen(opts);

            String input2 = "Hello world";
            PKCS11HashOpts.SHA1Opts hashOptSha1 = new PKCS11HashOpts.SHA1Opts();
            byte[] byteHash = csp.hash(input2.getBytes(), hashOptSha1);

            byte[] signature = csp.sign(myKey, byteHash, RsaSignOpts.SHA1);
            cspLog.setLogMsg("[[ sign ]] {Hello world}："+byteHash, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            boolean bvEriFy = csp.verify(myKey, signature, byteHash, RsaSignOpts.SHA1);

            Assert.assertTrue(bvEriFy);

        } catch (JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEncrypt() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey myKey = csp.keyGen(opts);

            String input2 = "Hello JuLongChain";

            byte[] encryptText = csp.encrypt(myKey, input2.getBytes(), RsaEncrypterOpts.PKCS1_Pub);
            cspLog.setLogMsg("[[ encrypt ]] {Hello JuLongChain}："+encryptText, cspLog.LEVEL_INFO, PKCS11CspTest.class);
            Assert.assertNotNull(encryptText);

        }catch(JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDecrypt() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey myKey = csp.keyGen(opts);
            String input2 = "Hello JuLongChain";

            byte[] encryptText = csp.encrypt(myKey, input2.getBytes(), RsaEncrypterOpts.PKCS1_Pub);
            byte[] decryptData = csp.decrypt(myKey, encryptText, RsaDecrypterOpts.PKCS1_Prv);
            cspLog.setLogMsg("[[ decrypt ]] {Hello JuLongChain}："+encryptText, cspLog.LEVEL_INFO, PKCS11CspTest.class);

            Assert.assertNotNull(decryptData);

        }catch(JulongChainException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestRng() {

        int[] randomLen = {16, 240, 1024};
        for(int index = 0; index < randomLen.length; index ++) {
            try {
                int len = randomLen[index];
                IRngOpts rngOpts = new IRngOpts() {
                    @Override
                    public String getAlgorithm() {
                        return null;
                    }
                };
                byte[] random = csp.rng(len, rngOpts);
                Assert.assertNotNull(random);
                if(null != random) {
                    cspLog.setLogMsg("[[ random data ]] ："+Convert.bytesToHexString(random), cspLog.LEVEL_INFO, PKCS11CspTest.class);
                } else {
                    cspLog.setLogMsg("[[ random data null !!!]] ：", cspLog.LEVEL_ERROR, PKCS11CspTest.class);
                }
            } catch (JulongChainException e) {
                e.printStackTrace();
            }
        }
    }
}