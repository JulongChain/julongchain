package org.bcia.julongchain.csp.gmt0016;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.ICspFactory;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspFactory;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016Csp;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016Lib;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SM1Opts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SM4Opts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SSF33Opts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMRngOpts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IEncrypterOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test methods to encrypt component GMT0016
 *
 * @author zhaoxiaobo
 * @date 2018/08/26
 * @company FEITIAN
 */
public class GMT0016CpsEncryptTest {
	private ICsp csp;

    @Before
    public void before() throws JulongChainException{
        System.out.println("==========class GMT0016Cps function encrypt test start==========");

        GMT0016Lib gmt0016Lib = new GMT0016Lib("/home/bcia/libes_3000gm.so","ePass3000GM","0955381103160217","rockey","123456","ENTERSAFE-ESPK");
        IFactoryOpts factoryOpts = new GMT0016FactoryOpts(gmt0016Lib);
        Assert.assertNotNull(factoryOpts);
        ICspFactory cspFactory = new GMT0016CspFactory();
        Assert.assertNotNull(cspFactory);
        csp = cspFactory.getCsp(factoryOpts);
        Assert.assertNotNull(csp);
    }

    @Test
    public void testEncryptBySM1() throws JulongChainException{
        System.out.println("==========test function encrypt and IEncrypterOpts instance SM1Opts.SM1EncrypterOpts==========");
        //1:get IKey
        IKeyGenOpts sm1ECBKeyGenOpts = new SM1Opts.SM1ECBKeyGenOpts(true);
        IKey sm1ECBKey = csp.keyGen(sm1ECBKeyGenOpts);
        Assert.assertNotNull(sm1ECBKey);
        //2:get IEncrypterOpts
        byte[] iv = csp.rng(16, new GMRngOpts());
        Assert.assertNotNull(iv);
        IEncrypterOpts sm1Encrypt = new SM1Opts.SM1EncrypterOpts(iv,0);
        Assert.assertNotNull(sm1Encrypt);
        //3:excute encrypt
        String text = "JulongChain";
        byte [] plaintext = text.getBytes();
        byte [] encryptByte = csp.encrypt(sm1ECBKey,plaintext,sm1Encrypt);
        Assert.assertNotNull(encryptByte);
    }

    @Test
    public void testEncryptBySM4() throws JulongChainException{
        System.out.println("==========test function encrypt and IEncrypterOpts instance SM4Opts.SM4EncrypterOpts==========");
        //1:get IKey
        IKeyGenOpts sm4ECBKeyGenOpts = new SM4Opts.SM4ECBKeyGenOpts(false);
        IKey sm4ECBKey = csp.keyGen(sm4ECBKeyGenOpts);
        Assert.assertNotNull(sm4ECBKey);
        //2:get IEncrypterOpts
        byte[] iv = csp.rng(16, new GMRngOpts());
        Assert.assertNotNull(iv);
        IEncrypterOpts sm4Encrypt = new SM4Opts.SM4EncrypterOpts(iv,0);
        Assert.assertNotNull(sm4Encrypt);
        //3:excute encrypt
        String text = "JulongChain";
        byte [] plaintext = text.getBytes();
        byte [] encryptByte = csp.encrypt(sm4ECBKey,plaintext,sm4Encrypt);
        Assert.assertNotNull(encryptByte);
    }

    @Test
    public void testEncryptBySSF33() throws JulongChainException{
        System.out.println("==========test function encrypt and IEncrypterOpts instance SSF33Opts.SSF33EncrypterOpts==========");
        //1:get IKey
        IKeyGenOpts ssf33ECBKeyGenOpts = new SSF33Opts.SSF33ECBKeyGenOpts(false);
        IKey ssf33ECBKey = csp.keyGen(ssf33ECBKeyGenOpts);
        Assert.assertNotNull(ssf33ECBKey);
        //2:get IEncrypterOpts
        byte[] iv = csp.rng(16, new GMRngOpts());
        Assert.assertNotNull(iv);
        IEncrypterOpts ssf33Encrypt = new SSF33Opts.SSF33EncrypterOpts(iv,0);
        Assert.assertNotNull(ssf33Encrypt);
        //3:excute encrypt
        String text = "JulongChain";
        byte [] plaintext = text.getBytes();
        byte [] encryptByte = csp.encrypt(ssf33ECBKey,plaintext,ssf33Encrypt);
        Assert.assertNotNull(encryptByte);
    }



    @After
    public void after()  throws JulongChainException{
        System.out.println("==========test end==========");
        ((IGMT0016Csp)csp).finalized();
    }
}
