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
public class ECCCipherBlob {
    private byte[] xCoordinate;
    private byte[] yCoordinate;
    private byte[] hash;
    private byte[] cipher;

    public ECCCipherBlob() {

    }

    public ECCCipherBlob(byte[] xCoordinate, byte[] yCoordinate, byte[] hash, byte[] cipher) {
        this.cipher = cipher;
        this.hash = hash;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public byte[] getyCoordinate() {
        return yCoordinate;
    }

    public byte[] getxCoordinate() {
        return xCoordinate;
    }

    public byte[] getCipher() {
        return cipher;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setyCoordinate(byte[] yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void setxCoordinate(byte[] xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setCipher(byte[] cipher) {
        this.cipher = cipher;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
