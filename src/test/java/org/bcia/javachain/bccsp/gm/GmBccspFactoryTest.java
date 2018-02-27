package org.bcia.javachain.bccsp.gm;

import org.bcia.javachain.bccsp.factory.IFactoryOpts;
import org.bcia.javachain.bccsp.intfs.IBccsp;
import org.bcia.javachain.bccsp.intfs.opts.IHashOpts;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;


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

public class GmBccspFactoryTest {

    @Test
    public void getName() {
        GmBccspFactory factory=new GmBccspFactory();
        String name=factory.getName();
        Assert.assertEquals(name,"GM");
    }

    @Test
    public void getBccsp() {
        GmBccspFactory factory = new GmBccspFactory();
        IFactoryOpts opts = new GmFactoryOpts(256, "SM3", true, true, "");
        IBccsp bccsp = factory.getBccsp(opts);
        Assert.assertNotNull(bccsp);
        try {
            String message = "abc";
            byte[] msg=message.getBytes("ASCII");
            String encodedHexMsg= Hex.toHexString(msg);
            System.out.println("To Hash Message:"+encodedHexMsg);
            String expectedResult = "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0";
            byte[] expectedHashMsg=Hex.decode(expectedResult);
            IHashOpts hashOpts = new SM3HashOpts();
            byte[] digests = bccsp.hash(msg, hashOpts);
            String encodedHexDigests=Hex.toHexString(digests);
            System.out.println(encodedHexDigests);
            Assert.assertArrayEquals(digests,expectedHashMsg);
        } catch (JavaChainException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}