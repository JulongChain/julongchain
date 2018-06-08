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

package org.bcia.julongchain.common.tools.cryptogen.bean;

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/13
 * @company Excelsecu
 */
public class MockKey implements IKey {

    private String pubKeyErr;

    private String bytesErr;

    private IKey pubKey;

    public MockKey(String pubKeyErr, String bytesErr, IKey pubKey) {
        this.pubKey = pubKey;
        this.bytesErr = bytesErr;
        this.pubKeyErr = pubKeyErr;
    }

    public MockKey() {
    }

    public String getPubKeyErr() {
        return pubKeyErr;
    }

    public void setPubKeyErr(String pubKeyErr) {
        this.pubKeyErr = pubKeyErr;
    }

    public String getBytesErr() {
        return bytesErr;
    }

    public void setBytesErr(String bytesErr) {
        this.bytesErr = bytesErr;
    }

    public IKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(IKey pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{1, 2, 3, 4};
    }

    @Override
    public byte[] ski() {
        return new byte[]{1, 2, 3, 4};
    }

    @Override
    public boolean isSymmetric() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public IKey getPublicKey() {
        return null;
    }
}
