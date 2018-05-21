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
package org.bcia.javachain.msp.mgmt;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.sm2.SM2KeyExport;
import org.bcia.javachain.csp.gm.sm2.SM2KeyGenOpts;
import org.bcia.javachain.csp.gm.sm2.SM2SignerOpts;
import org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.entity.IdentityIdentifier;
import org.bcia.javachain.msp.entity.OUIdentifier;
import org.bcia.javachain.msp.signer.Signer;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.msp.Identities;
import org.bouncycastle.asn1.x509.Certificate;


import java.io.IOException;

import java.security.cert.CertificateEncodingException;

import static org.bcia.javachain.common.util.Convert.bytesToHexString;
import static org.bcia.javachain.csp.factory.CspManager.getDefaultCsp;

/**
 * @author zhangmingyang
 * @Date: 2018/4/17
 * @company Dingxuan
 */
public class Identity implements ISigningIdentity {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Identity.class);
    public IdentityIdentifier identityIdentifier;
    public Certificate certificate;
    public IKey pk;
    public Msp msp;
    public Signer signer;

    public Identity() {
    }

    public Identity(Certificate certificate, IKey pk, Msp msp) {
        this.certificate = certificate;
        this.pk = pk;
        this.msp = msp;
    }

    public Identity(IdentityIdentifier identityIdentifier, Certificate certificate, IKey pk, Msp msp) {
        this.identityIdentifier = identityIdentifier;
        this.certificate = certificate;
        this.pk = pk;
        this.msp = msp;
    }

    public Identity(IdentityIdentifier identityIdentifier, Certificate certificate, IKey pk, Msp msp, Signer signer) {
        this.identityIdentifier = identityIdentifier;
        this.certificate = certificate;
        this.pk = pk;
        this.msp = msp;
        this.signer = signer;
    }

    Identity newIdentity(Certificate certificate, IKey pk, Msp msp) {

        Certificate cert = msp.sanitizeCert(certificate);
        try {
        //    byte[] certEncoded = cert.getEncoded();
            byte[] digest="123".getBytes();
            // byte[] digest = msp.csp.hash(certEncoded, new SM3HashOpts());
            IdentityIdentifier identityIdentifier=new IdentityIdentifier(msp.name, bytesToHexString(digest));
            return new Identity(identityIdentifier,certificate,pk,msp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    Identity newSigningIdentity(Certificate certificate, IKey pk, Signer signer, Msp msp) {

        Identity identity= (Identity) newIdentity(certificate,pk,msp);
        return new Identity(identity.identityIdentifier,identity.certificate,identity.pk,identity.msp,identity.signer);
    }


    @Override
    public byte[] sign(byte[] msg) {
        //通过签名算法进行签名
        byte[] signvalue;
        if (IFactoryOpts.PROVIDER_GM.equalsIgnoreCase(GlobalMspManagement.defaultCspValue)) {
            try {
                //  log.info("消息配置对称密钥："+mspConfig.getConfig());
                SM2KeyExport sm2KeyExport = (SM2KeyExport) SM2KeyUtil.getKey(new SM2KeyGenOpts());
                signvalue = getDefaultCsp().sign(sm2KeyExport, msg, new SM2SignerOpts());
                log.info("signvalue is ok");
                return signvalue;
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        } else if (IFactoryOpts.PROVIDER_GMT0016.equalsIgnoreCase(GlobalMspManagement.defaultCspValue)) {

        }

        return new byte[0];
    }

    @Override
    public IIdentity getPublicVersion() {
        return null;
    }

    @Override
    public String getMSPIdentifier() {
        return null;
    }

    @Override
    public void validate() {

    }

    @Override
    public OUIdentifier[] getOrganizationalUnits() {
        return new OUIdentifier[0];
    }

    @Override
    public void verify(byte[] msg, byte[] sig) {
        if (IFactoryOpts.PROVIDER_GM.equalsIgnoreCase(GlobalMspManagement.defaultCspValue)) {
            SM2KeyExport sm2KeyExport;
            try {
                sm2KeyExport = (SM2KeyExport) SM2KeyUtil.getKey(new SM2KeyGenOpts());

                boolean verify = getDefaultCsp().verify(sm2KeyExport, sig, msg, new SM2SignerOpts());
                log.info("验证结果：" + verify);
            } catch (JavaChainException e) {
                e.printStackTrace();
            }
        } else if (IFactoryOpts.PROVIDER_GMT0016.equalsIgnoreCase(GlobalMspManagement.defaultCspValue)) {

        }
    }

    @Override
    public byte[] serialize() {

            Identities.SerializedIdentity.Builder serializedIdentity=Identities.SerializedIdentity.newBuilder();
            serializedIdentity.setMspid(this.identityIdentifier.Mspid);
        try {
            serializedIdentity.setIdBytes(ByteString.copyFrom(certificate.getEncoded()));
            return serializedIdentity.build().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void satisfiesPrincipal(MspPrincipal principal) {

    }
}
