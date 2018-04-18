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
package org.bcia.javachain.msp.entity;

import org.bcia.javachain.csp.factory.IFactoryOpts;

import java.security.spec.ECPoint;

/**
 * @author zhangmingyang
 * @Date: 2018/3/29
 * @company Dingxuan
 */
public class GmCspConfig implements IFactoryOpts {
    private  String symmetricKey;
    private String asymmetric;
    private  String hashType;
    private  String signType;
    private  String publicKeyPath;
    private  String privateKeyPath;

    public GmCspConfig(String symmetricKey, String asymmetric, String hashType, String signType, String publicKeyPath, String privateKeyPath) {
        this.symmetricKey = symmetricKey;
        this.asymmetric = asymmetric;
        this.hashType = hashType;
        this.signType = signType;
        this.publicKeyPath = publicKeyPath;
        this.privateKeyPath = privateKeyPath;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public String getAsymmetric() {
        return asymmetric;
    }

    public String getSignType() {
        return signType;
    }

    public String getHashType() {
        return hashType;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    @Override
    public String getProviderName() {
        return "GM";
    }

    @Override
    public String getProviderDescription() {
        return null;
    }

    @Override
    public boolean isDefaultCsp() {
        return false;
    }
}
