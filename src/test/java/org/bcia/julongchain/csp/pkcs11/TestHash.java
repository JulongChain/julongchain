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
package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.julongchain.csp.pkcs11.util.PKCS11HashOpts;
import org.junit.Before;
import org.junit.Test;

public class TestHash {
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
			IHashOpts opts = new PKCS11HashOpts.MD2Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test2() {
		try {
			IHashOpts opts = new PKCS11HashOpts.MD5Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test3() {
		try {
			IHashOpts opts = new PKCS11HashOpts.SHA1Opts();
			byte[] hash = csp.hash(msg, opts);
			IHash test = csp.getHash(opts);
			String tt = "1234567890";
			String tt1 = "helo";
			String tt2 = "word";
			int len = test.write(tt.getBytes());
			len = test.write(tt1.getBytes());
			len = test.write(tt2.getBytes());
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test4() {
		try {
			IHashOpts opts = new PKCS11HashOpts.SHA256Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test5() {
		try {
			IHashOpts opts = new PKCS11HashOpts.SHA384Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test6() {
		try {
			IHashOpts opts = new PKCS11HashOpts.SHA3_256Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test7() {
		try {
			IHashOpts opts = new PKCS11HashOpts.SHA3_384Opts();
			byte[] hash = csp.hash(msg, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}