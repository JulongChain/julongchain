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
package org.bcia.javachain.consenter.util;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public class Expiration {
    public static Time expiresAt(byte[] identityBytes) {
        Certificate certificate = null;
        try {
            certificate = Certificate.getInstance(new PemReader(new InputStreamReader(new ByteArrayInputStream(identityBytes)))
                    .readPemObject().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (certificate==null){
            return null;
        }

        certificate.getEndDate();
        return certificate.getEndDate();

    }
}
