package org.bcia.julongchain.csp.gm.ftsafe;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.ICspFactory;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspFactory;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016Csp;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016Lib;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMRngOpts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.opts.IRngOpts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test methods to rng component GMT0016
 *
 * @author zhaoxiaobo
 * @date 2018/08/26
 * @company FEITIAN
 */
public class GMT0016CpsRngTest {
	private ICsp csp;

    @Before
    public void before() throws JulongChainException{
        System.out.println("==========class GMT0016Cps function rng test start==========");

        GMT0016Lib gmt0016Lib = new GMT0016Lib("/home/bcia/libes_3000gm.so","ePass3000GM","0955381103160217","rockey","123456","ENTERSAFE-ESPK");
        IFactoryOpts factoryOpts = new GMT0016FactoryOpts(gmt0016Lib);
        Assert.assertNotNull(factoryOpts);
        ICspFactory cspFactory = new GMT0016CspFactory();
        Assert.assertNotNull(cspFactory);
        csp = cspFactory.getCsp(factoryOpts);
        Assert.assertNotNull(csp);
    }

    @Test
    public void testRng() throws JulongChainException{
        System.out.println("==========test function rng==========");
        IRngOpts opts = new GMRngOpts();
        byte[] iv = csp.rng(16, opts);
        Assert.assertNotNull(iv);
    }


    @After
    public void after()  throws JulongChainException{
        System.out.println("==========test end==========");
        ((IGMT0016Csp)csp).finalized();
    }
}
