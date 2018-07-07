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
package org.bcia.julongchain.csp.gm.sdt;

import java.util.Map;

/**
 * SDT GM algorithm factory options
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SdtGmFactoryOpts implements ISdtGmFactoryOpts {

    private String symmetricKeyType;
    private String asymmetricKeyType;
    private String hashType;
    private String signType;
    private String publicKeyPath;
    private String privateKeyPath;
    private String keyPath;

    private int secLevel;
    private String hashFamily;
    private boolean bEphemeral;
    private String keyStorePath;
    private boolean bDefaultCsp;

    public SdtGmFactoryOpts() {
    }

    public SdtGmFactoryOpts(String symmetricKeyType, String asymmetricKeyType,
                            String hashType, String signType,
                            String publicKeyPath, String privateKeyPath,
                            String keyPath) {
        this.symmetricKeyType = symmetricKeyType;
        this.asymmetricKeyType = asymmetricKeyType;
        this.hashType = hashType;
        this.signType = signType;
        this.publicKeyPath = publicKeyPath;
        this.privateKeyPath = privateKeyPath;
        this.keyPath = keyPath;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_GM_SDT;
    }

    @Override
    public String getProviderDescription() {
        return "SM algorithm provided by SDT";
    }

    @Override
    public boolean isDefaultCsp() {
        return bDefaultCsp;
    }

    @Override
    public void parseFrom(Map<String, String> optMap) {
        this.symmetricKeyType = optMap.get("symmetricKey");
        this.asymmetricKeyType = optMap.get("asymmetric");
        this.hashType = optMap.get("hash");
        this.signType = optMap.get("sign");
        this.publicKeyPath = optMap.get("publicKeyStore");
        this.privateKeyPath = optMap.get("privateKeyStore");
        this.keyPath = optMap.get("keyStore");
    }

    @Override
    public int getSecLevel() {
        return secLevel;
    }

    @Override
    public String getHashFamily() {
        return hashFamily;
    }

    @Override
    public boolean isEphemeral() {
        return bEphemeral;
    }

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public String getSymmetricKeyType() {
        return symmetricKeyType;
    }

    @Override
    public String getAsymmetricKeyType() {
        return asymmetricKeyType;
    }

    @Override
    public String getHashType() {
        return hashType;
    }

    @Override
    public String getSignType() {
        return signType;
    }

    @Override
    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    @Override
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    @Override
    public String getKeyPath() {
        return keyPath;
    }

}
