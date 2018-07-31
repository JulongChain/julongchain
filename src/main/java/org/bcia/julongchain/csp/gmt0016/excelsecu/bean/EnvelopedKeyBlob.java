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
package org.bcia.julongchain.csp.gmt0016.excelsecu.bean;

import org.bcia.julongchain.csp.gmt0016.excelsecu.security.ECCPublicKeyBlob;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class EnvelopedKeyBlob {
    private long version;
    private long symmAlgID;
    private long bits;
    private byte[] encryptedPriKey;
    private ECCPublicKeyBlob pubKey;
    private ECCCipherBlob eccCipherBlob;

    public EnvelopedKeyBlob() {

    }

    public EnvelopedKeyBlob(long version, long symmAlgID, long bits, byte[] encryptedPriKey, ECCPublicKeyBlob pubKey, ECCCipherBlob eccCipherBlob) {
        this.version = version;
        this.symmAlgID = symmAlgID;
        this.bits = bits;
        this.encryptedPriKey = encryptedPriKey;
        this.pubKey = pubKey;
        this.eccCipherBlob = eccCipherBlob;
    }

    public byte[] getEncryptedPriKey() {
        return encryptedPriKey;
    }

    public ECCCipherBlob getEccCipherBlob() {
        return eccCipherBlob;
    }

    public ECCPublicKeyBlob getPubKey() {
        return pubKey;
    }

    public long getBits() {
        return bits;
    }

    public long getSymmAlgID() {
        return symmAlgID;
    }

    public long getVersion() {
        return version;
    }

    public void setBits(long bits) {
        this.bits = bits;
    }

    public void setEccCipherBlob(ECCCipherBlob eccCipherBlob) {
        this.eccCipherBlob = eccCipherBlob;
    }

    public void setEncryptedPriKey(byte[] encryptedPriKey) {
        this.encryptedPriKey = encryptedPriKey;
    }

    public void setPubKey(ECCPublicKeyBlob pubKey) {
        this.pubKey = pubKey;
    }

    public void setSymmAlgID(long symmAlgID) {
        this.symmAlgID = symmAlgID;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
