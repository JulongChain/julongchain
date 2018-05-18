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
package org.bcia.javachain.csp.gm.sdt;

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
    private boolean bDummyKeyStore;
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
        return "SDTGM";
    }

    @Override
    public String getProviderDescription() {
        return null;
    }

    @Override
    public boolean isDefaultCsp() {
        return bDefaultCsp;
    }

    @Override
    public int getSecLevel() {
        return 256;
    }

    @Override
    public String getHashFamily() {
        return "SM3";
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public String getKeyStorePath() { return keyStorePath; }

    @Override
    public boolean isDummyKeystore() {
        return bDefaultCsp;
    }

    public String getSymmetricKeyType() {
        return symmetricKeyType;
    }

    public String getAsymmetricKeyType() {
        return asymmetricKeyType;
    }

    public String getHashType() {
        return hashType;
    }

    public String getSignType() {
        return signType;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public boolean isbEphemeral() {
        return bEphemeral;
    }

    public boolean isbDummyKeyStore() {
        return bDummyKeyStore;
    }

    public boolean isbDefaultCsp() {
        return bDefaultCsp;
    }
}
