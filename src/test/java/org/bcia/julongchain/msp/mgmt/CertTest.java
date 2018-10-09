/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.msp.util.LoadLocalMspFiles;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 国密证书测试
 *
 * @author zhangmingyang
 * @date 2018/07/12
 * @company Dingxuan
 */
public class CertTest {
    private SM2 sm2;

    @Before
    public void setup() {
        sm2 = new SM2();
    }

    @Test
    public void cryptogenCertTest() throws IOException, CspException {
        String skPath = "msp/keystore";
        String signcerts = "msp/signcerts";
        String testData = "this is test data";
        //签名证书
        List<byte[]> signCerts = new LoadLocalMspFiles().getCertFromDir(signcerts);
        Certificate signCert = Certificate.getInstance(
                new PemReader(new InputStreamReader(new ByteArrayInputStream(signCerts.get(0)))).readPemObject().getContent());
        byte[] publickey = signCert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        List<byte[]> sks = new LoadLocalMspFiles().getSkFromDir(skPath);
        byte[] sign = sm2.sign(sks.get(0), testData.getBytes());
        boolean result = sm2.verify(publickey, sign, testData.getBytes());
        assertEquals(true, result);
    }


    @Test
    public void szcaCertTest() throws Exception {
        String skPath = "/szca/sk-test";
        String certPath = "/szca/signcert.pem";
        String testData = "this is test data";
        String privateKeyPath = CertTest.class.getResource(skPath).getPath();
        String signCertPath = CertTest.class.getResource(certPath).getPath();
        byte[] sk = CryptoUtil.getPrivateKey(privateKeyPath);
        byte[] certBytes = FileUtils.readFileBytes(signCertPath);
        Certificate signCert = Certificate.getInstance(
                new PemReader(new InputStreamReader(new ByteArrayInputStream(certBytes))).readPemObject().getContent());
        byte[] pk = signCert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        byte[] sign = sm2.sign(sk, testData.getBytes());
        boolean result = sm2.verify(pk, sign, testData.getBytes());
        assertEquals(true, result);
    }
}
