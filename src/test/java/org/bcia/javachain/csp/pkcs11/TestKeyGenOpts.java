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
package org.bcia.javachain.csp.pkcs11;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.javachain.csp.pkcs11.aes.AesOpts;
import org.bcia.javachain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.javachain.csp.pkcs11.rsa.RsaOpts;
import org.junit.Before;
import org.junit.Test;

/**
 * Class description
 *
 * @author
 * @date 5/27/18
 * @company FEITIAN
 */
public class TestKeyGenOpts {
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
    @Before
    public void before() {
        try {
            findlib = new PKCS11Lib(Library, Label, SN, Pin);
            findconf = new PKCS11Config(secLevel, hashFamily, bSoftVerify, bSensitive);
            iPKCS11FactoryOpts = new PKCS11FactoryOpts(findlib, findconf);
            cspfactory = new PKCS11CspFactory();
            csp = cspfactory.getCsp(iPKCS11FactoryOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test1() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA1024KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
            return;
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void test2() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA2048KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
/*    
 * the token no support
    @Test
    public void test3() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA3072KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            csp.getKey(mykey.ski());
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void test4() {

        try {
            IKeyGenOpts opts = new RsaOpts.RSA4096KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            csp.getKey(mykey.ski());
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 */   
    @Test
    public void test5() {

        try {
            IKeyGenOpts opts = new EcdsaOpts.ECDSA256KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
            return;
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void test6() {

        try {
            IKeyGenOpts opts = new EcdsaOpts.ECDSA192KeyGenOpts(false);
            IKey mykey = csp.keyGen(opts);
            IKey mykey1 = csp.getKey(mykey.ski());
            return;
        } catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void test7() {
    	
    	try {
    		//IKeyGenOpts opts = new AesOpts.AES128KeyGenOpts(false);
    		//IKey mykey = csp.keyGen(opts);
    		//IKey mykey1 = csp.getKey(mykey.ski());
    		byte[] ski = "31234567891264564894564".getBytes();
    		ski[0] = 0x03;
    		IKey mykey1 = csp.getKey(ski);
    		return;
    	} catch (JavaChainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
