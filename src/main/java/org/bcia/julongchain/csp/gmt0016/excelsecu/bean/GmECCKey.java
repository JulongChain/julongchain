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
public class GmECCKey extends GmKey {
    private boolean signFlag; //标识签名公钥还是加密公钥
    private byte[] xCoordinate;
    private byte[] yCoordinate;

    public GmECCKey(byte[] xCoordinate, byte[] yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public byte[] ski() {
        byte[] tlvContainer = getTLV(GmKey.TAG_CONTAINER, containerName.length(), containerName.getBytes());
        byte flag[] = new byte[1];
        flag[0] = signFlag ? (byte)1 : 0;
        byte[] tlvSignFlag = getTLV(GmKey.TAG_PUBLICK_KEY_SIGN_FLAG, 1, flag);
        byte[] skiData = new byte[tlvContainer.length + tlvSignFlag.length ];
        System.arraycopy(tlvContainer, 0, skiData, 0, tlvContainer.length);
        System.arraycopy(tlvSignFlag, 0, skiData, tlvContainer.length, tlvSignFlag.length);
        return skiData;
    }

    public byte[] getxCoordinate() {
        return xCoordinate;
    }

    public byte[] getyCoordinate() {
        return yCoordinate;
    }

    public void setSignFlag(boolean signFlag) {
        this.signFlag = signFlag;
    }

    public boolean getSignFlag() {
        return signFlag;
    }
}
