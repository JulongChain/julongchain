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
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test methods to getKey component GMT0016
 *
 * @author zhaoxiaobo
 * @date 2018/08/26
 * @company FEITIAN
 */
public class GMT0016CspGetKeyTest {
	private ICsp csp;

    @Before
    public void before() throws JulongChainException{
        System.out.println("==========class GMT0016Cps function getKey test start==========");
        GMT0016Lib gmt0016Lib = new GMT0016Lib("/home/bcia/libes_3000gm.so","ePass3000GM","0955381103160217","rockey","123456","ENTERSAFE-ESPK");
        IFactoryOpts factoryOpts = new GMT0016FactoryOpts(gmt0016Lib);
        Assert.assertNotNull(factoryOpts);
        ICspFactory cspFactory = new GMT0016CspFactory();
        Assert.assertNotNull(cspFactory);
        csp = cspFactory.getCsp(factoryOpts);
        Assert.assertNotNull(csp);
    }

    @Test
    public void testGetKeyByRSA1024()  throws JulongChainException{
        System.out.println("==========test function geyGen and IKeyGenOpts instance RSAOpts.RSA1024KeyGenOpts==========");
        IKeyGenOpts rsa1024keyGenOpts = new RSAOpts.RSA1024KeyGenOpts(false);
        IKey rsa1024Key = csp.keyGen(rsa1024keyGenOpts);
        Assert.assertNotNull(rsa1024Key);
        byte [] ski = rsa1024Key.ski();
        IKey rsa1024KeyCopy = csp.getKey(ski);
        Assert.assertNotNull(rsa1024KeyCopy);
        Assert.assertEquals(Hex.toHexString(rsa1024Key.toBytes()),Hex.toHexString(rsa1024KeyCopy.toBytes()));
    }

    @Test
    public void testGetKeyByRSA2048()  throws JulongChainException{
        System.out.println("==========test function geyGen and IKeyGenOpts instance RSAOpts.RSA2048KeyGenOpts==========");

        IKeyGenOpts rsa2048keyGenOpts = new RSAOpts.RSA2048KeyGenOpts(false);
        IKey rsa2048Key = csp.keyGen(rsa2048keyGenOpts);
        Assert.assertNotNull(rsa2048Key);
        byte [] ski = rsa2048Key.ski();
        IKey rsa2048KeyCopy = csp.getKey(ski);
        Assert.assertNotNull(rsa2048KeyCopy);
        Assert.assertEquals(Hex.toHexString(rsa2048Key.toBytes()),Hex.toHexString(rsa2048KeyCopy.toBytes()));
    }

    @Test
    public void testGetKeyByECC()  throws JulongChainException{
        System.out.println("==========test function geyGen and IKeyGenOpts instance ECCOpts.ECCKeyGenOpts==========");

        IKeyGenOpts ecckeyGenOpts = new ECCOpts.ECCKeyGenOpts(false);
        IKey eccKey = csp.keyGen(ecckeyGenOpts);
        Assert.assertNotNull(eccKey);
        byte [] ski = eccKey.ski();
        IKey eccKeyKeyCopy = csp.getKey(ski);
        Assert.assertNotNull(eccKeyKeyCopy);
        Assert.assertEquals(Hex.toHexString(eccKey.toBytes()),Hex.toHexString(eccKeyKeyCopy.toBytes()));
    }

    @After
    public void after()  throws JulongChainException{
        System.out.println("==========test end==========");
        ((IGMT0016Csp)csp).finalized();
    }
}
