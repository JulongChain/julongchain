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
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2SignerOpts;
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
 * 重写 X509CertImpl，加入 SM2 支持
 *
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
            this.signature = CspHelper.getCsp().sign(privateKey, digest, new SM2SignerOpts());

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
