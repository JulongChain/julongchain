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

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECCCipherBlob {

    private byte[] xCoordinate;
    private byte[] yCoordinate;
    private byte[] hash;
    private long lCipherLen;
    private byte[] byteCipher;

    public ECCCipherBlob() {
        this.xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
        this.yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
        this.hash = new byte[32];
        this.byteCipher = new byte[1];
        this.lCipherLen = 0L;
    }

    public ECCCipherBlob(byte[] xCoordinate, byte[] yCoordinate, byte[] hash,
                         long lCipherLen, byte[] byteCipher) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.hash = hash;
        this.byteCipher = byteCipher;
        this.lCipherLen = lCipherLen;
    }


    public void setXCoordinate(byte[] xCoordinate) {
        System.arraycopy(xCoordinate, 0, this.xCoordinate, 0, xCoordinate.length);
    }

    public void setYCoordinate(byte[] yCoordinate) {
        System.arraycopy(yCoordinate, 0, this.yCoordinate, 0, yCoordinate.length);
    }

    public void setHash(byte[] hash) {
        System.arraycopy(hash, 0, this.hash, 0, hash.length);
    }

    public void setCipher(byte[] byteCipher) {
        System.arraycopy(byteCipher, 0, this.byteCipher, 0, byteCipher.length);
        this.lCipherLen = byteCipher.length;
    }

    public byte[] getXCoordinate() {
        return xCoordinate;
    }

    public byte[] getYCoordinate() {
        return yCoordinate;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getCipher() {
        return byteCipher;
    }

    public long getCipherLen() {
        return lCipherLen;
    }

}
