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

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class GmKey implements IKey {
    public static final int TAG_CONTAINER = 0x01; //容器
    public static final int TAG_KEY_CIPHER_DATA = 0x02; //对称密钥的密文
    public static final int TAG_PUBLICK_KEY_SIGN_FLAG = 0x03; //公钥signFlag标识签名公钥和加密公钥
    protected String containerName;

    public byte[] toBytes() {
        return new byte[0];
    }

    public byte[] ski() {
        //TLV 结构
        return new byte[0];
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    protected byte[] getTLV(int tag, int length, byte[] value) {
        byte[] tlv = new byte[value.length + 2];
        tlv[0] = (byte) tag;
        tlv[1] = (byte) length;
        System.arraycopy(value, 0, tlv, 2, value.length);
        return tlv;
    }

    public boolean isSymmetric() {
        return false;
    }

    public boolean isPrivate() {
        return false;
    }

    public IKey getPublicKey() {
        return null;
    }


}
