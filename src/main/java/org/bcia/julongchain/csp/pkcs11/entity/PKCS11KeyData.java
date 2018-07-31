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
package org.bcia.julongchain.csp.pkcs11.entity;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11KeyData {

    private byte[] rawpub;

    private byte[] rawpri;

    public byte[] getRawPub() {
        return rawpub;
    }

    public void setRawPub(byte[] raw) {
        this.rawpub = raw;
    }

    public byte[] getRawPri() {
        return rawpri;
    }

    public void setRawPri(byte[] raw) {
        this.rawpri = raw;
    }
}
