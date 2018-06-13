/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.entity;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public class OUIdentifier {
    private byte[] certifiersIdentifier;
    private String OrganizationalUnitIdentifier;

    public byte[] getCertifiersIdentifier() {
        return certifiersIdentifier;
    }

    public void setCertifiersIdentifier(byte[] certifiersIdentifier) {
        this.certifiersIdentifier = certifiersIdentifier;
    }

    public String getOrganizationalUnitIdentifier() {
        return OrganizationalUnitIdentifier;
    }

    public void setOrganizationalUnitIdentifier(String organizationalUnitIdentifier) {
        OrganizationalUnitIdentifier = organizationalUnitIdentifier;
    }
}
