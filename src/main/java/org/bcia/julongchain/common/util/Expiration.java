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
package org.bcia.julongchain.common.util;

import org.bcia.julongchain.common.deliver.IExpiresAtFunc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * 逾期函数,根据身份获取证书过期时间
 *
 * @author zhangmingyang
 * @Date: 2018/5/17
 * @company Dingxuan
 */
public class Expiration implements IExpiresAtFunc {
    public Expiration() {
    }

    @Override
    public Date expiresAt(byte[] identityBytes) {
        X509Certificate certificate = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream stream = new ByteArrayInputStream(identityBytes);
            certificate = (X509Certificate) cf.generateCertificate(stream);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        if (certificate == null) {
            return null;
        }
        return certificate.getNotBefore();

    }
}
