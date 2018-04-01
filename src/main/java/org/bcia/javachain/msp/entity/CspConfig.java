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

import java.security.spec.ECPoint;

/**
 * @author zhangmingyang
 * @Date: 2018/3/29
 * @company Dingxuan
 */
public class CspConfig {
    private  String hashType;
    private  String signType;
    private  String publicKeyPath;
    private  String privateKeyPath;

    public CspConfig(String hashType, String publicKeyPath, String privateKeyPath) {
        this.hashType = hashType;
        this.publicKeyPath = publicKeyPath;
        this.privateKeyPath = privateKeyPath;
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
}
