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
package org.bcia.julongchain.common.tools.cryptogen.sm2cert;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.CspHelper;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3HashOpts;
import org.bcia.julongchain.csp.intfs.IKey;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

/**
 * @author chenhao
 * @date 2018/4/17
 * @company Excelsecu
 */
public class SM2X509CertImpl extends X509CertImpl {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SM2X509CertImpl.class);

    public static final ObjectIdentifier SM3_WITH_SM2 = ObjectIdentifier.newInternal(new int[] {1, 2, 156, 10197, 1, 501});
    public static final AlgorithmId SM3_WITH_SM2_ALGORITHM_ID = new AlgorithmId(SM3_WITH_SM2);

    public SM2X509CertImpl(X509CertInfo info) {
        super(info);
    }

    public void sm2Sign(IKey privateKey, AlgorithmId algorithmId) throws JavaChainException, CertificateException {
        if (isReadOnly()) {
            throw new CertificateEncodingException("cannot over-write existing certificate");
        }

        try {
            this.algId = algorithmId;
            DerOutputStream signedCert = new DerOutputStream();
            DerOutputStream signedData = new DerOutputStream();
            this.info.encode(signedData);
            byte[] signedBytes = signedData.toByteArray();
            this.algId.encode(signedData);

            byte[] digest = CspHelper.getCsp().hash(signedBytes, new SM3HashOpts());
            // TODO wait for GmCsp fix
            this.signature = new byte[] {
                    (byte)0x30, (byte)0x45,
                    (byte)0x02, (byte)0x21, (byte)0x00, (byte)0xc4, (byte)0xcb, (byte)0x80, (byte)0xb8, (byte)0x35, (byte)0xbc, (byte)0x9b, (byte)0x21, (byte)0x27, (byte)0xc9, (byte)0x80, (byte)0xc6, (byte)0x9d,
                    (byte)0x95, (byte)0xa0, (byte)0xa3, (byte)0x54, (byte)0x4e, (byte)0xd4, (byte)0xb5, (byte)0xad, (byte)0x29, (byte)0x2c, (byte)0xde, (byte)0x07, (byte)0x01, (byte)0xc3, (byte)0xdb, (byte)0xf0,
                    (byte)0x07, (byte)0x40, (byte)0xd5,
                    (byte)0x02, (byte)0x20, (byte)0x08, (byte)0xe5, (byte)0xbe, (byte)0xaf, (byte)0xef, (byte)0xda, (byte)0x2c, (byte)0x17, (byte)0xc3, (byte)0x51, (byte)0xda, (byte)0x17, (byte)0x64, (byte)0x5e,
                    (byte)0x84, (byte)0xec, (byte)0x5a, (byte)0x62, (byte)0x03, (byte)0x87, (byte)0xf0, (byte)0xc1, (byte)0xa4, (byte)0x5b, (byte)0x05, (byte)0x39, (byte)0xaa, (byte)0x6a, (byte)0xd4, (byte)0x3a,
                    (byte)0x47, (byte)0x27
            };
//            this.signature = CspHelper.getCsp().sign(privateKey, digest, new SM2SignerOpts());

            signedData.putBitString(this.signature);
            signedCert.write((byte)48, signedData);
            setSignedCert(signedCert.toByteArray());
            setReadOnly(true);
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    private boolean isReadOnly() {
        try {
            Field field = X509CertImpl.class.getDeclaredField("readOnly");
            field.setAccessible(true);
            return field.getBoolean(this);
        } catch (Exception e) {
            log.warn("reflect get field readOnly failed: {}", e);
        }
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    private void setReadOnly(boolean readOnly) {
        try {
            Field field = X509CertImpl.class.getDeclaredField("readOnly");
            field.setAccessible(true);
            field.setBoolean(this, readOnly);
        } catch (Exception e) {
            log.warn("reflect set field readOnly failed: {}", e);
        }
    }

    private void setSignedCert(byte[] signedCert) {
        try {
            Field field = X509CertImpl.class.getDeclaredField("signedCert");
            field.setAccessible(true);
            field.set(this, signedCert);
        } catch (Exception e) {
            log.warn("reflect set field signedCert failed: {}", e);
        }
    }
}
