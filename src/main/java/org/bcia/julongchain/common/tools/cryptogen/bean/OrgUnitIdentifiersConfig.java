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

public class OrgUnitIdentifiersConfig {
    //按go项目中的yaml指定的命名
    private String Certificate;
    private String OrganizationalUnitIdentifier;

    public OrgUnitIdentifiersConfig() {

    }

    public String getCertificate() {
        return Certificate;
    }

    public String getOrganizationalUnitIdentifier() {
        return OrganizationalUnitIdentifier;
    }

    public void setCertificate(String certificate) {
        Certificate = certificate;
    }

    public void setOrganizationalUnitIdentifier(String organizationalUnitIdentifier) {
        OrganizationalUnitIdentifier = organizationalUnitIdentifier;
    }
}
