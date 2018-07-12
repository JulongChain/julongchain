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

import org.bcia.julongchain.common.tools.cryptogen.Util;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

/**
 * 实现 ECParameterSpec，加入 SM2 支持
 *
 * @author chenhao
 * @date 2018/4/18
 * @company Excelsecu
 */
class SM2NamedCurve extends ECParameterSpec {
    private static final SM2NamedCurve SM2_NAMED_CURVE = genSM2NamedCurve();
    private final String name;
    private final String oid;
    private final byte[] encoded;

    public static SM2NamedCurve getSM2NamedCurve() {
        return SM2_NAMED_CURVE;
    }

    private static SM2NamedCurve genSM2NamedCurve() {
        ECParameterSpec sm2ParameterSpec = Util.getECParameterSpec();
        return new SM2NamedCurve("sm2p256v1", "1.2.156.10197.1.301", sm2ParameterSpec.getCurve(),
                sm2ParameterSpec.getGenerator(),
                sm2ParameterSpec.getOrder(),
                sm2ParameterSpec.getCofactor());
    }

    private SM2NamedCurve(String name, String oid, EllipticCurve ellipticCurve, ECPoint ecPoint, BigInteger n, int h) {
        super(ellipticCurve, ecPoint, n, h);
        this.name = name;
        this.oid = oid;
        DerOutputStream der = new DerOutputStream();

        try {
            der.putOID(new ObjectIdentifier(oid));
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e);
        }

        this.encoded = der.toByteArray();
    }

    String getName() {
        return this.name;
    }

    byte[] getEncoded() {
        return this.encoded.clone();
    }

    String getObjectId() {
        return this.oid;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.oid + ")";
    }
}
