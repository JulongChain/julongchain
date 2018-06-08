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

import org.bcia.julongchain.csp.gmt0016.excelsecu.common.AlgorithmID;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class BlockCipherParam {

    private byte[] IV;
    private long paddingType;
    private long feedBitLen;

    public static BlockCipherParam getDefault(int algId) {
        BlockCipherParam blockCipherParam = new BlockCipherParam();
        switch (algId) {
            case AlgorithmID.SGD_SM1_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[16]);
                return blockCipherParam;
            case AlgorithmID.SGD_DES_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[8]);
                return blockCipherParam;
            case AlgorithmID.SGD_TDES_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[8]);
                return blockCipherParam;
            case AlgorithmID.SGD_3DES_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[8]);
                return blockCipherParam;
            case AlgorithmID.SGD_SSF33_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[16]);
                return blockCipherParam;
            case AlgorithmID.SGD_SMS4_ECB:
                blockCipherParam.setFeedBitLen(0);
                blockCipherParam.setPaddingType(1);
                blockCipherParam.setIV(new byte[16]);
                return blockCipherParam;
            default:
                return null;
        }
    }

    public void BlockCipherParam() {

    }

    public void BlockCipherParam(byte[] IV, long paddingType, long feedBitLen) {
        this.IV = IV;
        this.paddingType = paddingType;
        this.feedBitLen = feedBitLen;
    }

    public void setFeedBitLen(long feedBitLen) {
        this.feedBitLen = feedBitLen;
    }

    public void setIV(byte[] IV) {
        this.IV = IV;
    }

    public void setPaddingType(long paddingType) {
        this.paddingType = paddingType;
    }

    public long getPaddingType() {
        return paddingType;
    }

    public byte[] getIV() {
        return IV;
    }

    public long getFeedBitLen() {
        return feedBitLen;
    }
}
