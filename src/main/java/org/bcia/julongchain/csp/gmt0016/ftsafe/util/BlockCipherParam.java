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
package org.bcia.julongchain.csp.gmt0016.ftsafe.util;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class BlockCipherParam {

    private byte[] byteIV;
    private long lIVLen;
    private long lPaddingType;
    private long lFeedBitLen;

    public BlockCipherParam() {
        this.byteIV = new byte[32];
        this.lIVLen = 0L;
        this.lPaddingType = 0L;
        this.lFeedBitLen = 0L;
    }

    public BlockCipherParam(byte[] byteIV, long lIVLen, long lPaddingType, long lFeedBitLen) {
        this.byteIV = byteIV;
        this.lIVLen = lIVLen;
        this.lPaddingType = lPaddingType;
        this.lFeedBitLen = lFeedBitLen;
    }

    public void setIV(byte[] byteIV) {
        System.arraycopy(byteIV, 0, this.byteIV, 0, byteIV.length);
    }

    public byte[] getIV() {
        return byteIV;
    }

    public void setIVLen(long lIVlen) {
        this.lIVLen = lIVlen;
    }

    public long getIVLen() {
        return lIVLen;
    }

    public void setPaddingType(long lPaddingType) {
        this.lPaddingType = lPaddingType;
    }

    public long getPaddingType() {
        return lPaddingType;
    }

    public void setFeedBitLen(long lFeedBitLen) {
        this.lFeedBitLen = lFeedBitLen;
    }

    public long getFeedBitLen() {
        return lFeedBitLen;
    }

}
