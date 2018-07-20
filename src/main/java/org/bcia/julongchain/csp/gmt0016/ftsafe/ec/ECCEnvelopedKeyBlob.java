/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe.ec;

import org.bcia.julongchain.csp.gmt0016.ftsafe.util.SKFCspKey;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECCEnvelopedKeyBlob {

    private long lVersion;
    private long lSymmAlgID;
    private long lBits;
    private byte[] bEncryptedPriKey;
    private SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob;
    private ECCCipherBlob eccCipherBlob;

    public ECCEnvelopedKeyBlob() {
        this.lVersion = 1L;
        this.lSymmAlgID = 0L;
        this.lBits = 0L;
        this.bEncryptedPriKey = new byte[64];
        this.eccPublicKeyBlob = new SKFCspKey.ECCPublicKeyBlob();
        this.eccCipherBlob = new ECCCipherBlob();
    }

    public ECCEnvelopedKeyBlob(long lVersion, long lSymmAlgID, long lBits,
                               byte[] bEncryptedPriKey, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob,
                               ECCCipherBlob eccCipherBlob) {
        this.lVersion = lVersion;
        this.lSymmAlgID = lSymmAlgID;
        this.lBits = lBits;
        this.bEncryptedPriKey = new byte[bEncryptedPriKey.length];
        System.arraycopy(bEncryptedPriKey, 0, this.bEncryptedPriKey, 0, bEncryptedPriKey.length);
        this.eccPublicKeyBlob = new SKFCspKey.ECCPublicKeyBlob(eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(), eccPublicKeyBlob.getBit());
        this.eccCipherBlob = new ECCCipherBlob(eccCipherBlob.getXCoordinate(),eccCipherBlob.getYCoordinate(),
                eccCipherBlob.getHash(),eccCipherBlob.getCipherLen(),eccCipherBlob.getCipher());
    }

    public void setVersion(long lVersion) {
        this.lVersion = lVersion;
    }

    public void setSymmAlgID(long lSymmAlgID) {
        this.lSymmAlgID = lSymmAlgID;
    }

    public void setBits(long lBits) {
        this.lBits = lBits;
    }

    public void setEncryptedPriKey(byte[] bEncryptedPriKey) {
        this.bEncryptedPriKey = new byte[bEncryptedPriKey.length];
        System.arraycopy(bEncryptedPriKey, 0, this.bEncryptedPriKey, 0, bEncryptedPriKey.length);
    }

    public void setEccPublicKeyBlob(SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob) {
        this.eccPublicKeyBlob = new SKFCspKey.ECCPublicKeyBlob(eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(), eccPublicKeyBlob.getBit());
    }

    public void setEccCipherBlob(ECCCipherBlob eccCipherBlob) {
        this.eccCipherBlob = new ECCCipherBlob(eccCipherBlob.getXCoordinate(),eccCipherBlob.getYCoordinate(),
                eccCipherBlob.getHash(),eccCipherBlob.getCipherLen(),eccCipherBlob.getCipher());
    }

    public long getVersion() {
        return lVersion;
    }

    public long getSymmAlgID() {
        return lSymmAlgID;
    }

    public long getBits() {
        return lBits;
    }

    public SKFCspKey.ECCPublicKeyBlob getEccPublicKeyBlob(){
        return eccPublicKeyBlob;
    }

    public ECCCipherBlob getEccCipherBlob() {
        return eccCipherBlob;
    }

    public byte[] getEncryptedPriKey() {
        return bEncryptedPriKey;
    }
}
