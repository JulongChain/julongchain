package org.bcia.julongchain.csp.gmt0016;

import org.apache.commons.codec.binary.Hex;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.ICspFactory;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspFactory;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016Csp;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016Lib;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMHashOpts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test methods to getHash component GMT0016
 *
 * @author zhaoxiaobo
 * @date 2018/08/26
 * @company FEITIAN
 */
public class GMT0016CpsGetHashTest {
	private ICsp csp;

    @Before
    public void before() throws JulongChainException{
        System.out.println("==========class GMT0016Cps function getHash test start==========");

        GMT0016Lib gmt0016Lib = new GMT0016Lib("/home/bcia/libes_3000gm.so","ePass3000GM","0955381103160217","rockey","123456","ENTERSAFE-ESPK");
        IFactoryOpts factoryOpts = new GMT0016FactoryOpts(gmt0016Lib);
        Assert.assertNotNull(factoryOpts);
        ICspFactory cspFactory = new GMT0016CspFactory();
        Assert.assertNotNull(cspFactory);
        csp = cspFactory.getCsp(factoryOpts);
        Assert.assertNotNull(csp);
    }


    @Test
    public void testGetHashByECC() throws JulongChainException{
        ECCOpts.ECCKeyGenOpts eccKeyGenOpts = new ECCOpts.ECCKeyGenOpts(false);
        IKey eccKey = csp.keyGen(eccKeyGenOpts);

        Assert.assertNotNull(eccKey);
        String pubID = "1234567812345678";

        GMHashOpts.SM3SignPreOpts sm3SignPreOpts = new GMHashOpts.SM3SignPreOpts();
        sm3SignPreOpts.setPubID(pubID);
        sm3SignPreOpts.setSki(eccKey.ski());

        IHash eccHash = csp.getHash(sm3SignPreOpts);
        Assert.assertNotNull(eccHash);
    }

    @Test
    public void testGetHashBySM3() throws JulongChainException{
        System.out.println("==========test function getHash and IHashOpts instance GMHashOpts.SM3HashOpts==========");

        IHashOpts sm3HashOpts = new GMHashOpts.SM3HashOpts();
        IHash sm3Hash = csp.getHash(sm3HashOpts);
        Assert.assertNotNull(sm3Hash);
    }

    @Test
    public void testGetHashBySHA1() throws JulongChainException{
        System.out.println("==========test function getHash and IHashOpts instance GMHashOpts.SHA1HashOpts==========");

        IHashOpts sha1HashOpts = new GMHashOpts.SHA1HashOpts();
        IHash sha1hash = csp.getHash(sha1HashOpts);
        Assert.assertNotNull(sha1hash);
    }

    @Test
    public void testGetHashBySHA256() throws JulongChainException{
        System.out.println("==========test function getHash and IHashOpts instance GMHashOpts.SHA256HashOpts==========");

        IHashOpts sha256HashOpts = new GMHashOpts.SHA256HashOpts();
        IHash sha256Hash = csp.getHash(sha256HashOpts);
        Assert.assertNotNull(sha256Hash);
    }


    @After
    public void after()  throws JulongChainException{
        System.out.println("==========test end==========");
        ((IGMT0016Csp)csp).finalized();
    }
}
