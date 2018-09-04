package org.bcia.julongchain.csp.gm.ftsafe;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.ICspFactory;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspFactory;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016Csp;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016Lib;
import org.bcia.julongchain.csp.gmt0016.ftsafe.rsa.RSAOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SM1Opts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SM4Opts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry.SSF33Opts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test methods to keyGen component GMT0016
 *
 * @author zhaoxiaobo
 * @date 2018/08/26
 * @company FEITIAN
 */
public class GMT0016CspKeyGenTest {
	private ICsp csp;

    @Before
    public void before() throws JulongChainException{
        System.out.println("==========class GMT0016Cps function keyGen test start==========");
        GMT0016Lib gmt0016Lib = new GMT0016Lib("/home/bcia/libes_3000gm.so","ePass3000GM","0955381103160217","rockey","123456","ENTERSAFE-ESPK");
        IFactoryOpts factoryOpts = new GMT0016FactoryOpts(gmt0016Lib);
        Assert.assertNotNull(factoryOpts);
        ICspFactory cspFactory = new GMT0016CspFactory();
        Assert.assertNotNull(cspFactory);
        csp = cspFactory.getCsp(factoryOpts);
        Assert.assertNotNull(csp);
    }

    @Test
    public void testKeyGenByRSA1024()  throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance RSAOpts.RSA1024KeyGenOpts==========");
        //1:RSA1024KeyGenOpts
        IKeyGenOpts rsa1024keyGenOpts = new RSAOpts.RSA1024KeyGenOpts(false);
        IKey rsa1024Key = csp.keyGen(rsa1024keyGenOpts);
        Assert.assertNotNull(rsa1024Key);
    }

    @Test
    public void testKeyGenByRSA2048()  throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance RSAOpts.RSA2048KeyGenOpts==========");
        //2:RSA2048KeyGenOpts
        IKeyGenOpts rsa2048keyGenOpts = new RSAOpts.RSA2048KeyGenOpts(false);
        IKey rsa2048Key = csp.keyGen(rsa2048keyGenOpts);
        Assert.assertNotNull(rsa2048Key);
    }

    @Test
    public void testKeyGenByECC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance ECCOpts.ECCKeyGenOpts==========");
        //3:ECCKeyGenOpts
        IKeyGenOpts eccKeyGenOpts = new ECCOpts.ECCKeyGenOpts(false);
        IKey eccKey = csp.keyGen(eccKeyGenOpts);
        Assert.assertNotNull(eccKey);
    }

    @Test
    public void testKeyGenBySM1ECB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM1Opts.SM1ECBKeyGenOpts==========");
        //4.1.1:SM1Opts.SM1ECBKeyGenOpts:128 192 256？
        IKeyGenOpts sm1ECBKeyGenOpts = new SM1Opts.SM1ECBKeyGenOpts(false);
        IKey sm1ECBKey = csp.keyGen(sm1ECBKeyGenOpts);
        Assert.assertNotNull(sm1ECBKey);
    }

    @Test
    public void testKeyGenBySM1CBC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM1Opts.SM1CBCKeyGenOpts==========");
        //4.1.2:SM1Opts.SM1ECBKeyGenOpts:128 192 256？
        IKeyGenOpts sm1CBCKeyGenOpts = new SM1Opts.SM1CBCKeyGenOpts(false);
        IKey sm1CBCKey = csp.keyGen(sm1CBCKeyGenOpts);
        Assert.assertNotNull(sm1CBCKey);
    }

    @Test
    public void testKeyGenBySM1CFB()  throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM1Opts.SM1CFBKeyGenOpts==========");
        //4.1.3:SM1Opts.SM1CFBKeyGenOpts:128 192 256？
        IKeyGenOpts sm1CFBKeyGenOpts = new SM1Opts.SM1CFBKeyGenOpts(false);
        IKey sm1CFBKey = csp.keyGen(sm1CFBKeyGenOpts);
        Assert.assertNotNull(sm1CFBKey);
    }

    @Test
    public void testKeyGenBySM1OFB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM1Opts.SM1OFBKeyGenOpts==========");
        //4.1.4:SM1Opts.SM1OFBKeyGenOpts:128 192 256？
        IKeyGenOpts sm1OFBKeyGenOpts = new SM1Opts.SM1OFBKeyGenOpts(false);
        IKey sm1OFBKey = csp.keyGen(sm1OFBKeyGenOpts);
        Assert.assertNotNull(sm1OFBKey);
    }

    @Test
    public void testKeyGenBySM1MAC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM1Opts.SM1MACKeyGenOpts==========");
        //4.1.5:SM1Opts.SM1MACKeyGenOpts:128 192 256？
        IKeyGenOpts sm1MACKeyGenOpts = new SM1Opts.SM1MACKeyGenOpts(false);
        IKey sm1MACKey = csp.keyGen(sm1MACKeyGenOpts);
        Assert.assertNotNull(sm1MACKey);
    }

    @Test
    public void testKeyGenBySM4ECB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM4Opts.SM4ECBKeyGenOpts==========");
        //4.2.1:SM4Opts.SM4ECBKeyGenOpts:128 192 256？
        IKeyGenOpts sm4ECBKeyGenOpts = new SM4Opts.SM4ECBKeyGenOpts(false);
        IKey sm4ECBKey = csp.keyGen(sm4ECBKeyGenOpts);
        Assert.assertNotNull(sm4ECBKey);
    }

    @Test
    public void testKeyGenBySM4CBC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM4Opts.SM4CBCKeyGenOpts==========");
        //4.2.2:SM4Opts.SM4ECBKeyGenOpts:128 192 256？
        IKeyGenOpts sm4CBCKeyGenOpts = new SM4Opts.SM4CBCKeyGenOpts(false);
        IKey sm4CBCKey = csp.keyGen(sm4CBCKeyGenOpts);
        Assert.assertNotNull(sm4CBCKey);
    }

    @Test
    public void testKeyGenBySM4CFB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM4Opts.SM4CFBKeyGenOpts==========");
        //4.2.3:SM4Opts.SM4CFBKeyGenOpts:128 192 256？
        IKeyGenOpts sm4CFBKeyGenOpts = new SM4Opts.SM4CFBKeyGenOpts(false);
        IKey sm4CFBKey = csp.keyGen(sm4CFBKeyGenOpts);
        Assert.assertNotNull(sm4CFBKey);
    }

    @Test
    public void testKeyGenBySM4OFB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM4Opts.SM4OFBKeyGenOpts==========");
        //4.2.4:SM4Opts.SM4OFBKeyGenOpts:128 192 256？
        IKeyGenOpts sm4OFBKeyGenOpts = new SM4Opts.SM4OFBKeyGenOpts(false);
        IKey sm4OFBKey = csp.keyGen(sm4OFBKeyGenOpts);
        Assert.assertNotNull(sm4OFBKey);
    }

    @Test
    public void testKeyGenBySM4MAC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SM4Opts.SM4MACKeyGenOpts==========");
        //4.2.5:SM4Opts.SM4MACKeyGenOpts:128 192 256？
        IKeyGenOpts sm4MACKeyGenOpts = new SM4Opts.SM4MACKeyGenOpts(false);
        IKey sm4MACKey = csp.keyGen(sm4MACKeyGenOpts);
        Assert.assertNotNull(sm4MACKey);
    }

    @Test
    public void testKeyGenBySSF33ECB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SSF33Opts.SSF33ECBKeyGenOpts==========");
        //4.3.1:SSF33Opts.SSF33ECBKeyGenOpts:128 192 256？
        IKeyGenOpts ssf33ECBKeyGenOpts = new SSF33Opts.SSF33ECBKeyGenOpts(false);
        IKey ssf33ECBKey = csp.keyGen(ssf33ECBKeyGenOpts);
        Assert.assertNotNull(ssf33ECBKey);
    }

    @Test
    public void testKeyGenBySSF33CBC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SSF33Opts.SSF33CBCKeyGenOpts==========");
        //4.3.2:SSF33Opts.SSF33ECBKeyGenOpts:128 192 256？
        IKeyGenOpts ssf33CBCKeyGenOpts = new SSF33Opts.SSF33CBCKeyGenOpts(false);
        IKey ssf33CBCKey = csp.keyGen(ssf33CBCKeyGenOpts);
        Assert.assertNotNull(ssf33CBCKey);
    }

    @Test
    public void testKeyGenBySSF33CFB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SSF33Opts.SSF33CFBKeyGenOpts==========");
        //4.3.3:SSF33Opts.SSF33CFBKeyGenOpts:128 192 256？
        IKeyGenOpts ssf33CFBKeyGenOpts = new SSF33Opts.SSF33CFBKeyGenOpts(false);
        IKey ssf33CFBKey = csp.keyGen(ssf33CFBKeyGenOpts);
        Assert.assertNotNull(ssf33CFBKey);
    }

    @Test
    public void testKeyGenBySSF33OFB() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SSF33Opts.SSF33OFBKeyGenOpts==========");
        //4.3.4:SSF33Opts.SSF33OFBKeyGenOpts:128 192 256？
        IKeyGenOpts ssf33OFBKeyGenOpts = new SSF33Opts.SSF33OFBKeyGenOpts(false);
        IKey ssf33OFBKey = csp.keyGen(ssf33OFBKeyGenOpts);
        Assert.assertNotNull(ssf33OFBKey);
    }

    @Test
    public void testKeyGenBySSF33MAC() throws JulongChainException{
        System.out.println("==========test function keyGen and IKeyGenOpts instance SSF33Opts.SSF33MACKeyGenOpts==========");
        //4.3.5:SSF33Opts.SSF33MACKeyGenOpts:128 192 256？
        IKeyGenOpts ssf33MACKeyGenOpts = new SSF33Opts.SSF33MACKeyGenOpts(false);
        IKey ssf33MACKey = csp.keyGen(ssf33MACKeyGenOpts);
        Assert.assertNotNull(ssf33MACKey);
    }

    @After
    public void after() throws JulongChainException{
        System.out.println("==========test end==========");
        ((IGMT0016Csp)csp).finalized();
    }
}
