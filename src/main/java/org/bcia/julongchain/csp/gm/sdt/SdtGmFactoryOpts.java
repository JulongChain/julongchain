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
 * SDT 国密算法工厂选项
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SdtGmFactoryOpts implements ISdtGmFactoryOpts {

    private String symmetricKeyType;    //对称密钥类型
    private String asymmetricKeyType;   //非对称密钥类型
    private String hashType;            //哈希类型
    private String signType;            //签名类型
    private String publicKeyPath;       //公钥数据存储路径
    private String privateKeyPath;      //私钥数据存储路径
    private String keyPath;             //对称密钥数据存储路径

    private String keyStorePath;        //密钥存储路径
    private boolean bDefaultCsp;        //默认Csp

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
        this.keyStorePath = keyPath;
    }

    /**
     * 获取密钥服务提供者名称
     * @return 提供者名称
     */
    @Override
    public String getProviderName() {
        return PROVIDER_GM_SDT;
    }

    /**
     * 获取密钥服务提供者描述信息
     * @return 提供者描述信息
     */
    @Override
    public String getProviderDescription() {
        return "SM algorithm provided by SDT";
    }

    /**
     * 获取密钥存储路径
     * @return 密钥存储路径
     */
    @Override
    public String getKeyStore() {
        return keyStorePath;
    }

    /**
     * 解析配置信息
     * @param optMap 配置信息Map
     */
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

    /**
     * 获取安全级别
     * @return 安全级别
     */
    @Override
    public int getSecLevel() {
        return 256;
    }

    /**
     * 获取哈希函数族
     * @return
     */
    @Override
    public String getHashFamily() {
        return null;
    }

    /**
     * 密钥是否为临时存储
     * @return true/false
     */
    @Override
    public boolean isEphemeral() {
        return false;
    }

    /**
     * 获取密钥存储路径
     * @return 密钥存储路径
     */
    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    /**
     * 获取对称密钥类型
     * @return 对称密钥类型
     */
    @Override
    public String getSymmetricKeyType() {
        return symmetricKeyType;
    }

    /**
     * 获取非对称密钥类型
     * @return 非对称密钥类型
     */
    @Override
    public String getAsymmetricKeyType() {
        return asymmetricKeyType;
    }

    /**
     * 获取哈希类型
     * @return 哈希类型
     */
    @Override
    public String getHashType() {
        return hashType;
    }

    /**
     * 获取签名类型
     * @return 签名类型
     */
    @Override
    public String getSignType() {
        return signType;
    }

    /**
     * 获取公钥数据存储路径
     * @return 公钥数据存储路径
     */
    @Override
    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    /**
     * 获取私钥数据存储路径
     * @return 私钥数据存储路径
     */
    @Override
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    /**
     * 获取对称密钥数据存储路径
     * @return 对称密钥数据存储路径
     */
    @Override
    public String getKeyPath() {
        return keyPath;
    }
}
