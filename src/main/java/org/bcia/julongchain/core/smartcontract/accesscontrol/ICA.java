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

    /** CertBytes returns the certificate of the CA in PEM encoding
     *
     * @return
     */
    byte[] certBytes();

    /** newCertKeyPair returns a certificate and private key pair and nil,
     * or nil, error in case of failure
     * The certificate is signed by the CA and is used for TLS client authentication
     */
    CertKeyPair newClientCertKeyPair();

    /** NewServerCertKeyPair returns a CertKeyPair and nil,
     * with a given custom SAN.
     * The certificate is signed by the CA.
     * Returns nil, error in case of failure
     */
    CertKeyPair newServerCertKeyPair(String host);

}
