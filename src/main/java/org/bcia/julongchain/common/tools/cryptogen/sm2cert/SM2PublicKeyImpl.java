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
import sun.security.util.ECUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509Key;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.InvalidParameterSpecException;

/**
 * Copy of sun.security.ec.ECPublicKeyImpl and compatible with SM2
 *
 * @author chenhao
 * @date 2018/4/18
 * @company Excelsecu
 */
public class SM2PublicKeyImpl extends X509Key implements ECPublicKey {

    static {
        Security.addProvider(new SM2Provider());
    }
    private static final ObjectIdentifier EC_OID = ObjectIdentifier.newInternal(new int[] {1, 2, 840, 10045, 2, 1});
    public static final ObjectIdentifier SM2_OID = ObjectIdentifier.newInternal(new int[] {1, 2, 156, 10197, 1, 301});

    private static final long serialVersionUID = -2462037275160462289L;
    private ECPoint w;
    private ECParameterSpec params = Util.getECParameterSpec();

    public SM2PublicKeyImpl(ECPoint ecPoint) throws InvalidKeyException {
        this.w = ecPoint;
        this.algid = new AlgorithmId(EC_OID, SM2Parameters.getAlgorithmParameters(params));
        this.key = ECUtil.encodePoint(ecPoint, params.getCurve());
    }

    public SM2PublicKeyImpl(byte[] var1) throws InvalidKeyException {
        this.decode(var1);
    }

    public String getAlgorithm() {
        return "SM2";
    }

    @Override
    public ECPoint getW() {
        return this.w;
    }

    @Override
    public ECParameterSpec getParams() {
        return this.params;
    }

    public byte[] getEncodedPublicValue() {
        return (byte[])this.key.clone();
    }

    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        AlgorithmParameters algorithmParameters = this.algid.getParameters();
        if (algorithmParameters == null) {
            throw new InvalidKeyException("EC domain parameters must be encoded in the algorithm identifier");
        } else {
            try {
                this.params = algorithmParameters.getParameterSpec(ECParameterSpec.class);
                this.w = ECUtil.decodePoint(this.key, this.params.getCurve());
            } catch (IOException var3) {
                throw new InvalidKeyException("Invalid EC key", var3);
            } catch (InvalidParameterSpecException var4) {
                throw new InvalidKeyException("Invalid EC key", var4);
            }
        }
    }

    public String toString() {
        return "Sun EC public key, " + this.params.getCurve().getField().getFieldSize() + " bits\n  public x coord: " + this.w.getAffineX() + "\n  public y coord: " + this.w.getAffineY() + "\n  parameters: " + this.params;
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }

}
