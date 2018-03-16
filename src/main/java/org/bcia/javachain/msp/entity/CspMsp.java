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

import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.bouncycastle.asn1.x509.CertificateList;

import java.util.Map;

import static org.bouncycastle.asn1.iana.IANAObjectIdentifiers.pkix;

/**
 * @author zhangmingyang
 * @Date: 2018/3/15
 * @company Dingxuan
 */
public class CspMsp {
    int version;
    void setupV11(MspConfigPackage.FabricMSPConfig fabricMSPConfig){
        //调用准备setupv1()函数
        //函数setupv1()中,进行setupCrypto，setupCAs，setupAdmins，setupCRLs，
        //setupSigningIdentity,setupOUs 等操作

    }
    void validateIdentityOUsV11(Identity identity){
    }

    IIdentity[] rootCerts;
    IIdentity[] intermediateCerts;
    byte[][] tlsRootCerts;
    byte[][]  tlsIntermediateCerts;
    Map<String,Boolean> certificationTreeInternalNodesMap;
    ISigningIdentity signer;
    IIdentity[] admins;
    ICsp csp;
    String name;
    VerifyOptions verifyOptions;


    CertificateList CRL[];
    Map<String,Byte[][]> ouIdentifiers;
    MspConfigPackage.FabricCryptoConfig fabricCryptoConfig;
    boolean ouEnforcement;
    OUIdentifier clientOU;
    OUIdentifier peerOU;
    OUIdentifier orderOU;
}
