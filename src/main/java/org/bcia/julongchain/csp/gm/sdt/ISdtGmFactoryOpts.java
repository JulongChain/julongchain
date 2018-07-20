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

import org.bcia.julongchain.csp.factory.IFactoryOpts;

/**
 * SDT 国密算法工厂选项接口
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public interface ISdtGmFactoryOpts extends IFactoryOpts {
    //获取安全级别
    int getSecLevel();
    //获取哈希函数族
    String getHashFamily();
    //是否为临时密钥
    boolean isEphemeral();
    //获取密钥存储路径
    String getKeyStorePath();
    //获取对称密钥类型
    String getSymmetricKeyType();
    //获取非对称密钥类型
    String getAsymmetricKeyType();
    //获取哈希类型
    String getHashType();
    //获取签名类型
    String getSignType();
    //获取公钥数据存储路径
    String getPublicKeyPath();
    //获取私钥数据存储路径
    String getPrivateKeyPath();
    //获取对称密钥数据存储路径
    String getKeyPath();
}
