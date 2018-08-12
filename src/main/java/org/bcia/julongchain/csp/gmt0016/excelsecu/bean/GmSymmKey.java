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

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class GmSymmKey extends GmKey {
    private long hKey; //对称密钥句柄
    private byte[] cipherData; //Key密文数据

    public GmSymmKey() {
    }

    public GmSymmKey(long hKey, byte[] cipherData) {
        this.hKey = hKey;
        this.cipherData = cipherData;
    }

    @Override
    public byte[] ski() {
        //TLV TLV
        byte[] tlvContainer = getTLV(GmKey.TAG_CONTAINER, containerName.length(), containerName.getBytes());
        byte[] tlvCipherData = getTLV(GmKey.TAG_KEY_CIPHER_DATA, cipherData.length, cipherData);
        byte[] skiData = new byte[tlvContainer.length + tlvCipherData.length];
        System.arraycopy(tlvContainer, 0, skiData, 0, tlvContainer.length);
        System.arraycopy(tlvCipherData, 0, skiData, tlvContainer.length, tlvCipherData.length);
        return skiData;
    }

    {}    public byte[] getCipherData() {
        return cipherData;
    }

    public void setCipherData(byte[] cipherData) {
        this.cipherData = cipherData;
    }

    public void setKeyHandle(long hKey) {
        this.hKey = hKey;
    }

    public long getKeyHandle() {
        return hKey;
    }

}
