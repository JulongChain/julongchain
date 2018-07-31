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
package org.bcia.julongchain.core.common.grpc;

/**
 * 安全选项
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class SecureOptions {
    /**
     * 是否在通讯中使用TLS协议
     */
    private boolean useTLS;
    /**
     * TLS客户端是否必须出示证书进行身份验证
     */
    private boolean requireClientCert;
    /**
     * 用于TLS通讯的PEM编码的X509公钥
     */
    private byte[] certificate;
    /**
     * 用于TLS通讯的PEM编码的私钥
     */
    private byte[] key;
    /**
     * 客户端用来验证服务器证书的PEM编码的X509证书颁发机构集合
     */
    private byte[][] serverRootCAs;
    /**
     * 服务器用来验证客户端证书的PEM编码的X509证书颁发机构集合
     */
    private byte[][] clientRootCAs;
    /**
     * 支持的TLS加密套件清单
     */
    private long[] cipherSuites;

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public boolean isRequireClientCert() {
        return requireClientCert;
    }

    public void setRequireClientCert(boolean requireClientCert) {
        this.requireClientCert = requireClientCert;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[][] getServerRootCAs() {
        return serverRootCAs;
    }

    public void setServerRootCAs(byte[][] serverRootCAs) {
        this.serverRootCAs = serverRootCAs;
    }

    public byte[][] getClientRootCAs() {
        return clientRootCAs;
    }

    public void setClientRootCAs(byte[][] clientRootCAs) {
        this.clientRootCAs = clientRootCAs;
    }

    public long[] getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(long[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }
}
