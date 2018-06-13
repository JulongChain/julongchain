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

import sun.security.util.ECKeySizeParameterSpec;

import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/**
 * @author chenhao
 * @date 2018/4/18
 * @company Excelsecu
 */
public final class SM2Parameters extends AlgorithmParametersSpi {
    private SM2NamedCurve namedCurve;

    static AlgorithmParameters getAlgorithmParameters(ECParameterSpec spec) throws InvalidKeyException {
        try {
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("SM2", "ExcelsecuSM2");
            algorithmParameters.init(spec);
            return algorithmParameters;
        } catch (GeneralSecurityException var2) {
            throw new InvalidKeyException("EC parameters error", var2);
        }
    }

    public SM2Parameters() {
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
        namedCurve = SM2NamedCurve.getSM2NamedCurve();
    }

    @Override
    protected void engineInit(byte[] var1) throws IOException {
        namedCurve = SM2NamedCurve.getSM2NamedCurve();
    }

    @Override
    protected void engineInit(byte[] var1, String var2) throws IOException {
        namedCurve = SM2NamedCurve.getSM2NamedCurve();
    }

    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> var1) throws InvalidParameterSpecException {
        if (var1.isAssignableFrom(ECParameterSpec.class)) {
            return var1.cast(this.namedCurve);
        } else if (var1.isAssignableFrom(ECGenParameterSpec.class)) {
            String var3 = this.namedCurve.getObjectId();
            return var1.cast(new ECGenParameterSpec(var3));
        } else if (var1.isAssignableFrom(ECKeySizeParameterSpec.class)) {
            int var2 = this.namedCurve.getCurve().getField().getFieldSize();
            return var1.cast(new ECKeySizeParameterSpec(var2));
        } else {
            throw new InvalidParameterSpecException("Only ECParameterSpec and ECGenParameterSpec supported");
        }
    }

    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.namedCurve.getEncoded();
    }

    @Override
    protected byte[] engineGetEncoded(String var1) throws IOException {
        return this.engineGetEncoded();
    }

    @Override
    protected String engineToString() {
        return this.namedCurve == null ? "Not initialized" : this.namedCurve.toString();
    }
}