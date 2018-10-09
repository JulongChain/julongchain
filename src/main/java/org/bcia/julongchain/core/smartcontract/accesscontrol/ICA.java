/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.core.smartcontract.accesscontrol;

/**
 * CA defines a certificate authority that can generate
 * certificates signed by it
 *
 * @author wanliangbing
 * @date 2018/3/23
 * @company Dingxuan
 */
public interface ICA {

    /** CertBytes 返回证书授权中心用PEM编码的证书
     *
     * @return
     */
    byte[] certBytes();

    /** newCertKeyPair 返回一个证书和私钥对和零值，
     *                 返回零值是为了以防错误和失败
     *                 证书是由证书授权中心签名授权的给TLS客户验证
     */
    CertKeyPair newClientCertKeyPair();

    /** NewServerCertKeyPair 返回一个带有自定义San的CertKeyPair和零值
     *                       返回一个带有自定义San的CertKeyPair和零值
     *                       证书是由证书授权中心签名授权的，
     *                       返回零值是为了以防错误和失败
     *
     */
    CertKeyPair newServerCertKeyPair(String host);

}
