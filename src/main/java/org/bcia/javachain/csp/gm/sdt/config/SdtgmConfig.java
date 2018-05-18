/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt.config;

/**
 * 配置信息
 *
 * @author tengxiumin
 * @date 2018/05/17
 * @company SDT
 */
public class SdtgmConfig {

    private String symmetricKey;
    private String sign;
    private String hash;
    private String asymmetric;
    private String security;
    private FileKeyStoreConfig fileKeyStore;

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAsymmetric() {
        return asymmetric;
    }

    public void setAsymmetric(String asymmetric) {
        this.asymmetric = asymmetric;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public FileKeyStoreConfig getFileKeyStore() {
        return fileKeyStore;
    }

    public void setFileKeyStore(FileKeyStoreConfig fileKeyStore) {
        this.fileKeyStore = fileKeyStore;
    }
}
