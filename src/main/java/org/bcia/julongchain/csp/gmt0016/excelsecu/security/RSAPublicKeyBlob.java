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
package org.bcia.julongchain.csp.gmt0016.excelsecu.security;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class RSAPublicKeyBlob {
    private long algID;
    private long bitLen;
    private byte[] modulus;
    private byte[] publicExponent;

    public RSAPublicKeyBlob() {

    }

    public RSAPublicKeyBlob(long algID, long bitLen, byte[] modulus, byte[] publicExponent)
    {
        this.algID = algID;
        this.bitLen = bitLen;
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    public long getAlgID()
    {
        return algID;
    }

    public long getBitLen()
    {
        return bitLen;
    }

    public byte[] getModulus()
    {
        return modulus;
    }

    public byte[] getPublicExponent()
    {
        return publicExponent;
    }


}
