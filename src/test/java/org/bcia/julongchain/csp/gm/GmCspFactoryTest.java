package org.bcia.julongchain.csp.gm;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.GmCspFactory;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3HashOpts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;


/**
 * Copyright BCIA. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author zhanglin
 * @purpose Define the interface, DecrypterOpts
 * @date 2018-01-25
 * @company Dingxuan
 */

public class GmCspFactoryTest {

    @Test
    public void getName() {
        GmCspFactory factory=new GmCspFactory();
        String name=factory.getName();
        Assert.assertEquals(name,"GM");
    }

    @Test
    public void getCsp() {
        GmCspFactory factory = new GmCspFactory();
       // IFactoryOpts opts = new GmFactoryOpts(256, "SM3", true, true, "",true);
        IFactoryOpts opts = new GmFactoryOpts();
        ICsp csp = factory.getCsp(opts);
        Assert.assertNotNull(csp);
        try {
            String message = "abc";
            byte[] msg=message.getBytes("ASCII");
            String encodedHexMsg= Hex.toHexString(msg);
            System.out.println("To Hash Message:"+encodedHexMsg);
            String expectedResult = "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
            byte[] expectedHashMsg=Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = csp.hash(msg, hashOpts);
            String encodedHexDigests=Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests,expectedHashMsg);
        } catch (JulongChainException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}