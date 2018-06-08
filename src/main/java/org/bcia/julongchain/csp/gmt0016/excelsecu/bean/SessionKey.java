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
public class SessionKey {
    private ECCCipherBlob eccCipherBlob;
    private long hSessionKey;

    public SessionKey() {

    }

    public SessionKey(ECCCipherBlob eccCipherBlob, long sessionKey) {
        this.eccCipherBlob = eccCipherBlob;
        this.hSessionKey = sessionKey;
    }

    public ECCCipherBlob getEccCipherBlob() {
        return eccCipherBlob;
    }

    public long getSessionKey() {
        return hSessionKey;
    }

    public void setEccCipherBlob(ECCCipherBlob eccCipherBlob) {
        this.eccCipherBlob = eccCipherBlob;
    }

    public void setSessionKey(long sessionKey) {
        this.hSessionKey = sessionKey;
    }
}
